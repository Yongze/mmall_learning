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
 * Created by yw850 on 6/25/2017.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){
        String filename = file.getOriginalFilename();
        String fileExtensionName = filename.substring(filename.lastIndexOf('.') + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("Start uploading file which of name is:{}, path is {} and new file name is {}",filename, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
//            mkdirs会创建路径上所有没创建的文件夹
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);
        try {
            //succeed to upload file
            file.transferTo(targetFile);

            //upload files to ftp server
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //remove images in upload dir after upload files to ftp server
//            targetFile.delete();
        } catch (IOException e) {
            logger.error("Exception for uploading files.",e);
            return null;
        }
        return targetFile.getName();
    }
}
