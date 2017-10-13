package cat.dream.miaomiao.enter.hbase

import org.apache.hadoop.hbase.client.{Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 将RDD数据存入HBase
  *
  * @author solitudesword
  */
object SparkSave {
    /** 入口函数 */
    def main(args: Array[String]): Unit = {
        Logger.getLogger("org").setLevel(org.apache.log4j.Level.ERROR)

        // 将Basic示例中的用户数据存入hbase
        val uidFavors = Map(
            "uid00001S" -> List(("游戏", 3.5), ("体育", 8.43), ("旅游", 5.423)),
            "uid00002S" -> List(("科技", 4.21), ("科幻", 6.4)),
            "uid00003S" -> List(("小说", 6.23), ("游戏", 5.4), ("股票", 1.432), ("侦探片", 3.434)),
            "uid00004S" -> List(("科幻", 9.2), ("小说", 7.42), ("体育", 1.3))
        ).toList

        val sc = new SparkContext(new SparkConf().setAppName("HBaseSaveDataBySpark"))

        sc.makeRDD(uidFavors)
            .map {
                case (uid, favors) =>
                    val put = new Put(Bytes.toBytes(uid))
                    val colBytes = Bytes.toBytes("favor")
                    favors.foreach {
                        case (label, weight) => put.addColumn(colBytes, Bytes.toBytes(label), Bytes.toBytes(weight))
                    }
                    (new ImmutableBytesWritable, put)
            }
            .saveAsNewAPIHadoopDataset{
                sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, "testTable")
                val job = Job.getInstance(sc.hadoopConfiguration)
                job.setOutputKeyClass(classOf[ImmutableBytesWritable])
                job.setOutputValueClass(classOf[Result])
                job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])
                job.getConfiguration
            }

        sc.stop
    }
}
