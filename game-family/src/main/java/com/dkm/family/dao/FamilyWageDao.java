package com.dkm.family.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dkm.family.entity.FamilyWageEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @description: 工资dao
 * @author: zhd
 * @create: 2020-06-03 21:09
 **/
@Repository
public interface FamilyWageDao extends BaseMapper<FamilyWageEntity> {

    @Update("update tb_user_info set user_info_gold = user_info_gold+#{gold}")
    Integer updateUserGold(@Param("gold") int gold);
}
