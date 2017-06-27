package org.doraemon.treasure.ares;

import org.doraemon.treasure.ares.mybatis.TableNameHandler;
import org.doraemon.treasure.ares.util.Pair;
import org.doraemon.treasure.ares.util.SQLParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SQLParserTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testFindTableNameAndType() {
        String truncate = "TRUNCATE TABLE CUSTOMERS ";
        Pair<String, String> tableNameAndType = SQLParser.findTableNameAndType(truncate);
        Assert.assertEquals(TableNameHandler.SQL_TYPE_TRUNCATE, tableNameAndType.getRight());
        Assert.assertEquals("customers", tableNameAndType.getLeft());

        String update = "   \r  \r\n \n     update  t_a$ble0 set a=1";
        tableNameAndType = SQLParser.findTableNameAndType(update);
        Assert.assertEquals(TableNameHandler.SQL_TYPE_UPDATE, tableNameAndType.getRight());
        Assert.assertEquals("t_a$ble0", tableNameAndType.getLeft());

        String insert = "insert into t_a$ble0(col_a, col_b) values(?,?) where id = 0";
        tableNameAndType = SQLParser.findTableNameAndType(insert);
        Assert.assertEquals(TableNameHandler.SQL_TYPE_INSERT, tableNameAndType.getRight());
        Assert.assertEquals("t_a$ble0", tableNameAndType.getLeft());

        String select = "select count(*) from CRM_KNOWLEDGE_DETAIL kc,CRM_KNOWLEDGE_BASE a where a.id=kc.KNOWLEDGE_ID";
        tableNameAndType = SQLParser.findTableNameAndType(select);
        Assert.assertEquals(TableNameHandler.SQL_TYPE_SELECT, tableNameAndType.getRight());
        Assert.assertEquals("crm_knowledge_detail", tableNameAndType.getLeft());

        String delete = "DELETE crm_adgroup_detail WHERE status = 1 AND adgroupno = :1";
        tableNameAndType = SQLParser.findTableNameAndType(delete);
        Assert.assertEquals(TableNameHandler.SQL_TYPE_DELETE, tableNameAndType.getRight());
        Assert.assertEquals("crm_adgroup_detail", tableNameAndType.getLeft());
    }

}
