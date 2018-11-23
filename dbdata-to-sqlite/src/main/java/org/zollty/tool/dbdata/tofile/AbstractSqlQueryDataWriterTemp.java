package org.zollty.tool.dbdata.tofile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.zollty.dbk.support.JdbcUtils;

public abstract class AbstractSqlQueryDataWriterTemp {
    
    abstract DataSource getDataSource();

    public void execute() {
        BufferedWriter outWrite = null;
        PreparedStatement pstmt = null;
        Connection con = null;
        ResultSet rs = null;

        DataSource ds = getDataSource();
        try {
            con = ds.getConnection();
            pstmt = InnerTools.getPrepareStatement(getStaticSql(), con);
            setParams(pstmt);
            rs = pstmt.executeQuery();

            outWrite = InnerTools.getBufferedWriter(getFilePath());
            
            writeTitleAndHead(outWrite);
            int index = 1;
            while (rs.next()) {
                outWrite.write(parserData(rs, index++));
            }
            outWrite.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outWrite != null) {
                try {
                    outWrite.close();
                } catch (IOException localIOException) {
                }
            }
            InnerTools.closeAll(rs, pstmt, con);
        }
    }
    
    private String parserData(ResultSet rs, int index) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Line result = new Line(columnCount);
        if (index == 1) {
            for (int j = 1; j <= columnCount; j++) {
                String key = JdbcUtils.lookupColumnName(rsmd, j);
                result.append(key);
            }
        }
        for (int j = 1; j <= columnCount; j++) {
            Object obj = JdbcUtils.getResultSetValue(rs, j);
            result.append(obj.toString());
        }
        return result.toString();
    }

    abstract void writeTitleAndHead(BufferedWriter outWrite) throws IOException;

    abstract void setParams(PreparedStatement pstmt);

    abstract String getStaticSql();
    
    abstract String getFilePath();
    
}
