package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by yw850 on 6/25/2017.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
