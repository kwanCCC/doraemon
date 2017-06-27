package org.doraemon.treasure.ares.mybatis;


public interface TableNameHandler {

    public static final String SQL_TYPE_INSERT = "insert";
    public static final String SQL_TYPE_UPDATE = "update";
    public static final String SQL_TYPE_DELETE = "delete";
    public static final String SQL_TYPE_SELECT = "select";
    public static final String SQL_TYPE_OTHER  = "other";
    public static final String SQL_TYPE_TRUNCATE  = "truncate";
    

    public String getTargetTableName(String sqlType, String tableName, Object params, String mapperId);


}
