// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

suite("test_agg_schema_key_change_modify1","p0") {
     def tbName1 = "test_agg_schema_key_change_modify1"
     def tbName2 = "test_agg_schema_key_change_modify_1"
     /**
      *  Test the agg model by modify a value type
      */

     sql """ DROP TABLE IF EXISTS ${tbName1} """
     def getTableStatusSql = " SHOW ALTER TABLE COLUMN WHERE IndexName='${tbName1}' ORDER BY createtime DESC LIMIT 1  "
     def initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `is_teacher` BOOLEAN COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `is_teacher`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     def initTableData = "insert into ${tbName1} values(123456789, 'Alice', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-07-07 22:00:00');"
     def initTable1 = ""
     def initTableData1 = ""
     def insertSql = ""

     /**
      *  Test the agg model by modify a key type from LARGEINT to other type
      */
     sql """ DROP TABLE IF EXISTS ${tbName1} """
     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `sn_number` LARGEINT COMMENT \"sn卡\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500)  REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`,`sn_number`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 2147483641, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 214748364, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 2147483441, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 2147483141, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 2127483141, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 2124483141, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 2123483141, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from LARGEINT  to BOOLEAN
     def errorMessage = "errCode = 2, detailMessage = Can not change LARGEINT to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number BOOLEAN  key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', false, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     // TODO Test the agg model by modify a key type from LARGEINT  to TINYINT
     errorMessage = "errCode = 2, detailMessage = Can not change LARGEINT to TINYINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number TINYINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 2, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from LARGEINT  to SMALLINT
     errorMessage = "errCode = 2, detailMessage = Can not change LARGEINT to SMALLINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number SMALLINT key  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 3, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from LARGEINT  to INT
     errorMessage = "errCode = 2, detailMessage = Can not change LARGEINT to INT"
     expectException({
          sql initTable
          sql initTableData

          sql """ alter  table ${tbName1} MODIFY  column sn_number INT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 4, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from LARGEINT  to BIGINT
     errorMessage = "errCode = 2, detailMessage = Can not change LARGEINT to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number BIGINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 5, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from LARGEINT  to FLOAT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number FLOAT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.2, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from LARGEINT  to DOUBLE
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number DOUBLE key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.23, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //TODO Test the agg model by modify a key type from LARGEINT  to DECIMAL
     errorMessage = "errCode = 2, detailMessage = Can not change LARGEINT to DECIMAL128"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number DECIMAL(38,0) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.23, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")

     }, errorMessage)

     //TODO Test the agg model by modify a  key type from LARGEINT  to CHAR
     errorMessage = "errCode = 2, detailMessage = Can not change LARGEINT to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number CHAR(15) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //TODO Data doubling Test the agg model by modify a key type from LARGEINT  to VARCHAR
     sql initTable
     sql initTableData
     sql """ alter  table ${tbName1} MODIFY  column sn_number VARCHAR(100) key """
     insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'vasd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
     waitForSchemaChangeDone({
          sql getTableStatusSql
          time 600
     }, insertSql, false, "${tbName1}")

     sql """ DROP TABLE IF EXISTS ${tbName2} """
     def initTable2 = " CREATE TABLE IF NOT EXISTS ${tbName2}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `sn_number` VARCHAR(100) COMMENT \"sn卡\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`,`sn_number`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     def initTableData2 = "insert into ${tbName2} values(123456789, 'Alice', 2147483641, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 214748364, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 2147483441, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 2147483141, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 2127483141, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 2124483141, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-06-06 20:00:00')," +
             "               (923456689, 'Alice', 'vasd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (789012345, 'Grace', 2123483141, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-07-07 22:00:00');"

     sql initTable2
     sql initTableData2
//     checkTableData("${tbName1}","${tbName2}","sn_number")
     sql """ DROP TABLE IF EXISTS ${tbName1} """


     //TODO Test the agg model by modify a  key type from LARGEINT  to VARCHAR
     //Test the agg model by modify a key type from LARGEINT  to VARCHAR
     errorMessage = "errCode = 2, detailMessage = Can not change from wider type largeint to narrower type varchar(2)"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number VARCHAR(2) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //TODO Test the agg model by modify a  key type from LARGEINT  to STRING
     //Test the agg model by modify a key type from LARGEINT  to STRING
     errorMessage = "errCode = 2, detailMessage = String Type should not be used in key column[sn_number]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column sn_number STRING key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     /**
      *  Test the agg model by modify a key type from FLOAT to other type
      */
     sql """ DROP TABLE IF EXISTS ${tbName1} """
     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `score` FLOAT COMMENT \"分数\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`,`score`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 1.8, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 1.89, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 2.6, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 3.9, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 4.2, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 2.5, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 2.1, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from FLOAT  to BOOLEAN
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score BOOLEAN  key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', false, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     // TODO Test the agg model by modify a key type from FLOAT  to TINYINT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score TINYINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 2, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from FLOAT  to SMALLINT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score SMALLINT key  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 3, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from FLOAT  to INT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData

          sql """ alter  table ${tbName1} MODIFY  column score INT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 4, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from FLOAT  to BIGINT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score BIGINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 545645, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from  FLOAT to LARGEINT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score FLOAT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 156546, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from FLOAT  to DOUBLE
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score DOUBLE key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.23, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //TODO Test the agg model by modify a key type from FLOAT  to DECIMAL
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score DECIMAL(38,0) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.23, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")

     }, errorMessage)

     //TODO Test the agg model by modify a  key type from FLOAT  to CHAR
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score CHAR(15) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //TODO Test the agg model by modify a  key type from FLOAT  to VARCHAR
     //Test the agg model by modify a key type from FLOAT  to VARCHAR
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score VARCHAR(100) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //TODO Test the agg model by modify a  key type from FLOAT  to STRING
     //Test the agg model by modify a key type from FLOAT  to STRING
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score STRING key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     /**
      *  Test the agg model by modify a key type from DOUBLE to other type
      */
     sql """ DROP TABLE IF EXISTS ${tbName1} """
     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `score` DOUBLE COMMENT \"分数\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`,`score`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 1.8, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 1.89, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 2.6, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 3.9, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 4.2, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 2.5, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 2.1, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-07-07 22:00:00');"

     //Test the agg model by modify a key type from DOUBLE  to BOOLEAN
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score BOOLEAN  key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', false, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     // Test the agg model by modify a key type from DOUBLE  to TINYINT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score TINYINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 2, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from DOUBLE  to SMALLINT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score SMALLINT key  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 3, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from DOUBLE  to INT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData

          sql """ alter  table ${tbName1} MODIFY  column score INT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 4, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from DOUBLE  to BIGINT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score BIGINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 545645, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from  DOUBLE to LARGEINT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score FLOAT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 156546, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from DOUBLE to  FLOAT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score FLOAT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.23, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     // Test the agg model by modify a key type from DOUBLE  to DECIMAL
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score DECIMAL(38,0) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.23, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")

     }, errorMessage)

     //TODO Test the agg model by modify a  key type from DOUBLE  to CHAR
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score CHAR(15) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //TODO Test the agg model by modify a  key type from DOUBLE  to VARCHAR
     //Test the agg model by modify a key type from FLOAT  to VARCHAR
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score VARCHAR(100) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from DOUBLE  to STRING
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column score STRING key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     /**
      *  Test the agg model by modify a key type from DECIMAL to other type
      */
     sql """ DROP TABLE IF EXISTS ${tbName1} """
     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `rice` DECIMAL(38,10) COMMENT \"米粒\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`,`rice`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 1.8, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 1.89, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 2.6, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 3.9, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 4.2, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 2.5, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 2.1, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-07-07 22:00:00');"

     //Test the agg model by modify a key type from DECIMAL  to BOOLEAN
     errorMessage = "errCode = 2, detailMessage = Can not change DECIMAL128 to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice BOOLEAN  key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', false, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     // Test the agg model by modify a key type from DECIMAL  to TINYINT
     errorMessage = "errCode = 2, detailMessage = Can not change DECIMAL128 to TINYINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice TINYINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 2, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from DECIMAL  to SMALLINT
     errorMessage = "errCode = 2, detailMessage = Can not change DECIMAL128 to SMALLINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice SMALLINT key  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 3, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from DECIMAL  to INT
     errorMessage = "errCode = 2, detailMessage = Can not change DECIMAL128 to INT"
     expectException({
          sql initTable
          sql initTableData

          sql """ alter  table ${tbName1} MODIFY  column rice INT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 4, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from DECIMAL  to BIGINT
     errorMessage = "errCode = 2, detailMessage = Can not change DECIMAL128 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice BIGINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 545645, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //Test the agg model by modify a key type from  DECIMAL to LARGEINT
     errorMessage = "errCode = 2, detailMessage = Can not change DECIMAL128 to LARGEINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice LARGEINT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 156546, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     //Test the agg model by modify a key type from DECIMAL to  FLOAT
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice FLOAT key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.23, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     // Test the agg model by modify a key type from DECIMAL  to DOUBLE
     errorMessage = "errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice DOUBLE key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.23, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")

     }, errorMessage)

     //Test the agg model by modify a  key type from DECIMAL  to CHAR
     errorMessage = "errCode = 2, detailMessage = Can not change DECIMAL128 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice CHAR(15) key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)

     //TODO Data doubling Test the agg model by modify a key type from DECIMAL  to VARCHAR
     sql initTable
     sql initTableData
     sql """ alter  table ${tbName1} MODIFY  column rice VARCHAR(100) key """
     insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
     waitForSchemaChangeDone({
          sql getTableStatusSql
          time 600
     }, insertSql, false, "${tbName1}")

     sql """ DROP TABLE IF EXISTS ${tbName2} """
     initTable2 = " CREATE TABLE IF NOT EXISTS ${tbName2}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `rice` VARCHAR(100) COMMENT \"米粒\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`,`rice`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData2 = "insert into ${tbName2} values(123456789, 'Alice', '1.8000000000', 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', '1.8900000000', 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', '2.6000000000', 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', '3.9000000000', 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', '4.2000000000', 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', '2.5000000000', 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-06-06 20:00:00')," +
             "               (923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00')," +
             "               (789012345, 'Grace', '2.1000000000', 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-07-07 22:00:00');"

     sql initTable2
     sql initTableData2
//     checkTableData("${tbName1}","${tbName2}","rice")
     sql """ DROP TABLE IF EXISTS ${tbName1} """

     //Test the agg model by modify a key type from DECIMAL  to STRING
     errorMessage = "errCode = 2, detailMessage = String Type should not be used in key column[rice]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column rice STRING key """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 'asd', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true, "${tbName1}")
     }, errorMessage)


     /**
      *  Test the agg model by modify a key type from DATE to other type
      */

     sql """ DROP TABLE IF EXISTS ${tbName1} """
     getTableStatusSql = " SHOW ALTER TABLE COLUMN WHERE IndexName='${tbName1}' ORDER BY createtime DESC LIMIT 1  "
     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `login_time` DATE COMMENT \"用户登陆时间\",\n" +
             "              `is_teacher` BOOLEAN REPLACE_IF_NOT_NULL COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `login_time`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', '2022-01-01', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing',  '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', '2022-01-01 12:00:00', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai',  '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', '2022-01-01 12:00:00', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', '2022-01-01 12:00:00', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen',  '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', '2022-01-01 12:00:00', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu',  '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', '2022-01-01 12:00:00', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou',  '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', '2022-01-01 12:00:00', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian',  '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from DATE to BOOLEAN
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BOOLEAN KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 0, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATE to TINYINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to TINYINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time TINYINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATE to SMALLINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to SMALLINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time SMALLINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to INT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to INT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time INT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATE to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATE to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time CHAR KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from DATE to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[login_time]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to VARCHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     /**
      *  Test the agg model by modify a key type from DATETIME to other type
      */


     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `login_time` DATETIME COMMENT \"用户登陆时间\",\n" +
             "              `is_teacher` BOOLEAN REPLACE_IF_NOT_NULL COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `login_time`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-01-01', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou','2022-01-01', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-01-01', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-01-01', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-01-01', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-01-01', '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from DATETIME to BOOLEAN
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BOOLEAN KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 0, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIME to TINYINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to TINYINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time TINYINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIME to SMALLINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to SMALLINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time SMALLINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIME to INT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to INT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time INT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATETIME to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIME to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATETIME to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIME to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time CHAR KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIME to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[login_time]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIME to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to VARCHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     /**
      *  Test the agg model by modify a key type from CHAR to other type
      */


     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` CHAR(255) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `login_time` DATETIME COMMENT \"用户登陆时间\",\n" +
             "              `is_teacher` BOOLEAN REPLACE_IF_NOT_NULL COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `login_time`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-01-01', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou','2022-01-01', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-01-01', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-01-01', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-01-01', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-01-01', '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from CHAR to BOOLEAN
     errorMessage="errCode = 2, detailMessage = Can not change VARCHAR to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username BOOLEAN KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 0, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from CHAR to TINYINT
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username TINYINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from CHAR to SMALLINT
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username SMALLINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from CHAR to INT
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username INT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from CHAR to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from CHAR to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from CHAR to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change VARCHAR to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from CHAR to  DATETIME
     errorMessage="errCode = 2, detailMessage = date literal [0] is invalid: null"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username DATETIME KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from CHAR to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[username]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from CHAR to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     /**
      *  Test the agg model by modify a key type from varchar to other type
      */


     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR NOT NULL COMMENT \"用户昵称\",\n" +
             "              `login_time` DATETIME COMMENT \"用户登陆时间\",\n" +
             "              `is_teacher` BOOLEAN REPLACE_IF_NOT_NULL COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `login_time`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-01-01', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou','2022-01-01', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-01-01', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-01-01', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-01-01', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-01-01', '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from VARCHAR to BOOLEAN
     errorMessage="errCode = 2, detailMessage = Can not change VARCHAR to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username BOOLEAN KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 0, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from VARCHAR to TINYINT
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username TINYINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from VARCHAR to SMALLINT
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username SMALLINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from VARCHAR to INT
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username INT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from VARCHAR to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change default value"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from VARCHAR to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from VARCHAR to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change VARCHAR to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from VARCHAR to  DATETIME
     errorMessage="errCode = 2, detailMessage = date literal [0] is invalid: null"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username DATETIME KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from VARCHAR to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[username]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from VARCHAR to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change VARCHAR to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column username CHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)




     /**
      *  Test the agg model by modify a key type from DATE to other type
      */

     sql """ DROP TABLE IF EXISTS ${tbName1} """
     getTableStatusSql = " SHOW ALTER TABLE COLUMN WHERE IndexName='${tbName1}' ORDER BY createtime DESC LIMIT 1  "
     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `login_time` DATE COMMENT \"用户登陆时间\",\n" +
             "              `is_teacher` BOOLEAN REPLACE_IF_NOT_NULL COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `login_time`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', '2022-01-01', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing',  '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', '2022-01-01 12:00:00', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai',  '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', '2022-01-01 12:00:00', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', '2022-01-01 12:00:00', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen',  '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', '2022-01-01 12:00:00', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu',  '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', '2022-01-01 12:00:00', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou',  '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', '2022-01-01 12:00:00', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian',  '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from DATE to BOOLEAN
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BOOLEAN KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 0, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATE to TINYINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to TINYINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time TINYINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATE to SMALLINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to SMALLINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time SMALLINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to INT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to INT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time INT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)




     //TODO Test the agg model by modify a key type from DATE to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATE to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time CHAR KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from DATE to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[login_time]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to VARCHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATE to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time CHAR KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from DATE to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[login_time]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATE to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to VARCHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     /**
      *  Test the agg model by modify a key type from DATEV2 to other type
      */

     sql """ DROP TABLE IF EXISTS ${tbName1} """
     getTableStatusSql = " SHOW ALTER TABLE COLUMN WHERE IndexName='${tbName1}' ORDER BY createtime DESC LIMIT 1  "
     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `login_time` DATEV2 COMMENT \"用户登陆时间\",\n" +
             "              `is_teacher` BOOLEAN REPLACE_IF_NOT_NULL COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `login_time`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', '2022-01-01', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing',  '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', '2022-01-01 12:00:00', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai',  '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', '2022-01-01 12:00:00', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', '2022-01-01 12:00:00', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen',  '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', '2022-01-01 12:00:00', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu',  '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', '2022-01-01 12:00:00', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou',  '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', '2022-01-01 12:00:00', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian',  '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from DATEV2 to BOOLEAN
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BOOLEAN KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 0, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATEV2 to TINYINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to TINYINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time TINYINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATEV2 to SMALLINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to SMALLINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time SMALLINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATEV2 to INT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to INT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time INT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)




     //TODO Test the agg model by modify a key type from DATEV2 to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from DATEV2 to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATEV2 to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATEV2 to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time CHAR KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from DATEV2 to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[login_time]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATEV2 to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to VARCHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATEV2 to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from DATEV2 to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATEV2 to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATEV2 to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time CHAR KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from DATEV2 to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[login_time]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATEV2 to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to VARCHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     /**
      *  Test the agg model by modify a key type from DATETIMEV2 to other type
      */


     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `login_time` DATETIMEV2 COMMENT \"用户登陆时间\",\n" +
             "              `is_teacher` BOOLEAN REPLACE_IF_NOT_NULL COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `login_time`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-01-01', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou','2022-01-01', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-01-01', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-01-01', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-01-01', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-01-01', '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from DATETIMEV2 to BOOLEAN
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BOOLEAN KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 0, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIMEV2 to TINYINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to TINYINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time TINYINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIMEV2 to SMALLINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to SMALLINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time SMALLINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIMEV2 to INT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to INT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time INT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATETIMEV2 to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIMEV2 to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATETIMEV2 to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIMEV2 to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time CHAR KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIMEV2 to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[login_time]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIMEV2 to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to VARCHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     /**
      *  Test the agg model by modify a key type from DATETIME to other type
      */


     initTable = " CREATE TABLE IF NOT EXISTS ${tbName1}\n" +
             "          (\n" +
             "              `user_id` LARGEINT NOT NULL COMMENT \"用户id\",\n" +
             "              `username` VARCHAR(50) NOT NULL COMMENT \"用户昵称\",\n" +
             "              `login_time` DATETIME COMMENT \"用户登陆时间\",\n" +
             "              `is_teacher` BOOLEAN REPLACE_IF_NOT_NULL COMMENT \"是否是老师\",\n" +
             "              `city` VARCHAR(20) REPLACE_IF_NOT_NULL COMMENT \"用户所在城市\",\n" +
             "              `age` SMALLINT REPLACE_IF_NOT_NULL COMMENT \"用户年龄\",\n" +
             "              `sex` TINYINT REPLACE_IF_NOT_NULL COMMENT \"用户性别\",\n" +
             "              `phone` LARGEINT REPLACE_IF_NOT_NULL COMMENT \"用户电话\",\n" +
             "              `address` VARCHAR(500) REPLACE_IF_NOT_NULL COMMENT \"用户地址\",\n" +
             "              `register_time` DATETIME REPLACE_IF_NOT_NULL COMMENT \"用户注册时间\"\n" +
             "          )\n" +
             "          AGGREGATE KEY(`user_id`, `username`, `login_time`)\n" +
             "          DISTRIBUTED BY HASH(`user_id`) BUCKETS 1\n" +
             "          PROPERTIES (\n" +
             "          \"replication_allocation\" = \"tag.location.default: 1\"\n" +
             "          );"

     initTableData = "insert into ${tbName1} values(123456789, 'Alice', 0, 'Beijing', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01', '2022-01-01 10:00:00')," +
             "               (234567890, 'Bob', 0, 'Shanghai', 30, 1, 13998765432, 'No. 456 Street, Shanghai', '2022-01-01', '2022-02-02 12:00:00')," +
             "               (345678901, 'Carol', 1, 'Guangzhou', 28, 0, 13724681357, 'No. 789 Street, Guangzhou','2022-01-01', '2022-03-03 14:00:00')," +
             "               (456789012, 'Dave', 0, 'Shenzhen', 35, 1, 13680864279, 'No. 987 Street, Shenzhen', '2022-01-01', '2022-04-04 16:00:00')," +
             "               (567890123, 'Eve', 0, 'Chengdu', 27, 0, 13572468091, 'No. 654 Street, Chengdu', '2022-01-01', '2022-05-05 18:00:00')," +
             "               (678901234, 'Frank', 1, 'Hangzhou', 32, 1, 13467985213, 'No. 321 Street, Hangzhou', '2022-01-01', '2022-06-06 20:00:00')," +
             "               (789012345, 'Grace', 0, 'Xian', 29, 0, 13333333333, 'No. 222 Street, Xian', '2022-01-01', '2022-07-07 22:00:00');"

     //TODO Test the agg model by modify a key type from DATETIME to BOOLEAN
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BOOLEAN"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BOOLEAN KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 0, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIME to TINYINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to TINYINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time TINYINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIME to SMALLINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to SMALLINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time SMALLINT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIME to INT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to INT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time INT KEY DEFAULT "1"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATETIME to BIGINT
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to BIGINT"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time BIGINT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIME to FLOAT
     errorMessage="errCode = 2, detailMessage = Float or double can not used as a key, use decimal instead."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time FLOAT KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)



     //TODO Test the agg model by modify a key type from DATETIME to DECIMAL
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to DECIMAL32"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time DECIMAL KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', 1.0, 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIME to CHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to CHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time CHAR KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")

     },errorMessage)

     //TODO Test the agg model by modify a key type from DATETIME to STRING
     errorMessage="errCode = 2, detailMessage = String Type should not be used in key column[login_time]."
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time STRING KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)


     //TODO Test the agg model by modify a key type from DATETIME to VARCHAR
     errorMessage="errCode = 2, detailMessage = Can not change DATEV2 to VARCHAR"
     expectException({
          sql initTable
          sql initTableData
          sql """ alter  table ${tbName1} MODIFY  column login_time VARCHAR(32) KEY DEFAULT "0"  """
          insertSql = "insert into ${tbName1} values(923456689, 'Alice', '1', 'Yaan', 25, 0, 13812345678, 'No. 123 Street, Beijing', '2022-01-01 10:00:00'); "
          waitForSchemaChangeDone({
               sql getTableStatusSql
               time 600
          }, insertSql, true,"${tbName1}")
     },errorMessage)

}
