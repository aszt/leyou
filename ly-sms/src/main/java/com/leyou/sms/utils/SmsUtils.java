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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties prop;

    public String sendSms(String phoneNumber, String signName, String templateCode, String params) {
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
            // 存入redis中
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【短信服务】发送短信异常，手机号码：{}", phoneNumber, e);
            return null;
        }
    }
}
