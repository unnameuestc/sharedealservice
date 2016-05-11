package com.bmi.sharedeal.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.bmi.sharedeal.service.utils.TextUtils;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 运行时配置
 *
 * @author Ma
 */
public class Config {

    //服务器
	public static boolean DEBUG = true;
    public static String ServerName = "JiaDaLiCaiServer";
    public static int PortNum = 8088;
    public static int ThreadCnt = 10;

    //Api授权，以后改为动态的
    public static String ApiKey = "A6F7F0D6CD13058D40C1110F007E7F13";
    
    //数据库
    protected static String DB_Host = "120.26.222.176";
    protected static int DB_Port = 3336;
    protected static String DB_Name = "sharedeal";
    protected static String DB_Charset = "utf8";
    protected static String DB_User = "bmiadmin";
    protected static String DB_Password = "bmiadmin123";

    protected static String DB_Driver = "com.mysql.jdbc.Driver";
    protected static DruidDataSource ds = new DruidDataSource();
    protected static Dao dao = null;

    public static Dao getDao() {
        return dao;
    }

    protected static final String propertyFileName = "config.properties";
    protected static Properties properties = new Properties();

    public static boolean init() {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(propertyFileName));
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }

            return false;
        }

        DEBUG = (Boolean) readFromCnf("DEBUG", "true", ConfigType.T_bool);
        PortNum = (Integer) readFromCnf("Port", "8081", ConfigType.T_int);
        ThreadCnt = (Integer) readFromCnf("Thread", "10", ConfigType.T_int);
        ServerName = (String) readFromCnf("ServerName", "BMIServer", ConfigType.T_str);
        ApiKey = (String) readFromCnf("ApiKey", "A6F7F0D6CD13058D40C1110F007E7F13", ConfigType.T_str);

        DB_Host = (String) readFromCnf("DB_Host", "120.26.222.176", ConfigType.T_str);
        DB_Port = (Integer) readFromCnf("DB_Port", "3336", ConfigType.T_int);
        DB_Name = (String) readFromCnf("DB_Name", "artservice", ConfigType.T_str);
        DB_Charset = (String) readFromCnf("DB_Charset", "utf8", ConfigType.T_str);
        DB_User = (String) readFromCnf("DB_User", "bmiadmin", ConfigType.T_str);
        DB_Password = (String) readFromCnf("DB_Password", "bmiadmin123", ConfigType.T_str);
        DB_Driver = (String) readFromCnf("DB_Driver", "com.mysql.jdbc.Driver", ConfigType.T_str);

        String DB_URL = "jdbc:mysql://"
                + DB_Host + ":"
                + DB_Port + "/"
                + DB_Name + "?characterEncoding="
                + DB_Charset;

        ds.setDriverClassName(DB_Driver);
        ds.setUrl(DB_URL);
        ds.setUsername(DB_User);
        ds.setPassword(DB_Password);

        dao = new NutDao(ds);

        return true;
    }

    public static enum ConfigType {T_str, T_int, T_bool}

    public static Object readFromCnf(String key, String defaultValue, ConfigType type) {
        String value = getString(key);
        if (value == null) {
            value = defaultValue;
        }

        switch (type) {
            case T_str:
                return value;
            case T_int:
                return Integer.parseInt(value);
            case T_bool:
                return Boolean.parseBoolean(value);
        }

        return value;
    }

    public static String getString(String key) {
        String result = properties.getProperty(key);
        if (result == null || result.equals("") || result.equals("null")) {
            return null;
        }

        return result;
    }

    public static Boolean getBoolean(String key) {
        String v = getString(key);
        if (TextUtils.isEmpty(v)) {
            return null;
        }

        return Boolean.parseBoolean(v);
    }

    public static Integer getInteger(String key) {
        String v = getString(key);
        if (TextUtils.isEmpty(v)) {
            return null;
        }

        return Integer.parseInt(v);
    }
}
