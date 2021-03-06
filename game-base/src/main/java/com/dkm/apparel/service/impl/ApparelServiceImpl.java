package com.dkm.apparel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dkm.apparel.dao.ApparelMapper;
import com.dkm.apparel.dao.ApparelUserMapper;
import com.dkm.apparel.entity.ApparelEntity;
import com.dkm.apparel.entity.ApparelUserEntity;
import com.dkm.apparel.entity.dto.ApparelDto;
import com.dkm.apparel.service.ApparelService;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.utils.DateUtils;
import com.dkm.utils.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: game_project
 * @description: 服饰
 * @author: zhd
 * @create: 2020-05-19 19:22
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ApparelServiceImpl implements ApparelService {
    @Resource
    private ApparelMapper apparelMapper;
    @Resource
    private ApparelUserMapper apparelUserMapper;
    @Resource
    private IdGenerator idGenerator;

    @Override
    public List<ApparelEntity> getAllApparels(Integer type,Long userId) {
        List<Long> collect = apparelUserMapper.selectList(new QueryWrapper<ApparelUserEntity>().lambda().eq(ApparelUserEntity::getUserId, userId)).stream().mapToLong(ApparelUserEntity::getApparelDetailId).boxed().collect(Collectors.toList());
        if(type!=null){
           return apparelMapper.selectList(new QueryWrapper<ApparelEntity>().lambda().eq(ApparelEntity::getApparelType,type).notIn(ApparelEntity::getApparelId,collect));
        }
        return apparelMapper.selectList(new QueryWrapper<ApparelEntity>().lambda().notIn(ApparelEntity::getApparelId,collect));
    }

    @Override
    public List<ApparelEntity> getUserApparel(Long userId,Integer type) {
        return apparelMapper.getUserApparel(userId,type);
    }

    @Override
    public void doApparel(Long apparelId,Long userId) {
        Integer count = apparelUserMapper.selectCount(new LambdaQueryWrapper<ApparelUserEntity>().eq(ApparelUserEntity::getUserId,userId).gt(ApparelUserEntity::getApparelCompleteTime, LocalDateTime.now()));
        if(count!=0){
            throw new ApplicationException(CodeType.SERVICE_ERROR,"当前服饰还未制作完成！");
        }
        ApparelUserEntity apparelUserEntity = new ApparelUserEntity();
        apparelUserEntity.setApparelCompleteTime(LocalDateTime.now().minusHours(-12));
        apparelUserEntity.setUserId(userId);
        apparelUserEntity.setApparelDetailId(apparelId);
        apparelUserEntity.setApparelUserId(idGenerator.getNumberId());
        if(apparelUserMapper.insert(apparelUserEntity)<1|| apparelUserMapper.updateUser(userId,2000,0)<1){
            throw new ApplicationException(CodeType.SERVICE_ERROR,"制作失败");
        }
    }

    @Override
    public ApparelDto getDoing(Long userId) {
        ApparelUserEntity apparelUserEntity = apparelUserMapper.selectOne(new QueryWrapper<ApparelUserEntity>().lambda().eq(ApparelUserEntity::getUserId, userId).gt(ApparelUserEntity::getApparelCompleteTime, LocalDateTime.now()));
        if(apparelUserEntity==null){
            return null;
        }
        ApparelDto doing = apparelUserMapper.getDoing(apparelUserEntity.getApparelUserId());
        int l = (int) (apparelUserEntity.getApparelCompleteTime().toEpochSecond(ZoneOffset.of("+8")) - (System.currentTimeMillis() / 1000));
        int l1 = l / 60 ;
        doing.setDiamond(Math.max(l1,1));
        doing.setApparelCompleteTime(DateUtils.formatDateTime(apparelUserEntity.getApparelCompleteTime()).replace("-","/"));
        return doing;
    }

    @Override
    public void nowDoing(Long userId, Long apparelId,Integer diamond) {
        ApparelUserEntity apparelUserEntity = apparelUserMapper.selectOne(new QueryWrapper<ApparelUserEntity>().lambda().eq(ApparelUserEntity::getUserId, userId).eq(ApparelUserEntity::getApparelDetailId, apparelId));
        apparelUserEntity.setApparelCompleteTime(LocalDateTime.now());
        if(apparelUserMapper.updateUser(userId,0, diamond)<1|| apparelUserMapper.updateById(apparelUserEntity)<1){
            throw  new ApplicationException(CodeType.SERVICE_ERROR);
        }
    }

    @Override
    public void equipApparel(Long userId,Long apparelId,Integer type){
        ApparelUserEntity apparelUserEntity = apparelUserMapper.selectOne(new QueryWrapper<ApparelUserEntity>().lambda().eq(ApparelUserEntity::getUserId, userId).eq(ApparelUserEntity::getApparelDetailId, apparelId));
        ApparelUserEntity apparelUserEntity1 = apparelUserMapper.selectOne(new QueryWrapper<ApparelUserEntity>().lambda().eq(ApparelUserEntity::getUserId, userId).eq(ApparelUserEntity::getIsEquip, 1));
        apparelUserEntity.setIsEquip(type);
        apparelUserMapper.updateById(apparelUserEntity);
        if(apparelUserEntity1!=null){
            apparelUserEntity1.setIsEquip(0);
            apparelUserMapper.updateById(apparelUserEntity1);
        }
    }

    @Override
    public List<ApparelDto> getEquip(Long userId) {
        return apparelUserMapper.getEquip(userId);
    }

    @Override
    public void sellApparel(Long apparelUserId,Long userId) {
        ApparelUserEntity apparelUserEntity = apparelUserMapper.selectOne(new QueryWrapper<ApparelUserEntity>().lambda().eq(ApparelUserEntity::getApparelUserId, apparelUserId));
        if(apparelUserEntity.getIsEquip()==1){
            throw new ApplicationException(CodeType.SERVICE_ERROR,"服饰正在穿戴中");
        }
        apparelUserMapper.updateUser(userId,-12500,0);
        apparelUserMapper.deleteById(apparelUserId);
    }
}
