package com.dkm.file.service;

import com.dkm.file.utils.FileVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author qf
 * @date 2020/5/15
 * @vesion 1.0
 **/
public interface IFileService {


   /**
    *  生成二维码并上传服务器
    * @param content
    * @return
    */
   FileVo getQrCode(String content);
}
