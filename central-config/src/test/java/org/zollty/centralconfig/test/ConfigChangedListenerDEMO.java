/* 
 * Copyright (C) 2016-2017 the CSTOnline Technology Co.
 * Create by ZollTy on 2016-5-17 (zoutianyong@cstonline.com)
 */
package org.zollty.centralconfig.test;

import java.util.Properties;
import java.util.Set;

import org.zollty.centralconfig.ConfigChangedListener;

/**
 * 
 * @author zollty
 * @since 2016-5-17
 */
public class ConfigChangedListenerDEMO implements ConfigChangedListener {

    /* (non-Javadoc)
     * @see org.zollty.centralconfig.ConfigChangedListener#onChanged(java.lang.String, java.util.Properties, java.util.Properties, java.util.Set, java.util.Set, java.util.Set)
     */
    @Override
    public void onChanged(String configFilename, Properties curProps, Properties oldProps, Set<String> addedKeys,
            Set<String> changedKeys, Set<String> removedKeys) {
        
        System.out.println("configFilename="+configFilename);
        StringBuilder sbu = new StringBuilder("addedKeys: ");
        for(String key: addedKeys) {
            sbu.append(key).append(", ");
        }
        System.out.println(sbu);
        
        sbu = new StringBuilder("changedKeys: ");
        for(String key: changedKeys) {
            sbu.append(key).append(", ");
        }
        System.out.println(sbu);
        
        sbu = new StringBuilder("removedKeys: ");
        for(String key: removedKeys) {
            sbu.append(key).append(", ");
        }
        System.out.println(sbu);

    }

}
