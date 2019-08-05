package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 问题：避免重复造轮子，及接口路径的不定时变化，API应该由接口提供方进行维护
 * 两种解决方案：
 * 1、直接继承
 * 2、单独抽取一个module去处理Feign请求（因为service引入了interface，不可写在一起），使用者引入即可
 *
 *
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
