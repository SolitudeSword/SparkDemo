#!/bin/sh
    
:<<!
    执行WordCount
    @author solitudesword
!

# 项目根目录
ProjectRootDir=`readlink -f \`dirname $0\`/../../`

# 导入变量
source ${ProjectRootDir}/shell/baseEnv.sh

# 提交作业
${SparkSubmitPath} \
    --master ${SparkMaster} \
    --executor-memory 1g \
    --executor-cores 2 \
    --total-executor-cores 6 \
    --driver-class-path ${ProjectRootDir}/base/target/spark.small.demo.base-1.0.jar \
    --class cat.dream.miaomiao.enter.WordCount \
    ${ProjectRootDir}/basic/target/spark.small.demo.basic-1.0.jar
