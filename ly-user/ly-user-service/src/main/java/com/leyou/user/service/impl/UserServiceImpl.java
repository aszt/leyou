package com.leyou.user.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userDao;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify:phone:";

    @Override
    public Boolean checkData(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE_ERROR);
        }
        return userDao.selectCount(record) == 0;
    }

    @Override
    public void sendCode(String phone) {
        // 生成key
        String key = KEY_PREFIX + phone;
        // 随机生成6位数字验证码
        String code = NumberUtils.generateCode(6);
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        // 发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);
        // 保存验证码（5分钟有效）
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public void register(User user, String code) {
        String key = KEY_PREFIX + user.getPhone();
        // 从redis取出验证码
        String cacheCode = redisTemplate.opsForValue().get(key);
        // 校验验证码
        if (!StringUtils.equals(code, cacheCode)) {
            throw new LyException(ExceptionEnum.INAVLID_VERIFY_CODE);
        }
        // 生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        // 对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), user.getSalt()));

        // 写入数据库
        user.setCreated(new Date());
        int count = userDao.insert(user);

        if (count != 1) {
            throw new LyException(ExceptionEnum.INVALID_PARAM);
        }
        //把验证码从Redis中删除
        redisTemplate.delete(key);
    }

    @Override
    public User queryUserByUsernameAndPassword(String username, String password) {
        // 查询用户
        User record = new User();
        record.setUsername(username);
        User user = userDao.selectOne(record);

        // 校验
        if (user == null) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        // 检验密码
        if (!StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password, user.getSalt()))) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        // 用户名和密码正确
        return user;
    }
}
