/**
 * 
 */
package org.zollty.tool.dbdata.tofile;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author zollty
 * @since 2018年6月28日
 */
public class InnerTools {

    public static void closeAll(ResultSet rs, Statement stat, Connection connection) {
        closeResult(rs);
        closeStatement(stat);
        closeConnection(connection);
    }

    public static PreparedStatement getPrepareStatement(String psql, Connection con) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(psql, 1003, 1007, 2);
        pstmt.setFetchSize(200);
        pstmt.setQueryTimeout(0);
        return pstmt;
    }

    public static BufferedWriter getBufferedWriter(String fileFullPath) throws IOException {
        OutputStream stream = new FileOutputStream(fileFullPath);
        OutputStreamWriter osw = new OutputStreamWriter(stream, "UTF-8");
        return new BufferedWriter(osw);
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