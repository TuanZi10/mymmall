package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByDate(Date createDate);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);//check existence of user

    int checkEmail(String email);

    User selectLogin(@Param("username") String username, @Param("password") String password);//mybatis在传递多个参数需要使用param注解

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);


    int updatePwdByUsername(@Param("username") String username, @Param("passwdNew") String passwdNew);

    int checkPassword(@Param(value = "password") String password, @Param("userId") Integer id);

    int checkEmailByUserId(@Param("email") String email,@Param("userId") Integer userId);
}