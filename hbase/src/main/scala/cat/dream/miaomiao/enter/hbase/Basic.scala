package cat.dream.miaomiao.enter.hbase

import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Put}
import org.apache.hadoop.hbase.util.Bytes

/**
  * 一些基本的Scala操作HBase的基础操作
  *
  * @author solitudesword
  */
object Basic {
    /** 要操作的HBase表名 */
    private[this] val tableName = TableName.valueOf("testTable")
    /** 列族名字 */
    private[this] val colBytes = Bytes.toBytes("favor")

    /** 入口函数 */
    def main(args: Array[String]): Unit = {
        println("create table...")
        createTable()
        println("write data to table...")
        writeData()
        println("read data from table by get...")
        readData()
    }

    /**
      * 创建表，如果已存在则删除，建表语句：
      * create 'testTable', {NAME => 'favor', TTL => '2160000', VERSIONS => 1}
      * 表的功能是存储用户的偏好信息
      * 表的主键是uid
      * favor列族 列名是 偏好名称 值是 权重值
      */
    def createTable(): Unit = {
        val conf = HBaseConfiguration.create()
        val connection = ConnectionFactory.createConnection(conf)
        val hbaseAdmin = connection.getAdmin
        if (hbaseAdmin.tableExists(tableName)) {
            // 如果表已经存在则删除
            hbaseAdmin.disableTable(tableName)
            hbaseAdmin.deleteTable(tableName)
        }
        val tableDescriptor = new HTableDescriptor(tableName)
        tableDescriptor.addFamily {
            val f = new HColumnDescriptor(colBytes) // 这里也可以直接用字符串
            f.setTimeToLive(2160000)
            f.setMaxVersions(1)
            f
        }
        hbaseAdmin.createTable(tableDescriptor)
        hbaseAdmin.close()
        connection.close()
    }

    /** 将数据写入HBase */
    def writeData(): Unit = {
        val conf = HBaseConfiguration.create()
        val connection = ConnectionFactory.createConnection(conf)
        val htable = connection.getTable(tableName)

        // 插入一条数据
        htable.put {
            // 用户 uid00001 偏好信息 {游戏:3.5 体育:8.43 旅游:5.423}
            val put = new Put(Bytes.toBytes("uid00001"))
            put.addColumn(colBytes, Bytes.toBytes("游戏"), Bytes.toBytes(3.5))
            put.addColumn(colBytes, Bytes.toBytes("体育"), Bytes.toBytes(8.43))
            put.addColumn(colBytes, Bytes.toBytes("旅游"), Bytes.toBytes(5.423))
            put
        }

        // 插入多条数据
        /**
          * 用户 uid00002 偏好信息 {科技:4.21 科幻:6.4}
          * 用户 uid00003 偏好信息 {小说:6.23 游戏:5.4 股票:1.432 侦探片:3.434}
          * 用户 uid00004 偏好信息 {科幻:9.2 小说:7.42 体育:1.3}
          */
        import scala.collection.JavaConverters._
        htable.put(
            Map(
                "uid00002" -> List(("科技", 4.21), ("科幻", 6.4)),
                "uid00003" -> List(("小说", 6.23), ("游戏", 5.4), ("股票", 1.432), ("侦探片", 3.434)),
                "uid00004" -> List(("科幻", 9.2), ("小说", 7.42), ("体育", 1.3))
            )
                .map {
                    case (uid, favors) =>
                        val put = new Put(Bytes.toBytes(uid))
                        favors.foreach { case (label, weight) => put.addColumn(colBytes,
                            Bytes.toBytes(label),
                            Bytes.toBytes(weight))
                        }
                        put
                }
                .toList
                .asJava
        )
        htable.close()
        connection.close()
    }

    /** 用get获取HBase中的指定数据 */
    def readData(): Unit = {
        val conf = HBaseConfiguration.create()
        val connection = ConnectionFactory.createConnection(conf)
        val htable = connection.getTable(tableName)


        // get可以不加addFamily函数来解除限制
        1.to(5)
            .map(i => s"uid0000$i")
            .map(uid => (uid, htable.get(new Get(uid.getBytes).addFamily(colBytes))))
            .foreach {
                case (uid, ret) if ret.isEmpty => println(s"uid[$uid]不存在")
                case (uid, ret) =>
                    val cellList = ret.listCells
                    val favors = (0 until cellList.size())
                        .map(idx =>
                            (Bytes.toString(CellUtil.cloneQualifier(cellList.get(idx))),
                                Bytes.toFloat(CellUtil.cloneValue(cellList.get(idx)))))
                        .map { case (label, weight) => s"$label:$weight" }
                        .mkString(",")
                    println(s"uid [$uid] 的偏好信息 {$favors}")
            }


        htable.close()
        connection.close()
    }
}
