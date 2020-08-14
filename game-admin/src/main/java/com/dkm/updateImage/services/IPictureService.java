package com.dkm.updateImage.services;

import com.dkm.updateImage.entities.ImageUpdateResultVo;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;


public interface IPictureService {
   @NotNull
   ImageUpdateResultVo update(@NotNull MultipartFile var1, @NotNull String var2);
}
