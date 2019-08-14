package com.leyou.cart.service.impl;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "cart:uid:";

    @Override
    public void addCart(Cart cart) {
        // 获取登录用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PREFIX + user.getId();
        // hashKey
        String hashKey = cart.getSkuId().toString();
        // 记录num
        Integer num = cart.getNum();

        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        // 判断当前购物车商品，是否存在
        if (operation.hasKey(hashKey)) {
            // 存在，修改数量
            String json = operation.get(hashKey).toString();
            cart = JsonUtils.parse(json, Cart.class);
            cart.setNum(cart.getNum() + num);
        }
        // 写回redis
        operation.put(hashKey, JsonUtils.serialize(cart));
    }

    @Override
    public List<Cart> queryCartList() {
        // 获取登录用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PREFIX + user.getId();

        if (!redisTemplate.hasKey(key)) {
            // key 不存在，返回404
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        // 获取登录用户的所有购物车
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        List<Cart> carts = operation.values().stream()
                .map(s -> JsonUtils.parse(s.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }

    @Override
    public void updateNum(Long skuId, Integer num) {
        // 获取登录用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PREFIX + user.getId();
        // hashKey
        String hashKey = skuId.toString();
        // 获取操作
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        // 判断是否存在
        if (!operation.hasKey(hashKey)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }

        // 查询购物车
        Cart cart = JsonUtils.parse(operation.get(hashKey).toString(), Cart.class);
        cart.setNum(num);

        // 写回redis
        operation.put(hashKey, JsonUtils.serialize(cart));
    }

    @Override
    public void deleteCart(Long skuId) {
        // 获取登录用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PREFIX + user.getId();
        // hashKey
        String hashKey = skuId.toString();
        // 获取操作
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        if (!operation.hasKey(hashKey)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        // 删除商品
        operation.delete(hashKey);
    }
}
