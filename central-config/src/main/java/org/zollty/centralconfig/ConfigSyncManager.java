/* 
 * Copyright (C) 2016-2017 the CSTOnline Technology Co.
 * Create by ZollTy on 2016-5-16 (zoutianyong@cstonline.com)
 */
package org.zollty.centralconfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 方案一：每次获取ConfigSyncManager时就刷新一次配置
 * 方案二：第一次获取ConfigSyncManager时才刷新一次配置，以后使用旧配置
 * 
 * 采用方案二，每次使用new新对象
 * 
 * @author zollty
 * @since 2016-5-16
 */
public class ConfigSyncManager extends AbstractConfigSyncManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConfigSyncManager.class);
    protected static final int sessionTimeoutMs = 12000;
    
    // 根据路径层级读取各层级配置生成value为properties的map集合
    private TreeMap<String, Properties> propsMap = new TreeMap<String, Properties>();
    
    private String randomPath = UUID.randomUUID().toString();
    
    protected Map<String, ConfigChangedHandler> configChangedHandlerMap = new HashMap<String, ConfigChangedHandler>();
    protected ConfigChangedListener changedListener;
    protected ConfigCache configCache;
    
    public ConfigSyncManager(LocalPropsService localPropsService) {
        this.localPropsService = localPropsService;
        
        initial();
    }
    
    public ConfigSyncManager setChangedListener(ConfigChangedListener changedListener) {
        this.changedListener = changedListener;
        return this;
    }
    
    private void startIfClient() {
        // 根据IP及端口连接ZooKeeper
        if(client == null) {
            client = CuratorFrameworkFactory.builder()
                    .connectString(zookeeperUrl)
                    .sessionTimeoutMs(sessionTimeoutMs)
                    .retryPolicy(new RetryNTimes(4, 2000))
                    .build();
            client.start();
        }
    }
    
    /**
     * 启动客户端数据交换，并阻塞至初始化完成（或超时）
     */
    protected void initial() {
        
        this.localPropsService.ensureInitial();
        
        EnsurePath ensurePath = null;
        try {
            LOG.info("启动加载配置");
            // 客户端启动时判断客户端属性等,setter>local>default值
            checkClientConfig();
            detectLocalIp();
            
            this.startIfClient();
            
            // 版本校验(同级别,接近当前版本的最新版本)
            checkVersion();
            
            String tempPath = "";
            // 根据层级等封装路径,若路径不存在则自动创建
            Iterator<?> iterator = levels.keySet().iterator();
            while (iterator.hasNext()) {
                String l = levels.get(iterator.next());
                if(l == null || l.isEmpty()) continue;
                tempPath = tempPath + "/" + l;
                propsMap.put(propsPathPrefix + tempPath, new Properties());
            }
            // 配置路径
            String configFilepath = tempPath;
            // 确认配置路径,若无则创建目录等
            ensurePath = new EnsurePath(propsPathPrefix + configFilepath);
            ensurePath.ensure(client.getZookeeperClient());
            
            for(String configFilename: configFileNameList) {
                usingPath = usingPathPrefix + tempPath + "/" + configFilename;
                ensurePath = new EnsurePath(usingPath);
                ensurePath.ensure(client.getZookeeperClient());
            }

            // 客户端启动时加载,读取且保存当前配置属性值
            // refreshConfig();
            LOG.info("完成加载配置");
        } catch (Exception exception) {
            LOG.error("加载配置异常", exception);
        } finally {
            this.stop();
        }
    }


    /**
     * 客户端启动时初始化,读取且保存当前配置属性值
     * 
     * @throws Exception
     */
    public synchronized void refreshConfig() throws Exception {
        try {
            this.startIfClient();
            
            // 初始化 configCache
            configCache = new ConfigCache();

            for (String configFilename : configFileNameList) {
                Properties remoteProps = new Properties();
                Iterator<?> iterator = propsMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String configPath = (String) iterator.next();
                    String tempPath = configPath + "/" + configFilename;
                    // 判断当前配置存在与否,若不存在则继续循环
                    if (null == client.checkExists().forPath(tempPath)) {
                        continue;
                    }
                    byte[] data = client.getData().forPath(tempPath);
                    if (null == data || data.length == 0) {
                        continue;
                    }
                    Properties tempProps = new Properties();
                    tempProps.load(new ByteArrayInputStream(data));
                    // 判断properties,若为空则继续循环
                    if (tempProps.size() == 0) {
                        continue;
                    }

                    remoteProps.putAll(tempProps);
                    // 初始化时将配置保存至propsMap集合
                    propsMap.put(configPath, tempProps);
                }

                // 根据设置的优先级别进行配置属性的添加或覆盖等
                setConfigProps(configFilename, remoteProps);

                // remotePropsList.add(remoteProps);
                // 比较配置及回调通知等
                if (configChangedHandlerMap.get(configFilename) != null) {
                    configChangedHandlerMap.get(configFilename).configChanged(remoteProps);
                } else {
                    ConfigChangedHandler configChangedHandler = new ConfigChangedHandler(configFilename,
                            changedListener);
                    configChangedHandler.configChanged(remoteProps);
                    configChangedHandlerMap.put(configFilename, configChangedHandler);
                }
            }

            // 保存正在使用的配置属性等
            saveConfigPropsToZookeeper();

        } finally {
            this.stop();
        }
    }

    /**
     * 保存配置
     * 
     * @throws Exception
     */
    private void saveConfigPropsToZookeeper() {
        Properties props = new Properties();

        for (Map.Entry<String, Properties> entry : configCache.configPropsMap.entrySet()) {
            props.putAll(entry.getValue());
        }

        // String ip = InetAddress.getLocalHost().getHostAddress();
        ByteArrayOutputStream fos = null;
        try {
            fos = new ByteArrayOutputStream();
            props.store(fos, "IP: " + localip);
            String tempPath = usingPath + "/" + randomPath;
            if (client.checkExists().forPath(tempPath) == null) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(tempPath, fos.toByteArray());
            } else {
                client.setData().forPath(tempPath, fos.toByteArray());
            }
        } catch (Exception e) {
            LOG.error("saveConfigPropsToZookeeper error due to ", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 根据设置的优先级别进行配置属性的添加或覆盖等
     */
    protected void setConfigProps(String configFilename, Properties remoteProps) {
        Properties tmp = new Properties();
        switch (priority) {
        case 1: // 读取远程配置
            LOG.info("配置优先级别:读取远程配置-{}", configFilename);
            tmp = remoteProps;
            break;
        case 2: // 读取本地配置
            LOG.info("配置优先级别:读取本地配置-{}", configFilename);
            tmp = localPropsService.getLocalProps(configFilename);
            break;
        case 4: // 本地覆盖远程
            LOG.info("配置优先级别:本地覆盖远程-{}", configFilename);
            tmp = new Properties();
            tmp.putAll(remoteProps);
            Properties tp = localPropsService.getLocalProps(configFilename);
            if (tp != null) {
                tmp.putAll(tp);
            }
            break;
        default:
            // 远程覆盖本地
            LOG.info("配置优先级别:远程覆盖本地-{}", configFilename);
            tmp = new Properties();
            Properties tpp = localPropsService.getLocalProps(configFilename);
            if (tpp != null) {
                tmp.putAll(tpp);
            }
            tmp.putAll(remoteProps);
            break;
        }
        
        configCache.configPropsMap.put(configFilename, tmp);

        configCache.configPropsAll.putAll(tmp);
    }

    /**
     * 停止客户端数据交换
     */
    public void stop() {
        if(client != null) {
            client.close();
            client = null;
        }
    }

    public synchronized ConfigHolder getConfig() {
        if (configCache == null) {
            try {
                this.refreshConfig();
            } catch (Exception e) {
                LOG.error("refreshConfig error due to ", e);
            }
        }
        return configCache;
    }
}
