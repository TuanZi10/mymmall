package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 */
@Service("iUserService")//向上注入时，与Controller调用的名称要对应
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMsg("can not find username！");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);//将加密后的password传入sql
        if (user == null) {
            return ServerResponse.createByErrorMsg("Error PWD");
        }
        //处理返回值密码
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("register success", user);
    }

    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(),Const.EMIAL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }

        //调用常量，设置用户身份
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMsg("register fail");
        }
        return ServerResponse.createBySuccessMsg("register success");
    }

    /**
     * @param str
     * @param type 根据用户名还是邮箱判断str调用接口
     * @return
     */
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //check uesrname
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMsg("User exists");
                }
            }
            //check email
            if (Const.EMIAL.equals(str)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMsg("Email exists");
                }

            }
        } else {
            return ServerResponse.createByErrorMsg("Error param");
        }
        return ServerResponse.createBySuccessMsg("check success");
    }

    public ServerResponse selectquestion(String username) {
        ServerResponse response = this.checkValid(username,Const.USERNAME);
        if (response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在！");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMsg("密码问题为空！");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount > 0) {
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMsg("问题答案错误！");
    }

    public ServerResponse<String> fogetResetPwd(String username,String passwdNew,String forgetToken) {
        //1.判断token
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMsg("token为空！");
        }
        //2.判断用户是否存在
        ServerResponse response = this.checkValid(username,Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMsg("用户不存在！");
        }
        //判断token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMsg("token无效或者过期！");
        }
        if (StringUtils.equals(token,forgetToken)){
            String md5passwd = MD5Util.MD5EncodeUtf8(passwdNew);
            int count = userMapper.updatePwdByUsername(username,md5passwd);

            if (count > 0){
                return ServerResponse.createBySuccessMsg("修改密码成功！");
            }
        }else {
            return ServerResponse.createByErrorMsg("token错误");
        }
        return ServerResponse.createByErrorMsg("修改密码失败！");
    }

    public ServerResponse<String> resetPasswd(String passwordOld,String passwordNew,User user) {
        int count = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (count == 0) {
            return ServerResponse.createByErrorMsg("旧密码错误");
        }
        //设置新密码
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("密码更新成功！");
        }
        return ServerResponse.createByErrorMsg("密码更新失败！");
    }

    public ServerResponse<User> updateInfo(User user) {
        int count = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (count > 0){
            return ServerResponse.createByErrorMsg("email已存在，请更换！");
        }
        User updateuser = new User();
        updateuser.setId(user.getId());
        updateuser.setQuestion(user.getQuestion());
        updateuser.setEmail(user.getEmail());
        updateuser.setAnswer(user.getAnswer());
        updateuser.setPhone(user.getPhone());
        int updatecount = userMapper.updateByPrimaryKeySelective(user);
        if (updatecount > 0){
            return ServerResponse.createBySuccess("更新信息成功！",updateuser);
        }
        return ServerResponse.createByErrorMsg("更新个人信息失败！");
    }

    public ServerResponse<User> getInfo(Integer userID){
        User user = userMapper.selectByPrimaryKey(userID);
        if (user == null){
            return ServerResponse.createByErrorMsg("找不到当前用户！");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
