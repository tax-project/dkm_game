package com.dkm.backpack.dao;

import com.dkm.IBaseMapper.IBaseMapper;
import com.dkm.backpack.entity.BackPackEntity;
import com.dkm.backpack.entity.vo.UserBackpackGoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: game_project
 * @description:
 * @author: zhd
 * @create: 2020-07-17 21:20
 **/
@Mapper
public interface BackpackMapper extends IBaseMapper<BackPackEntity> {

    @Select("SELECT ubg.*FROM (" +
            "SELECT ub.backpack_id,ub.number,g.good_content,g.name,g.url,g.good_type FROM (" +
            "SELECT*FROM tb_user_backpack WHERE user_id=#{userId}) ub LEFT JOIN tb_goods g ON ub.good_id=g.id) ubg " +
            "LEFT JOIN tb_user_equipment ue ON ubg.backpack_id=ue.backpack_id WHERE ue.is_equip !=1")
    List<UserBackpackGoodsVo> getBackpackGoods(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM" +
            "(SELECT backpack_id FROM tb_user_backpack WHERE user_id = #{userId}) ub " +
            "LEFT JOIN ( SELECT  backpack_id FROM tb_user_equipment WHERE is_equip=0)  ue on ub.backpack_id = ue.backpack_id ")
    Integer getBackpackNumber(@Param("userId") Long userId);

    Integer insertList(@Param("list") List<BackPackEntity> backPackEntities);
}