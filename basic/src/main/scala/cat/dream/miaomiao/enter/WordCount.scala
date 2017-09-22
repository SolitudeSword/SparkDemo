package cat.dream.miaomiao.enter

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
    def main(args:Array[String]):Unit = {
        val sc = new SparkContext(new SparkConf().setAppName("WordCount"))

        sc.stop()
    }
}
