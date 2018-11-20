/**
 * 
 */
package org.zollty.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jretty.util.FileUtils;
import org.jretty.util.StringUtils;

/**
 * 
 * @author zollty
 * @since 2018年1月28日
 */
public class SubTxParser3 {
    
    public static final String TAG_TITLE = "题干=";
    public static final String TAG_ANSWER = "答案=";
    public static final String TAG_ANALYSIS = "分析=";
    public static final String TAG_COMMENT = "点评=";
    
    public static final String OPTION_TAG = "=%";
    
    public static final String ANS_UNDL = "=u";
    public static final String ANS_BLK = "=b";
    public static final String ANS_LINE = "=l";
    
    public static final String ANS_NUM_END = "%";
    public static final String ANS_END = "%%";
    
    public static final String START_TAG = TAG_TITLE;
    public static Set<String> endTag = new HashSet<>();
    static {
        //endTag.add(TAG_TITLE);
        endTag.add(TAG_ANSWER);
        endTag.add(TAG_ANALYSIS);
        endTag.add(TAG_COMMENT);
    }
    
    public static Subject subject;
    
    public static boolean isEnd(String line) {
        for(String tag: endTag) {
            if(tag.equals(line)) {
                return true;
            }
        }
        return false;
    }
    
    public static void normal(int num, List<Integer> list) {
        if(num != -1) {
            list.add(num);
        }
    }
    
    public static void parsePart(String str, String tag) {
        if(TAG_ANSWER.equals(tag)) {
            System.out.println("答案：");
            System.out.println(str);
        } else if(TAG_ANALYSIS.equals(tag)) {
            System.out.println("分析：");
            System.out.println(str);
        } else if(TAG_COMMENT.equals(tag)) {
            System.out.println("点评：");
            System.out.println(str);
        }
    }
    
    public static void parseOne(String str) {
        List<Integer> list = new ArrayList<Integer>();
        Map<Integer, String> map = new HashMap<Integer, String>();
        
        int num = str.indexOf(TAG_ANSWER);
        if(num != -1) {
            list.add(num);
            map.put(num, TAG_ANSWER);
        }
        
        num = str.indexOf(TAG_ANALYSIS);
        if(num != -1) {
            list.add(num);
            map.put(num, TAG_ANALYSIS);
        }
        
        num = str.indexOf(TAG_COMMENT);
        if(num != -1) {
            list.add(num);
            map.put(num, TAG_COMMENT);
        }
        
        if(list.isEmpty()) {
            parseTitle(str);
        }
        
        Collections.sort(list);
        
        // sfk[12]fskj[34]jhjgj[7]
        // start = 0;
        // start = index
        int start = 0;
        String tmp = null;
        int len = str.length();
        int last = 0;
        for(int index: list) {
            tmp = str.substring(0, index - start);
            tmp = StringUtils.beforeLastIndex(tmp, "<p>");
            if(start == 0) {
                parseTitle(tmp);
            } else {
                parsePart(tmp, map.get(last));
            }
            
            str = str.substring(index - start + TAG_ANSWER.length(), str.length());
            str = StringUtils.afterIndex(str, "</p>");
            
            start = len - str.length();
            last = index;
        }
        parsePart(str, map.get(last));
    }
    
    public static void parseTitle(String str) {
        System.out.println("题目：");
        System.out.println(str);
    }
    
    public static void parseNext(String str) {
        // ~ 取得 title_tag 后面的正文内容
        str = StringUtils.afterIndex(str, TAG_TITLE);
        if (str == null) {
            return;
        }
        str = StringUtils.afterIndex(str, "</p>");
        if (str == null) {
            System.err.println("no </p> tag!");
            return;
        }
        
        // ~ 取得下一个 title_tag 位置（如果有）
        String middle = StringUtils.beforeIndex(str, TAG_TITLE);
        if (middle == null) {
            // TODO 处理这个 body
            System.out.println("No next title_tag, result：");
//            System.out.println(str);
            parseOne(str);
            return;
        } else {
            // 剥析出 body内容
            middle = StringUtils.beforeLastIndex(middle, "<p>");
            System.out.println("Find next title_tag, this result：");
            // TODO 处理这个 body
//            System.out.println(middle);
            parseOne(middle);
            
            // TODO 处理下一个
            System.out.println("Parse Next is: ");
            parseNext(str);
            return;
        }
    }

    public static void parseSubTxFile(String textFullPath) throws IOException {

        File textFile = Paths.get(textFullPath).toFile();
        
        if (!textFile.exists()) {
            return;
        }

        List<String> lines = FileUtils.getTextFileContent(new FileInputStream(textFile));
        StringBuilder sbu = new StringBuilder();
        for(String line : lines) {
            sbu.append(line.trim());
            //System.out.println(line);
        }
        String str = sbu.toString();
        System.out.println(str);
        sbu = null;
        lines = null;
        
        parseNext(str);
        
        // ~ 取得 title_tag 后面的正文内容
//        str = StringUtils.afterIndex(str, TAG_TITLE);
//        if (str == null) {
//            return;
//        }
//        str = StringUtils.afterIndex(str, "</p>");
//        if (str == null) {
//            System.err.println("no </p> tag!");
//            return;
//        }
//        
//        // ~ 取得下一个 title_tag 位置（如果有）
//        String middle = StringUtils.beforeIndex(str, TAG_TITLE);
//        if (middle == null) {
//            // TODO 处理这个 body
//            System.out.println("No next title_tag, result：");
//            System.out.println(str);
//            return;
//        } else {
//            // 剥析出 body内容
//            middle = StringUtils.beforeLastIndex(middle, "<p>");
//            System.out.println("Find next title_tag, this result：");
//            // TODO 处理这个 body
//            System.out.println(middle);
//            
//            // TODO 处理下一个
//            System.out.println("Next is: ");
//            System.out.println(str);
//            return;
//        }
        
        
        
        
        
//        List<String> subTx = new ArrayList<String>();
//        boolean start = false;
//        for(String line : lines) {
//            if(!start) {
//                if(START_TAG.equals(line.trim())) {
//                    start = true;
//                }
//                continue;
//            } else if(START_TAG.equals(line.trim())) {
//                parseSubTx(new ArrayList<String>(subTx));
//                System.out.println("\n\n");
//                System.out.println(JSONUtils.toJSONString(ReflectionUtils.toMap(subject)));
//                System.out.println("\n\n");
//                subTx = new ArrayList<String>();
//                start = false;
//                continue;
//            } else {
//                subTx.add(line);
//            }
//        }
//        if (start && subTx.size() > 0) {
//            parseSubTx(new ArrayList<String>(subTx));
//            System.out.println("\n\n");
//            System.out.println(JSONUtils.toJSONString(ReflectionUtils.toMap(subject)));
//            System.out.println("\n\n");
//            subTx = new ArrayList<String>();
//            start = false;
//        }

    }
    
    
    public static void parseSubTx(List<String> lines) throws IOException {
        subject = new Subject();
        List<String> tmp = new ArrayList<String>();
        boolean parseContent = false;
        String tag = null;
        for (String line : lines) {
            if(!parseContent) {
                if (isEnd(line.trim())) {
                    tag = line.trim();
                    parseContent(new ArrayList<String>(tmp));
                    tmp = new ArrayList<String>();
                    parseContent = true;
                } else {
                    tmp.add(line);
                }
            } else if(isEnd(line.trim())) {
                if(TAG_ANSWER.equals(tag)) {
                    parseAnswer(tmp);
                } else if(TAG_ANALYSIS.equals(tag)) {
                    addAnalysis(tmp);
                } else if(TAG_COMMENT.equals(tag)) {
                    addComment(tmp);
                }
                tag = line.trim();
                tmp = new ArrayList<String>();
                continue;
            } else {
                tmp.add(line);
            }
        }
        if (!tmp.isEmpty()) {
            if(TAG_ANSWER.equals(tag)) {
                parseAnswer(tmp);
            } else if(TAG_ANALYSIS.equals(tag)) {
                addAnalysis(tmp);
            } else if(TAG_COMMENT.equals(tag)) {
                addComment(tmp);
            }
        }
    }
    
    public static void parseContent(List<String> content) {
        List<String> title = new ArrayList<String>();
        List<String> item = new ArrayList<String>();
        boolean endTitle = false;
        for (String line : content) {
            if (!endTitle) {
                if (line.trim().startsWith(OPTION_TAG)) {
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
    
    public static void addComment(List<String> lines) {
        System.out.println("点评：");
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
        subject.setComment(sbu.toString());
        System.out.println(subject.getComment());
        System.out.println();
    }
    
    public static void addAnalysis(List<String> lines) {
        System.out.println("分析：");
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
        subject.setAnalysis(sbu.toString());
        System.out.println(subject.getAnalysis());
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
        System.out.println("答案：");
        StringBuilder sbu = new StringBuilder();
        boolean notStart = false;
        for(String line : answer) {
            if(notStart) {
                sbu.append("\n").append(line);
            } else {
                sbu.append(line);
                notStart = true;
            }
        }
        subject.setAnswer(sbu.toString());
        System.out.println(subject.getAnswer());
        System.out.println();
        System.out.println("答题卡：");
        stripAnswer(subject.getAnswer());
        System.out.println();
    }
    
    public static void parseAnswer2(List<String> answer) {
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
        if ((idxB = str.indexOf(ANS_LINE)) > -1) {
            tmp = str.substring(idxB, str.length());
            idxE = tmp.indexOf(ANS_NUM_END);
            num = str.substring(idxB + 2, idxB + idxE);
            idxE = tmp.indexOf(ANS_END);
            //System.out.println(str.substring(idxB, idxB + idxE + 2));
            tmp = str.substring(0, idxB);
            for(int i=0; i<Integer.parseInt(num); i++) {
                tmp += "\n";
            }
            tmp += str.substring(idxB + idxE + 2, str.length());
//            System.out.println(tmp);
            stripAnswer(tmp);
            str = tmp;
        } else if ((idxB = str.indexOf(ANS_BLK)) > -1) {
            tmp = str.substring(idxB, str.length());
            idxE = tmp.indexOf(ANS_NUM_END);
            num = str.substring(idxB + 2, idxB + idxE);
            idxE = tmp.indexOf(ANS_END);
            //System.out.println(str.substring(idxB, idxB + idxE + 2));
            tmp = str.substring(0, idxB);
            for(int i=0; i<Integer.parseInt(num); i++) {
                tmp += " ";
            }
            tmp += str.substring(idxB + idxE + 2, str.length());
//            System.out.println(tmp);
            stripAnswer(tmp);
            str = tmp;
        } else if ((idxB = str.indexOf(ANS_UNDL)) > -1) {
            // 类似于 
            // (1)[_4]A[] (2)[_4]C[]
            // 或者
            // (1)=u4%A%%，(2)=u4%C%%
            tmp = str.substring(idxB, str.length());
            idxE = tmp.indexOf(ANS_NUM_END);
            num = str.substring(idxB + 2, idxB + idxE);
            idxE = tmp.indexOf(ANS_END);
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
//            parseSubTxFile("D:/5temp/teststr/str5.txt");
            parseSubTxFile("D:\\1sync\\subject\\content\\2v6xbz.txt");
            System.out.println("-----");
//            parseSubTxFile("D:/Users/admin/Desktop/teststr/str02.txt");
//            System.out.println("-----");
//            parseSubTxFile("D:/Users/admin/Desktop/teststr/str4.txt");
//            System.out.println("-----");
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
