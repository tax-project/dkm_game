package com.dkm.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.family.dao.FamilyDao;
import com.dkm.family.entity.FamilyEntity;
import com.dkm.mine.bean.FamilyAddition;
import com.dkm.mine.bean.entity.MineBattleEntity;
import com.dkm.mine.bean.entity.MineBattleItemEntity;
import com.dkm.mine.bean.entity.MineBattleLevelEntity;
import com.dkm.mine.bean.vo.*;
import com.dkm.mine.dao.FamilyAdditionMapper;
import com.dkm.mine.dao.MineBattleItemMapper;
import com.dkm.mine.dao.MineBattleLevelMapper;
import com.dkm.mine.dao.MineBattleMapper;
import com.dkm.mine.rule.BattleItemRule;
import com.dkm.mine.service.IMineService;
import com.dkm.utils.IdGenerator;
import com.dkm.utils.ObjectUtils;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author dragon
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MineServiceImpl implements IMineService {
    @Resource
    private IdGenerator idGenerator;
    @Resource
    private MineBattleMapper mineBattleMapper;
    @Resource
    private MineBattleItemMapper mineBattleItemMapper;
    @Resource
    private BattleItemRule battleItemRule;
    @Resource
    private MineBattleLevelMapper mineBattleLevelMapper;
    @Resource
    private FamilyDao familyDao;
    @Resource
    private FamilyAdditionMapper mapper;

    @Override
    public MineVo getAllInfo(Long userId, Long familyId) {
        MineBattleEntity entity = getMineBattleEntity(familyId);
        val result = new MineVo();
        result.setFamilyId(familyId);
        val locationId = entity2Vo(entity, result);
        includeMineItem(entity.getId(), result.getPublicItem(), 0);
        // 导入公开矿区信息
        includeMineItem(entity.getId(), result.getPrivateItem(), locationId);
        // 导入私有矿区信息
        result.setFamilyLevel(getFamilyLevel(familyId));
        return result;
    }


    @Override
    public List<FamilyAddition> getFamilyType() {
        return mapper.selectList(null);
    }

    @Override
    public MineItemDetailVo detail(long battleId) {
        val item = mineBattleItemMapper.selectById(battleId);
        if (ObjectUtils.isNullOrEmpty(item)) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "未找到此矿山.");
        }
        return null;
    }

    @Override
    public OccupyResultVo occupy(long battleId) {
        return null;
    }


    @Override
    public List<BattleItemInfoVo> getItemsLevelType() {
        val entityList = mineBattleLevelMapper.selectList(null);
        val result = new ArrayList<BattleItemInfoVo>(entityList.size());
        for (MineBattleLevelEntity levelEntity : entityList) {
            BattleItemInfoVo battleItemInfoVo = new BattleItemInfoVo();
            battleItemInfoVo.setNpcName(levelEntity.getNpcName());
            battleItemInfoVo.setNpcSkillLevel(levelEntity.getNpcLevel());
            battleItemInfoVo.setGoldYield(levelEntity.getGoldYield());
            battleItemInfoVo.setIntegralYield(levelEntity.getIntegralYield());
            battleItemInfoVo.setLevel(levelEntity.getLevel());
            result.add(battleItemInfoVo);
        }
        return result;
    }

    private void includeMineItem(Long id, List<MineItemVo> publicItem, int i) {
        val itemEntities = mineBattleItemMapper.selectList(new QueryWrapper<MineBattleItemEntity>()
                .lambda().eq(MineBattleItemEntity::getBattleId, id).eq(MineBattleItemEntity::getBelongItem, i));
        for (int j = 0; j < itemEntities.size(); j++) {
            MineItemVo item = new MineItemVo();
            MineBattleItemEntity itemEntity = itemEntities.get(j);
            item.setId(itemEntity.getId());
            item.setIndex(j);
            item.setLevel(itemEntity.getLevel());
            publicItem.add(item);
        }
    }

    private int entity2Vo(MineBattleEntity entity, MineVo result) {
        long[] arr = new long[4];
        int resultId = -1;
        val longs = new ArrayList<Long>();
        arr[0] = entity.getFirstFamilyId();
        arr[1] = entity.getSecondFamilyId();
        arr[2] = entity.getThirdFamilyId();
        arr[3] = entity.getFourthFamilyId();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != result.getFamilyId()) {
                longs.add(arr[i]);
            } else {
                resultId = i + 1;
            }
        }
        if (longs.size() > 3) {
            throw new IndexOutOfBoundsException("数据异常");
        }
        result.setTopLeftFamilyId(longs.get(0));
        result.setTopRightFamilyId(longs.get(1));
        result.setBottomRightFamilyId(longs.get(2));
        ;
        result.setTopLeftFamilyName(loadFamilyName(longs.get(0)));
        result.setTopRightFamilyName(loadFamilyName(longs.get(1)));
        result.setBottomRightFamilyName(loadFamilyName(longs.get(2)));
        return resultId;
    }


    /**
     * 根据家族ID 来获取家族名称
     *
     * @param familyId
     * @return
     * @deprecated sql查询可优化
     */
    @Deprecated
    private String loadFamilyName(Long familyId) {
        if (familyId == 0) {
            return "无";
        } else {
            return familyDao.selectNameByFamilyId(familyId);
        }
    }

    /**
     * 得到金矿，如果家族不位于任何金矿时将自动添加，
     * 如果金矿不存在则将自动创建一个新的金矿
     */
    private MineBattleEntity getMineBattleEntity(Long familyId) {
        MineBattleEntity entity = mineBattleMapper.selectByFamilyId(familyId);
        if (Objects.isNull(entity)) {
            entity = mineBattleMapper.selectByEmpty();
            if (Objects.isNull(entity)) {
                entity = battleItemRule.createBattle();
            }
            entity.setFirstFamilyId(chooseFamilyExists(entity.getFirstFamilyId()));
            entity.setSecondFamilyId(chooseFamilyExists(entity.getSecondFamilyId()));
            entity.setThirdFamilyId(chooseFamilyExists(entity.getThirdFamilyId()));
            entity.setFourthFamilyId(chooseFamilyExists(entity.getFourthFamilyId()));
            putFamilyToBattle(entity, familyId);
            //战场不存在，需要生成
        }
        return entity;
    }

    /**
     * 判断家族是否存在，不存在返回 0
     */
    private long chooseFamilyExists(long firstFamilyId) {
        FamilyEntity familyEntity = familyDao.selectById(firstFamilyId);
        if (Objects.isNull(familyEntity)) {
            return 0;
        } else {
            return firstFamilyId;
        }
    }

    /**
     * 将一个新家族添加到矿区
     */
    private void putFamilyToBattle(MineBattleEntity entity, Long familyId) {
        if (entity.getFirstFamilyId() == 0) {
            entity.setFirstFamilyId(familyId);
        } else if (entity.getSecondFamilyId() == 0) {
            entity.setSecondFamilyId(familyId);
        } else if (entity.getThirdFamilyId() == 0) {
            entity.setThirdFamilyId(familyId);
        } else if (entity.getFourthFamilyId() == 0) {
            entity.setFourthFamilyId(familyId);
        }
        mineBattleMapper.updateById(entity);
    }


    public Integer getFamilyLevel(Long familyId) {
        val familyEntity = familyDao.selectById(familyId);
        return familyEntity.getFamilyGrade();
    }
}
