package com.dkm.attendant.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dkm.attendant.dao.AttendantMapper;
import com.dkm.attendant.entity.AttenDant;
import com.dkm.attendant.entity.AttendantUser;
import com.dkm.attendant.entity.vo.*;
import com.dkm.attendant.service.IAttendantService;
import com.dkm.attendant.service.IAttendantUserService;
import com.dkm.config.RedisConfig;
import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.exception.ApplicationException;
import com.dkm.feign.BaseFeignClient;
import com.dkm.feign.UserFeignClient;
import com.dkm.feign.entity.PetsDto;
import com.dkm.jwt.contain.LocalUser;
import com.dkm.jwt.entity.UserLoginQuery;
import com.dkm.knapsack.domain.vo.TbEquipmentKnapsackVo;
import com.dkm.knapsack.service.ITbEquipmentKnapsackService;
import com.dkm.utils.DateUtil;
import com.dkm.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: dkm_game
 * @DESCRIPTION:
 * @USER: 刘梦祺
 * @DATE: 2020/5/11 14:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttendantServiceImpl implements IAttendantService {
    @Autowired
    private AttendantMapper attendantMapper;

    @Autowired
    private LocalUser localUser;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ITbEquipmentKnapsackService iTbEquipmentKnapsackService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private BaseFeignClient baseFeignClient;

    @Autowired
    private IAttendantUserService attendantUserService;

    @Autowired
    private RedisConfig redisConfig;

    private final String REDIS_LOCK = "REDIS::LOCK:ATTENDANT";

    /**
     * 获取用户抓到的跟班信息
     * @return
     */
    @Override
    public Map<String,Object> queryThreeAtt() {
        //得到用户登录的token信息
        UserLoginQuery query = localUser.getUser();
        //查询到所有系统跟班
        List<AttUserAllInfoVo> list = attendantMapper.queryThreeAtt(query.getId(), 0);

        //查询到所有用户跟班
        List<AttUserAllInfoVo> list1 = attendantMapper.queryThreeAtt(query.getId(), 1);

        Map<String,Object> map = new HashMap<>(2);

        //系统跟班
        map.put("sys-att",list);

        //用户跟班
        map.put("user-att",list1);

        return map;
    }



    /**
     * 获取用户声望和金币
     * @return
     */
    @Override
    public User queryUserReputationGold() {
        //得到用户登录的token信息
        UserLoginQuery query = localUser.getUser();
        User user = attendantMapper.queryUserReputationGold(query.getId());
        if(user==null){
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "获取用户声望和金币异常");
        }
        return user;
    }

    @Override
    public List<TbEquipmentKnapsackVo> selectUserIdAndFood() {
        //得到用户登录的token信息
        UserLoginQuery query = localUser.getUser();
        return iTbEquipmentKnapsackService.selectFoodId();
    }

    @Override
    public List<User> queryRandomUser() {
        //得到用户登录的token信息
        UserLoginQuery query = localUser.getUser();
        List<User> users = attendantMapper.queryRandomUser();
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getUserId()==query.getId()){
                users.remove(i);
            }
        }
        return users;
    }
    /**
     * 解雇
     */
    @Override
    public int dismissal(Long caughtPeopleId) {
        return attendantMapper.dismissal(caughtPeopleId);
    }

    @Override
    public Map<String, Object> petBattle(Long caughtPeopleId) {
        //我方随机上场的宠物
        String myPet=null;

        //他方随机上场的宠物
        String hePet=null;

        //我方装备血量之和
        double myedLisfe=0;

        //他方装备血量之和
        double heEdLisfe=0;

        //我方各装被属性加成
        double bonuses=0;

        //他各装被属性加成
        double heBonuses=0;






        //他方防御力
        double defenseOtherSide=0;

        //得到他方血量
        double ourHealth1=0;




        //得到我方战力
        Integer ourCapabilities=0;


        /**
         * 我方数据
         */
        //我方装备加成
        double myEquipmentBonus = 0;

        //得到最终我方的战力
        Integer myRipetime=0;

        //我方防御力
        double ourDefenses=0;

        //得到最终我方血量
        double ourHealth=0;

        //我方装备加成
       // double myEquipBonus=0;


        /**
         * 他方数据
         */
        //得到最终他方的防御力
        double heDefense = 0;

        //他方装备加成
        double heEquipmentBonus = 0;

        //得到最终他方的战力
        int heRipetime1=0;

        //他方最终得到的血量
        double heEquipBonus=0;

        Map<String,Object> map=new HashMap<>();
        //得到用户登录的token信息
        UserLoginQuery query = localUser.getUser();
        //自己的信息
        Result<UserInfoQueryBo> userInfoQueryBoResult = userFeignClient.queryUser(query.getId());
        //对手用户信息
        Result<UserInfoQueryBo> userInfoQueryBoResultCaughtPeopleId = userFeignClient.queryUser(caughtPeopleId);


        //他方宠物信息
        Result<List<PetsDto>> petInfo1 = baseFeignClient.getPetInfo(caughtPeopleId);
        //随机获取他方宠物
        PetsDto hePetsDto = petInfo1.getData().get(new Random().nextInt(petInfo1.getData().size()));
        hePet=hePetsDto.getPetName();


        Integer defaultOtherForce=0;
        //得到装备信息
        List<TbEquipmentKnapsackVo> tbEquipmentKnapsackVos1 = iTbEquipmentKnapsackService.selectUserIdTwo(caughtPeopleId);
        System.out.println("他方装备 = " + tbEquipmentKnapsackVos1.size());
        //如果没有装备
        if(tbEquipmentKnapsackVos1.size()==0){
            //血量
            heEquipBonus=500;
            //他方装备防御力
            heDefense=0;
            //得到他方的战力
            heRipetime1=100;
            System.out.println("对方没有装备 = " + "对方没有装备");
        }else {
            for (int i = 0; i < tbEquipmentKnapsackVos1.size(); i++) {
                /**
                 * 属性加成 1就代表有加成 0代表没有加成
                 */
                if (tbEquipmentKnapsackVos1.get(i).getEdAttribute().intValue() == 1) {
                    // 1 为生命加成 2为才华加成
                    if (tbEquipmentKnapsackVos1.get(i).getEdType().intValue() == 1) {
                        //装备血量加上加成的装备血量
                        //得到最终的血量
                        heEquipBonus = heEquipBonus + tbEquipmentKnapsackVos1.get(i).getEdLife().intValue() * tbEquipmentKnapsackVos1.get(i).getEdTypevalue().doubleValue();
                    }
                    // 1 为生命加成 2 为才华加成
                    if (tbEquipmentKnapsackVos1.get(i).getEdType().intValue() == 2) {
                        //装备才华加上加成的装备才华
                        heEquipBonus = heEquipBonus + tbEquipmentKnapsackVos1.get(i).getEdDefense().intValue() * tbEquipmentKnapsackVos1.get(i).getEdTypevalue().doubleValue();
                    }
                }
                System.out.println("血量======== = " + heEquipBonus);

                /**
                 * 问题： 没有加成我怎么知道他是血量还是才华
                 *
                 * 属性加成 1就代表有加成 0代表没有加成
                 *
                 */
                if (tbEquipmentKnapsackVos1.get(i).getEdAttribute().intValue() == 0) {
                    //没有加成的的话 直接将装备的生命赋值
                    heEquipBonus = heEquipBonus + tbEquipmentKnapsackVos1.get(i).getEdLife().intValue();
                }




/*
                double heEquipmentBonus = 0;*/
                /**
                 * 属性加成 1就代表有加成 0代表没有加成
                 * 如果有加成在判断是生命还是才华
                 */
                //他方装备加成
                if (tbEquipmentKnapsackVos1.get(i).getEdAttribute().intValue() == 1) {
                    // 1 为生命加成 2 为才华加成
                    if (tbEquipmentKnapsackVos1.get(i).getEdType().intValue() == 1) {
                        // 生命加成
                        heEquipmentBonus =heEquipmentBonus+ tbEquipmentKnapsackVos1.get(i).getEdTypevalue().doubleValue();
                    } else if(tbEquipmentKnapsackVos1.get(i).getEdType().intValue() == 2){
                        //才华加成
                        heEquipmentBonus =heEquipmentBonus+ tbEquipmentKnapsackVos1.get(i).getEdTypevalue().doubleValue();
                    }
                }

                System.out.println("1 为生命加成 2 为才华加成 = " + heEquipmentBonus);



                /**
                 * 装备防御力*各个装备属性加成
                 */
                System.out.println("tbEquipmentKnapsackVos1.get(i).getEdDefense().doubleValue() * heEquipBonus = " + tbEquipmentKnapsackVos1.get(i).getEdDefense().doubleValue() * heEquipBonus);
                System.out.println("tbEquipmentKnapsackVos1.get(i).getEdDefense().doubleValue() = " + tbEquipmentKnapsackVos1.get(i).getEdDefense().doubleValue());
                System.out.println("他方装备加成 = " + heEquipBonus);
                heDefense = heDefense + tbEquipmentKnapsackVos1.get(i).getEdDefense().doubleValue() * heEquipBonus;


                //我方方装备加成
                //double myEquipmentBonus = 0;

                if (myEquipmentBonus == 0) {
                    //我方装备加成
                    List<TbEquipmentKnapsackVo> tbEquipmentKnapsackVos = iTbEquipmentKnapsackService.selectUserIdTwo(query.getId());
                    /**
                     * 属性加成 1就代表有加成 0代表没有加成
                     * 如果有加成在判断是生命还是才华
                     */
                    if (tbEquipmentKnapsackVos.get(i).getEdAttribute().intValue() == 1) {
                        // 1 为生命加成 2 为才华加成
                        if (tbEquipmentKnapsackVos.get(i).getEdType().intValue() == 1) {
                            // 生命加成
                            myEquipmentBonus =myEquipmentBonus+tbEquipmentKnapsackVos.get(i).getEdTypevalue().doubleValue();
                        } else {
                            //才华加成
                            myEquipmentBonus =myEquipmentBonus+tbEquipmentKnapsackVos.get(i).getEdTypevalue().doubleValue();
                        }
                    }
                }
            }

            //得到他方的战力
            double heRipetime = StrictMath.pow(userInfoQueryBoResultCaughtPeopleId.getData().getUserInfoRenown(), 1/2.0)+
                    (userInfoQueryBoResultCaughtPeopleId.getData().getUserInfoRenown() * heEquipmentBonus - userInfoQueryBoResult.getData().getUserInfoRenown() + myEquipmentBonus);

            System.out.println("userInfoQueryBoResultCaughtPeopleId.getData().getUserInfoRenown() * heEquipmentBonus = " + userInfoQueryBoResultCaughtPeopleId.getData().getUserInfoRenown() * heEquipmentBonus);
            System.out.println("userInfoQueryBoResult.getData().getUserInfoRenown() + myEquipmentBonus = " + userInfoQueryBoResult.getData().getUserInfoRenown() + myEquipmentBonus);
            //得到最终他方的战力
            heRipetime1 = (int) heRipetime;

            //他方最终得到的血量
            //B = heEdLisfe * heEquipBonus;
            System.out.println("他方最终血量 = " + heEquipBonus);
            System.out.println("他方最终战斗力 = " + heRipetime1);
            System.out.println("他方最终防御力 = " + heDefense);
        }















        //我方宠物信息
        Result<List<PetsDto>> petInfo = baseFeignClient.getPetInfo(query.getId());
        //随机获取我方宠物
        PetsDto myPetsDto = petInfo.getData().get(new Random().nextInt(petInfo.getData().size()));
        myPet=myPetsDto.getPetName();

        //得到自己装备信息
        List<TbEquipmentKnapsackVo> tbEquipmentKnapsackVos = iTbEquipmentKnapsackService.selectUserIdTwo(query.getId());
        System.out.println("我方装备 = " + tbEquipmentKnapsackVos.size());
        if(tbEquipmentKnapsackVos.size()==0){
            //血量
            ourHealth=500;
            //得到我方装备防御力
            ourDefenses=0;
            //得到我方战力
            ourCapabilities=100;
            System.out.println("没有装备 = " + "没有装备");
        }else{
/*            if(tbEquipmentKnapsackVos.size()==0){
                //得到我方战力
                ourCapabilities=100;
            }*/
            for (int i = 0; i < tbEquipmentKnapsackVos.size(); i++) {

                /**
                 * 属性加成 1就代表有加成 0代表没有加成
                 * 得到各个装备属性加成的值
                 */
                if (tbEquipmentKnapsackVos.get(i).getEdAttribute().intValue() == 1) {
                    // 1 为生命加成 2为才华加成
                    if (tbEquipmentKnapsackVos.get(i).getEdType().intValue() == 1) {
                        //装备血量加上加成的装备血量
                        ourHealth = ourHealth + tbEquipmentKnapsackVos.get(i).getEdLife().intValue() * tbEquipmentKnapsackVos.get(i).getEdTypevalue().doubleValue();
                    }
                    // 1 为生命加成 2 为才华加成
                    if (tbEquipmentKnapsackVos.get(i).getEdType().intValue() == 2) {
                        //装备才华加上加成的装备才华
                        ourHealth = ourHealth + tbEquipmentKnapsackVos.get(i).getEdDefense().intValue() * tbEquipmentKnapsackVos.get(i).getEdTypevalue().doubleValue();
                    }
                }
                System.out.println("我方-----血量======== = " + ourHealth);


                /**
                 *
                 * 属性加成 1就代表有加成 0代表没有加成
                 * 如果没有加成直接拿到装备生命
                 */
                if (tbEquipmentKnapsackVos.get(i).getEdAttribute().intValue() == 0) {
                    //没有加成的的话 直接将装备的生命赋值
                    myedLisfe = myedLisfe + tbEquipmentKnapsackVos.get(i).getEdLife().intValue();
                }



                    /**
                     * 属性加成 1就代表有加成 0代表没有加成
                     * 如果有加成在判断是生命还是才华
                     */
                   //我方装备加成
                    if (tbEquipmentKnapsackVos.get(i).getEdAttribute().intValue() == 1) {
                        // 1 为生命加成 2 为才华加成
                        if (tbEquipmentKnapsackVos.get(i).getEdType().intValue() == 1) {
                            // 生命加成
                            myEquipmentBonus =myEquipmentBonus+ tbEquipmentKnapsackVos.get(i).getEdTypevalue().doubleValue();
                        } else if(tbEquipmentKnapsackVos.get(i).getEdType().intValue() == 2){
                            //才华加成
                            myEquipmentBonus =myEquipmentBonus+ tbEquipmentKnapsackVos.get(i).getEdTypevalue().doubleValue();
                        }
                    }

                System.out.println("我方装备加成ppp = " + myEquipmentBonus);


                //他方装备属性加成等于0 在查询一遍赋值
                if(heEquipBonus==0){
                    //List<TbEquipmentKnapsackVo> tbEquipmentKnapsackVoss = iTbEquipmentKnapsackService.selectUserIdTwo(caughtPeopleId);
                    //System.out.println("tbEquipmentKnapsackVoss = " + tbEquipmentKnapsackVoss.size());
                    //System.out.println("tbEquipmentKnapsackVoss.get(i).getEdAttribute().intValue()  = " + tbEquipmentKnapsackVoss.get(i).getEdAttribute().intValue() );
                    /**
                     * 属性加成 1就代表有加成 0代表没有加成
                     * 如果有加成在判断是生命还是才华
                     */
                    if (tbEquipmentKnapsackVos1.get(i).getEdAttribute().intValue() == 1) {
                        // 1 为生命加成 2 为才华加成
                        if (tbEquipmentKnapsackVos1.get(i).getEdType().intValue() == 1) {
                            // 生命加成
                            heEquipmentBonus =heEquipmentBonus+tbEquipmentKnapsackVos1.get(i).getEdTypevalue().doubleValue();
                        } else {
                            //才华加成
                            heEquipmentBonus =heEquipmentBonus+tbEquipmentKnapsackVos1.get(i).getEdTypevalue().doubleValue();
                        }
                    }
                }



                /**
                 * 装备防御力*各个装备属性加成
                 * 得到最终我方防御力
                 */
                System.out.println("tbEquipmentKnapsackVos.get(i).getEdDefense().doubleValue() * myEquipmentBonus = " + tbEquipmentKnapsackVos.get(i).getEdDefense().doubleValue() * myEquipmentBonus);
                System.out.println("我方装备加成 = " + myEquipmentBonus);
                ourDefenses = ourDefenses + tbEquipmentKnapsackVos.get(i).getEdDefense().doubleValue() * myEquipmentBonus;
            }
            //得到我方的战力
            double heRipetime = StrictMath.pow(userInfoQueryBoResult.getData().getUserInfoRenown().doubleValue(), 1/2.0)+
                    (userInfoQueryBoResult.getData().getUserInfoRenown().doubleValue() *myEquipmentBonus  - userInfoQueryBoResultCaughtPeopleId.getData().getUserInfoRenown().doubleValue() + heEquipmentBonus);
            //得到最终我方的战力
            //System.out.println("heRipetime = " + heRipetime);
            myRipetime= (int) heRipetime;
            System.out.println("我方血量 = " + ourHealth);
            System.out.println("我方防御力 = " + ourDefenses);
            System.out.println("我方战斗力 = " + myRipetime);

        }




        //如果双方宠物相同 等级高的先动手
        if(myPet.equals(hePet)){
            if(myPetsDto.getPGrade()>hePetsDto.getPGrade()){
                //我方先动手
                map.put("status",0);
            }else{
                //他方先动手
                map.put("status",1);
            }
        }
        if ("老鼠".equals(myPet)) {
           if("大象".equals(hePet)){
               //我方先动手
               map.put("status",0);
           }else{
               //他方先动手
               map.put("status",1);
           }
        }else if("老虎".equals(myPet)){
            if("老鼠".equals(hePet)){
                map.put("status",0);
            }else{
                map.put("status",1);
            }
        }else if("大象".equals(myPet)){
            if("老虎".equals(hePet)){
                map.put("status",0);
            }else{
                map.put("status",1);
            }
        }

        map.put("userInfoQueryBoResult",userInfoQueryBoResult);
        map.put("userInfoQueryBoResultCaughtPeopleId",userInfoQueryBoResultCaughtPeopleId);
        //随机生成我方宠物信息
        map.put("myPetsDto",myPetsDto);
        //随机生成我他方宠物信息
        map.put("hePetsDto",hePetsDto);
        //我方血量             我方血量加上我方防御力得到最终血量
        map.put("ourHealth",ourHealth+ourDefenses);
        //他方血量
        map.put("ourHealth1",heEquipBonus+heDefense);
        //我方战力
        map.put("ourCapabilities",ourCapabilities);
        //System.out.println("他方战斗力 = " + otherForce);
        //他方战力
        map.put("heRipetime1",heRipetime1);
        return map;
    }


    @Override
    public AttUserVo addGraspFollowing(Long caughtPeopleId, Integer status, Long attendantId) {

        UserLoginQuery user = localUser.getUser();

        AttUserVo vo = new AttUserVo();

        //根据用户Id查询所有跟班信息
        List<AttendantUser> list = attendantUserService.queryListByUserId(user.getId());

        if (list.size() >= 6) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "您最多只能抓6个跟班");
        }

        //推后12小时
        Long s=System.currentTimeMillis()/1000+43200;
        vo.setS(s);
        AttendantUser attendantUser=new AttendantUser();

        if (status == 1) {
            //抓用户跟班

            try {
                //加锁,保证原子性
                Boolean lock = redisConfig.redisLock(REDIS_LOCK);

                if (!lock) {
                    throw new ApplicationException(CodeType.RESOURCES_NOT_FIND, "网络繁忙请稍后再试");
                }

                AttendantUser attendantUser1 = attendantUserService.queryOne(caughtPeopleId);

                if (attendantUser1 != null) {
                    //该用户已被抓，得到他主人的用户Id返回给前端,继续打
                    Long userId = attendantUser1.getUserId();
                    vo.setAId(attendantId);
                    vo.setCaughtPeopleId(userId);
                    vo.setStatus(1);
                    return vo;
                }

                //查询主人的跟班的用户id
                AttendantUser queryAttendantUser = attendantUserService.queryAttendantUser(caughtPeopleId, user.getId());

                long id = idGenerator.getNumberId();
                if (queryAttendantUser != null) {
                    //说明这是跟主人在打架
                    //将主人的跟班id添加
                    attendantUser.setAtuId(id);
                    attendantUser.setAttendantId(attendantId);
                    attendantUser.setCaughtPeopleId(queryAttendantUser.getCaughtPeopleId());
                    attendantUser.setUserId(user.getId());
                    attendantUser.setExp1(s);
                    attendantUserService.insert(attendantUser);
                    //代表抢用户跟班成功
                    vo.setStatus(0);
                    return vo;
                }
                //跟没有主人的用户打架
                attendantUser.setAtuId(id);
                attendantUser.setAttendantId(attendantId);
                attendantUser.setCaughtPeopleId(caughtPeopleId);
                attendantUser.setUserId(user.getId());
                attendantUser.setExp1(s);
                attendantUserService.insert(attendantUser);
                //代表抢用户跟班成功
                vo.setStatus(0);
                return vo;
            } finally {
                redisConfig.deleteLock(REDIS_LOCK);
            }

        }

        //抓系统跟班
        attendantUser.setAtuId(idGenerator.getNumberId());
        attendantUser.setAttendantId(attendantId);
        attendantUser.setCaughtPeopleId(0L);
        attendantUser.setUserId(user.getId());
        attendantUser.setExp1(s);
        attendantUserService.insert(attendantUser);
        vo.setStatus(0);
        return vo;
    }





    @Override
    public int gather(Integer atuId) {
        long exp1 = System.currentTimeMillis() / 1000 + 43200;
        int gather = attendantMapper.gather(exp1,Long.valueOf(atuId));
        return gather;
    }

    @Override
    public Map<String, Object> combatResults(AttendantVo vo) {

        Map<String, Object> map = new HashMap<>();

        ResultAttendeantVo result = getResult(vo.getMyHealth(),vo.getOtherHealth(),vo.getMyCapabilities(), vo.getOtherForce(), vo.getStatus());

        map.put("myMuch", result.getMyMuch());
        map.put("otherMuch",result.getOtherMuch());
        //我方打对方一次掉的血量
        map.put("myHealth", vo.getMyCapabilities());
        //对方打我方掉的血量
        map.put("otherHealth", vo.getOtherForce());

        //0--我方赢了
        //1--对面赢了
        map.put("result",result);

        return map;
    }


    public ResultAttendeantVo getResult (Integer allMyHealth, Integer allOtherHealth, Integer myCapabilities, Integer otherForce, Integer status) {

        ResultAttendeantVo vo = new ResultAttendeantVo();

        Integer myMuch = 0;
        Integer otherMuch = 0;

        if (status == 0) {
            //我先动手
            while (true) {
                Integer other = allOtherHealth - myCapabilities;
                //得到被攻击后的血量    在赋值给总血量
                allOtherHealth=other;
                myMuch += 1;
                if (other <= 0) {
                    //我方赢了
                    vo.setMyMuch(myMuch);
                    vo.setOtherMuch(otherMuch);
                    vo.setResult(0);
                    return vo;
                }
                Integer my = allMyHealth - otherForce;
                //得到被攻击后的血量    在赋值给总血量
                allMyHealth=my;
                otherMuch += 1;
                if (my <= 0) {
                    //对方赢了
                    vo.setMyMuch(myMuch);
                    vo.setOtherMuch(otherMuch);
                    vo.setResult(1);
                    return vo;
                }
            }


        }

        //对面先动手
        while (true) {
            Integer my = allMyHealth - otherForce;
            //得到被攻击后的血量    在赋值给总血量
            allMyHealth=my;
            otherMuch += 1;
            if (my <= 0) {
                //对方赢了
                vo.setMyMuch(myMuch);
                vo.setOtherMuch(otherMuch);
                vo.setResult(1);
                return vo;
            }
            Integer other = allOtherHealth - myCapabilities;
            //得到被攻击后的血量    在赋值给总血量
            allOtherHealth=other;
            myMuch += 1;
            if (other <= 0) {
                //我方赢了
                vo.setMyMuch(myMuch);
                vo.setOtherMuch(otherMuch);
                vo.setResult(0);
                return vo;
            }
        }

    }


    @Override
    public Map<String,Object> queryAidUser(Long CaughtPeopleId) {
        //得到用户登录的token信息
        UserLoginQuery query = localUser.getUser();

        Map<String,Object> map=new HashMap<>();
        //主人信息
        AttendantUserVo attendantUserVo = attendantMapper.queryAidUser(CaughtPeopleId);
        if(attendantUserVo == null){
            map.put("msg","没有主人");
        }else{
            map.put("attendantUserVo",attendantUserVo);
        }
        //自己的信息
        Result<UserInfoQueryBo> userInfoQueryBoResult = userFeignClient.queryUser(query.getId());
        System.out.println("-->" + userInfoQueryBoResult);
        if (userInfoQueryBoResult.getCode() != 0) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "你他妈就是个傻逼");
        }
        map.put("UserInfoQueryBo",userInfoQueryBoResult.getData());

        return map;
    }

    @Override
    public List<AttenDant> listAttenDant() {
        return attendantMapper.selectList(null);
    }


}
