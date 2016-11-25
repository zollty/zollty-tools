package org.zollty.tool;

import org.junit.Assert;
import org.junit.Test;
import org.jretty.util.RandomUtils;

public class CommonToolsTests {

	@Test
	public void toPositiveTest() {

		for (int i = 0; i < 10000; i++) {
			int rn = 0 - RandomUtils.nextInt(10000000);

			Assert.assertTrue(rn < 0);

			int ret = CommonTools.toPositive(rn);

			Assert.assertTrue(ret > 0);
		}
	}

}