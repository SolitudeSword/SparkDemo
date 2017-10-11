#!/bin/sh
    
:<<!
    执行WordCount
    @author solitudesword
!

# 项目根目录
ProjectRootDir=`readlink -f \`dirname $0\`/../../`

# 导入变量
source ${ProjectRootDir}/shell/baseEnv.sh

# 删除前面的输出
${HadoopPath} fs -rm -f -r /output

testFns=(reduce group)

for testFn in ${testFns[*]}; do
    # 提交作业
    ${SparkSubmitPath} \
        --master ${SparkMaster} \
        --executor-memory 1g \
        --executor-cores 2 \
        --total-executor-cores 6 \
        --driver-class-path ${BaseJar} \
        --class cat.dream.miaomiao.enter.WordCount \
        ${ProjectRootDir}/basic/target/spark.small.demo.basic-1.0.jar ${testFn}
done

