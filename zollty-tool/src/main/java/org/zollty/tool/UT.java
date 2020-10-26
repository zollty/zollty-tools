package org.zollty.tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jretty.log.LogFactory;
import org.jretty.log.Logger;
import org.jretty.util.AlgorithmUtils;
import org.jretty.util.CollectionUtils;
import org.jretty.util.DateFormatUtils;
import org.jretty.util.ExceptionUtils;
import org.jretty.util.FileUtils;
import org.jretty.util.IOUtils;
import org.jretty.util.PathUtils;
import org.jretty.util.RandomUtils;
import org.jretty.util.ReflectionUtils;
import org.jretty.util.StringSplitUtils;
import org.jretty.util.StringUtils;
import org.jretty.util.ThreadUtils;
import org.jretty.util.WebUtils;

/**
 * 工具类统一入口。（目的： 
 *    1.方便使用、无需记忆 只需输入“UT.”工具类就列出来了； 
 *    2.统一工具类、统一管理）
 * 
 * @author zollty
 * @since 2017-4-23
 */
public class UT {
    private static final Logger logger = LogFactory.getLogger("");
    /*
工具类列表：
    Util.Str     -- 字符串
    Util.Coll    -- 集合
    Util.Json    -- Json处理
    Util.Date    -- 日期、格式化
    Util.Split   -- 字符串拆分
    Util.IO      -- IO流
    Util.Thread  -- 线程
    Util.Random  -- 随机数
    Util.Res     -- 资源文件
    Util.Web     -- Web相关
    Util.Algr    -- 算法
    Util.Ref     -- 反射
    Util.Excp    -- 异常
     */
    
    /** 通用Logger */
    public static Logger getLogger() {
        return logger;
    }
    
    
    public static class Coll extends CollectionUtils {
        public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
            Iterator<Entry<K, V>> i = map.entrySet().iterator();
            if (value != null) {
                while (i.hasNext()) {
                    Entry<K, V> e = i.next();
                    if (value.equals(e.getValue()))
                        return e.getKey();
                }
            } else {
                while (i.hasNext()) {
                    Entry<K, V> e = i.next();
                    if (e.getValue() == null)
                        return e.getKey();
                }
            }
            return null;
        }
    }
    
    public static class Path extends PathUtils {
        
    }
    
    public static class Str extends StringUtils {
        /**
         * Returns a string representation of the "deep contents" of the specified array.
         *   Adjacent elements are separated by the characters <tt>","</tt> (a comma).
         *   Elements are converted to strings as by
         * <tt>String.valueOf(Object)</tt>.
         * 
         * <p>e.g.</p>
         * <p>Object[]{2L,8L,5L} ==&gt; String "2,5,8" </p>
         * 
         */
        public static String toSQLString(Object[] a) {
            if (a == null || a.length==0)
                return "";

            int bufLen = 20 * a.length;
            if (bufLen <= 0)
                bufLen = Integer.MAX_VALUE;
            StringBuilder buf = new StringBuilder(bufLen);
            int iMax = a.length - 1;
            for (int i = 0; ; i++) {
                Object element = a[i];
                if (element != null) {
                    buf.append(element.toString());
                }
                if (i == iMax)
                    break;
                buf.append(",");
            }
            return buf.toString();
        }
    }
    
    public static class Split extends StringSplitUtils {
        
    }
    
    public static class Random extends RandomUtils {

    }

    public static class Thread extends ThreadUtils {

    }
    
    public static class IO extends IOUtils {

    }
    
    public static class File extends FileUtils {

    }
    
    /*public static class Res extends ResourceUtils {
        
    }*/

    public static class Algr extends AlgorithmUtils {
        
    }
    
    public static class Date extends DateFormatUtils {
        
    }
    
    public static class Ref extends ReflectionUtils {
        
    }
    
    public static class Excp extends ExceptionUtils {
        /**
         * 获取一个最长为32的string作为Exception对象的标识
         */
        public static String getExceptionSign(Throwable ex) {
            String key = ex.getMessage();
            if (key != null && key.length() > 36) {
                key = key.substring(0, 24) + key.substring(key.length() - 12, key.length());
            }
            return ex.getClass().getSimpleName() + key;
        }
    }
    
    public static class Obj {
        /**
         * 比较两个值的大小，支持null
         */
        public static <T> int compareTo(Comparable<T> obj, T other) {
            if (obj != null) {
                if (other != null) {
                    return obj.compareTo(other);
                }
                return 1;
            }
            else {
                if (other != null) {
                    return -1;
                }
                return 0;
            }
        }
    }
    
    public static class Web extends WebUtils {
        
    }

    
    /** 快速new一个HashMap */
    public static <K, V> FMap<K, V> newMap(K key, V value) {
        FMap<K, V> map = new FMap<>();
        map.put(key, value);
        return map;
    }
    /** 快速new一个Properties */
    public static FProp newProp(String key, Object value) {
        FProp map = new FProp();
        map.put(key, value);
        return map;
    }
    
    public static class FMap<K, V> extends HashMap<K, V>{
        private static final long serialVersionUID = 1L;
        public FMap<K, V> add(K key, V value) {
            super.put(key, value);
            return this;
        }
    }
    public static class FProp extends Properties {
        private static final long serialVersionUID = 1L;
        public FProp add(String key, Object value) {
            super.put(key, value);
            return this;
        }
    }
}
