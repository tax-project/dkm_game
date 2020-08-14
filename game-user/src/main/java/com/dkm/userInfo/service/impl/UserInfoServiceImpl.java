package com.dkm.userInfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dkm.constanct.CodeType;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.entity.bo.UserInfoSkillBo;
import com.dkm.entity.bo.UserPlunderBo;
import com.dkm.entity.user.SeedCollectVo;
import com.dkm.entity.vo.AttendantWithUserVo;
import com.dkm.entity.vo.IdVo;
import com.dkm.entity.vo.OpponentVo;
import com.dkm.entity.vo.UserInfoAttVo;
import com.dkm.exception.ApplicationException;
import com.dkm.jwt.contain.LocalUser;
import com.dkm.jwt.entity.UserLoginQuery;
import com.dkm.userInfo.dao.UserInfoMapper;
import com.dkm.userInfo.entity.UserInfo;
import com.dkm.userInfo.entity.bo.IncreaseUserInfoBO;
import com.dkm.userInfo.entity.bo.ReputationRankingBO;
import com.dkm.userInfo.entity.bo.UserSectionInfoBO;
import com.dkm.userInfo.service.IUserInfoService;
import com.dkm.utils.IdGenerator;
import com.dkm.wechat.entity.vo.WeChatUserInfoVo;
import com.dkm.wechat.service.IWeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author qf
 * @date 2020/5/11
 * @vesion 1.0
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

   @Autowired
   private IdGenerator idGenerator;

   @Autowired
   private IWeChatService weChatService;

   @Autowired
   private LocalUser localUser;

   @Override
   public void insertUserInfo(Long userId) {
      UserInfo userInfo = new UserInfo();

      userInfo.setUserInfoId(idGenerator.getNumberId());
      userInfo.setUserId(userId);

      //初始化用户信息
      userInfo.setUserInfoGrade(1);
      userInfo.setUserInfoGold(10000);
      userInfo.setUserInfoRenown(500);
      userInfo.setUserInfoDiamonds(0);
      userInfo.setUserInfoIsVip(0);
      userInfo.setUserInfoIntegral(0);
      userInfo.setUserInfoStrength(200);
      userInfo.setUserInfoAllStrength(200);
      userInfo.setUserInfoNowExperience(0L);
      userInfo.setUserInfoNextExperience(600L);
      userInfo.setUserInfoPacketBalance(0.0);
      userInfo.setUserInfoEnvelopeMuch(0);
      userInfo.setUserInfoAllEnvelopeMuch(15);
      userInfo.setUserInfoSkillStatus(0);

      int insert = baseMapper.insert(userInfo);

      if (insert <= 0) {
         throw new ApplicationException(CodeType.SERVICE_ERROR, "增加失败");
      }
   }


   /**
    * 修改红包总次数
    * @param much 次数
    * @param userId 用户id
    */
   @Override
   public void updateUserInfo(Integer much, Long userId,Integer userInfoDiamonds) {

      LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>()
            .eq(UserInfo::getUserId,userId);

      UserInfo userInfo = baseMapper.selectOne(wrapper);

      if (userInfo == null) {
         throw new ApplicationException(CodeType.SERVICE_ERROR, "查询错误");
      }

      UserInfo userInfo1 = new UserInfo();

      userInfo1.setUserInfoAllEnvelopeMuch(userInfo.getUserInfoAllEnvelopeMuch() + much);
      userInfo1.setUserInfoDiamonds(userInfo.getUserInfoDiamonds() - userInfoDiamonds);

      int update = baseMapper.update(userInfo1, wrapper);

      if (update <= 0) {
         throw new ApplicationException(CodeType.SERVICE_ERROR);
      }
   }

   @Override
   public void increaseUserInfo(IncreaseUserInfoBO increaseUserInfoBO) {
      Integer integer = baseMapper.increaseUserInfo(increaseUserInfoBO);
      if (integer<=0){
         throw new ApplicationException(CodeType.SERVICE_ERROR, "添加失败");
      }
   }

   @Override
   public void cutUserInfo(IncreaseUserInfoBO increaseUserInfoBO) {
      Integer integer = baseMapper.cutUserInfo(increaseUserInfoBO);
      if (integer<=0){
         throw new ApplicationException(CodeType.SERVICE_ERROR, "减少失败");
      }
   }

   @Override
   public List<UserPlunderBo> listUserPlunder() {
      return baseMapper.listUserPlunder ();
   }

   @Override
   public void updateStrength(Long userId, Integer grade) {
      Integer integer = baseMapper.updateStrength(userId, grade);

      if (integer <= 0) {
         throw new ApplicationException(CodeType.SERVICE_ERROR);
      }
   }

   @Override
   public void updateInfo(UserInfoSkillBo bo) {
      Integer integer = baseMapper.updateInfo(bo);

      if (integer <= 0) {
         throw new ApplicationException(CodeType.SERVICE_ERROR);
      }
   }

   @Override
   public List<AttendantWithUserVo> listAttUser(Long userId) {
      return baseMapper.listAttUser (userId);
   }

   @Override
   public List<ReputationRankingBO> reputationRanking() {
      return baseMapper.reputationRanking();
   }

   @Override
   public List<OpponentVo> listOpponent(List<IdVo> list) {

      if (null == list || list.size() == 0) {
         return null;
      }

      return baseMapper.listOpponent(list);
   }

   @Override
   public WeChatUserInfoVo queryWeChatUserInfo() {
      UserLoginQuery user = localUser.getUser();
      UserInfoQueryBo bo = weChatService.queryUser(user.getId());

      WeChatUserInfoVo result = new WeChatUserInfoVo();
      BeanUtils.copyProperties(bo,result);
      return result;
   }


   @Override
   public List<UserInfoAttVo> queryUserInfoAtt(List<Long> list) {
      if (null == list || list.size() == 0) {
         return null;
      }
      return baseMapper.queryUserInfoAtt (list);
   }

   @Override
   public UserSectionInfoBO queryUserSection(Long userId) {
      return baseMapper.queryUserSection(userId);
   }

   @Override
   public void addSeedCollect(SeedCollectVo seedCollectVo) {

      LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>()
            .eq(UserInfo::getUserId, seedCollectVo.getUserId());

      //收取红包和金币
      UserInfo userInfo = new UserInfo();

      if (seedCollectVo.getUserGold() != null) {
         userInfo.setUserInfoGold (seedCollectVo.getUserGold());
      }

      if (seedCollectVo.getUserInfoPacketBalance() != null) {
         userInfo.setUserInfoPacketBalance (seedCollectVo.getUserInfoPacketBalance());
      }

      if (seedCollectVo.getUserInfoNowExperience() != null) {
         userInfo.setUserInfoNowExperience (seedCollectVo.getUserInfoNowExperience());
      }

      if (seedCollectVo.getUserInfoNextExperience() != null) {
         userInfo.setUserInfoNextExperience (seedCollectVo.getUserInfoNextExperience());
      }

      if (seedCollectVo.getGrade() != null) {
         userInfo.setUserInfoGrade(seedCollectVo.getGrade());
      }

      int update = baseMapper.update(userInfo, wrapper);

      if (update <= 0) {
         throw new ApplicationException(CodeType.SERVICE_ERROR, "修改失败");
      }

   }
}
