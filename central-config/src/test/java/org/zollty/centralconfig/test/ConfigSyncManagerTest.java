/* 
 * Copyright (C) 2016-2017 the CSTOnline Technology Co.
 * Create by ZollTy on 2016-5-16 (zoutianyong@cstonline.com)
 */
package org.zollty.centralconfig.test;

import java.io.IOException;

import org.zollty.centralconfig.ConfigChangedLogListener;
import org.zollty.centralconfig.ConfigHolder;
import org.zollty.centralconfig.ConfigSyncManager;
import org.zollty.centralconfig.LocalPropsService;

/**
 * 
 * @author zollty
 * @since 2016-5-16
 */
public class ConfigSyncManagerTest {
    
    
    /**
     * GDCP Kafka 的consumer配置文件路径
     */
    private static final String GDCP_KAFKA_CONUSMER_CONFIG_PATH = "classpath:kafkaapi-config/gdcp-kafka-conusmer.properties";
    private static final String GDCP_KAFKA_CONUSMER_FILE_ID = "push/gdcp-kafka-conusmer.properties";
    
    public static void main(String[] args) throws Exception {
        
        LocalPropsService localPropsService = new LocalPropsService();
        
        try {
            localPropsService.setLocation(GDCP_KAFKA_CONUSMER_FILE_ID, GDCP_KAFKA_CONUSMER_CONFIG_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        long start = System.currentTimeMillis();
        ConfigSyncManager manger = new ConfigSyncManager(localPropsService).setChangedListener(new ConfigChangedLogListener());
        
        ConfigHolder configService = manger.getConfig();
        System.out.println("cost time "+(System.currentTimeMillis()-start));
        start = System.currentTimeMillis();
        
        System.out.println(configService.getPropertyByFile(GDCP_KAFKA_CONUSMER_FILE_ID,"ds"));
        System.out.println(configService.getProperty("aaaa"));
        
        manger.refreshConfig();
        System.out.println("cost time "+(System.currentTimeMillis()-start));
        
        configService = manger.getConfig();
        System.out.println(configService.getPropertyByFile(GDCP_KAFKA_CONUSMER_FILE_ID,"ds"));
        System.out.println(configService.getProperty("aaaa"));
    }


}
