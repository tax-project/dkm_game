package com.dkm.manyChat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dkm.manyChat.dao.ManyChatInfoMapper;
import com.dkm.manyChat.entity.ManyChatInfo;
import com.dkm.manyChat.entity.vo.ManyChatInfoVo;
import com.dkm.manyChat.service.IManyChatInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author qf
 * @date 2020/5/15
 * @vesion 1.0
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ManyChatInfoServiceImpl extends ServiceImpl<ManyChatInfoMapper, ManyChatInfo> implements IManyChatInfoService {



   /**
    *  建立群聊的具体群员
    * @param list 成员信息
    */
   @Override
   public void insertAllUser(List<ManyChatInfoVo> list) {


   }
}
