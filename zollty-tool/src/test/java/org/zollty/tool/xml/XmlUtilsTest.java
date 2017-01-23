package org.zollty.tool.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jretty.util.IOUtils;
import org.jretty.util.ResourceUtils;
import org.junit.Test;

public class XmlUtilsTest {

    @Test
    public void testXmlValidator() throws IOException {
        Reader in = new BufferedReader(new InputStreamReader(
                ResourceUtils.getClassPathResource("classpath:org/zollty/tool/xml/bean-test.xml").getInputStream()));
        String xmlStr = IOUtils.copyToString(in);

        XmlSaxErrorHandler ret = XmlUtils.xmlValidator(xmlStr,
                ResourceUtils.getClassPathResource("classpath:org/zollty/tool/xml/zolltymvc-beans-1.0.xsd").getFile());

        if (!ret.isSuccess()) {
            System.out.println(ret.getErrorMsg());
        } else {
            System.out.println("OK");
        }

    }

}