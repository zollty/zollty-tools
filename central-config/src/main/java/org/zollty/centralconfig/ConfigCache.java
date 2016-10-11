/* 
 * Copyright (C) 2016-2017 the CSTOnline Technology Co.
 * Create by ZollTy on 2016-5-16 (zoutianyong@cstonline.com)
 */
package org.zollty.centralconfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author zollty
 * @since 2016-5-16
 */
public class ConfigCache implements ConfigHolder {
    
    Map<String, Properties> configPropsMap = new HashMap<String, Properties>();
    Properties configPropsAll = new Properties();

    /**
     * 通过复制对象引用获取已合并处理的 properties
     * 
     * @param props
     */
    public void loadProperties(Properties props) {
        props.putAll(configPropsAll);
    }

    /**
     * 通过key值获取已处理后 properties 单个属性值
     * 
     * @param key
     * @return value
     */
    public String getProperty(String key) {
        return configPropsAll.getProperty(key);
    }

    /**
     * 通过key值和默认值获取已处理properties单个属性值
     * 
     * @param key
     * @param def
     * @return
     */
    public String getProperty(String key, String def) {
        return configPropsAll.getProperty(key, def);
    }

    @Override
    public void loadProperties(String file, Properties props) {
        props.putAll(configPropsMap.get(file));
    }

    @Override
    public String getPropertyByFile(String file, String key) {
        return configPropsMap.get(file).getProperty(key);
    }
}
