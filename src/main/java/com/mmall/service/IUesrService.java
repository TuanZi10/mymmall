package com.mmall.service;


import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUesrService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse selectquestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> fogetResetPwd(String username,String passwdNew,String forgetToken);

    ServerResponse<String> resetPasswd(String passwordOld,String passwordNew,User user);

    ServerResponse<User> updateInfo(User user);

    ServerResponse<User> getInfo(Integer userID);

    ServerResponse checkAdminRole(User user);
    }
