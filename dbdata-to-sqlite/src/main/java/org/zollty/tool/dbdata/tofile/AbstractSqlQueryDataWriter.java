package org.zollty.tool.dbdata.tofile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public abstract class AbstractSqlQueryDataWriter extends AbstractSqlQueryDataHandle {
    
    protected BufferedWriter outWrite;

    abstract public DataSource getDataSource();
    
    abstract String getFilePath();
    
    abstract void writeTitleAndHead(BufferedWriter outWrite) throws IOException;
    
    public void execute() {
        try {
            outWrite = InnerTools.getBufferedWriter(getFilePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (outWrite != null) {
            try {
                writeTitleAndHead(outWrite);
                super.execute();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    outWrite.flush();
                    outWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handleData(ResultSet rs, int index) throws SQLException {
        String line = parserData(rs, index, ",");
        try {
            outWrite.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract public void setParams(PreparedStatement pstmt);

    abstract public String getStaticSql();

}