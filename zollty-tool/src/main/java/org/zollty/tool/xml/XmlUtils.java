package org.zollty.tool.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.StringUtils;
import org.xml.sax.SAXException;

/**
 * XML工具类
 * 
 * @author zollty
 * @since 2012-12-17
 */
public class XmlUtils {

    private static final Logger LOG = LogFactory.getLogger();
    public static final String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";    
    public static SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    
    private XmlUtils(){
    }
    
    /**
     * 
     * @param xmlStr
     * @param xsdSource
     * @return 调用XmlSaxErrorHandler的isSuccess方法和getErrorMsg获取解析结果
     */
    public static XmlSaxErrorHandler xmlValidator(String xmlStr,
            Source xsdSource) {
        XmlSaxErrorHandler errorHandler = new XmlSaxErrorHandler();
        if (StringUtils.isBlank(xmlStr)) {
            errorHandler.setSuccess(false);
            errorHandler.setErrorMsg("传入的xml参数错误");
            return errorHandler;
        }
        InputStream xmlInStream = null;
        try {
            xmlInStream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
            StreamSource xmlSource = new StreamSource(xmlInStream);
            return xmlValidator(xmlSource, xsdSource);
        } catch (UnsupportedEncodingException e) {
            errorHandler.setSuccess(false);
            errorHandler.setErrorMsg("无法进行utf-8编码：" + e.getMessage());
            return errorHandler;
        }finally{
            if (null != xmlInStream) {
                try {
                    xmlInStream.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }       
        }
    }

    /**
     * 
     * @param xmlStr
     * @param xsdFile
     * @return 调用XmlSaxErrorHandler的isSuccess方法和getErrorMsg获取解析结果
     */
    public static XmlSaxErrorHandler xmlValidator(String xmlStr, File xsdFile) {
        XmlSaxErrorHandler errorHandler = new XmlSaxErrorHandler();
        if (xsdFile == null || !xsdFile.isFile()) {
            errorHandler.setSuccess(false);
            errorHandler.setErrorMsg("传入的xsd参数错误");
            return errorHandler;
        }
        return xmlValidator(xmlStr, new StreamSource(xsdFile));
    }

    /**
     * 
     * @param xmlSource
     * @param xsdSource
     * @return 调用XmlSaxErrorHandler的isSuccess方法和getErrorMsg获取解析结果
     */
    public static XmlSaxErrorHandler xmlValidator(Source xmlSource,
            Source xsdSource) {

        XmlSaxErrorHandler errorHandler = new XmlSaxErrorHandler();
        try {
            Schema schema = factory.newSchema(xsdSource);
            Validator validator = schema.newValidator();
            validator.setErrorHandler(errorHandler);
            validator.validate(xmlSource);

        } catch (SAXException e) {
            errorHandler.setSuccess(false);
            errorHandler.setErrorMsg("无法验证xml文件，可能是xml的基本格式不正确！具体原因："
                    + e.getMessage());
        } catch (IOException e) {
            errorHandler.setSuccess(false);
            errorHandler.setErrorMsg("无法验证xml文件，IO异常，具体原因：" + e.getMessage());
        }

        return errorHandler;
    }

}