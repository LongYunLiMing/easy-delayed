/*
 Navicat Premium Data Transfer

 Source Server         : Docker
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3307
 Source Schema         : delayed

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 22/02/2021 13:50:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for DELAYED_JOB
-- ----------------------------
DROP TABLE IF EXISTS `DELAYED_JOB`;
CREATE TABLE `DELAYED_JOB` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `STATUS` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '状态',
  `GROUP` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '组',
  `CODE` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '编码',
  `JOB_CLASS` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'job 的 class ',
  `DATE` timestamp NOT NULL COMMENT '触发时间',
  `NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '名称',
  `DESCRIPTION` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  `PARAM` text CHARACTER SET utf8 COLLATE utf8_bin COMMENT '参数',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
