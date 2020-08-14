package com.dkm.seed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dkm.attendant.dao.AttendantMapper;
import com.dkm.attendant.entity.vo.User;
import com.dkm.config.RedisConfig;
import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.entity.user.SeedCollectVo;
import com.dkm.exception.ApplicationException;
import com.dkm.feign.UserFeignClient;
import com.dkm.jwt.contain.LocalUser;
import com.dkm.jwt.entity.UserLoginQuery;
import com.dkm.knapsack.domain.bo.IncreaseUserInfoBO;
import com.dkm.land.dao.LandMapper;
import com.dkm.land.entity.vo.Message;
import com.dkm.land.entity.vo.UserLandUnlock;
import com.dkm.seed.dao.LandSeedMapper;
import com.dkm.seed.dao.SeedMapper;
import com.dkm.seed.dao.SeedUnlockMapper;
import com.dkm.seed.entity.LandSeed;
import com.dkm.seed.entity.Seed;
import com.dkm.seed.entity.SeedUnlock;
import com.dkm.seed.entity.bo.SeedFallBO;
import com.dkm.seed.entity.bo.SendCollectBO;
import com.dkm.seed.entity.bo.SendPlantBO;
import com.dkm.seed.entity.vo.*;
import com.dkm.seed.service.IDropStatusService;
import com.dkm.seed.service.ISeedService;
import com.dkm.utils.DateUtils;
import com.dkm.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.dkm.seed.vilidata.TimeLimit.TackBackLimit;

/**
 * @author MQ
 * @PROJECT_NAME: game_project
 * @DESCRIPTION:
 * @DATE: 2020/5/11 16:17
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SeedServiceImpl implements ISeedService {

    @Autowired
    private SeedMapper seedMapper;

    @Autowired
    private LocalUser localUser;

    @Autowired
    private AttendantMapper attendantMapper;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private LandMapper landMapper;

    @Autowired
    private LandSeedMapper landSeedMapper;

    @Autowired
    private IDropStatusService dropStatusService;

    @Autowired
    private SeedUnlockMapper seedUnlockMapper;

   @Autowired
   private RedisConfig redisConfig;

   private String seedRedis = "REDIS::SEED::";


    /**
     * 根据用户id得到种子信息和是否解锁
     */
    @Override
    public List<SeedPlantUnlock> queryUserIdSeed(Long userId) {

        //添加的长度
        int size = 0;

        List<SeedUnlock> seedUnlocks = seedMapper.queryIsById(userId);

        //查询所有的种子
        List<Seed> attenDants = seedMapper.selectList(null);

        //种子长度不等于需要解锁的种子长度 就进行添加后有的种子
        if (attenDants.size() != seedUnlocks.size()) {

            //种植所有数据的长度减去 种子解锁表的长度
            size = attenDants.size() - seedUnlocks.size();

            List<SeedUnlock> seedUnlocks1 = new ArrayList<>();


            for (int j = 0; j < size; j++) {

                SeedUnlock seedUnlock = new SeedUnlock();

                seedUnlock.setPuId(idGenerator.getNumberId());
                seedUnlock.setUserId(userId);

                //得到总需要解锁的次数
                double pow = 0.0;
                if (seedUnlocks.size() == 0) {
                    pow = Math.pow(Math.ceil(attenDants.get(j).getSeedGrade() / 2.0), 2);
                } else {
                    //应为下边是从0开始的所以不需要在加一
                    pow = Math.pow(Math.ceil(attenDants.get(seedUnlocks.size() + j).getSeedGrade() / 2.0), 2);
                }

                Integer powInteger = (int) pow;

                seedUnlock.setSeedAllUnlock(powInteger);


                if (j == 0) {
                    seedUnlock.setSeedId(seedUnlocks.size() + 1);
                } else {
                    seedUnlock.setSeedId(seedUnlocks.size() + j + 1);
                }

                seedUnlocks1.add(seedUnlock);
            }

            //增加新用户进来和用户对应的种子
            seedMapper.insertSeedUnlock(seedUnlocks1);

        }

        List<SeedPlantUnlock> seeds = seedMapper.queryUserIdSeed(userId);

        for (int i = 0; i < seeds.size(); i++) {

            //种植所获得的经验
            double experience = Math.pow(seeds.get(i).getSeedGrade(), 2 / 5.0) * 100;
            Integer experienceInteger = (int) experience;
            seeds.get(i).setSeedExperience(experienceInteger);

            //种子种植金币
            double userGold = Math.pow(seeds.get(i).getSeedGrade(), 2) * 50 + 500;
            Integer userGoldInteger = (int) userGold;
            seeds.get(i).setSeedGold(userGoldInteger);

        }

        return seeds;
    }

    /**
     * 根据种子id得到种子
     */
    @Override
    public SeedDetailsVo querySeedById(Long seeId) {

        UserLoginQuery user = localUser.getUser();

        SeedDetailsVo seedDetailsVo = seedMapper.querySeedById(seeId, user.getId());

        //int sum=(int)Math.ceil(seedDetailsVo.getSeedGrade()/10.00)*10;
        seedDetailsVo.setPrestige(18);
        //算出种子解锁消耗的金币
        seedDetailsVo.setUnlockFragmentedGoldCoins(seedDetailsVo.getSeedGrade() * 1000);

        return seedDetailsVo;
    }


    /**
     * 解锁植物
     * unlockmoeny 解锁金额
     * grade      种子等级
     * seedid     种子id
     *
     * @return
     * @seedPresentUnlock 种子当前解锁进度
     * seedPresentAggregate 种子当前解锁进度
     */
    @Override
    public Message unlockPlant(SeedVo seedVo) {
        UserLoginQuery user = localUser.getUser();

        //判断前面的种子是否解锁
        LambdaQueryWrapper<SeedUnlock> wrapper = new LambdaQueryWrapper<SeedUnlock>()
                .eq(SeedUnlock::getUserId, user.getId());

        List<SeedUnlock> seedUnlocks = seedUnlockMapper.selectList(wrapper);
        for (int i = 0; i < seedUnlocks.size(); i++) {
            if (seedUnlocks.get(i).getSeedId() == seedVo.getSeedId()) {
                //第一个次解锁才进行判断
                if (seedUnlocks.get(i).getSeedPresentUnlock() == 0) {
                    //不是第一个种子 才让他判断前面的种子是否解锁
                    if (seedVo.getSeedId() != 1) {
                        //下标减一 就得到当前种子前面一个种子的状态是否解锁 如果没有解锁就不能解锁当前种子
                        if (seedUnlocks.get(i - 1).getSeedStatus() != 1) {
                            throw new ApplicationException(CodeType.SERVICE_ERROR, "请先解锁前面的种子");
                        }
                    }
                }
            }
        }

        //限制一天只能解锁15次
        if (TackBackLimit(user.getId(), 15)) {
            //解锁种子
            return unlockSeed(seedVo);
        } else {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "今天解锁的次数已超出");
        }

    }

    /**
     * 解锁种子
     *
     * @param seedVo
     * @return
     */
    public Message unlockSeed(SeedVo seedVo) {
        UserLoginQuery user = localUser.getUser();

        //得到用户金币
        User user1 = attendantMapper.queryUserReputationGold(user.getId());

        if (user1.getUserInfoGold() < seedVo.getUnlockMoney()) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "金币不足");
        }

        Message message = new Message();

        //如果当前进度等于总进度 则解锁种子 修改种子状态
        Integer seedPresentUnlock = seedVo.getSeedPresentUnlock() + 1;
        if (seedPresentUnlock.equals(seedVo.getSeedPresentAggregate())) {
            seedMapper.updateSeedPresentUnlock(user.getId(), seedVo.getSeedId(), null, 1);
        }

        //种子等级除以10 得出声望
        //等级余10大于0则进一
        //int sum=(int)Math.ceil(seedVo.getGrade()/10.00);
        //修改当前种子解锁进度
        seedMapper.updateSeedPresentUnlock(user.getId(), seedVo.getSeedId(), seedVo.getSeedPresentUnlock(), null);

        //修改用户的金币和声望
        int i = seedMapper.uploadUnlockMoneyAndPrestige(seedVo.getUnlockMoney(), 18, user.getId());

        if (i <= 0) {
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "解锁碎片异常");
        }

        message.setMsg("解锁碎片成功");

        return message;
    }

    @Override
    public void queryAlreadyPlantSeed(SendPlantBO sendPlantBO) {

        //得到用户token信息
        UserLoginQuery user = localUser.getUser();

        //算出种植种子需要多少钱
        Integer gold = sendPlantBO.getSeedGold() * sendPlantBO.getLandNumber();

        Result<UserInfoQueryBo> userInfoQueryBoResult = userFeignClient.queryUser(user.getId());
        if(userInfoQueryBoResult.getCode()!=0){
            log.info("user feign err-");
            throw new ApplicationException(CodeType.SERVICE_ERROR,"网络忙，请稍后再试");
        }

        if (userInfoQueryBoResult.getData().getUserInfoGold() < gold) {
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "金币不足");
        }

        //是否第一次种植
        LambdaQueryWrapper<LandSeed> wrapper = new LambdaQueryWrapper<LandSeed>()
              .eq(LandSeed::getUserId, user.getId())
              .eq(LandSeed::getSeedId, sendPlantBO.getSeedId());

        List<LandSeed> seedList = landSeedMapper.selectList(wrapper);

        //根据用户查询解锁的土地
        List<UserLandUnlock> userLandUnlocks = landMapper.queryUnlockLand(user.getId());

        //种植时减去用户金币
        IncreaseUserInfoBO increaseUserInfoBO = new IncreaseUserInfoBO();

        increaseUserInfoBO.setUserId(user.getId());
        increaseUserInfoBO.setUserInfoGold(gold);


        //减少金币
        Result result = userFeignClient.cutUserInfo(increaseUserInfoBO);
        if(result.getCode()!=0){
            log.info("user feign err");
            throw new ApplicationException(CodeType.SERVICE_ERROR,"网络忙，请稍后再试");
        }

        //种植种子
        List<LandSeed> list = new ArrayList<>();

        //计算种子成熟时间 得到秒数。等级的3次方除以2.0*20+60
        double ripeTime = Math.pow(sendPlantBO.getSeedGrade(), 3 / 2.0) * 20 + 60;
        //将秒数转换成整数类型
        int integer = (int) ripeTime;
        //得到时间戳转换成时间格式，最后得到种子成熟的时间
        LocalDateTime time2 = LocalDateTime.ofEpochSecond(System.currentTimeMillis() / 1000 + integer, 0, ZoneOffset.ofHours(8));

        //当前时间后的一分钟
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(1);

        LocalDateTime now = LocalDateTime.now();

        long until = now.until(time2, ChronoUnit.MINUTES);

        if (null != seedList && seedList.size() > 0) {
            //旧种子种植
            LambdaQueryWrapper<LandSeed> queryWrapper = new LambdaQueryWrapper<LandSeed>()
                    .eq(LandSeed::getUserId, user.getId())
                    .eq(LandSeed::getSeedId, sendPlantBO.getSeedId());

            int delete = landSeedMapper.delete(queryWrapper);

            if (delete <= 0) {
                throw new ApplicationException(CodeType.SERVICE_ERROR, "操作有误");
            }

           for (UserLandUnlock landUnlock : userLandUnlocks) {
              LandSeed landSeed = new LandSeed();
              //生成主键id
              landSeed.setId(idGenerator.getNumberId());
              //土地编号
              landSeed.setLaNo(landUnlock.getLaNo());
              //种子id
              landSeed.setSeedId(sendPlantBO.getSeedId());
              //根据token得到用户id
              landSeed.setUserId(user.getId());
              //状态 1为种植
              landSeed.setNewSeedIs(0);
              landSeed.setPlantTime(time2);
              landSeed.setLeStatus(1);
              landSeed.setTimeNumber(until);
              list.add(landSeed);
           }
           //增加要种植种子的信息和用户信息
           int i = seedMapper.addPlant(list);
           if (i <= 0) {
              throw new ApplicationException(CodeType.PARAMETER_ERROR, "种植异常");
           }
           return;
        }

        for (int i = 0; i < userLandUnlocks.size(); i++) {
            LandSeed landSeed = new LandSeed();
            //生成主键id
            landSeed.setId(idGenerator.getNumberId());
            //土地编号
            landSeed.setLaNo(userLandUnlocks.get(i).getLaNo());
            //种子id
            landSeed.setSeedId(sendPlantBO.getSeedId());
            //根据token得到用户id
            landSeed.setUserId(user.getId());
            if (i == 0) {
                //结束时间
                landSeed.setPlantTime(localDateTime);
                //是否新种子
                landSeed.setNewSeedIs(1);
            } else {
                //结束时间
                landSeed.setPlantTime(time2);
                //是否新种子
                landSeed.setNewSeedIs(0);
            }
            //状态 1为种植
            landSeed.setLeStatus(1);
            landSeed.setTimeNumber(until);
            list.add(landSeed);
        }
        //增加要种植种子的信息和用户信息
        int i = seedMapper.addPlant(list);
        if (i <= 0) {
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "种植异常");
        }

    }

    @Override
    public List<SeedUnlockVo> queryAreUnlocked(Long userId) {
        //得到用户token信息
        return seedMapper.queryAreUnlocked(userId);
    }

    @Override
    public Result<UserInfoQueryBo> queryUserAll() {
        //得到用户token信息
        UserLoginQuery user = localUser.getUser();
        Result<UserInfoQueryBo> userInfoQueryBoResult = userFeignClient.queryUser(user.getId());
        if(userInfoQueryBoResult.getCode()!=0){
            log.info("user feign err");
            throw new ApplicationException(CodeType.SERVICE_ERROR,"网络忙，请稍后再试");
        }
        return userInfoQueryBoResult;
    }

    @Override
    public List<LandYesVo> queryAlreadyPlantSd() {
        //得到用户token信息
        UserLoginQuery user = localUser.getUser();

        List<LandYesVo> landYesVos = seedMapper.queryAlreadyPlantSd(user.getId());

        if (landYesVos.size() == 0) {
            return null;
        } else {
            for (int i = 0; i < landYesVos.size(); i++) {
                //种植结束时间装换成秒数
                long l1 = landYesVos.get(i).getPlantTime().toEpochSecond(ZoneOffset.of("+8"));
                landYesVos.get(i).setTime(l1);
            }
        }

        return landYesVos;
    }

    /**
     * 批量修改种子状态
     */
    @Override
    public int updateSeedStatus(List<Long> id) {
        return seedMapper.updateSeedStatus(id);
    }


    /**
     *  收取
     * @param sendCollectBO 种子收取参数
     */
    @Override
    public void collectSeed(SendCollectBO sendCollectBO) {
       //得到用户token信息
       UserLoginQuery user = localUser.getUser();

       int golds = 0;
        double seedEnvelopes = 0.0;
       Result<UserInfoQueryBo> userInfoQueryBoResult;
        if (sendCollectBO.getSeedMeOrOther() == 1) {
           //别人抢
           userInfoQueryBoResult = userFeignClient.queryUser(sendCollectBO.getUserId());
        } else {
           userInfoQueryBoResult = userFeignClient.queryUser(user.getId());
        }

        if(userInfoQueryBoResult.getCode()!=0){
            log.info("user feign err");
            throw new ApplicationException(CodeType.SERVICE_ERROR,"网络忙，请稍后再试");
        }

        SeedFallBO seedFallBO = (SeedFallBO)redisConfig.getString(seedRedis + user.getId());
        UserInfoQueryBo data = userInfoQueryBoResult.getData();

        if (seedFallBO != null) {
            if (seedFallBO.getDropped() == null) {
                seedFallBO.setDropped(0);
            }
            if (seedFallBO.getRedPacketsDropped() == null) {
                seedFallBO.setRedPacketsDropped(0.0);
            }
            if (data.getUserInfoGold() == null) {
                data.setUserInfoGold(0);
            }
            if (data.getUserInfoPacketBalance() == null) {
                data.setUserInfoPacketBalance(0.0);
            }
            golds = seedFallBO.getDropped() + data.getUserInfoGold();
            seedEnvelopes = seedFallBO.getRedPacketsDropped() + data.getUserInfoPacketBalance();
        }
        if (sendCollectBO.getStatus() == 0 && sendCollectBO.getSeedMeOrOther() == 0) {
            //正常收取
           SeedCollectVo vo = new SeedCollectVo();
            if (golds != 0) {
                vo.setUserGold(golds);
            }

           vo.setUserInfoPacketBalance(seedEnvelopes);
           vo.setUserId(user.getId());
           vo.setStatus(0);
           Result result = userFeignClient.addSeedCollect(vo);

           if (result.getCode() != 0) {
              log.info("user feign err.,,,.");
              throw new ApplicationException(CodeType.SERVICE_ERROR, "网络忙，请稍后再试");
           }

           //清空redis
           redisConfig.remove(seedRedis + user.getId());
           return;
        }

       //收取种子(经验和金币)
       int number = 0;

       List<UserLandUnlock> unlockList = landMapper.queryNotUnlocked(user.getId());
       if (data.getUserInfoIsVip() == 1) {
          //是VIP
          number = 10 - unlockList.size();
       } else {
          number = 9 - unlockList.size();
       }

       //别人抢还是自己收
       if (sendCollectBO.getSeedMeOrOther() == 1) {
          //别人抢
          SeedFallBO bo = (SeedFallBO)redisConfig.getString(seedRedis + sendCollectBO.getUserId());

          Object string = redisConfig.getString(seedRedis + "much::" + sendCollectBO.getUserId());

          if (string == null) {
             //记录次数   3次以后不能再被抢
             redisConfig.setString(seedRedis + "much::" + sendCollectBO.getUserId(), 1);
          } else {
             int much = (int) string;

             if (much > 3) {
                throw new ApplicationException(CodeType.SERVICE_ERROR, "该用户已经被抢超过3次不能再抢了");
             }

             redisConfig.setString(seedRedis + "much::" + sendCollectBO.getUserId(), 1+ much);
          }

          if (bo == null) {
             throw new ApplicationException(CodeType.SERVICE_ERROR, "用户id参数传的有误");
          }

          SeedCollectVo vo = new SeedCollectVo();
          int gold = (int) (bo.getDropped() * 0.1);
          double envelopes = 0.01;

          vo.setUserId(user.getId());
          vo.setUserGold(gold + data.getUserInfoGold());
          vo.setUserInfoPacketBalance(envelopes + data.getUserInfoPacketBalance());
          vo.setStatus(0);
          Result result = userFeignClient.addSeedCollect(vo);

          if (result.getCode() != 0) {
             log.info("user feign err--");
             throw new ApplicationException(CodeType.SERVICE_ERROR, "网络忙，请稍后再试");
          }

          //重新装配redis
          SeedFallBO bo1 = new SeedFallBO();
          bo1.setDropped(bo.getDropped() - gold);
          bo1.setRedPacketsDropped(bo.getRedPacketsDropped() - envelopes);
          bo1.setFallingNumber(bo.getFallingNumber());

          redisConfig.setString(seedRedis + sendCollectBO.getUserId(), bo1);

          return;
       }

       //种植所获得的经验
       double seedExperience = Math.pow(sendCollectBO.getSeedGrade(), 2 / 5.0) * 100 * number;
       Long resultExperience = (long) seedExperience;
       //先算出该用户是否升级
       Long experience = resultExperience + data.getUserInfoNowExperience();

       //删除种子状态表信息
       dropStatusService.deleteDrop(user.getId());

        //先判断是否解锁土地
       //算出解锁土地
       //解锁一块土地  最多9块
       //可以解锁
       //判断是不是VIP
       int seedNumber = data.getUserInfoGrade()/3;
       if (unlockList.size() <= 10 && data.getUserInfoIsVip() == 1 && seedNumber > (10 - unlockList.size()) && data.getUserInfoGrade() % 3 == 0) {
          //是VIP
          //继续解锁
          int updateStatus = landMapper.updateStatus(user.getId(), unlockList.get(0).getLaNo());

          if (updateStatus <= 0) {
             log.info("解锁土地失败......");
             throw new ApplicationException(CodeType.SERVICE_ERROR, "解锁土地失败");
          }
       } else {
          //不是VIP
          if (unlockList.size() > 1) {
             //继续解锁
             int updateStatus = landMapper.updateStatus(user.getId(), unlockList.get(0).getLaNo());

             if (updateStatus <= 0) {
                log.info("解锁土地失败---");
                throw new ApplicationException(CodeType.SERVICE_ERROR);
             }
          }

       }

       //金币和红包
       //判断当前时间大于等于成熟时间
       LocalDateTime now = LocalDateTime.now();
       LambdaQueryWrapper<LandSeed> wrapper = new LambdaQueryWrapper<LandSeed>()
             .eq(LandSeed::getUserId, user.getId());
       List<LandSeed> landSeeds = landSeedMapper.selectList(wrapper);

       for (LandSeed landSeed : landSeeds) {
          long until = now.until(landSeed.getPlantTime(), ChronoUnit.SECONDS);

          //如果是新种子，修改种子状态
          if (landSeed.getNewSeedIs() == 1) {
             //修改成0
             LandSeed land = new LandSeed();
             land.setId(land.getId());
             land.setNewSeedIs(0);
             landSeedMapper.updateById(land);

          }

          if (until > 0) {
             throw new ApplicationException(CodeType.SERVICE_ERROR, "种子还未成熟,成熟时间:" + DateUtils.formatDateTime(landSeed.getPlantTime()));
          }
       }

       //修改种子状态为3
       int updateStatus = landSeedMapper.updateStatus(user.getId());

       if (updateStatus <= 0) {
          throw new ApplicationException(CodeType.SERVICE_ERROR, "修改失败");
       }

       if (experience < data.getUserInfoNextExperience()) {
          //小于下一级升级需要的经验
          //不升级
          SeedCollectVo vo = new SeedCollectVo();
          vo.setStatus(1);
          vo.setUserId(user.getId());
          if (golds != 0) {
              vo.setUserGold(golds);
          }

          vo.setUserInfoPacketBalance(seedEnvelopes);
          vo.setUserInfoNowExperience(experience);
          Result result = userFeignClient.addSeedCollect(vo);

          if (result.getCode() != 0) {
             log.info("seed user feign err........");
             throw new ApplicationException(CodeType.SERVICE_ERROR, "网络忙，请稍后再试");
          }
          return;
       }

       //升级
       //先算出下一级升级所需要的经验
       Long nowExperience = data.getUserInfoNextExperience() - experience;

       //算出下一级的总经验
       Double nextExperience = (((data.getUserInfoGrade() - 1) +
             ((data.getUserInfoGrade() - 1) *
                   (data.getUserInfoGrade() - 2) * 2) / 2.0) * 100) + 600;

       SeedCollectVo vo = new SeedCollectVo();
       vo.setStatus(2);
       vo.setUserInfoNowExperience(nowExperience);
        if (golds != 0) {
            vo.setUserGold(golds);
        }

       Integer grade = data.getUserInfoGrade() + 1;
       vo.setUserInfoPacketBalance(seedEnvelopes);

       vo.setGrade(grade);
       vo.setUserInfoNextExperience(nextExperience.longValue());
       vo.setUserId(user.getId());

       Result result = userFeignClient.addSeedCollect(vo);

       if (result.getCode() != 0) {
          log.info("user feign err...");
          throw new ApplicationException(CodeType.SERVICE_ERROR, "网络忙，请稍后再试");
       }

    }

}
