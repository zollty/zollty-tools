package org.zollty.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jretty.util.Const;
import org.jretty.util.FileUtils;

/**
 * 
 * @author zollty
 * @since 2020年5月29日
 */
public class GenerateUpdateScript {


    public static void main(String[] args) {
        String currPath = System.getProperty("user.dir");
        StringBuilder sb = new StringBuilder();
        sb.append(":: update jar cache").append(System.lineSeparator());
        sb.append("@echo off").append(System.lineSeparator());
        sb.append("taskkill /f /t /im javaw.exe").append(System.lineSeparator());
        sb.append("rd /s /q \"").append(getPath(FileUtils.class)).append("\"").append(System.lineSeparator());
        writeStr2(currPath + "/" + "update.bat", sb.toString(), Const.UTF_8);
        open(currPath);
        System.out.println("请执行 update.bat");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void writeStr2(String fileFullPath, String str, String charSet) {
        BufferedWriter out = null;
        try {
            out = UT.File.getBufferedWriter(fileFullPath, false, charSet);
            out.write(str);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            UT.IO.closeIO(out);
        }
    }

    public static String getPath(Class clazz) {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        if (path.contains("jar")) {
            System.out.println(path);
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        System.out.println(path);
        return path;
    }
    
    
    /**
     * 
     * 
     * cmd /c dir 是执行完dir命令后关闭命令窗口.
     * cmd /k dir 是执行完dir命令后不关闭命令窗口.
     * cmd /c start dir 会打开一个新窗口后执行dir指令, 原窗口会关闭.
     * cmd /k start dir 会打开一个新窗口后执行dir指令, 原窗口不会关闭.
     * 
     * @param locationCmd
     * @param dir
     * @return
     */
    public static boolean callCommand(String locationCmd, File dir) {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            Process child;
            if (dir == null) {
                child = Runtime.getRuntime().exec(locationCmd, null);
            } else {
                child = Runtime.getRuntime().exec(locationCmd, null, dir);
            }
            InputStream in = child.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // sb.append(line + "\n");
                System.out.println(line);
            }
            in.close();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            System.out.println("sb:" + sb.toString());
            System.out.println("callCmd execute finished");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            UT.IO.close(bufferedReader);
        }
    }

    
    /**
     * 打开目录
     */
    public static boolean open(String dir) {
        String osName = System.getProperty("os.name");
        if (osName != null) {
            try {
                if (osName.contains("Mac")) {
                    Runtime.getRuntime().exec("open " + dir);
                    return true;
                } else if (osName.contains("Windows")) {
                    Runtime.getRuntime().exec("cmd /c start " + dir);
                    return true;
                }
            } catch (Exception e) {
                // ignore...
            }
        }
        return false;
    }


}
