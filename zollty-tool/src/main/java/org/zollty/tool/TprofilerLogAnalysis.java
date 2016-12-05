package org.zollty.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jretty.util.Const;
import org.jretty.util.FileUtils;
import org.jretty.util.StringSplitUtils;

public class TprofilerLogAnalysis {

    public static void main(String[] args) {
        int nline = 500;
        final String tprofilerPath = "/home/zollty/tprofiler.log";
        final String tmethodPath = "/home/zollty/tmethod.log";
        final String threadId = "98";
        final int maxLine = nline;

        final List<String> methodsid = FileUtils.parseTextFile(tprofilerPath, new FileUtils.TextFileParse<String>() {
            int i = 0;

            @Override
            public String parseOneLine(String line) {

                String[] ar = StringSplitUtils.splitIgnoreEmpty(line, "\t");
                if (ar.length > 3 && threadId.equals(ar[0])) {
                    if (i++ > maxLine) {
                        return null;
                    }
                    // System.out.println(line);
                    return ar[2];
                }

                return null;
            }
        }, Const.UTF_8);

        final List<String> methodscost = FileUtils.parseTextFile(tprofilerPath, new FileUtils.TextFileParse<String>() {
            int i = 0;

            @Override
            public String parseOneLine(String line) {
                String[] ar = StringSplitUtils.splitIgnoreEmpty(line, "\t");
                if (ar.length > 3 && threadId.equals(ar[0])) {
                    if (i++ > maxLine) {
                        return null;
                    }
                    return ar[3];
                }

                return null;
            }
        }, Const.UTF_8);

        final List<Map<String, String>> methodnames = FileUtils.parseTextFile(tmethodPath,
                new FileUtils.TextFileParse<Map<String, String>>() {

                    @Override
                    public Map<String, String> parseOneLine(String line) {
                        String[] ar = StringSplitUtils.splitIgnoreEmpty(line, " ");
                        if (ar.length > 1) {
                            // System.out.println(ar[0]);
                            for (String id : methodsid) {
                                if (id.equals(ar[0])) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put(id, ar[1]);
                                    return map;
                                }
                            }
                        }
                        return null;
                    }
                }, Const.UTF_8);

        System.out.println(methodnames.size());

        for (int i = 0; i < methodsid.size(); i++) {
            String id = methodsid.get(i);
            for (Map<String, String> name : methodnames) {
                if (name.containsKey(id)) {
                    System.out.println(name.get(id) + "|----" + methodscost.get(i));
                }
            }
        }

    }

}