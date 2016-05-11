package com.bmi.sharedeal.service.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.json.Json;

import com.bmi.sharedeal.service.Config;
import com.bmi.sharedeal.service.utils.TextUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class BaseHandler implements HttpHandler{

	public Dao dao;
	
	private Headers requestHeaders;
	private Headers responseHeaders;
	private Map arugments;
	
	protected String response;
	
	public BaseHandler() {
        dao = Config.getDao();
    }
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		requestHeaders = exchange.getRequestHeaders();
		responseHeaders = exchange.getResponseHeaders();
		arugments = (Map)exchange.getAttribute("parameters");
		boolean checkApiKey = checkApiKey();
//		System.out.println("checkApiKey的值：" + checkApiKey);
		if(checkApiKey()){
			invokeMethod(exchange.getHttpContext().getPath());
		}		
		
		responseHeaders.set("Content-Type", "application/json; charset=utf-8;");
		responseHeaders.set("Server", Config.ServerName);
		responseHeaders.set("Access-Control-Allow-Origin", "*");
		
		byte[] bytes = response.getBytes("UTF-8");
		exchange.sendResponseHeaders(200, bytes.length);
		OutputStream responseBody = exchange.getResponseBody();
		responseBody.write(bytes);
		responseBody.close();
		
		exchange.close();
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object getArgument (String key) {
		return this.arugments.get(key);
	}

	/**
	 * 写Response(Json字符串)
	 * @param isOk
	 * @param errString
	 * @param data
	 */
	private void writeResponse(boolean isOk, String errString, Map data) {
		Map res = new HashMap();
		res.put("invoke", isOk);
		
		if (isOk) {
			res.put("result", data);
		} else {
			res.put("error", errString);
		}
		
		response = Json.toJson(res);
	}
	
	/**
     * (调用成功)输出结果
     * @param data
     */
    public void writeResult(Map data) {
        writeResponse(true, "", data);
    }
	
	/**
	 * (调用失败)输出出错信息
	 * @param errString
	 */
	public void writeError(String errStr) {
		writeResponse(false, errStr, null);
	}
	
	/**
	 * 通过反射调用对应的api方法
	 * @param urlPath
	 */
	private void invokeMethod(String urlPath) {
		String method = urlPath.substring(urlPath.lastIndexOf('/') + 1);
//		System.out.println("method：" + method);
		if (TextUtils.isEmpty(method)) {
			writeError("未指定api");
		}
		
		boolean isInvokeOk = true;
		long startTime = System.currentTimeMillis();
		
		Class me = this.getClass();
		System.out.println("me:" + me);
		try {
			Method queryMethod = me.getMethod("api_" + method);
			queryMethod.invoke(this);
		} catch (NoSuchMethodException e){
			writeError("未找到api" + method);
		} catch (Exception e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
			
			if (TextUtils.isEmpty(response)) {
				writeError("服务器内部错误: " + e.getMessage());
			}
			
			isInvokeOk = false;
		}
		
		System.out.println("test1****method:" + method);
		
		//记录服务器响应时间
		if (!method.equals("status")) {
			long costTime = System.currentTimeMillis() - startTime;
			ApiMonitor.get().addRecord(urlPath, !isInvokeOk, (int) costTime);
		}
	}

	/**
     * api授权检查
     * @return
     */
    public boolean checkApiKey() {
        String apiKey = (String) getArgument("apikey");
        if (TextUtils.isEmpty(apiKey) || !apiKey.equals(Config.ApiKey)) {
            writeError("未授权调用");
            return false;
        }

        return true;
    }
}
