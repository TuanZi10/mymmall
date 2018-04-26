package com.mmall.controller.portal;


import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUesrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/") //公共的地址写在类上面，可以为下面每个方法重用
public class UserController {
    //注入IUserService
    @Autowired
    private IUesrService iUesrService;
    /**
     * 用户登录
     * @param username
     * @param password
     * @param session 用户登陆成功就要把信息放入session
     * @return
     */
    @RequestMapping(value = "login.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //调用service方法
        ServerResponse<User> response = iUesrService.login(username,password);
        if (response.isSuccess()){
            /**
             * 如果登录成功，就把用户放入session
             * 创建并调用另外的常量类Const的CURRENT_USER 作为session的key
             */
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 登出功能
     * 把添加的session删除
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "regist.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUesrService.register(user);
    }

    /**
     *
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUesrService.checkValid(str, type);
    }

    @RequestMapping(value = "getuserinfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMsg("user not login ");
    }

    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUesrService.selectquestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer) {
        return iUesrService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "forget_reset_pwd.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> fogetResetPwd(String username,String passwdNew,String forgetToken){
        return iUesrService.fogetResetPwd(username,passwdNew,forgetToken);
    }

    @RequestMapping(value = "reset_pwd.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String oldPassword,String newPassword) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户未登录！！");
        }
        return iUesrService.resetPasswd(oldPassword,newPassword,user);
    }

    @RequestMapping(value = "update_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfo(HttpSession session,User user) {
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null) {//
            return ServerResponse.createByErrorMsg("用户未登录！");
        }
        user.setId(currentuser.getId());//防止id变化
        user.setUsername(currentuser.getUsername());//uesrname也不能更新
        ServerResponse<User> response = iUesrService.updateInfo(user);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInfo(HttpSession session){
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"未登录需要登录！");
        }
        return iUesrService.getInfo(currentuser.getId());
    }

}
