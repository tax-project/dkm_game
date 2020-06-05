package com.dkm.family.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.family.dao.FamilyDetailDao;
import com.dkm.family.dao.FamilyWageDao;
import com.dkm.family.entity.FamilyDetailEntity;
import com.dkm.family.entity.FamilyWageEntity;
import com.dkm.family.service.FamilyWageService;
import com.dkm.utils.IdGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: game_project
 * @description: 实现类
 * @author: zhd
 * @create: 2020-06-03 14:36
 **/
@Service
public class FamilyWageServiceImpl implements FamilyWageService {


    @Resource
    private FamilyDetailDao familyDetailDao;

    @Resource
    private FamilyWageDao familyWageDao;

    @Resource
    private IdGenerator idGenerator;

    @Override
    public List<Map<Integer,Integer>> getWageList(Long userId) {
        FamilyDetailEntity familyDetailEntity = familyDetailDao.selectOne(new QueryWrapper<FamilyDetailEntity>().lambda().eq(FamilyDetailEntity::getUserId, userId));
        if(familyDetailEntity==null){
            throw new ApplicationException(CodeType.SERVICE_ERROR,"没有加入家族,无法领取工资");
        }
        //获取今日日期
        LocalDate now = LocalDate.now();
        //获取周几
        int weekDay = now.getDayOfWeek().getValue();
        //查询是否有领取记录
        FamilyWageEntity familyWageEntity = familyWageDao.selectOne(new QueryWrapper<FamilyWageEntity>().lambda().eq(FamilyWageEntity::getUserId, userId));
        //是否过了这一周
        LocalDate localDate = now.minusDays(7 - now.getDayOfWeek().getValue());
        if(familyWageEntity!=null&&familyWageEntity.getFamilyWageTime().isBefore(now)){
            //过了上次记录周时间 ->重新记录本周周日时间并清空记录
            familyWageEntity.setFamilyWageTime(localDate);
            familyWageEntity.setDay1(0);
            familyWageEntity.setDay2(0);
            familyWageEntity.setDay3(0);
            familyWageEntity.setDay4(0);
            familyWageDao.updateById(familyWageEntity);
        }else if(familyWageEntity==null){
            //第一次领取 添加默认记录
            FamilyWageEntity familyWageEntity1 = new FamilyWageEntity();
            familyWageEntity1.setUserId(userId);
            familyWageEntity1.setFamilyWageTime(localDate);
            familyWageEntity1.setFamilyWageId(idGenerator.getNumberId());
            familyWageEntity1.setDay1(0);
            familyWageEntity1.setDay2(0);
            familyWageEntity1.setDay3(0);
            familyWageEntity1.setDay4(0);
            familyWageEntity = familyWageEntity1;
            familyWageDao.insert(familyWageEntity1);
        }

        //根据权限设置工资
        List<Map<Integer,Integer>> wage  = new ArrayList<>();
        Map<Integer,Integer> map = new HashMap<>();
        if(familyDetailEntity.getIsAdmin()==0){
            //成员工资
            map.put(familyWageEntity.getDay1(),50000);
            map.put(weekDay>=2?familyWageEntity.getDay2():2,50000);
            map.put(weekDay>=3?familyWageEntity.getDay3():2,50000);
            map.put(weekDay>=7?familyWageEntity.getDay4():2,600000);
        }else if(familyDetailEntity.getIsAdmin()==1){
            //管理员工资
            map.put(familyWageEntity.getDay1(),150000);
            map.put(weekDay>=2?familyWageEntity.getDay2():2,200000);
            map.put(weekDay>=3?familyWageEntity.getDay3():2,300000);
            map.put(weekDay>=7?familyWageEntity.getDay4():2,2650000);
        }else if(familyDetailEntity.getIsAdmin()==2){
            //族长工资
            map.put(familyWageEntity.getDay1(),200000);
            map.put(weekDay>=2?familyWageEntity.getDay2():2,300000);
            map.put(weekDay>=3?familyWageEntity.getDay3():2,400000);
            map.put(weekDay>=7?familyWageEntity.getDay4():2,4100000);
        }
        wage.add(map);
        return wage;
    }

    @Override
    public void updateUserWage(Integer wage, Long userId) {
        //获取周几
        int weekDay = LocalDate.now().getDayOfWeek().getValue();
        Integer integer = familyDetailDao.updateUserWage(wage, userId);
        FamilyWageEntity familyWageEntity = familyWageDao.selectOne(new QueryWrapper<FamilyWageEntity>().lambda().eq(FamilyWageEntity::getUserId, userId));
        if(weekDay==1){
            familyWageEntity.setDay1(1);
        }else if(weekDay==2){
            familyWageEntity.setDay2(1);
        }else if(weekDay==3){
            familyWageEntity.setDay3(1);
        }else if(weekDay==7){
            familyWageEntity.setDay4(1);
        }
        //更新领取记录
        int i = familyWageDao.updateById(familyWageEntity);
        if(integer<1||i<1){
            throw new ApplicationException(CodeType.SERVICE_ERROR,"领取失败");
        }
    }

}
