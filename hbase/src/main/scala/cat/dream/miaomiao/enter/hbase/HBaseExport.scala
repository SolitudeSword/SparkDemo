package cat.dream.miaomiao.enter.hbase

import java.io.{BufferedInputStream, FileInputStream}
import java.util.{Properties, ResourceBundle}

import cat.dream.common.BaseConfig
import cat.dream.miaomiao.hbase.utils
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{HFileOutputFormat2, TableInputFormat}
import org.apache.hadoop.hbase.{CellUtil, HBaseConfiguration, KeyValue}
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 利用Spark实现HBase数据表的导出
  *
  * @author solitudesword
  */
object HBaseExport {
    /**
      * 入口函数
      *
      * @param args 参数列表
      *             0 HBase表名
      *             1 存储的hdfs地址，不包含主机名和端口部分
      *             2 可选参数，hdfs主机及端口地址，不填写则使用
      */
    def main(args: Array[String]): Unit = {
        Logger.getRootLogger.setLevel(org.apache.log4j.Level.ERROR)
        if (args.length < 2) {
            Logger.getLogger("org").fatal("参数个数不足，必须不能少于2个")
            return
        }

        val (tableName, uri) = (args(0), args(1))
        val conf = HBaseConfiguration.create()

        // 判断HBase表是否存在
        if (!utils.tableExist(tableName, conf)) {
            println(s"HBase table[$tableName] not exists")
            return
        }

        conf.set(TableInputFormat.INPUT_TABLE, tableName)

        // 备份的hdfs主机名
        val hadoopMaster =
            if (args.length < 3) {
                val coreConf = new Properties
                coreConf.load(new BufferedInputStream(new FileInputStream(BaseConfig.getProjectRootDir + "/conf/hdfs.properties")))
                coreConf.getProperty("hdfs.master")
            } else
                args(2)


        val sc = new SparkContext(new SparkConf().setAppName(s"ExportHBase-$tableName"))

        // 将HBase数据导出至HDFS
        sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result])
            .flatMap {
                case (w, result) =>
                    val cellList = result.listCells
                    (for (idx <- 0 until cellList.size) yield cellList.get(idx)).toList
                        .map(cell => (w, new KeyValue(CellUtil.cloneRow(cell),
                            CellUtil.cloneFamily(cell),
                            CellUtil.cloneQualifier(cell),
                            cell.getTimestamp,
                            CellUtil.cloneValue(cell))))
            }
            .saveAsNewAPIHadoopFile(s"hdfs://$hadoopMaster/$uri",
                classOf[ImmutableBytesWritable],
                classOf[KeyValue],
                classOf[HFileOutputFormat2],
                conf)

        sc.stop()
    }
}
