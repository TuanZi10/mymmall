package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Flash on 2018/3/21.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService{

    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 返回上传之后的文件名
     * @param file
     * @param path
     * @return
     */
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
    //扩展名
        String fileExtensionName = fileName.substring(fileName.indexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件,文件名:{},上传路径:{},新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);//赋予写权限
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);
            //1.将文件上传至ftp服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //2.把上传成功的源文件删除
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;//捕获异常
        }
        return targetFile.getName();
    }
}
