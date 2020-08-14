package com.dkm.admin.entities.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLoginResultVo {
    private Boolean loginStatus;
    private String loginToken;
}
