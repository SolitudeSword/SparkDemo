package cat.dream.miaomiao.enter.hbase

import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 统计一个HBase表的数据条数
  *
  * @author solitudesword
  */
object HBaseCount {
    /**
      * 入口函数
      *
      * @param args 参数列表
      *             0 HBase表名
      */
    def main(args: Array[String]): Unit = {
        if (args.length < 1) {
            Logger.getLogger("org").fatal("缺失表名参数")
            return
        }

        Logger.getLogger("org").setLevel(org.apache.log4j.Level.ERROR)

        // 判断表是否存在
        val conf = HBaseConfiguration.create()
        val tableName = TableName.valueOf(args(0))
        val tableNameString = tableName.getNameAsString
        val connection = ConnectionFactory.createConnection(conf)
        val hbaseAdmin = connection.getAdmin
        if (!hbaseAdmin.tableExists(tableName)) {
            println(s"HBase table[$tableNameString] 不存在")
            return
        }

        //设置查询的表名
        conf.set(TableInputFormat.INPUT_TABLE, tableNameString)
        val sc = new SparkContext(new SparkConf().setAppName(s"HBaseCount-$tableNameString"))

        // 以HBase表数据为源，建立RDD，之后可以做任意的RDD操作了
        val count = sc
            .newAPIHadoopRDD(conf, classOf[TableInputFormat],
                classOf[ImmutableBytesWritable],
                classOf[Result])
            .count()
        println(s"HBase table[$tableNameString] count = $count")
        sc.stop()
    }
}
