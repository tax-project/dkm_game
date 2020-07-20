package com.dkm.backpack.dao;

import com.dkm.IBaseMapper.IBaseMapper;
import com.dkm.backpack.entity.EquipmentEntity;
import com.dkm.backpack.entity.vo.EquipmentVo;
import com.dkm.backpack.entity.vo.UserEquipmentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description: 装备详情
 * @author: zhd
 * @create: 2020-07-17 20:56
 **/
@Mapper
public interface EquipmentMapper extends IBaseMapper<EquipmentEntity> {

     Integer insertList(@Param("list")List<EquipmentEntity> list);

     @Select("SELECT ue.*,g.name,g.url FROM (SELECT good_id FROM tb_user_backpack WHERE backpack_id = #{backpackId}) ub " +
             "LEFT JOIN tb_goods g on g.id=ub.good_id LEFT JOIN tb_user_equipment ue on ue.backpack_id=#{backpackId}")
     EquipmentVo getEquipmentInfo(@Param("backpackId")Long backpackId);

     @Select("SELECT ub.*,g.name,g.url,ue.grade FROM (SELECT good_id,backpack_id FROM tb_user_backpack WHERE user_id = #{userId}) ub " +
             "LEFT JOIN tb_user_equipment ue ON ue.backpack_id=ub.backpack_id LEFT JOIN tb_goods g on ub.good_id=g.id WHERE ue.is_equip = 1 ")
     List<UserEquipmentVo> getUserEquipment(@Param("userId")Long userId);
}
