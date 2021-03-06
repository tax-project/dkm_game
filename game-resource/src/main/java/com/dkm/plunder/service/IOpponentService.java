package com.dkm.plunder.service;

import com.dkm.plunder.entity.Opponent;

import java.util.Map;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: game_project
 * @DESCRIPTION:
 * @DATE: 2020/6/22 14:49
 */
public interface IOpponentService {

    /**
     * 查询对手信息
     * @return
     */
    Map<String,Object> queryOpponent();

    /**
     * 添加对手信息
     */
    int addOpponent(Opponent opponent);

    /**
     *  根据自己的用户id查询数据
     * @return 返回对手信息
     */
    Map<String, Object> getOpponentInfo ();
}
