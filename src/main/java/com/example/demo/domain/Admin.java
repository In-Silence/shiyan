package com.example.demo.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Admin {
    // 管理员ID
    private Integer id;

    // 管理员名称
    private String name;

    // 管理员密码
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}