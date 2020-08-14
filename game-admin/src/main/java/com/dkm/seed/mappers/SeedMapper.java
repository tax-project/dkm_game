package com.dkm.seed.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.dkm.seed.entities.bo.SeedEntity;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository

public interface SeedMapper extends BaseMapper<SeedEntity> {
   @Select({"select * from tb_seed"})
   List<SeedEntity> selectAll();
}
