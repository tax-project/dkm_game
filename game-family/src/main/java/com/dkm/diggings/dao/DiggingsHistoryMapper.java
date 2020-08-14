package com.dkm.diggings.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dkm.diggings.bean.entity.DiggingsHistoryEntity;
import com.dkm.diggings.bean.vo.FamilyExperienceVo;
import com.dkm.diggings.bean.vo.PersonalVo;
import com.dkm.utils.CollectionUtils;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OpenE
 */
public interface DiggingsHistoryMapper extends BaseMapper<DiggingsHistoryEntity> {


    /**
     * 查询最后一条数据
     */
    DiggingsHistoryEntity selectLastOneByUserIdAndFamilyId(@Param("userId") Long userId, @Param("familyId") Long familyId);

    List<DiggingsHistoryEntity> selectAccept(@Param("list") List<CollectionUtils.Pair<Long, Long>> list, @Param("familyId") Long familyId, @Param("date") LocalDateTime date);


    List<PersonalVo> selectAllTheIntegral(@Param("familyId") Long familyId, @Param("afterDate") LocalDateTime afterDate);

    FamilyExperienceVo selectDiggingsExperienceSort(@Param("family") Long family);

    Integer selectRanking(Long familyId);
}
