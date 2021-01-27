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

 Date: 27/01/2021 16:33:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for delayed_job
-- ----------------------------
DROP TABLE IF EXISTS `delayed_job`;
CREATE TABLE `delayed_job` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `status` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '状态',
  `group` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '组',
  `code` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '编码',
  `job_class` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'job 的 class ',
  `date` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '触发时间',
  `name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '名称',
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  `param` text COLLATE utf8_bin COMMENT '参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
