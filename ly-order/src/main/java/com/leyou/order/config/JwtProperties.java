package com.leyou.order.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;

    private String cookieName;

    // 公钥
    private PublicKey publicKey;

    /**
     * 对象一旦实例化后，就应该读取公钥和私钥
     * 此注解在构造函数执行之后执行
     */
    @PostConstruct
    public void init() throws Exception {
        // 读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
