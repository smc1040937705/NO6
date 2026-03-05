/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : cl515882190

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 2025-03-05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tongzhijilu
-- ----------------------------
DROP TABLE IF EXISTS `tongzhijilu`;
CREATE TABLE `tongzhijilu`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tongzhibianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知编号',
  `yuyuebianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预约编号',
  `yishengzhanghao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '医生账号',
  `zhanghao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户账号',
  `tongzhileixing` int(11) NULL DEFAULT NULL COMMENT '通知类型（1-预约成功通知 2-就诊前提醒 3-就诊当天提醒）',
  `tongzhineirong` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '通知内容',
  `jihuafasongshijian` datetime(0) NULL DEFAULT NULL COMMENT '计划发送时间',
  `shijifasongshijian` datetime(0) NULL DEFAULT NULL COMMENT '实际发送时间',
  `fasongzhuangtai` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '发送状态（0-待发送 1-发送成功 2-发送失败）',
  `shibaiyuanyin` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '失败原因',
  `chongshicishu` int(11) NULL DEFAULT 0 COMMENT '重试次数',
  `jieshouzhuangtai` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '用户接收状态（0-未接收 1-已接收 2-已读）',
  `jieshoushijian` datetime(0) NULL DEFAULT NULL COMMENT '接收时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `tongzhibianhao`(`tongzhibianhao`) USING BTREE,
  INDEX `yuyuebianhao`(`yuyuebianhao`) USING BTREE,
  INDEX `zhanghao`(`zhanghao`) USING BTREE,
  INDEX `fasongzhuangtai`(`fasongzhuangtai`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知记录' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
