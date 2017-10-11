#!/bin/sh
    
:<<!
    查看一张HBase表的条数
    @param 1 表名
    @author solitudesword
!

if [ -z $1 ]; then
    echo "miss table name"
    exit
fi

# 项目根目录
ProjectRootDir=`readlink -f \`dirname $0\`/../../`

# 导入变量
source ${ProjectRootDir}/hbase/shell/hbaseEnv.sh

# 提交作业
${SparkSubmitPath} \
    --master ${SparkMaster} \
    --driver-class-path ${HBASE_HOME}/conf/:${BaseJar} \
    --jars ${HBaseJars},${BaseJar} \
    --executor-memory 1g \
    --executor-cores 2 \
    --total-executor-cores 6 \
    --class cat.dream.miaomiao.enter.hbase.HBaseCount \
    ${ProjectRootDir}/hbase/target/spark.small.demo.hbase-1.0.jar $1
