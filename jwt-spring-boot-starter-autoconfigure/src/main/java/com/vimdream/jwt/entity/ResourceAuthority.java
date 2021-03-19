package com.vimdream.jwt.entity;

/**
 * @Title: ResourceAuthority
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2021/2/25 17:07
 */
public interface ResourceAuthority {

    /**
     * 通过url获取资源所需权限表达式
     * 支持  "p1 | p2, p3"  =>  存在p1或p2 并且 存在p3
     * @param url
     * @return
     */
    String getAuthority(String url);

}
