package com.dkm.feign.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLoginStatusVo {
    @Nullable
    private String cid;
    @Nullable
    private ResultUser resultUser;
    @Nullable
    private String token;


}