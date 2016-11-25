package org.zollty.tool;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.jretty.util.FileUtils;

public class MavenInstallJar {

    private static String[] getInfo(String jarName) {
        String name = jarName;
        String artifactId = null;
        String version = null;
        if (jarName.endsWith(".jar")) {
            name = name.replaceAll(".jar", "");
            String[] ns = name.split("-");
            version = ns[ns.length - 1];
            StringBuffer sb = new StringBuffer(ns[0]);
            for (int i = 1; i < ns.length - 1; i++) {
                sb.append("-").append(ns[i]);
            }
            artifactId = sb.toString();
        }
        return new String[] { artifactId, version };
    }
    
    private static String[] getJarInfo(String repoPath, String jarPath) {
        String name = jarPath.substring(repoPath.length() + 1);
        String groupId = null;
        String artifactId = null;
        String version = null;
        String arr[] = name.split("/");
        version = arr[arr.length-1];
        artifactId = arr[arr.length-2];
        groupId = arr[0];
        for(int i=1; i<arr.length-2; i++){
            groupId = groupId + "." + arr[i];
        }
        
        return new String[] { groupId, artifactId, version, jarPath+"/"+artifactId+"-"+version+".jar" };
    }
    
    private static String outputInstallJarScript(String jarInfo[]) {
        StringBuilder sb = new StringBuilder();
        sb.append("mvn install:install-file -DgroupId=").append(jarInfo[0]);
        sb.append(" -DartifactId=").append(jarInfo[1]);
        sb.append(" -Dversion=").append(jarInfo[2]);
        sb.append(" -Dfile=").append(jarInfo[3]);
        sb.append(" -Dpackaging=jar -DgeneratePom=true");      
        return sb.toString();
    }

    public static void main(String[] args) {
        String repoPath = "/home/zollty/soft/maven-repo";
        String path = "lightning-common/1.2.0";
        String jarPath = null;
        List<File> allFiles = FileUtils.loopFolders(new File("/home/zollty/soft/maven-repo"));
        System.out.println(allFiles.size());
        for (File file : allFiles) {
            String tmp = file.getAbsolutePath();
            if (tmp.endsWith(path)) {
                jarPath = tmp;
                break;
            }
        }
        if (jarPath != null) {
            String jarInfo[] = getJarInfo(repoPath, jarPath);
            System.out.println(Arrays.deepToString(jarInfo));
            System.out.println(outputInstallJarScript(jarInfo));
        }
        
    }
    


}