package org.zollty.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FindLargeFiles {

    public static void searchAllFile(List<String> result, String rootDir, int sizeKb) {

        File rootFile = new File(rootDir);

        if (!rootFile.isDirectory()) {
            return;
        }

        // 获取源文件夹当前下的文件或目录
        File[] file = rootFile.listFiles();
        if (file.length == 0) {
            return;
        }

        List<File> tmpDirFile = new ArrayList<File>();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isDirectory()) {
                tmpDirFile.add(file[i]);
            } else if ((file[i].length() / 1024) > sizeKb) {
                result.add(file[i].getAbsolutePath());
            }
        }

        for (int i = 0; i < tmpDirFile.size(); i++) {
            String newRootDir = rootDir + "/" + tmpDirFile.get(i).getName();
            searchAllFile(result, newRootDir, sizeKb);
        }

    }

    public static void main(String[] args) {
        List<String> result = new ArrayList<>();
        searchAllFile(result, "D:/0sync-local/workspace/zoa-static", 100);
        for(String str: result) {
            System.out.println(str);
        }
    }

}
