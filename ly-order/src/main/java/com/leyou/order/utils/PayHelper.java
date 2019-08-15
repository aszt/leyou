package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayStateEnum;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayConfig config;

    @Autowired
    private OrderMapper orderDao;

    @Autowired
    private OrderStatusMapper statusDao;

    public String createOrder(Long orderId, Long totalPay, String desc) {
        try {
            Map<String, String> data = new HashMap<>();
            // 商品描述
            data.put("body", desc);
            // 订单号
            data.put("out_trade_no", orderId.toString());
            // 金额，单位分
            data.put("total_fee", totalPay.toString());
            // 调用微信支付的终端IP
            data.put("spbill_create_ip", "127.0.0.1");
            // 回调地址
            data.put("notify_url", config.getNotifyUrl());
            // 交易类型为扫码支付
            data.put("trade_type", "NATIVE");

            // 利用wxPay工具，完成下单
            Map<String, String> result = wxPay.unifiedOrder(data);

            // 打印结果
            for (Map.Entry<String, String> entry : result.entrySet()) {
                String key = entry.getKey();
                System.out.println(key + (key.length() >= 8 ? "\t:" : "\t\t:") + entry.getValue());
            }
            System.out.println("-------------------------------");

            // 判断通信和业务标识
            isSuccess(result);

            // 下单成功，获取支付连接
            String url = result.get("code_url");
            return url;
        } catch (Exception e) {
            log.error("【微信下单】 创建预交易订单异常失败", e);
            return null;
        }
    }

    public void isSuccess(Map<String, String> result) {
        // 判断通信标识
        if (WXPayConstants.FAIL.equals(result.get("return_code"))) {
            log.error("【微信下单】微信下单通信失败，失败原因：{}", result.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }

        // 判断业务标识
        if (WXPayConstants.FAIL.equals(result.get("result_code"))) {
            log.error("【微信下单】微信下单业务失败，错误码：{}，错误原因：{}",
                    result.get("err_code"), result.get("err_code_des"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
    }


    public void isValidSign(Map<String, String> data) {
        try {
            // 重新生成签名，和传过来的签名进行比较
            String sign1 = WXPayUtil.generateSignature(data, config.getKey(), WXPayConstants.SignType.HMACSHA256);
            String sign2 = WXPayUtil.generateSignature(data, config.getKey(), WXPayConstants.SignType.MD5);

            // 和传过来的签名进行比较
            String sign = data.get("sign");
            if (!StringUtils.equals(sign, sign1) && !StringUtils.equals(sign, sign2)) {
                // 签名有误，抛出异常
                throw new LyException(ExceptionEnum.INVALID_SIGN_ERROR);
            }
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_SIGN_ERROR);
        }

    }

    public PayStateEnum queryPayState(Long orderId) {
        try {
            // 组织请求参数
            Map<String, String> data = new HashMap<>();
            // 订单号
            data.put("out_trade_no", orderId.toString());
            // 查询状态
            Map<String, String> result = wxPay.orderQuery(data);

            // 校验状态
            isSuccess(result);

            // 校验签名
            isValidSign(result);

            // 校验金额
            // 3 检验金额
            String totalFeeStr = result.get("total_fee");  //订单金额
            String tradeNo = result.get("out_trade_no");  //订单编号
            if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)) {
                throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }
            // 3.1 获取结果中的金额
            Long totalFee = Long.valueOf(totalFeeStr);
            // 3.2 获取订单金额
            Order order = orderDao.selectByPrimaryKey(orderId);
            if (totalFee != /*order.getActualPay()*/ 1) {
                // 金额不符
                throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }

            /**
             * SUCCESS—支付成功
             * REFUND—转入退款
             * NOTPAY—未支付
             * CLOSED—已关闭
             * REVOKED—已撤销（付款码支付）
             * USERPAYING--用户支付中（付款码支付）
             * PAYERROR--支付失败(其他原因，如银行返回失败)
             */
            String state = result.get("trade_state");
            // 支付成功
            if (WXPayConstants.SUCCESS.equals(state)) {
                // 修改订单状态
                OrderStatus status = new OrderStatus();
                status.setStatus(OrderStatusEnum.PAYED.value());
                status.setOrderId(orderId);
                status.setPaymentTime(new Date());
                int count = statusDao.updateByPrimaryKeySelective(status);
                if (count != 1) {
                    throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
                }
                // 返回成功
                return PayStateEnum.SUCCESS;
            }
            // 未支付
            if ("NOTPAY".equals(state) || "USERPAYING".equals(state)) {
                return PayStateEnum.NOT_PAY;
            }
            // 失败
            return PayStateEnum.FAIL;
        } catch (Exception e) {
            return PayStateEnum.NOT_PAY;
        }
    }
}
