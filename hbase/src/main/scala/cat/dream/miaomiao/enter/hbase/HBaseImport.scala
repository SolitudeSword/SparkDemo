package cat.dream.miaomiao.enter.hbase

import java.io.{BufferedInputStream, FileInputStream}
import java.util.{Properties, ResourceBundle}

import cat.dream.common.BaseConfig
import cat.dream.miaomiao.hbase.utils
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.mapreduce.{LoadIncrementalHFiles, TableInputFormat}
import org.apache.log4j.Logger

/**
  * 由HDFS导入HBase数据
  *
  * @author solitudesword
  */
object HBaseImport {
    /**
      * 入口函数
      *
      * @param args 参数列表
      *             0 HBase表名
      *             1 存储的hdfs地址，不包含主机名和端口部分
      *             2 可选参数，hdfs主机及端口地址，不填写则使用
      */
    def main(args: Array[String]): Unit = {
        Logger.getLogger("org").setLevel(org.apache.log4j.Level.ERROR)
        if (args.length < 2) {
            Logger.getLogger("org").fatal("参数个数不足，必须不能少于2个")
            return
        }

        val (tableName, uri) = (args(0), args(1))
        val conf = HBaseConfiguration.create()


        val connection = ConnectionFactory.createConnection(conf)
        val hbaseAdmin = connection.getAdmin

        // 判断HBase表是否存在
        val tName = TableName.valueOf(tableName)
        if (!hbaseAdmin.tableExists(tName)) {
            println(s"HBase table[$tableName] not exists")

            hbaseAdmin.close()
            connection.close()
            return
        }

        // 备份的hdfs主机名
        val hadoopMaster =
            if (args.length < 3) {
                val coreConf = new Properties
                coreConf.load(new BufferedInputStream(new FileInputStream(BaseConfig.getProjectRootDir + "/conf/hdfs.properties")))
                coreConf.getProperty("hdfs.master")
            } else
                args(2)

        // 开始导入
        conf.set(TableInputFormat.INPUT_TABLE, tableName)
        val table = connection.getTable(tName)
        val load = new LoadIncrementalHFiles(conf)
        load.doBulkLoad(new Path(s"hdfs://$hadoopMaster/$uri"), hbaseAdmin, table, connection.getRegionLocator(tName))


        hbaseAdmin.close()
        table.close()
        connection.close()
    }
}
