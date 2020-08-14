package com.dkm.updateImage.services.impl;

import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.vo.FileVo;
import com.dkm.exception.ApplicationException;
import com.dkm.feign.AnotherFileFeignClient;
import com.dkm.updateImage.entities.ImageUpdateResultVo;
import com.dkm.updateImage.services.IPictureService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
public class PictureServiceImpl implements IPictureService {
   private final Logger logger = LoggerFactory.getLogger(this.getClass());
   @Resource
   private AnotherFileFeignClient anotherFileFeignClient;

   @NotNull
   public ImageUpdateResultVo update(@NotNull MultipartFile multipartFile, @NotNull String token) {
      logger.info(multipartFile.getName());
      logger.info(String.valueOf(multipartFile.getSize()));
      logger.info(multipartFile.getOriginalFilename());

      Result<FileVo> var7 = anotherFileFeignClient.storeFile(token, multipartFile);
      FileVo var8 = var7.getData();
      if (var8 != null) {
         return new ImageUpdateResultVo(var8.getFileUrl());
      } else {
         throw (new ApplicationException(CodeType.FEIGN_CONNECT_ERROR, "请求文件上传失败！"));
      }
   }
}
