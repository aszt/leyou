package com.leyou.order.service;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.PayStateEnum;
import com.leyou.order.pojo.Order;

import java.util.Map;

public interface OrderService {
    Long createOrder(OrderDTO orderDTO);

    Order queryOrderById(Long id);

    String createPayUrl(Long orderId);

    void handleNotify(Map<String, String> result);

    PayStateEnum queryOrderState(Long orderId);
}
