package com.dkm.backpack.dao;

import com.dkm.IBaseMapper.IBaseMapper;
import com.dkm.backpack.entity.BackPackEntity;
import com.dkm.backpack.entity.vo.FoodInfoVo;
import com.dkm.backpack.entity.vo.GoldStarVo;
import com.dkm.backpack.entity.vo.UserBackpackGoodsVo;
import com.dkm.personalcenter.entity.bo.PsBottleBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @program: game_project
 * @description:
 * @author: zhd
 * @create: 2020-07-17 21:20
 **/
@Mapper
public interface BackpackMapper extends IBaseMapper<BackPackEntity> {

    @Select("SELECT ubg.*,ue.need_grade FROM (" +
            "SELECT ub.backpack_id,ub.number,g.good_content,g.name,g.url,g.good_type FROM (" +
            "SELECT*FROM tb_user_backpack WHERE user_id=#{userId}) ub LEFT JOIN tb_goods g ON ub.good_id=g.id) ubg " +
            "LEFT JOIN tb_user_equipment ue ON ubg.backpack_id=ue.backpack_id WHERE ue.is_equip =0 or ue.is_equip is null")
    List<UserBackpackGoodsVo> getBackpackGoods(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM" +
            " (SELECT backpack_id FROM tb_user_backpack WHERE user_id = #{userId}) ub " +
            " LEFT JOIN tb_user_equipment   ue on ub.backpack_id = ue.backpack_id WHERE ue.is_equip = 0 or ue.is_equip is null")
    Integer getBackpackNumber(@Param("userId") Long userId);

    Integer insertList(@Param("list") List<BackPackEntity> backPackEntities);

    @Select("SELECT backpack_id,ifnull(number,0) as number FROM tb_user_backpack WHERE user_id = #{userId} and good_id = 6")
    GoldStarVo getStars(@Param("userId") Long userId);

    @Select("SELECT g.id as food_id,IFNULL(ub.number,0) as food_number,g.url,g.name FROM " +
            "(SELECT * FROM tb_user_backpack WHERE user_id = #{userId})ub RIGHT JOIN (SELECT * FROM tb_goods WHERE good_type = 3 ) g on g.id = ub.good_id")
    List<FoodInfoVo> getFoods(@Param("userId") Long userId);

    @Select("SELECT g.name,g.url,ub.backpack_id,IFNULL(ub.number,0) as number FROM (" +
            "SELECT name,url,id FROM tb_goods WHERE name LIKE '%体力瓶'" +
            ") g LEFT JOIN " +
            "(SELECT  backpack_id,number,good_id FROM tb_user_backpack WHERE user_id = #{userId}) ub on ub.good_id=g.id")
    List<PsBottleBo> getPsBottle(@Param("userId") Long userId);

    @Update("UPDATE tb_user_info SET user_info_strength = CASE" +
            "    WHEN user_info_strength+#{add}>=user_info_all_strength THEN user_info_all_strength" +
            "    ELSE user_info_strength + #{add}" +
            "END WHERE user_id = #{userId}")
    int updateUserStrength(@Param("add")Integer add,@Param("userId")Long userId);
}
