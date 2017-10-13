#!/bin/sh
    
:<<!
    Scala通过HBase的API操作HBase
    @author solitudesword
!

# 项目根目录
ProjectRootDir=`readlink -f \`dirname $0\`/../../`

# 导入变量
source ${ProjectRootDir}/hbase/shell/hbaseEnv.sh

# 其实吧，这里其实是可以不用Submit的，因为根本就没用Spark的东西
# 提交作业
${SparkSubmitPath} \
    --master ${SparkMaster} \
    --driver-class-path ${HBASE_HOME}/conf/ \
    --jars ${HBaseJars} \
    --executor-memory 1g \
    --executor-cores 2 \
    --total-executor-cores 6 \
    --class cat.dream.miaomiao.enter.hbase.Basic \
    ${ProjectRootDir}/hbase/target/spark.small.demo.hbase-1.0.jar
