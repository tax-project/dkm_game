package com.dkm.feign.fallback;

import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.vo.FileVo;
import com.dkm.feign.AnotherFileFeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qf
 * @date 2020/6/11
 * @vesion 1.0
 **/
@Component
public class PictureFeignClientFallback implements AnotherFileFeignClient {


    @Override
    public Result<FileVo> getQrCode(String content) {
        return Result.fail(CodeType.FEIGN_CONNECT_ERROR);
    }

    @Override
    public Result<FileVo> storeFile(String token, MultipartFile file) {
        return Result.fail(CodeType.FEIGN_CONNECT_ERROR);
    }



}
