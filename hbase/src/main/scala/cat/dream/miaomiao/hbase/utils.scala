package cat.dream.miaomiao.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.ConnectionFactory

/**
  * hbase相关几个工具函数
  *
  * @author solitudesword
  */
object utils {
    /**
      * 判断一个HBase表是否存在
      * @param tableName 表名
      * @param conf 配置，如果不传入则自动创建
      * @return 表是否存在
      */
    def tableExist(tableName: String, conf: Configuration = null): Boolean = {
        val cf = if (conf == null) HBaseConfiguration.create() else conf
        val connection = ConnectionFactory.createConnection(conf)
        val hbaseAdmin = connection.getAdmin
        val ret = hbaseAdmin.tableExists(TableName.valueOf(tableName))
        hbaseAdmin.close()
        connection.close()
        ret
    }
}
