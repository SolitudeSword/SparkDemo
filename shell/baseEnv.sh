#!/bin/sh
    
:<<!
    必须的环境变量
    @author 王锦
!

# 项目根目录
ProjectRootDir=`dirname $0`
ProjectRootDir=`readlink -f ${ProjectRootDir}`
# spark-submit的路径
if [ -z ${SPARK_HOME} ]; then
    # 如果没有定义SparkHome需要在这里手动修改
    SparkSubmitPath=/opt/app/spark/bin/spark-submit
else
    SparkSubmitPath=${SPARK_HOME}/bin/spark-submit
fi
