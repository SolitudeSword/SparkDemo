#!/bin/sh
    
:<<!
    将RDD数据存入HBase

    @author solitudesword
!

# 项目根目录
ProjectRootDir=`readlink -f \`dirname $0\`/../../`

# 导入变量
source ${ProjectRootDir}/hbase/shell/hbaseEnv.sh

# 提交作业
${SparkSubmitPath} \
    --master ${SparkMaster} \
    --driver-class-path ${HBASE_HOME}/conf/ \
    --jars ${HBaseJars} \
    --executor-memory 1g \
    --executor-cores 2 \
    --total-executor-cores 6 \
    --class cat.dream.miaomiao.enter.hbase.SparkSave \
    ${ProjectRootDir}/hbase/target/spark.small.demo.hbase-1.0.jar
