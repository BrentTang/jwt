package com.vimdream.jwt.entity;

import com.vimdream.htool.json.JsonUtil;
import com.vimdream.htool.string.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import lombok.*;

/**
 * @Title: JwtInfo
 * @Author vimdream
 * @ProjectName jwt-spring-boot-starter-autoconfigure
 * @Description: TODO
 * @Date 2020/6/23 16:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class JwtInfo implements TokenInfo {

    @NonNull
    private String customInfo;
    private Integer type;

    public static final int NORMAL_TOKEN = 1;
    public static final int REFRESH_TOKEN = 2;

    private static final String CUSTOM_INFO = "customInfo";
    private static final String TOKEN_TYPE = "type";

    @Override
    public JwtBuilder builderToken(JwtBuilder builder) {
        if (StringUtil.isNotBlank(customInfo))
            builder.claim(CUSTOM_INFO, customInfo);
        builder.claim(TOKEN_TYPE, type == null ? NORMAL_TOKEN : type);
        return builder;
    }

    public static JwtInfo deserialize(Claims body) {
        JwtInfo jwtInfo = new JwtInfo();
        if (body.get(CUSTOM_INFO) != null)
            jwtInfo.setCustomInfo(body.get(CUSTOM_INFO).toString());
        jwtInfo.setType(Integer.valueOf(body.get(TOKEN_TYPE).toString()));
        return jwtInfo;
    }

    public <T> T convert(Class<T> clazz) {
        if (StringUtil.isBlank(customInfo))
            return null;
        return JsonUtil.parse(customInfo, clazz);
    }
}
