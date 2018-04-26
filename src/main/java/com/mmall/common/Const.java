package com.mmall.common;

/**
 * Created by Flash on 2017/8/11.
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String EMIAL = "email";
    public static final String USERNAME = "username";


    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public interface Cart{
        int CHECKED = 1;//选中状态
        int UN_CHECKED = 0;//未选中
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }


}
