package com.vimdream.jwt.properties;

import com.vimdream.htool.encrypt.RsaUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "vimdream.jwt")
@Data
public class JwtProperties {

    private String secret = "vimdream@(jwt}*^31)&hao6631%f3q2";
    private String pubKeyPath = "id_rsa.pub";
    private String priKeyPath = "id_rsa";
    private Long expire = 2 * 3600L;
    private Long refreshExpire = 12 * 3600L;
    private String headerName = "token";
    private String refreshTokenTemplate = "";
    private String resultPlaceholder = "\\$data";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws Exception {
        File pubKey = new File(pubKeyPath);
        File priKey = new File(priKeyPath);
        if (!pubKey.exists() || !priKey.exists()) {
            // 生成公钥和私钥
            RsaUtil.generateKey(pubKeyPath, priKeyPath, secret);
        }

        this.publicKey = RsaUtil.getPublicKey(this.pubKeyPath);
        this.privateKey = RsaUtil.getPrivateKey(this.priKeyPath);
    }

}
