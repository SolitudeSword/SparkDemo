#!/bin/sh
    
:<<!
    统计日志的PV、IP、UV，用两种方法统计多个不同的时间日志对比
    @author solitudesword
!

# 项目根目录
ProjectRootDir=`readlink -f \`dirname $0\`/../../`

# 导入变量
source ${ProjectRootDir}/shell/baseEnv.sh


for hour in `seq -w 0 23`; do
    for plan in `seq 1 2`; do
        ${SparkSubmitPath} \
            --master ${SparkMaster} \
            --executor-memory 1g \
            --executor-cores 2 \
            --total-executor-cores 6 \
            --driver-class-path ${BaseJar} \
            --class cat.dream.miaomiao.enter.DealLogs \
            ${ProjectRootDir}/basic/target/spark.small.demo.basic-1.0.jar ${hour} ${plan}
    done
done
