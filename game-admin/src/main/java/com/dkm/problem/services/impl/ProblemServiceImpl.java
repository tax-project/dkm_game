package com.dkm.problem.services.impl;

import com.dkm.problem.entities.bo.ProblemEntity;
import com.dkm.problem.entities.vo.ProblemVo;
import com.dkm.problem.mappers.ProblemMapper;
import com.dkm.problem.services.IProblemService;
import com.dkm.utils.IdGenerator;
import com.dkm.utils.bean.ResultVo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class ProblemServiceImpl implements IProblemService {
    @Resource
    private ProblemMapper problemMapper;
    @Resource
    private IdGenerator idGenerator;

    @Override
    @NotNull
    public List<ProblemVo> getAllProblems() {
        val result = new ArrayList<ProblemVo>();
        List<ProblemEntity> var14 = problemMapper.selectAll();
        for (ProblemEntity it : var14) {
            result.add(new ProblemVo(String.valueOf(it.getId()), it.getProblemSubject(), it.getProblemAnswerA(), it.getProblemAnswerB(), it.getProblemAnswerC(), it.getProblemAnswerD(), it.getProblemAnswer()));
        }

        return result;
    }

    @Override
    @NotNull
    public ResultVo insert(@NotNull ProblemVo problemVo) {
        long newId = idGenerator.getNumberId();
        problemMapper.insert(new ProblemEntity(newId, problemVo.getProblemSubject(), problemVo.getProblemAnswerA(), problemVo.getProblemAnswerB(), problemVo.getProblemAnswerC(), problemVo.getProblemAnswerD(), problemVo.getProblemAnswer()));
        return new ResultVo(true, newId);
    }

    @Override
    @NotNull
    public ResultVo update(long id, @NotNull ProblemVo problemVo) {
        ProblemEntity entity = new ProblemEntity(id, problemVo.getProblemSubject(), problemVo.getProblemAnswerA(), problemVo.getProblemAnswerB(), problemVo.getProblemAnswerC(), problemVo.getProblemAnswerD(), problemVo.getProblemAnswer());
        int res = problemMapper.updateById(entity);
        return res == 0 ? new ResultVo(false, "数据行不存在") : new ResultVo(true, entity.getId());
    }

    @Override
    @NotNull
    public ResultVo delete(long id) {

        int result = problemMapper.deleteById(id);
        return result == 0 ? new ResultVo(false, "数据行不存在") : new ResultVo(true, id);
    }
}
