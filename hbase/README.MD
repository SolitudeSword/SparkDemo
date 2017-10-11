# 示例列表

## HBaseCount

### 功能
计数HBase表的记录条数

### 实现方法
以HBase表的数据为源生成RDD，然后通过count算子得到记录条数，也可以用其他算子实现需要的功能


## HBaseExport
### 功能
将HBase表的数据导出至HDFS便于迁移和备份
### 实现方法
首先用HBase表生成RDD，再用saveAsNewAPIHadoopFile方法将数据存到HDFS
