/**
 * 
 */
package org.zollty.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jretty.util.FileUtils;
import org.jretty.util.ReflectionUtils;
import org.jretty.util.json.JSONUtils;

/**
 * 
 * @author zollty
 * @since 2018年1月28日
 */
public class SubTxParser {
    
    
    public static Subject subject;

    public static void parseSubTxFile(String textFullPath) throws IOException {

        File textFile = Paths.get(textFullPath).toFile();
        
        if (!textFile.exists()) {
            return;
        }

        List<String> lines = FileUtils.getTextFileContent(new FileInputStream(textFile));
        
        List<String> subTx = new ArrayList<String>();
        String tmpStr;
        boolean start = false;
        for(String line : lines) {
            if(!start) {
                if(line.startsWith("/--/")) {
                    start = true;
                    tmpStr = line.substring(4);
                    if (tmpStr.length() > 0) {
                        subTx.add(tmpStr);
                    }
                }
                continue;
            } else if(line.endsWith("\\--\\")) {
                subTx.add(line.substring(0, line.length()-4));
                
                parseSubTx(new ArrayList<String>(subTx));
                System.out.println("\n\n");
                System.out.println(JSONUtils.toJSONString(ReflectionUtils.toMap(subject)));
                System.out.println("\n\n");
                subTx = new ArrayList<String>();
                start = false;
                continue;
            } else {
                subTx.add(line);
            }
        }
        

    }
    
    public static void parseSubTx(List<String> subTx) throws IOException {
        subject = new Subject();
        List<String> tmp = new ArrayList<String>();
        for (String line : subTx) {
            // System.out.println(line);
            if (line.startsWith("/an/")) {
                parseContent(new ArrayList<String>(tmp));
                tmp = new ArrayList<String>();
            } else {
                tmp.add(line);
            }
        }
        if (!tmp.isEmpty()) {
            parseAnswer(tmp);
        }
    }
    
    public static void parseContent(List<String> content) {
        List<String> title = new ArrayList<String>();
        List<String> item = new ArrayList<String>();
        boolean endTitle = false;
        for (String line : content) {
            if (!endTitle) {
                if (line.startsWith("[i]")) {
                    endTitle = true;
                    item.add(line);
                } else {
                    title.add(line);
                }
            } else {
                item.add(line);
            }
        }
        addTitle(title);
        addItem(item);
    }
    
    public static void addItem(List<String> lines) {
        if(lines.isEmpty()) {
            return;
        }
        System.out.println("选项：");
        StringBuilder sbu = new StringBuilder();
        boolean notStart = false;
        for(String line : lines) {
            if(notStart) {
                sbu.append("\n").append(line);
            } else {
                sbu.append(line);
                notStart = true;
            }
        }
        subject.setOption(sbu.toString());
        System.out.println(subject.getOption());
        System.out.println();
    }
    
    public static void addTitle(List<String> lines) {
        System.out.println("题目：");
        StringBuilder sbu = new StringBuilder();
        boolean notStart = false;
        for(String line : lines) {
            if(notStart) {
                sbu.append("\n").append(line);
            } else {
                sbu.append(line);
                notStart = true;
            }
        }
        subject.setContent(sbu.toString());
        System.out.println(subject.getContent());
        System.out.println();
    }
    
    public static void parseAnswer(List<String> answer) {
        //System.out.println("答案：");
        StringBuilder sbu = new StringBuilder();
        for(String line : answer) {
            sbu.append(line).append("\n");
            //System.out.println(line);
        }
        String str = sbu.toString();
        str = str.substring(0, str.length()-1);
        int[] ret = stripSign(str, "[析]");
        if(ret!=null) {
            String xi = str.substring(ret[0], ret[1]);
            String tmp = xi.substring(3, xi.length() - 2).trim();
            subject.setAnalysis(tmp);
            //System.out.println(xi);
            str = str.substring(0, ret[0])+str.substring(ret[1]+1, str.length());
        }
        ret = stripSign(str, "[评]");
        if(ret!=null) {
            String ping = str.substring(ret[0], ret[1]);
            String tmp = ping.substring(3, ping.length() - 2).trim();
            subject.setComment(tmp);
            //System.out.println(ping);
            str = str.substring(0, ret[0])+str.substring(ret[1]+1, str.length());
        }
        str = str.substring(0, str.length()-1);
        System.out.println("答案：");
        System.out.println(str);
        subject.setAnswer(str);
        System.out.println("答题卡：");
        stripAnswer(str);
        
        
        System.out.println();
        System.out.println("分析：");
        System.out.println(subject.getAnalysis());
        System.out.println("点评：");
        System.out.println(subject.getComment());
    }
    
    public static int[] stripSign(String str, String sign) {
        int[] ret = new int[2];
        String tmp;
        int idxB, idxE;
        if ((idxB = str.indexOf(sign)) > -1) {
            ret[0] = idxB;
            tmp = str.substring(idxB, str.length());
            idxE = tmp.indexOf("[]");
            ret[1] = idxB + idxE + 2;
            return ret;
        }
        return null;
    }
    
    public static void stripAnswer(String str) {
        int idxB, idxE;
        String tmp, num;
        if ((idxB = str.indexOf("[_L")) > -1) {
            tmp = str.substring(idxB, str.length());
            idxE = tmp.indexOf("]");
            num = str.substring(idxB + 3, idxB + idxE);
            idxE = tmp.indexOf("[]");
            //System.out.println(str.substring(idxB, idxB + idxE + 2));
            tmp = str.substring(0, idxB);
            for(int i=0; i<Integer.parseInt(num); i++) {
                tmp += "\n";
            }
            tmp += str.substring(idxB + idxE + 2, str.length());
//            System.out.println(tmp);
            stripAnswer(tmp);
            str = tmp;
        } else if ((idxB = str.indexOf("[_(")) > -1) {
            tmp = str.substring(idxB, str.length());
            idxE = tmp.indexOf("]");
            num = str.substring(idxB + 3, idxB + idxE);
            idxE = tmp.indexOf("[]");
            //System.out.println(str.substring(idxB, idxB + idxE + 2));
            tmp = str.substring(0, idxB);
            for(int i=0; i<Integer.parseInt(num); i++) {
                tmp += " ";
            }
            tmp += str.substring(idxB + idxE + 2, str.length());
//            System.out.println(tmp);
            stripAnswer(tmp);
            str = tmp;
        } else if ((idxB = str.indexOf("[_")) > -1) {
            tmp = str.substring(idxB, str.length());
            idxE = tmp.indexOf("]");
            num = str.substring(idxB + 2, idxB + idxE);
            idxE = tmp.indexOf("[]");
            //System.out.println(str.substring(idxB, idxB + idxE + 2));
            tmp = str.substring(0, idxB);
            for(int i=0; i<Integer.parseInt(num); i++) {
                tmp += "_";
            }
            tmp += str.substring(idxB + idxE + 2, str.length());
//            System.out.println(tmp);
            stripAnswer(tmp);
            str = tmp;
        } else {
            System.out.println(str);
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            parseSubTxFile("D:/5temp/题库/subject.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        List<String> ss = FileUtils.getTextFileContent(new FileInputStream("D:/D/maven-repo/com/baomidou/mybatis-plus/2.1.8/subject.txt"));
//        System.out.println(ss);
//        List<String> co = FileUtils.getTextFileContent(new FileInputStream("‪D:/D/subject.txt"));
//        System.out.println(co);
//        List<File> ls = FileUtils.loopFolders(new File("‪D:\\Users\\Desktop"));
//        System.out.println(ls);
        
        
        
//        Path pp = Paths.get("‪C:\\");
//        
//        File rootFile = pp.toFile();//new File("‪C:\\");
//
//        System.out.println(rootFile.isFile());
//        System.out.println(rootFile.isDirectory());
//        if (!rootFile.isDirectory()) {
//            return;
//        }
//
//        // 获取源文件夹当前下的文件或目录
//        File[] file = rootFile.listFiles();
//        if (file.length == 0) {
//            return;
//        }
//        for (int i = 0; i < file.length; i++) {
//            System.out.println(file[i].getPath());
//        }
//        
//        try {
//            parseSubTxFile("‪D:\\Users\\Desktop\\subject.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
