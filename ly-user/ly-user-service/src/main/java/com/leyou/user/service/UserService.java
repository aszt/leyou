package com.leyou.user.service;

public interface UserService {
    Boolean checkData(String data, Integer type);

    void sendCode(String phone);
}
