package com.xxl.thirdservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssuploadService {
    String uploadFile(MultipartFile file);

    void removeFile(String url);
}
