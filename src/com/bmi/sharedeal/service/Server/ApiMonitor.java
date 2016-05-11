package com.bmi.sharedeal.service.Server;

import com.bmi.sharedeal.service.Config;
import com.bmi.sharedeal.service.utils.TextUtils;
import org.nutz.dao.Dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Keith on 2015/5/6.
 */
public class ApiMonitor {

    private static ApiMonitor _instance;

    /**
     * 没加同步，需要全局提前调用一次
     *
     * @return
     */
    public static ApiMonitor get() {
        if (_instance == null) {
            _instance = new ApiMonitor();
        }

        return _instance;
    }

    private ConcurrentHashMap<String, ApiStatus> apiStatusList;

    private ApiMonitor() {
        synchronized (this) {
            apiStatusList = new ConcurrentHashMap<String, ApiStatus>();
            loadFromDB();

            //定时写入数据库
            service.scheduleAtFixedRate(saveToDbRunnable, 5, 5, TimeUnit.MINUTES);
        }
    }

    public void addRecord(String apiName, boolean isFail, int timeCost) {
        if (TextUtils.isEmpty(apiName)) {
            return;
        }

        ApiStatus status = apiStatusList.get(apiName);
        if (status == null) {
            status = new ApiStatus();
            status.setApiName(apiName);
            apiStatusList.put(apiName, status);
        }

        status.addRecord(isFail, timeCost);
    }

    public List getStatusList() {
        return new ArrayList<ApiStatus>(apiStatusList.values());
    }

    private void loadFromDB(){
        apiStatusList.clear();

        Dao dao = Config.getDao();
        List<ApiStatus> list = dao.query(ApiStatus.class, null);
        if(list != null){
            for(ApiStatus status : list){
                if(status != null){
                    apiStatusList.put(status.getApiName(), status);
                }
            }
        }
    }

    private void saveToDB(){
        Dao dao = Config.getDao();
        dao.clear(ApiStatus.class);
        for(ApiStatus status : apiStatusList.values()){
            dao.insert(status);
        }
    }

    private Runnable saveToDbRunnable = new Runnable() {
        @Override
        public void run() {
            saveToDB();
        }
    };

    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
}
