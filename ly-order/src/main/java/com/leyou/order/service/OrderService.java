package com.leyou.order.service;

import com.leyou.order.dto.OrderDTO;

public interface OrderService {
    Long createOrder(OrderDTO orderDTO);
}
