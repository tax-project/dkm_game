package com.dkm.money.controller;

import com.dkm.money.service.IRedMoneyService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qf
 * @date 2020/6/9
 * @vesion 1.0
 **/
@Slf4j
@Api(tags = "口令红包API")
@RestController
@RequestMapping("/v1/RedMoney")
public class RedMoneyController {

   @Autowired
   private IRedMoneyService redMoneyService;



}
