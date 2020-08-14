DROP TABLE IF EXISTS tb_mall_lucky_gift;
DROP TABLE IF EXISTS tb_mall_reward_goods;
DROP TABLE IF EXISTS tb_mall_reward_history;
DROP TABLE IF EXISTS tb_mall_reward;
DROP TABLE IF EXISTS tb_mall_reward_info;



CREATE TABLE IF NOT EXISTS `tb_mall_reward_info`
(
    `id`   INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50)     NOT NULL COMMENT '名称'
)
    COLLATE = 'utf8_bin'
    ENGINE = InnoDB
    COMMENT '累计充值或者消耗的类型'
;

CREATE TABLE IF NOT EXISTS tb_mall_reward
(
    id            INT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    `type`        INT NOT NULL COMMENT '表代表的状态',
    `constraints` INT NOT NULL COMMENT '条件',
    FOREIGN KEY (type) REFERENCES tb_mall_reward_info (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
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


CREATE TABLE IF NOT EXISTS tb_mall_lucky_gift
(
    id      INT        NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    gift_id BIGINT(20) NOT NULL COMMENT '物品ID',
    FOREIGN KEY (gift_id) REFERENCES tb_gift (gi_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE

) COMMENT '幸运礼物的基础表'
    COLLATE = 'utf8_bin'
    ENGINE = InnoDB;
