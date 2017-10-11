#!/bin/sh
    
:<<!
    HBase相关程序的环境变量
    @author 王锦
!

if [ -z ${ProjectRootDir} ]; then
    ProjectRootDir=`readlink -f \`dirname $0\`/../../`
fi

source ${ProjectRootDir}/shell/baseEnv.sh

if [ -z ${HBASE_HOME} ]; then
    # hbase目录
    HBASE_HOME=/opt/app/hbase
fi

# 运行hbase时需要引用的jar包
HBaseJars=""
for jar in `ls ${HBASE_HOME}/lib/*.jar`;
do
  HBaseJars=${HBaseJars},${jar}
done
HBaseJars=${HBaseJars:1}
