package com.vimdream.jwt.entity;

import java.util.Set;

public interface CustomAuthority {

    /**
     * 用户的权限
     * @return
     */
    Set<String> getCustomAuthority();

}
