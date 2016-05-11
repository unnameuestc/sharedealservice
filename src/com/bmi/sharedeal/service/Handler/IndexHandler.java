package com.bmi.sharedeal.service.Handler;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bmi.sharedeal.service.Server.ApiMonitor;
import com.bmi.sharedeal.service.Server.ApiStatus;
import com.bmi.sharedeal.service.Server.BaseHandler;

public class IndexHandler extends BaseHandler{
	private Comparator<ApiStatus> comparatorStatus = new Comparator<ApiStatus>() {
		@Override
		public int compare(ApiStatus o1, ApiStatus o2) {
			return o1.getApiName().compareTo(o2.getApiName());
		}
	};
	
	public void api_() {
		writeError("∑«∑®«Î«Û");
	}
	
	public void api_status() {
		Map map = new HashMap();
		
		List statusList = ApiMonitor.get().getStatusList();
		Collections.sort(statusList, comparatorStatus);
		
		map.put("cnt", statusList.size());
        map.put("status",statusList);

        writeResult(map);
	}
}
