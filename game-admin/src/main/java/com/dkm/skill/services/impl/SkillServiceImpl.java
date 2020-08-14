package com.dkm.skill.services.impl;

import com.dkm.skill.entities.bo.SkillEntity;
import com.dkm.skill.entities.vo.SkillVo;
import com.dkm.skill.mappers.SkillMapper;
import com.dkm.skill.services.ISkillService;
import com.dkm.utils.IdGenerator;
import com.dkm.utils.bean.ResultVo;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)

public class SkillServiceImpl implements ISkillService {
    @Resource
    private SkillMapper skillMapper;
    @Resource
    private IdGenerator idGenerator;

    @NotNull
    public List<SkillVo> getAll() {
        val var5 = skillMapper.selectAll();
        val result = new ArrayList<SkillVo>();
        for (SkillEntity entity : var5) {
            result.add(new SkillVo(String.valueOf(entity.getId()), entity.getSkName(), entity.getSkEffectOne(), entity.getSkEffectTwo()));
        }
        return result;
    }

    @NotNull
    public ResultVo insert(@NotNull SkillVo skillVo) {
        long newId = idGenerator.getNumberId();
        skillMapper.insert(new SkillEntity(newId, skillVo.getSkinName(), skillVo.getFirstSkinEffect(), skillVo.getSecondSkinEffect()));
        return new ResultVo(true, newId);
    }

    @NotNull
    public ResultVo update(long id, @NotNull SkillVo skillVo) {
        SkillEntity entity = new SkillEntity(id, skillVo.getSkinName(), skillVo.getFirstSkinEffect(), skillVo.getSecondSkinEffect());
        int res = skillMapper.updateById(entity);
        return res == 0 ? new ResultVo(false, "数据行不存在") : new ResultVo(true, entity.getId());
    }

    @NotNull
    public ResultVo delete(long id) {
        int result = skillMapper.deleteById(id);
        return result == 0 ? new ResultVo(false, "数据行不存在") : new ResultVo(true, id);
    }
}
