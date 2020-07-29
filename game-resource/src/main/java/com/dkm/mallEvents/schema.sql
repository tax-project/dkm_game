CREATE TABLE IF NOT EXISTS `tb_mall_single`
(
    id  INT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    day INT NOT NULL DEFAULT 0 COMMENT '第几天'

)
    COMMENT '单笔充值满30'
    COLLATE = 'utf8_bin'
    ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `tb_mall_single_item`
(
    id                INT        NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    goods_id          BIGINT(20) NOT NULL COMMENT '物品ID',
    `size`            INT(11)    NOT NULL DEFAULT '0' COMMENT '物品数目',
    tb_mall_single_id INT        NOT NULL,
    FOREIGN KEY (tb_mall_single_id) REFERENCES tb_mall_single (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (goods_id) REFERENCES tb_goods (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
    COMMENT '单笔充值满30奖励的物品'
    COLLATE = 'utf8_bin'
    ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `tb_mall_single_user`
(
    id                INT        NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    user_id           BIGINT(20) NOT NULL COMMENT 'user id',
    tb_mall_single_id INT        NOT NULL,
    FOREIGN KEY (tb_mall_single_id) REFERENCES tb_mall_single (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
    COMMENT '充值奖励领取'
    COLLATE = 'utf8_bin'
    ENGINE = InnoDB;


# ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS tb_mall_reward
(
    id            INT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    `type`        INT NOT NULL COMMENT '表代表的状态',
    `constraints` INT NOT NULL COMMENT '条件'
) COMMENT '累计充值或者消耗的表'
    COLLATE = 'utf8_bin'
    ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tb_mall_reward_goods
(
    id             INT        NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    goods_id       BIGINT(20) NOT NULL COMMENT '物品ID',
    `size`         INT(11)    NOT NULL DEFAULT '0' COMMENT '物品数目',
    mall_reward_id INT        NOT NULL COMMENT '累计充值或者消耗表 ID',
    FOREIGN KEY (mall_reward_id) REFERENCES tb_mall_reward (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (goods_id) REFERENCES tb_goods (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE

) COMMENT '充值或者消耗的奖励'
    COLLATE = 'utf8_bin'
    ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `tb_mall_reward_history`
(
    id             INT        NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    user_id        BIGINT(20) NOT NULL COMMENT 'user id',
    mall_reward_id INT        NOT NULL COMMENT '累计充值或者消耗表 ID',
    FOREIGN KEY (mall_reward_id) REFERENCES tb_mall_reward (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user (user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
    COMMENT '充值或者消耗领取的用户历史记录'
    COLLATE = 'utf8_bin'
    ENGINE = InnoDB;
