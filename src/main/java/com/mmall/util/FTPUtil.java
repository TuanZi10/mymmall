package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Flash on 2018/3/21.
 */
public class FTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pass;
    private FTPClient ftpClient;

    public static boolean uploadFile(List<File> fileList) throws IOException {//抛给业务层,做特殊处理，具体看业务层安排
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器,ip:{}",ftpIp);
        boolean result = ftpUtil.uploadFile("img",fileList);//放到指定目录下
        logger.info("结束上传,上传结果:{}",result);
        return result;
    }

    /**
     *
     * @param remotePath 远程路径
     * @param fileList
     * @return
     */
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;//记录是否上传成功
        FileInputStream inputStream = null;
        //连接ftp服务器
        if (connectServer(this.getIp(),this.port,this.getUser(),this.getPass())) {
            //更改工作目录
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);//缓冲区
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置成二进制文件类型,防止乱码
                ftpClient.enterLocalPassiveMode();//打开被动模式
                for(File fileItem : fileList){
                    inputStream = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),inputStream);//存储文件方法
                }
            } catch (IOException e) {
                uploaded= false;
                logger.error("上传文件异常",e);
            } finally {
                ftpClient.disconnect();
                inputStream.close();
            }
        }
        return uploaded;
    }

    private boolean connectServer(String ip,int port,String user,String pass){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pass);
        } catch (IOException e) {
            logger.error("连接ftp服务异常",e);
        }
        return isSuccess;
    }

    public FTPUtil(String ip, int port, String user, String pass) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public static String getFtpIp() {
        return ftpIp;
    }

    public static void setFtpIp(String ftpIp) {
        FTPUtil.ftpIp = ftpIp;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }

    public static String getFtpPass() {
        return ftpPass;
    }

    public static void setFtpPass(String ftpPass) {
        FTPUtil.ftpPass = ftpPass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
