/**
 * 
 */
package org.zollty.tool.dbdata.tosqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.zollty.dbk.datasource.DriverManagerDataSource;
import org.zollty.dbk.support.JdbcUtils;

/**
 * 导出数据到SQLiteDB
 * 
 * @author zollty
 * @since 2018年6月29日
 */
public class SqlQueryDataToSqlLite {
    
    private DataSource dataSource;
    
    private static String dbFilePath = "tmp-data.db";
    
    // 配置数据源
    public SqlQueryDataToSqlLite() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://192.168.11.243:3306/mydb?allowMultiQueries=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8");
        ds.setUsername("zollty");
        ds.setPassword("Pub#1234");
        dataSource = ds;
    }

    public static void main(String... args) throws Exception {
        String table = "fast_menu";
        String sql = "select id, parent_id, name, url, perms, type_, icon, order_num, modify_userid, modify_date from fast_menu";
        
        new SqlQueryDataToSqlLite().execute(table, sql);
    }

    /**
     * <p>导出数据到SQLiteDB </p>
     * <p>1.SQLite数据库和数据表会自动创建，全自动化； </p>
     * <p>2.原生SQL实现，稳定支持超大数据（本源码来源于大数据报表引擎系统，经过生产的长期考验）。</p>
     * 
     * @param table 表名
     * @param sql 要执行的SQL
     */
    public void execute(String table, String sql) {
        long tm = System.currentTimeMillis();
        Statement pstmt = null;
        Connection con = null;
        ResultSet rs = null;

        Connection sqliteConn = null;
        Statement sqliteStmt = null;
        try {
            con = dataSource.getConnection();
            sqliteConn = sqliteDataSource.getConnection();
            pstmt = getStatement(con);
            sqliteStmt = sqliteConn.createStatement();
            sqliteConn.setAutoCommit(false);

            rs = pstmt.executeQuery(sql);
            int index = 1;
            StringBuilder result = null;
            StringBuilder value = null;
            while (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                if (index == 1) {
                    result = new StringBuilder("create table if not exists " + table + "(");
                    value = new StringBuilder("insert into " + table + " values(");
                    for (int j = 1; j <= columnCount; j++) {
                        String key = JdbcUtils.lookupColumnName(rsmd, j);
                        if (j != 1) {
                            result.append(",");
                            value.append(",");
                        }
                        result.append(key).append(" varchar(1000)");
                        value.append("'xxxx'");
                    }
                    result.append(")");
                    value.append(")");
                    System.out.println(result);

                    sqliteStmt.executeUpdate("drop table if exists " + table);
                    sqliteStmt.executeUpdate(result.toString());
                    //sqliteStmt.executeUpdate(value.toString());
                    sqliteConn.commit();
                }

                // 读取数据并生成insert语句
                value = new StringBuilder("insert into " + table + " values(");
                for (int j = 1; j <= columnCount; j++) {
                    if (j != 1) {
                        value.append(",");
                    }
                    Object obj = JdbcUtils.getResultSetValue(rs, j);
                    if (obj != null) {
                        if (obj instanceof java.sql.Timestamp) {
                            value.append("'").append(format.format((Date) obj)).append("'");
                        } else {
                            value.append("'").append(obj.toString()).append("'");
                        }
                    } else {
                        value.append("null");
                    }
                }
                value.append(")");
                System.out.println(value);
                sqliteStmt.addBatch(value.toString());

                // 1000条记录插入一次
                if (index % 1000 == 0) {
                    sqliteStmt.executeBatch();
                    sqliteConn.commit();
                }
                index++;
            }
            // 最后插入不足1000条的数据
            sqliteStmt.executeBatch();
            sqliteConn.commit();
            System.out.println("handled " + --index + " items, cost: " + (System.currentTimeMillis() - tm) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cost: " + (System.currentTimeMillis() - tm) + " ms");
        } finally {
            closeAll(rs, pstmt, con);
            closeStatement(sqliteStmt);
            closeConnection(sqliteConn);
        }
    }

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String dbUrl = "jdbc:sqlite:" + dbFilePath;
    private static DriverManagerDataSource sqliteDataSource = new DriverManagerDataSource();
    static {
        sqliteDataSource.setDriverClassName("org.sqlite.JDBC");
        sqliteDataSource.setUrl(dbUrl);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public static void closeAll(ResultSet rs, Statement stat, Connection connection) {
        closeResult(rs);
        closeStatement(stat);
        closeConnection(connection);
    }

    public static Statement getStatement(Connection con) throws SQLException {
        Statement pstmt = con.createStatement(1003, 1007, 2);
        pstmt.setFetchSize(200);
        pstmt.setQueryTimeout(0);
        return pstmt;
    }

    public void createTable(String sql, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
    }

    public static void closeStatement(Statement stat) {
        try {
            if (stat != null) {
                stat.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResult(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
