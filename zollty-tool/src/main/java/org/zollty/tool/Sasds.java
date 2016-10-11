package org.zollty.tool;

import org.zollty.tesper.LoopExecute;
import org.zollty.tesper.TestTools;

public class Sasds {
	
	public static void main(String[] args) throws Exception {
		
		TestTools.loopExecute(new LoopExecute() {
			int a=1;
			int b = 100000;
			@Override
			public int getLoopTimes() {
				return 100000000;
			}
			
			@Override
			public void execute() throws Exception {
				if(a==b){
					a=b;
				}
			}
		});
		
		
	}

}
