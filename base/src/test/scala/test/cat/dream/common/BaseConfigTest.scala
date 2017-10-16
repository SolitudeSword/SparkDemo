package test.cat.dream.common

import java.io.File

import cat.dream.common.BaseConfig
import org.junit.Test
import java.nio.file.Paths
import junit.framework.TestCase.assertEquals

/**
  * cat.dream.common.BaseConfig的测试类
  *
  * @author solitudesword
  */
@Test
class BaseConfigTest {
    /** 测试getProjectRootDir和getModuleDir方法 */
    @Test
    def testPathFn(): Unit = {
        val projectDir = Paths.get(new File(this.getClass.getResource("/").getPath) + "/../../../").toRealPath().toString
        assertEquals("项目根目录函数", projectDir, BaseConfig.getProjectRootDir)
        assertEquals("模块目录函数", Paths.get(projectDir + "/base").toRealPath().toString, BaseConfig.getModuleDir)
    }
}
