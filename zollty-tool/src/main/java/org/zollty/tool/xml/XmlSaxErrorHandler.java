package org.zollty.tool.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author zollty
 * @since 2012-12-17
 */
public class XmlSaxErrorHandler implements ErrorHandler {

    private boolean success = true;
    private String errorMsg;

    @Override
    public void error(SAXParseException ex) throws SAXException {
        success = false;
        errorMsg = ex.getMessage();
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        success = false;
        errorMsg = ex.getMessage();
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
        success = false;
        errorMsg = ex.getMessage();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}