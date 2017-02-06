package org.zollty.tool.web;

import javax.servlet.http.HttpServletRequest;

public class WebTools {
    
    public static boolean isMSIEBrowser(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        boolean rtn = false;
        if (userAgentString != null && userAgentString.indexOf("MSIE") != -1) {
            rtn = true;
        }
        return rtn;
    }

}
