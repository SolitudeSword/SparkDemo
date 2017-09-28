package cat.dream.miaomiao.enter

import java.io.{BufferedInputStream, FileInputStream}
import java.util.Properties

import cat.dream.common.BaseConfig
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 集群编程界的 hello world ，对一个文本文件的单词计数
  *
  * @author solitudesword
  */
object WordCount {
    /**
      * 入口函数
      *
      * @param args 参数列表
      *             0 : 使用方法 支持reduce group
      */
    def main(args: Array[String]): Unit = {
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
        (if (args.length > 0) args(0) else "reduce") match {
            case "reduce" => byReduce(hdfsMaster)
            case "group" => byGroup(hdfsMaster)
            case _ => Logger.getLogger("org").error("invalid type")
        }
    }

    /**
      * 通过groupBy来操作
      * @param hdfsMaster hdfs地址
      */
    def byGroup(hdfsMaster: String): Unit = {
        val sc = new SparkContext(new SparkConf().setAppName("WordCount-group"))
        sc.textFile(s"hdfs://$hdfsMaster/word.txt")
            .flatMap(_.split(" "))
            .groupBy(_.toString)
            .map { case (word, it) => s"$word," + it.size.toString }
            .saveAsTextFile(s"hdfs://$hdfsMaster/output/group")

        // 结束清理
        sc.stop() // 关闭spark上下文
    }

    /**
      * 通过reduceByKey来操作
      * @param hdfsMaster hdfs地址
      */
    def byReduce(hdfsMaster: String): Unit = {
        val sc = new SparkContext(new SparkConf().setAppName("WordCount-reduceByKey"))
        sc.textFile(s"hdfs://$hdfsMaster/word.txt")
            .flatMap(_.split(" "))
            .map((_, 1L))
            .reduceByKey(_ + _)
            .map { case (word, cnt) => s"$word,$cnt" }
            .saveAsTextFile(s"hdfs://$hdfsMaster/output/reduce")

        // 结束清理
        sc.stop() // 关闭spark上下文
    }
}
