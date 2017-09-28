package cat.dream.miaomiao.enter

import java.io.{BufferedInputStream, FileInputStream}
import java.util.Properties

import cat.dream.common.BaseConfig
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
      * @param args 参数列表，无
      */
    def main(args: Array[String]): Unit = {
        // 初始化
        val projectRoot = BaseConfig.getProjectRootDir // 项目根目录
        // 配置读取器
        val coreConf = new Properties
        // 加载根目录的hdfs配置
        coreConf.load(new BufferedInputStream(new FileInputStream(s"$projectRoot/conf/hdfs.properties")))
        // 读取hdfs地址
        val hdfsMaster = coreConf.getProperty("hdfs.master")

        val sc = new SparkContext(new SparkConf().setAppName("WordCount"))
        sc.textFile(s"hdfs://$hdfsMaster/word.txt")
            .flatMap(_.split(" "))
            .map((_, 1L))
            .reduceByKey(_ + _)
            .map { case (word, cnt) => s"$word,$cnt" }
            .saveAsTextFile(s"hdfs://$hdfsMaster/output")

        // 结束清理
        sc.stop() // 关闭spark上下文
    }
}
