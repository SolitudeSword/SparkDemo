package cat.dream.miaomiao.enter

import java.io.{BufferedInputStream, FileInputStream}
import java.util.{Date, Properties}

import cat.dream.common.BaseConfig
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession


/**
  * 用SparkSQL处理日志，统计PV、IP、UV
  * 日志格式，一行一条，用\t分隔，依次是时间、ip、uid
  *
  * @author solitudesword
  */
object DealLogsSQL {
    /**
      * 生成DataFrame用的case类
      * @param ip ip
      * @param uid 用户id
      */
    case class OneLog(ip: String, uid: String)
    /**
      * 入口函数
      *
      * @param args 参数列表
      *             0 : 日志小时数
      */
    def main(args: Array[String]): Unit = {
        // 只显示错误日志
        Logger.getLogger("org").setLevel(org.apache.log4j.Level.ERROR)
        if (args.length < 1) {
            Logger.getLogger("org").fatal("参数个数不足，必须不能少于1个")
            return
        }
        val startTm = (new Date).getTime
        val hour = args(0)
        // 初始化
        val projectRoot = BaseConfig.getProjectRootDir // 项目根目录
        // 配置读取器
        val coreConf = new Properties
        // 加载根目录的hdfs配置
        coreConf.load(new BufferedInputStream(new FileInputStream(s"$projectRoot/conf/hdfs.properties")))
        // 读取hdfs地址
        val hdfsMaster = coreConf.getProperty("hdfs.master")

        val spark = SparkSession.builder.appName(s"DealLogsSQL-$hour").getOrCreate

        import spark.sqlContext.implicits._
        spark.sparkContext
            .textFile(s"hdfs://$hdfsMaster/guess.union2.50bang.org/logs/$hour.txt")
            .map(_.split("\t"))
            .filter(_.length > 2)
            .map(x => OneLog(x(1), x(2)))
            .toDF
            .createOrReplaceTempView("tmpTableLogs")

        val ret = spark.sql("select count(distinct(ip)), count(distinct(uid)), count(*) from tmpTableLogs")
            .collect()(0)
        val (ip, uv, pv) = (ret.getLong(0), ret.getLong(1), ret.getLong(2))

        spark.stop()
        val endTm = (new Date).getTime
        printf("time = %.3f, hour = %s, ip = %d, uv = %d, pv = %d\n", (endTm - startTm) / 1000.0, hour, ip, uv, pv)
    }
}
