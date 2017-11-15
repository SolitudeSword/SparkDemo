package cat.dream.miaomiao.enter

import java.io.{BufferedInputStream, FileInputStream}
import java.util.{Date, Properties}

import cat.dream.common.BaseConfig
import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 处理日志，统计PV、IP、UV
  * 日志格式，一行一条，用\t分隔，依次是时间、ip、uid
  *
  * @author solitudesword
  */
object DealLogs {
    /**
      * 入口函数
      *
      * @param args 参数列表
      *             0 : 日志小时数
      *             1 : 使用的方案，1或2
      */
    def main(args: Array[String]): Unit = {
        if (args.length < 2) {
            Logger.getLogger("org").fatal("参数个数不足，必须不能少于2个")
            return
        }

        val startTm = (new Date).getTime
        val (hour, plan) = (args(0), args(1))
        // 只显示错误日志
        Logger.getLogger("org").setLevel(org.apache.log4j.Level.ERROR)
        // 初始化
        val projectRoot = BaseConfig.getProjectRootDir // 项目根目录
        // 配置读取器
        val coreConf = new Properties
        // 加载根目录的hdfs配置
        coreConf.load(new BufferedInputStream(new FileInputStream(s"$projectRoot/conf/hdfs.properties")))
        // 读取hdfs地址
        val hdfsMaster = coreConf.getProperty("hdfs.master")

        val sc = new SparkContext(new SparkConf().setAppName(s"DealLogs-$hour-plan$plan"))

        // 这里的RDD偷懒就自己写死了，正式使用时使用textFile读文件
        val logsRdd = sc
            .textFile(s"hdfs://$hdfsMaster/guess.union2.50bang.org/logs/$hour.txt")
            .map(_.split("\t"))
            .filter(_.length > 2)
            .map(x => (x(1), x(2)))
            .persist(StorageLevel.MEMORY_AND_DISK)
        // 由于会重复使用RDD，所以做持久化处理

        val fn: RDD[(String, String)] => (Long, Long, Long) = plan match {
            case "1" => plan1
            case _ => plan2
        }
        val (pv, ip, uv) = fn(logsRdd)
        sc.stop()

        printf("ip = %d, uv = %d, pv = %d\n", ip, uv, pv)
        val endTm = (new Date).getTime
        printf("use time = %.3f, hour = %s, plan = %s\n", (endTm - startTm) / 1000.0, hour, plan)
    }

    /**
      * 方案一统计
      *
      * @param logsRdd RDD
      * @return (pv, ip, uv)
      */
    def plan1(logsRdd: RDD[(String, String)]): (Long, Long, Long) = {
        val (pv, ip) = logsRdd
            .map { case (i, _) => (i, 1L) }
            .reduceByKey(_ + _)
            .map { case (_, ipv) => (ipv, 1L) }
            .reduce { case (x, y) => (x._1 + y._1, x._2 + y._2) }
        val uv = logsRdd.map(_._2).distinct().count()
        (pv, ip, uv)
    }

    /**
      * 方案二统计
      *
      * @param logsRdd RDD
      * @return (pv, ip, uv)
      */
    def plan2(logsRdd: RDD[(String, String)]): (Long, Long, Long) =
        (logsRdd.count(), logsRdd.map(_._1).distinct().count(), logsRdd.map(_._2).distinct().count())

}
