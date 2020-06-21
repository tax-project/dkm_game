package com.dkm.seed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper;
import com.dkm.attendant.dao.AttendantMapper;
import com.dkm.attendant.entity.vo.User;
import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.bo.UserInfoQueryBo;
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
import com.dkm.seed.dao.SeedsFallMapper;
import com.dkm.seed.dao.UserLandUnlockMapper;
import com.dkm.seed.entity.LandSeed;
import com.dkm.seed.entity.Seed;
import com.dkm.seed.entity.SeedsFall;
import com.dkm.seed.entity.vo.*;
import com.dkm.seed.service.ISeedService;
import com.dkm.seed.vilidata.RandomUtils;
import com.dkm.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: dkm_game
 * @DESCRIPTION:
 * @USER: 刘梦祺
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
    private SeedsFallMapper seedsFallMapper;


    /**
     * 根据用户id得到种子
     */
    @Override
    public List<SeedPlantUnlock> queryUserIdSeed(Long userId) {

        //查看用户是否是新用户
        List<SeedUnlock> seedUnlocks = seedMapper.queryIsById(userId);

        if(seedUnlocks.size()==0){
            List<SeedUnlock> seedUnlocks1=new ArrayList<>();

            List<Seed> attenDants = seedMapper.selectList(null);

            if(attenDants.size()==0) {
               throw new ApplicationException(CodeType.PARAMETER_ERROR,"没有种子");
            }

            for (int j = 0; j < attenDants.size(); j++) {

                SeedUnlock seedUnlock=new SeedUnlock();
                seedUnlock.setPuId(idGenerator.getNumberId());
                seedUnlock.setUserId(userId);

                //得到总需要解锁的次数
                double pow = Math.pow(Math.ceil(attenDants.get(j).getSeedGrade() / 2.0), 2);
                Integer powInteger = Integer.valueOf((int) pow);

                seedUnlock.setSeedAllUnlock(powInteger);
                seedUnlock.setSeedId(attenDants.get(j).getSeedId());
                seedUnlocks1.add(seedUnlock);
            }

            //增加新用户进来和用户对应的种子
            seedMapper.insertSeedUnlock(seedUnlocks1);

        }

        List<SeedPlantUnlock> seeds = seedMapper.queryUserIdSeed(userId);

        for (int i = 0; i < seeds.size(); i++) {

            //种植所获得的经验
            double experience = Math.pow(seeds.get(i).getSeedGrade(), 2 / 5.0) * 100;
            Integer experienceInteger = Integer.valueOf((int) experience);

            //种植一次所获得的金币
            double userGold  = Math.pow(seeds.get(i).getSeedGrade(), 2)*50 +500;
            Integer userGoldInteger = Integer.valueOf((int) userGold);
            seeds.get(i).setSeedExperience(experienceInteger);
            seeds.get(i).setSeedGold(userGoldInteger);

        }

        return seeds;
    }
    /**
     * 根据种子id得到种子
     *
     */
    @Override
    public SeedDetailsVo querySeedById(Integer seeId) {

        UserLoginQuery user = localUser.getUser();

        SeedDetailsVo seedDetailsVo = seedMapper.querySeedById(seeId, user.getId());

        //int sum=(int)Math.ceil(seedDetailsVo.getSeedGrade()/10.00)*10;
        seedDetailsVo.setPrestige(18);
        seedDetailsVo.setUnlockFragmentedGoldCoins(seedDetailsVo.getSeedGrade()*1000);

        return seedDetailsVo;
    }



    /**
     * 解锁植物
     * unlockmoeny 解锁金额
     *  grade      种子等级
     * seedid     种子id
     * @seedPresentUnlock 种子当前解锁进度
     * seedPresentAggregate 种子当前解锁进度
     * @return
     */
    @Override
    public Message unlockPlant(SeedVo seedVo) {
        UserLoginQuery user = localUser.getUser();

        //得到用户金币
        User user1 = attendantMapper.queryUserReputationGold(user.getId());

        if(user1.getUserInfoGold()<seedVo.getUnlockMoney()){
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "金币不足");
        }

        Message message=new Message();

        //如果当前进度等于总进度 则解锁种子 修改种子状态
        Integer seedPresentUnlock = seedVo.getSeedPresentUnlock()+1;
        if(seedPresentUnlock.equals(seedVo.getSeedPresentAggregate())){
            seedMapper.updateSeedPresentUnlock(user.getId(),seedVo.getSeedId(),null,1);
        }

            //种子等级除以10 得出声望
            //等级余10大于0则进一
            //int sum=(int)Math.ceil(seedVo.getGrade()/10.00);
            //修改当前种子解锁进度
            seedMapper.updateSeedPresentUnlock(user.getId(),seedVo.getSeedId(),seedVo.getSeedPresentUnlock(),null);

            //修改用户的金币和声望
            int i= seedMapper.uploadUnlockMoneyAndPrestige(seedVo.getUnlockMoney(), 18, user.getId());

            if(i<=0){
                throw new ApplicationException(CodeType.PARAMETER_ERROR, "解锁碎片异常");
            }

            message.setMsg("解锁碎片成功");

            return message;
    }
    /**
     * 种植种子
     * 收取种子
     */
    @Override
    public void queryAlreadyPlantSeed(SeedPlantVo seedPlantVo) {

        //如果等于一就是种植种子
        if (seedPlantVo.getStatus() == 1) {

            Map<String, Object> map = new HashMap<>();

            List<LandSeed> list = new ArrayList<>();

            //得到用户token信息
            UserLoginQuery user = localUser.getUser();

            User user1 = attendantMapper.queryUserReputationGold(user.getId());
            if (user1.getUserInfoGold() < seedPlantVo.getSeedGold()) {
                throw new ApplicationException(CodeType.PARAMETER_ERROR, "金币不足");
            }

            //根据用户查询解锁的土地
            List<UserLandUnlock> userLandUnlocks = landMapper.queryUnlockLand(localUser.getUser().getId());

            //种植时减去用户金币
            IncreaseUserInfoBO increaseUserInfoBO = new IncreaseUserInfoBO();

            //算出种植种子需要多少钱
            Integer gold = seedPlantVo.getSeedGold() * userLandUnlocks.size();
            increaseUserInfoBO.setUserId(user.getId());
            increaseUserInfoBO.setUserInfoGold(gold);

            //减少金币
            Result result = userFeignClient.cutUserInfo(increaseUserInfoBO);

            //计算种子成熟时间 得到秒数。等级的3次方除以2.0*20+60
            double ripetime = Math.pow(seedPlantVo.getSeedGrade(), 3 / 2.0) * 20 + 60;
            //将秒数转换成整数类型
            Integer integer = Integer.valueOf((int) ripetime);
            //得到时间戳转换成时间格式，最后得到种子成熟的时间
            LocalDateTime time2 = LocalDateTime.ofEpochSecond(System.currentTimeMillis() / 1000 + integer, 0, ZoneOffset.ofHours(8));

            //当前时间后的一分钟
            LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(-1);

            //查询已种植的种子
            LambdaQueryWrapper<LandSeed> queryWrapper1 = new LambdaQueryWrapper<LandSeed>()
                    .eq(LandSeed::getUserId, user.getId())
                    .eq(LandSeed::getLeStatus, 1);

            List<LandSeed> listLand = landSeedMapper.selectList(queryWrapper1);



            LambdaQueryWrapper<LandSeed> queryWrapper = new LambdaQueryWrapper<LandSeed>()
                    .eq(LandSeed::getUserId, user.getId())
                    //.eq(LandSeed::getLeStatus, 3)
                    .eq(LandSeed::getSeedId,seedPlantVo.getSeedId());

            List<LandSeed> list1 = landSeedMapper.selectList(queryWrapper);

            int PlantingTimes=0;

            //已解锁土地数量减去已种植的种子数量  得到最终种植的次数
            PlantingTimes= userLandUnlocks.size()-listLand.size();

            //如果查询出来长度等于0说明是新种植 添加到数据库 ，第一个种子持续1分钟产出红包
            if(list1.size()==0){

                for (int i = 0; i < PlantingTimes; i++) {

                    LandSeed landSeed = new LandSeed();
                    //生成主键id
                    landSeed.setId(idGenerator.getNumberId());
                    //土地编号
                    landSeed.setLaNo(userLandUnlocks.get(i).getLaNo());
                    //种子id
                    landSeed.setSeedId(seedPlantVo.getSeedId());
                    //根据token得到用户id
                    landSeed.setUserId(user.getId());

                    if(i==0){
                        //结束时间
                        landSeed.setPlantTime(localDateTime);
                        //是否新种子
                        landSeed.setNewSeedIs(1);
                    }else{
                        //结束时间
                        landSeed.setPlantTime(time2);
                    }

                    //状态 1为种植
                    landSeed.setLeStatus(1);


                    list.add(landSeed);

                }

                //增加要种植种子的信息和用户信息
                int i = seedMapper.addPlant(list);
                if (i <= 0) {
                    throw new ApplicationException(CodeType.PARAMETER_ERROR, "种植异常");
                }

                //如果不是新种子
            }else{

                //查询已经收取种子的数量  作为种植的次数
                LambdaQueryWrapper<LandSeed> queryWrapper2 = new LambdaQueryWrapper<LandSeed>()
                        .eq(LandSeed::getUserId, user.getId())
                        .eq(LandSeed::getLeStatus, 3)
                        .eq(LandSeed::getSeedId,seedPlantVo.getSeedId());

                List<LandSeed> list2 = landSeedMapper.selectList(queryWrapper2);

                //如果土地的长度大于种子已经种植的次数
                if(userLandUnlocks.size()>list2.size()){
                   /* LambdaQueryWrapper<LandSeed> insertqueryWrapper = new LambdaQueryWrapper<LandSeed>()
                            .eq(LandSeed::getUserId, user.getId())
                            .eq(LandSeed::getLeStatus, 3)
                            .eq(LandSeed::getSeedId,seedPlantVo.getSeedId());*/
                   LandSeed landSeed=new LandSeed();
                   landSeed.setId(idGenerator.getNumberId());
                   landSeed.setPlantTime(LocalDateTime.now());
                   landSeed.setUserId(localUser.getUser().getId());
                   landSeed.setSeedId(seedPlantVo.getSeedId());
                   landSeed.setLeStatus(3);
                   landSeed.setLaNo(userLandUnlocks.size());
                   landSeed.setNewSeedIs(null);
                   landMapper.insert(landSeed);
                }

                for (int i = 0; i < PlantingTimes; i++) {
                    LambdaQueryWrapper<LandSeed> wrapper = new LambdaQueryWrapper<LandSeed>()
                            .eq(LandSeed::getId,list2.get(i).getId());

                    LandSeed landSeed=new LandSeed();
                    landSeed.setLeStatus(1);
                    landSeed.setPlantTime(time2);
                    int update = landSeedMapper.update(landSeed, wrapper);
                    if (update <= 0) {
                        throw new ApplicationException(CodeType.SERVICE_ERROR, "更新失败");
                    }
                }
            }













               /* int num=0;

                LambdaQueryWrapper<LandSeed> queryWrapper1 = new LambdaQueryWrapper<LandSeed>()
                        .eq(LandSeed::getUserId, user.getId());

                List<LandSeed> listtwo = landSeedMapper.selectList(queryWrapper1);
                if(listtwo.size()==0){
                    //循环用户解锁土地，解锁多少多少土地 种植多少种子
                    for (int i = 0; i < userLandUnlocks.size(); i++) {

                        LandSeed landSeed = new LandSeed();
                        //生成主键id
                        landSeed.setId(idGenerator.getNumberId());
                        //土地编号
                        landSeed.setLaNo(userLandUnlocks.get(i).getLaNo());
                        //种子id
                        landSeed.setSeedId(seedPlantVo.getSeedId());
                        //根据token得到用户id
                        landSeed.setUserId(user.getId());

                        if(i==0){
                            //结束时间
                            landSeed.setPlantTime(localDateTime);
                            //是否新种子
                            landSeed.setNewSeedIs(1);
                        }else{
                            //结束时间
                            landSeed.setPlantTime(time2);
                        }

                        //状态 1为种植
                        landSeed.setLeStatus(1);


                        list.add(landSeed);

                    }

                    //增加要种植种子的信息和用户信息
                    int i = seedMapper.addPlant(list);
                    if (i <= 0) {
                        throw new ApplicationException(CodeType.PARAMETER_ERROR, "种植异常");
                    }
                }else{

                    for (int i = 0; i <listtwo.size(); i++) {
                        if(System.currentTimeMillis()/1000>=listtwo.get(i).getPlantTime().toEpochSecond(ZoneOffset.of("+8"))){
                            num=num+1;
                        }
                    }

                    //查询已经收取的种子
                    LambdaQueryWrapper<LandSeed> queryWrapperTwo = new LambdaQueryWrapper<LandSeed>()
                            .eq(LandSeed::getUserId, user.getId())
                            .eq(LandSeed::getLeStatus, 3);

                    List<LandSeed> list2 = landSeedMapper.selectList(queryWrapperTwo);

                    //循环用户解锁土地，解锁多少多少土地 种植多少种子
                    for (int i = 1; i <=num; i++) {

                        LandSeed landSeed = new LandSeed();
                        //生成主键id
                        landSeed.setId(idGenerator.getNumberId());
                        //土地编号
                        landSeed.setLaNo(list2.get(0).getLaNo());
                        //种子id
                        landSeed.setSeedId(seedPlantVo.getSeedId());
                        //根据token得到用户id
                        landSeed.setUserId(user.getId());
                        if(i==1){
                            //结束时间
                            landSeed.setPlantTime(localDateTime);
                            //是否新种子
                            landSeed.setNewSeedIs(1);
                        }else{
                            //结束时间
                            landSeed.setPlantTime(time2);
                        }

                        //状态 1为种植
                        landSeed.setLeStatus(1);


                        list.add(landSeed);

                    }

                    //增加要种植种子的信息和用户信息
                    int i = seedMapper.addPlant(list);
                    if (i <= 0) {
                        throw new ApplicationException(CodeType.PARAMETER_ERROR, "种植异常");
                    }
                }

            }else{
                //如果自己种子的数量等于自己解锁的土地数量  则修改种植状态
                if (list1.size() == userLandUnlocks.size()) {

                    LambdaQueryWrapper<LandSeed> queryWrapperTwo = new LambdaQueryWrapper<LandSeed>()
                            .eq(LandSeed::getUserId, user.getId())
                            .eq(LandSeed::getLeStatus, 3)
                            .eq(LandSeed::getSeedId,seedPlantVo.getSeedId());

                    List<LandSeed> list2 = landSeedMapper.selectList(queryWrapperTwo);

                    for (int i = 0; i < list2.size(); i++) {
                        LambdaQueryWrapper<LandSeed> wrapper = new LambdaQueryWrapper<LandSeed>()
                                .eq(LandSeed::getId,list2.get(i).getId());

                        LandSeed landSeed=new LandSeed();
                        landSeed.setLeStatus(1);
                        landSeed.setPlantTime(time2);
                        int update = landSeedMapper.update(landSeed, wrapper);
                        if (update <= 0) {
                            throw new ApplicationException(CodeType.SERVICE_ERROR, "更新失败");
                        }
                    }

                } else {
                    LambdaQueryWrapper<LandSeed> wrapper = new LambdaQueryWrapper<LandSeed>()
                            .eq(LandSeed::getUserId,user.getId())
                            .eq(LandSeed::getLeStatus,1);

                    LandSeed landSeed=new LandSeed();
                    landSeed.setLeStatus(1);

                    //修改用户种子种植状态
                    int update = landSeedMapper.update(landSeed, wrapper);


                    LambdaQueryWrapper<LandSeed> queryWrapper1 = new LambdaQueryWrapper<LandSeed>()
                            .eq(LandSeed::getUserId, user.getId());
                    List<LandSeed> LandSeedList = landSeedMapper.selectList(queryWrapper1);


                    //生成主键id
                    landSeed.setId(idGenerator.getNumberId());
                    //土地编号
                    landSeed.setLaNo(LandSeedList.size()+1);
                    //种子id
                    landSeed.setSeedId(seedPlantVo.getSeedId());
                    //根据token得到用户id
                    landSeed.setUserId(user.getId());
                    //结束时间
                    landSeed.setPlantTime(time2);

                    list.add(landSeed);

                    //增加要种植种子的信息和用户信息
                    int i = seedMapper.addPlant(list);
                    if (i <= 0) {
                        throw new ApplicationException(CodeType.PARAMETER_ERROR, "种植异常");
                    }
                }*/



        }
        //等于2代表收取
        if(seedPlantVo.getStatus()==2){
            updateUser(seedPlantVo);
        }
    }






    public void updateUser(SeedPlantVo seedPlantVo) {

        //得到用户token信息
        UserLoginQuery user = localUser.getUser();

        /*int sizes=1;*/

        //得到用户已经种植的数据
        List<LandYesVo> landYesVos = seedMapper.queryAlreadyPlantSd(localUser.getUser().getId());
        /*if(landYesVos.size()==1){
            sizes=0;
        }*/

           //当前时间必须大于等于种植种植结束时间 才能收取
            if(System.currentTimeMillis()/1000>=landYesVos.get(0).getPlantTime().toEpochSecond(ZoneOffset.of("+8"))){

                //根据用户查询解锁的土地
                List<UserLandUnlock> userLandUnlocks = landMapper.queryUnlockLand(localUser.getUser().getId());


                GoldRedVo goldRedVo=null;

                //查询已种植的种子 判断这个种子的成熟时间是否小于等于当前时间，如果小于等于当前时间则可以收取
                LambdaQueryWrapper<LandSeed> queryWrapper1 = new LambdaQueryWrapper<LandSeed>()
                        .eq(LandSeed::getUserId, user.getId())
                        .eq(LandSeed::getLeStatus,1);
                List<LandSeed> LandSeedList = landSeedMapper.selectList(queryWrapper1);
                for (int i = 0; i < LandSeedList.size(); i++) {
                    if(System.currentTimeMillis()/1000>=LandSeedList.get(i).getPlantTime().toEpochSecond(ZoneOffset.of("+8"))){
                        //收取种子后 修改当前用户土地种子的状态
                        LambdaQueryWrapper<LandSeed> wrapper = new LambdaQueryWrapper<LandSeed>()
                                .eq(LandSeed::getId,LandSeedList.get(i).getId());
                        LandSeed landSeed=new LandSeed();
                        landSeed.setLeStatus(3);
                        landSeed.setNewSeedIs(0);

                        //修改用户种子种植状态
                        int update = landSeedMapper.update(landSeed, wrapper);
                    }


                }


                LambdaQueryWrapper<LandSeed> queryWrapper = new LambdaQueryWrapper<LandSeed>()
                        .eq(LandSeed::getUserId, user.getId())
                        .eq(LandSeed::getLeStatus, 3);

                List<LandSeed> list1 = landSeedMapper.selectList(queryWrapper);

                //种植所获得的经验
                double experience = Math.pow(seedPlantVo.getSeedGrade(), 2 / 5.0) * 100*list1.size();
                Integer experienceInteger = Integer.valueOf((int) experience);


                //种植一次所获得的金币
                double userGold  = Math.pow(seedPlantVo.getSeedGrade(), 2)*50 +1000*list1.size();
                Integer userGoldInteger = Integer.valueOf((int) userGold);

                //判断当前经验是否等级下一级的等级 如果等于等级加一
                if(seedPlantVo.getUserInfoNowExperience()+experienceInteger>=seedPlantVo.getUserInfoNextExperience()){

                    seedPlantVo.setUserInfoNowExperience(seedPlantVo.getUserInfoNextExperience()+experienceInteger);
                    //当前经验减去总经验
                    seedPlantVo.setUserInfoNowExperience(seedPlantVo.getUserInfoNowExperience()-seedPlantVo.getUserInfoNextExperience());
                    Result<UserInfoQueryBo> userInfoQueryBoResult = userFeignClient.queryUser(user.getId());

                    //算出下一级的总经验
                    double v = (((userInfoQueryBoResult.getData().getUserInfoGrade() - 1) +
                                ((userInfoQueryBoResult.getData().getUserInfoGrade() - 1) *
                                 (userInfoQueryBoResult.getData().getUserInfoGrade() - 2) * 2) / 2.0) * 100) + 600;

                    Integer nextExperience = Integer.valueOf((int) v);
                    seedPlantVo.setUserInfoNextExperience(nextExperience);

                    //查询掉落的金币红包
                     goldRedVo = seedsFallMapper.queryGoldAndRed(localUser.getUser().getId());

                    //不等于空 说明掉落了红包
                    if (goldRedVo!=null&&goldRedVo.getRedEnvelopes() != null) {
                        seedPlantVo.setRedPacketDropped(goldRedVo.getRedEnvelopes());
                    }

                    if(goldRedVo!=null&&goldRedVo.getGoldCoin()!=null){
                        seedPlantVo.setUserGold(userGoldInteger+goldRedVo.getGoldCoin());
                    }else{
                        seedPlantVo.setUserGold(userGoldInteger);

                    }

                    //随便传值 sql语句只是加了1
                    seedPlantVo.setSeedGrade(seedPlantVo.getUserGold()+1);
                    seedPlantVo.setUserId(user.getId());

                    //修改用户信息
                    int i = seedMapper.updateUser(seedPlantVo);
                    Result<UserInfoQueryBo> userInfoQueryBoResults = userFeignClient.queryUser(user.getId());

                    //每三级解锁一块土地
                    if(userInfoQueryBoResults.getData().getUserInfoGrade()%3==0){
                        //查询用户没有解锁的土地 状态等于0解锁第一块土地
                        List<UserLandUnlock> userLandUnlocks1 = landMapper.queryNotUnlocked(user.getId());

                        //应该拥有土地
                        int i2 = userInfoQueryBoResults.getData().getUserInfoGrade() / 3 +1;

                        //现在拥有土地
                        int size = userLandUnlocks.size();

                        //需要解锁
                        if(i2>size&&i2<=9){
                            //需要解锁的土地数
                            if(userLandUnlocks1.get(0).getLaStatus() == 0){
                                int i1 = landMapper.updateStatus(user.getId(), userLandUnlocks1.get(0).getLaNo());
                                if(i1<=0){
                                    throw new ApplicationException(CodeType.SERVICE_ERROR,"等级等于"+userInfoQueryBoResults.getData().getUserInfoGrade()+"！解锁失败");
                                }
                            }
                        }
                    }
                } else{
                    //加上掉落的金币
                    if(goldRedVo!=null&&goldRedVo.getGoldCoin()==null){
                        seedPlantVo.setUserGold(userGoldInteger+goldRedVo.getGoldCoin());
                    }else{
                        seedPlantVo.setUserGold(userGoldInteger);
                    }


                    seedPlantVo.setUserInfoNowExperience(experienceInteger);

                    //不等于空 说明掉落了红包
                    if(goldRedVo!=null&&goldRedVo.getRedEnvelopes()!=0.0){
                        seedPlantVo.setRedPacketDropped(goldRedVo.getRedEnvelopes());
                    }

                    seedPlantVo.setUserId(user.getId());

                    //修改用户信息
                    int i = seedMapper.updateUsers(seedPlantVo);
                    if(i<=0){
                        log.info("收取时，修改用户信息失败");
                        throw new ApplicationException(CodeType.SERVICE_ERROR);
                    }

                }

            }else{
                throw new ApplicationException(CodeType.PARAMETER_ERROR,"种子成熟时间还没到哦");
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

        return userFeignClient.queryUser(user.getId());
    }

    @Override
    public List<LandYesVo> queryAlreadyPlantSd() {
        //得到用户token信息
        UserLoginQuery user = localUser.getUser();

        List<LandYesVo> landYesVos = seedMapper.queryAlreadyPlantSd(user.getId());

        if(landYesVos.size()==0){
                return null;
        }else{
            for (int i = 0; i < landYesVos.size(); i++) {
                //种植结束时间装换成秒数
                long l1 = landYesVos.get(i).getPlantTime().toEpochSecond(ZoneOffset.of("+8"));
                landYesVos.get(i).setTime(l1);
            }
        }

        return landYesVos;
    }

}
