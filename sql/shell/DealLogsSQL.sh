#!/bin/sh
    
:<<!
    SparkSQL统计日志的PV、IP、UV
    @author solitudesword
!

# 项目根目录
ProjectRootDir=`readlink -f \`dirname $0\`/../../`

# 导入变量
source ${ProjectRootDir}/shell/baseEnv.sh


for hour in `seq -w 0 23`; do
    ${SparkSubmitPath} \
        --master ${SparkMaster} \
        --executor-memory 1g \
        --executor-cores 2 \
        --total-executor-cores 6 \
        --driver-class-path ${BaseJar} \
        --class cat.dream.miaomiao.enter.DealLogsSQL \
        ${ProjectRootDir}/sql/target/spark.small.demo.sql-1.0.jar ${hour}
done

