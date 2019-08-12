package com.leyou.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties prop;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "sms:phone:";

    private static final long SMS_MIN_INTERVAL_IN_MILLIS = 60000;

    public String sendSms(String phoneNumber, String signName, String templateCode, String params) {
        // 限流控制
        String key = KEY_PREFIX + phoneNumber;
        String lastTime = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(lastTime)) {
            Long last = Long.valueOf(lastTime);
            if (System.currentTimeMillis() - last < SMS_MIN_INTERVAL_IN_MILLIS) {
                log.info("【短信服务】短信发送频率过高，被拦截，手机号：{}", phoneNumber);
                return null;
            }
        }
        DefaultProfile profile = DefaultProfile.getProfile("default", prop.getAccessKeyId(), prop.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", params);
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            Map<String, String> resp = JsonUtils.parseMap(data, String.class, String.class);
            if (!resp.get("Code").equals("OK")) {
                log.info("【短信服务】发送短信失败，phoneNumber：{}，原因：{}", phoneNumber, resp.get("Message"));
                return null;
            }

            // 发送短信日志
            log.info("[短信服务]，发送短信验证码，手机号:{}", phoneNumber);

            // 发送短信成功后，写入redis，指定生存时间未1分钟
            redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), 1, TimeUnit.MINUTES);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【短信服务】发送短信异常，手机号码：{}", phoneNumber, e);
            return null;
        }
    }
}
