/**
 * 
 */
package org.zollty.tool;

import org.jretty.tesper.LoopExecute;
import org.jretty.tesper.TestTools;
import org.zollty.tool.file.FileTools;

/**
 * 
 * @author zollty
 * @since 2018年2月26日
 */
public class FileToolsTest {
    
    public static void main(String[] args) throws Exception {
        final String template = "D:\\0sync-local\\git\\fast\\fast-demo-mini";
        final String target = "D:\\0sync-local\\git\\fast\\temp\\aaa";
        
        TestTools.loopExecute(new LoopExecute() {
            
            @Override
            public int getLoopTimes() {
                
                return 100;
            }
            
            @Override
            public void execute() throws Exception {
                //  167.67 ms/n
                FileTools.copyDirectory(template, target, null, true, false);
            }
        });
    }

}
