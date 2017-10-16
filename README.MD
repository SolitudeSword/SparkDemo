# 简介
这个项目是一些Spark相关的入门Demo代码段，有比较详尽的注释，便于学习

项目是用Maven编译的，单元测试使用的是JUnit

# 模块列表
## base
公用模块，为其他模块提供函数

## public
用来引用base模块的中介

## basic
基础示例，包含
- WordCount 统计文章的单词个数

## hbase
与hbase的连接演示，包含
- HBaseCount 计数HBase表的记录条数
- HBaseExport 导出HBase表到HDFS
- HBaseImport 从备份的HDFS导入到HBase
- Basic Scala操作HBase的基本操作，和Spark没什么关系
- SparkSave 将RDD存入HBase


# 部署说明
1. 安装Spark、maven等相关软件
2. 打开shell/baseEnv.sh和hbase/shell/hbaseEnv.sh修改相应的路径
3. 全项目编译在项目根目录下执行 *mvn package* 或 *mvn clean package*
4. 用命令 *mvn package -pl basic -amd -am* 编译basic模块及依赖basic模块的其他模块和basic依赖的其他模块
5. 打开conf/hdfs.properties配置hdfs相关设置
6. 运行各模块的对应shell建议打开看看，考虑是否要调整参数
7. 使用命令 *mvn cobertura:cobertura* 生成代码覆盖率报告，报告位于各模块的target/site/cobertura
