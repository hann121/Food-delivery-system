package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /*
    *文件上传阿里云
    * */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("上传文件:{}",file);

        try {
            //用uuid实现文件名
            String originalFileName=file.getOriginalFilename();
            String extension=originalFileName.substring(originalFileName.lastIndexOf("."));
            String objectName= UUID.randomUUID().toString()+extension;

            String filePath= aliOssUtil.upload(file.getBytes(),objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败{}",e);
        }
        return Result.error("文件上传失败");
    }
}
