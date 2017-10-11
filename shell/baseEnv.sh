#!/bin/sh
    
:<<!
    必须的环境变量
    @author 王锦
!

if [ -z ${ProjectRootDir} ]; then
    ProjectRootDir=`readlink -f \`dirname $0\`/../`
fi

# spark-submit的路径
if [ -z ${SPARK_HOME} ]; then
    # 如果没有定义SparkHome需要在这里手动修改
    SparkSubmitPath=/opt/app/spark/bin/spark-submit
else
    SparkSubmitPath=${SPARK_HOME}/bin/spark-submit
fi

# hadoop的可执行文件路径
if [ -z ${HADOOP_HOME} ]; then
    # 如果没有定义HadoopHome需要在这里手动修改
    HadoopPath=/opt/app/hadoop/bin/hadoop
else
    HadoopPath=${HADOOP_HOME}/bin/hadoop
fi

# spark的提交地址
SparkMaster=spark://127.0.0.1:7077

# maven库地址
MavenRepPath=/opt/app/maven3/var/repository

# base的jar包
BaseJar=${ProjectRootDir}/base/target/spark.small.demo.base-1.0.jar
