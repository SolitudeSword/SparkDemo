# 示例列表

## DealLogsSQL
### 功能
用SparkSQL统计日志中的IP、UV、PV
### 实现方法和结果
由RDD转为DataFrame，然后用SQL在SparkSQL引擎上执行得到结果

执行时间显著优于算子的方案，相同的测试方案下时间能降至一半以下