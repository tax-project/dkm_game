package com.dkm.pay.vilidata.entity;

import eqlee.ctm.apply.entry.vilidata.ResultTokenVo;
import lombok.Data;

/**
 * @author qf
 * @date 2019/12/24
 * @vesion 1.0
 **/
@Data
public class PayVo {


   private Integer StatusCode;

   private Boolean Error;

   private ResultTokenVo Result;

}
