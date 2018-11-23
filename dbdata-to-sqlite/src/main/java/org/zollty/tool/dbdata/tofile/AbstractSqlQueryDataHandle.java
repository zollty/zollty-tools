package org.zollty.tool.dbdata.tofile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.zollty.dbk.support.JdbcUtils;
import org.zollty.tool.dbdata.csv.Line;

public abstract class AbstractSqlQueryDataHandle {

    abstract public DataSource getDataSource();

    abstract public void handleData(ResultSet rs, int index) throws SQLException;

    public void execute() {
        long tm = System.currentTimeMillis();
        PreparedStatement pstmt = null;
        Connection con = null;
        ResultSet rs = null;
        DataSource ds = getDataSource();
        try {
            con = ds.getConnection();
            pstmt = InnerTools.getPrepareStatement(getStaticSql(), con);
            setParams(pstmt);
            rs = pstmt.executeQuery();
            
            int index = 1;
            while (rs.next()) {
                handleData(rs, index++);
            }
            System.out.println("handled "+ index +" items, cost: " + (System.currentTimeMillis() -tm) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cost: " + (System.currentTimeMillis() -tm) + " ms");
        } finally {
            InnerTools.closeAll(rs, pstmt, con);
        }
    }

    protected String parserData(ResultSet rs, int index, String splitChar) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Line result = new Line(columnCount, splitChar);
        Line header = new Line(columnCount, splitChar);
        if (index == 1) {
            for (int j = 1; j <= columnCount; j++) {
                String key = JdbcUtils.lookupColumnName(rsmd, j);
                header.append(key);
            }
        }
        for (int j = 1; j <= columnCount; j++) {
            Object obj = JdbcUtils.getResultSetValue(rs, j);
            if (obj != null) {
                if(obj instanceof java.sql.Timestamp) {
//                    result.append(UT.Date.format_yyyy_MM_dd_HH_mm_ss((Date)obj));
                    result.append(obj.toString());
                } else {
                    result.append(obj.toString());
                }
            }
        }
        return header.toString() + result.toString();
    }

    abstract public void setParams(PreparedStatement pstmt);

    abstract public String getStaticSql();

}
