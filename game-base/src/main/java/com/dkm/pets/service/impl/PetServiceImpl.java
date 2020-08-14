package com.dkm.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dkm.config.RedisConfig;
import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.exception.ApplicationException;
import com.dkm.feign.ResourceFeignClient;
import com.dkm.feign.entity.AddGoodsInfo;
import com.dkm.feign.entity.FoodInfoVo;
import com.dkm.pets.dao.PetsMapper;
import com.dkm.pets.entity.PetUserEntity;
import com.dkm.pets.entity.dto.EatFoodDto;
import com.dkm.pets.entity.dto.PetsDto;
import com.dkm.pets.entity.dto.UserInfo;
import com.dkm.pets.entity.vo.FeedPetInfoVo;
import com.dkm.pets.service.PetService;
import com.dkm.task.service.TaskService;
import com.dkm.turntable.dao.GoodsDao;
import com.dkm.turntable.entity.GoodsEntity;
import com.dkm.utils.DateUtils;
import com.dkm.utils.IdGenerator;
import com.dkm.utils.StringUtils;
import javassist.runtime.Desc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhd
 * @date 2020/5/9 9:17
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PetServiceImpl implements PetService {
    @Resource
    private PetsMapper petsMapper;

    @Resource
    private RedisConfig redisConfig;

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private TaskService taskService;
    @Resource
    private ResourceFeignClient resourceFeignClient;

    /**
     * 获取宠物页面所需信息
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> getAllPets(Long userId) {
        Map<String, Object> map = new HashMap<>();
        //查询背包食物信息
        Result<List<FoodInfoVo>> listResult = resourceFeignClient.getFoodsFegin(userId);
        if (listResult.getCode() != 0) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "获取不到食物信息");
        }
        List<FoodInfoVo> goodsEntities = listResult.getData();
        map.put("foodInfo", goodsEntities);
        //获取用户信息
        UserInfo userInfo = petsMapper.findUserInfo(userId);
        map.put("userInfo", userInfo);
        //计算等级应有宠物数
        Integer petCount = Math.min(userInfo.getUserInfoGrade() / 5 + 1, 3);
        //获得查询宠物数
        Integer count = petsMapper.selectCount(new QueryWrapper<PetUserEntity>().lambda().eq(PetUserEntity::getUserId, userId));
        if (count < petCount) {
            //当前宠物小于应有宠物 >>还有宠物未解锁 >>添加应有宠物
            List<PetUserEntity> list = new ArrayList<>();
            for (int i = count; i < petCount; i++) {
                PetUserEntity pet = new PetUserEntity();
                pet.setPGrade(1);
                pet.setPNowFood(0);
                pet.setUserId(userId);
                pet.setPId(idGenerator.getNumberId());
                pet.setPetId(i + 1L);
                list.add(pet);
            }
            petsMapper.insertList(list);
        }
        //查询宠物信息
        List<PetsDto> petInfo = petsMapper.findById(userId);
        //食物重新排序
        List<FoodInfoVo> collect = goodsEntities.stream().sorted((o1, o2) -> (int) (o1.getFoodId() - o2.getFoodId())).collect(Collectors.toList());
        for (PetsDto petsDto : petInfo) {
            //获取喂食进度  >>  每5级加一次喂食
            double roles = (double) petsDto.getPGrade() / 5 + 2;
            petsDto.setSchedule(new BigDecimal(petsDto.getPNowFood() * 100 / roles).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            //根据宠物等级获取喂食规则
            List<EatFoodDto> role = new ArrayList<>();
            if (petsDto.getSchedule() >= 100) {
                //升级
                FoodInfoVo foodInfoVo = goodsEntities.stream().filter(good -> good.getFoodId() == 3).collect(Collectors.toList()).get(0);
                EatFoodDto eatFoodDto = new EatFoodDto();
                eatFoodDto.setFoodUrl(foodInfoVo.getUrl());
                eatFoodDto.setFoodName(foodInfoVo.getName());
                eatFoodDto.setFoodId(foodInfoVo.getFoodId());
                //奶瓶数量
                eatFoodDto.setENumber(1);
                eatFoodDto.setFoodNumber(listResult.getData().stream().filter(item -> item.getFoodId() == 3).collect(Collectors.toList()).get(0).getFoodNumber());
                role.add(eatFoodDto);
            } else {
                //10级之后才喂食鱼干 >> 喂食种类
                int feed = petsDto.getPGrade() >= 10 ? 2 : 1;
                //食物数量
                int c = petsDto.getPGrade() / 10 + 1;
                //喂食
                    for (int i = 0; i < feed; i++) {
                        FoodInfoVo foodDetailEntity = collect.get(i);
                        EatFoodDto eatFoodDto = new EatFoodDto();
                        eatFoodDto.setFoodUrl(foodDetailEntity.getUrl());
                        eatFoodDto.setFoodName(foodDetailEntity.getName());
                        eatFoodDto.setFoodId(foodDetailEntity.getFoodId());
                        //食物数量
                        eatFoodDto.setENumber(i == 0 ? c : c - 1);
                        eatFoodDto.setFoodNumber(foodDetailEntity.getFoodNumber());
                        role.add(eatFoodDto);
                    }
            }
            petsDto.setCanFeed(true);
            petsDto.setEatFood(role);
        }
        map.put("petInfo", petInfo);
        return map;
    }

    /**
     * 喂食
     */
    @Override
    public void feedPet(FeedPetInfoVo petInfoVo) {
        //修改宠物喂食进度
        PetUserEntity petUserEntity = petsMapper.selectById(petInfoVo.getPId());
        petUserEntity.setPNowFood(petUserEntity.getPNowFood() + 1);
        AddGoodsInfo addGoodsInfo = new AddGoodsInfo();
        addGoodsInfo.setGoodId(1L);
        addGoodsInfo.setNumber(-(petInfoVo.getPGrade() / 10 + 1));
        addGoodsInfo.setUserId(petInfoVo.getUserId());
        Result result = resourceFeignClient.addBackpackGoods(addGoodsInfo);
        //大于十级喂食鱼干
        Result result1 = null;
        if (petInfoVo.getPGrade() >= 10 && result.getCode() == 0) {
            addGoodsInfo.setGoodId(2L);
            addGoodsInfo.setNumber(-(petInfoVo.getPGrade() / 10));
            result1 = resourceFeignClient.addBackpackGoods(addGoodsInfo);
        }
        //更新背包食物
        if (petsMapper.updateUserRenown(petInfoVo.getUserId(), petInfoVo.getPGrade() / 5 + 50) < 1
                || petsMapper.updateById(petUserEntity) < 1 || result.getCode() != 0
                || (petInfoVo.getPGrade() >= 10 && result1.getCode() != 0)) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "喂食失败");
        }
        try {
            taskService.setTaskProcess(petInfoVo.getUserId(),2L);
        } catch (Exception e) {
            log.info("宠物喂食任务出错");
            e.printStackTrace();
        }
        redisConfig.setString("pet" + petInfoVo.getUserId(), DateUtils.formatDateTime(LocalDateTime.now()));
    }

    /**
     * 宠物升级
     *
     * @param petInfoVo
     */
    @Override
    public void petLevelUp(FeedPetInfoVo petInfoVo) {
        //修改宠物喂食进度>> 提升等级
        PetUserEntity petUserEntity = petsMapper.selectById(petInfoVo.getPId());
        petUserEntity.setPNowFood(0);
        petUserEntity.setPGrade(petUserEntity.getPGrade() + 1);
        AddGoodsInfo addGoodsInfo = new AddGoodsInfo();
        addGoodsInfo.setGoodId(3L);
        addGoodsInfo.setNumber(-1);
        addGoodsInfo.setUserId(petInfoVo.getUserId());
        //更新背包食物 >> 升级需要一个奶瓶
        if (petsMapper.updateUserRenown(petInfoVo.getUserId(), petInfoVo.getPGrade() / 5 + 70) < 1
                || petsMapper.updateById(petUserEntity) < 1
                || resourceFeignClient.addBackpackGoods(addGoodsInfo).getCode() != 0) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "升级失败");
        }
    }

    @Override
    public List<PetsDto> getPetInfo(Long userId) {
        //获取用户信息
        UserInfo userInfo = petsMapper.findUserInfo(userId);
        //计算等级应有宠物数
        Integer petCount = Math.min(userInfo.getUserInfoGrade() / 5 + 1, 3);
        //获得查询宠物数
        Integer count = petsMapper.selectCount(new QueryWrapper<PetUserEntity>().lambda().eq(PetUserEntity::getUserId, userId));
        if (count < petCount) {
            //当前宠物小于应有宠物 >>还有宠物未解锁 >>添加应有宠物
            List<PetUserEntity> list = new ArrayList<>();
            for (int i = count; i < petCount; i++) {
                PetUserEntity pet = new PetUserEntity();
                pet.setPGrade(1);
                pet.setPNowFood(0);
                pet.setUserId(userId);
                pet.setPId(idGenerator.getNumberId());
                pet.setPetId(i + 1L);
                list.add(pet);
            }
            petsMapper.insertList(list);
        }
        return petsMapper.findById(userId);
    }

    @Override
    public String isHunger(Long userId) {
        String string = (String) redisConfig.getString("pet" + userId);
        if (StringUtils.isNotEmpty(string) && DateUtils.parseDateTime(string).minusSeconds(-10).isBefore(LocalDateTime.now())) {
            return "宠物已经饿的不行了！";
        } else throw new ApplicationException(CodeType.SERVICE_ERROR, "不需要喂食！");
    }

}
