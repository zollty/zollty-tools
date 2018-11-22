package org.zollty.tool.dbdata.tofile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zollty 
 * @since 2013-11-16
 */
public class Line {
    private final int columnSize;
    private int currentSize;
    List<String> lineTempl = new ArrayList<String>();
    private String splitChar = ",";

    public Line(int columnSize) {
        this.columnSize = columnSize;
    }
    
    public Line(int columnSize, String splitChar) {
        this.columnSize = columnSize;
        this.splitChar = splitChar;
    }

    /**
     * 即csv新起一行
     */
    public static Line newLine(int columnSize) {
        return new Line(columnSize);
    }

    /**
     * append一个空数据列
     */
    public Line appendComma() {
        addSize();
        lineTempl.add(splitChar);
        return this;
    }

    /**
     * append N 个空数据列
     */
    public Line appendComma(int num) {
        for (int i = 0; i < num; i++) {
            addSize();
            lineTempl.add(splitChar);
        }
        return this;
    }

    /**
     * append一个字符数据列
     */
    public Line append(String s) {
        ++currentSize;
        if (currentSize < columnSize) {
            lineTempl.add(s);
            lineTempl.add(splitChar);
            return this;
        } else if (currentSize == columnSize) {
            lineTempl.add(s);
            return this;
        }
        throw new RuntimeException("数据越界，最大column = " + columnSize);
    }

    /**
     * 参数中可能包含英文逗号时，用双引号包装，比如 A,F,H -- "A,F,H" s.indexOf(',')!=-1
     */
    public Line appendWrapWithDQM(String s) {
        return append("\"" + s + "\"");
    }

    /**
     * 参数中可能包含会被Excel或WPS格式化的内容时（比如时间、数字等）， 用 双引号+\t 包装，比如 2013-11-01 -- "	2013-11-01"
     */
    public Line appendWrapWithHack(String s) {
        return append("\"\t" + s + "\"");
    }

    public Line append(Double d) {
        return append(d.toString());
    }

    public Line append(Integer d) {
        return append(d.toString());
    }

    public Line append(Character d) {
        return append(d.toString());
    }

    private void addSize() {
        if (++currentSize > columnSize) {
            throw new RuntimeException("数据越界，设定的最大column = " + columnSize);
        }
    }

    @Override
    public String toString() {
        int size = lineTempl.size();
        if (size == 0) {
            return "";
        }
        StringBuilder strb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            strb.append(lineTempl.get(i));
        }
        int remain = columnSize - currentSize - 1;
        for (int i = 0; i < remain; i++) {
            strb.append(splitChar);
        }
        strb.append("\r\n");
        return strb.toString();
    }
}