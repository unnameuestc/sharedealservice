package com.bmi.sharedeal.service.Server;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.bmi.sharedeal.service.utils.TextUtils;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

public class ParameterFilter extends Filter{

	@Override
	public String description() {
		return "Parses the requested URI for parameters";
	}

	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		parseGetParameters(exchange);
//		parsePostParameters(exchange);
		//invoke the next filter in the chain
		chain.doFilter(exchange);	
	}

	private void parseGetParameters(HttpExchange exchange) throws UnsupportedEncodingException {
		Map parameters = new HashMap();
		URI requestedUri = exchange.getRequestURI();
		String query = requestedUri.getRawQuery();
		System.out.println("query的值：" + query);
		decodeParams(query, parameters);
		exchange.setAttribute("parameters", parameters);
	}
	
	private void parsePostParameters(HttpExchange exchange)
            throws IOException {

        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map parameters = (Map) exchange.getAttribute("parameters");

            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);

            if (contentType.startsWith("application/x-www-form-urlencoded")) {
                String line = br.readLine();
                while (line != null) {
                    if (line.length() > 0) {
                        decodeParams(line, parameters);
                        break;
                    }
                    line = br.readLine();
                }
            } else if (contentType.startsWith("multipart/form-data")) {
                String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
                boundary = boundary.substring(0, boundary.indexOf(";"));

                String line = br.readLine();
                while (line != null && !line.equals("--" + boundary + "--")) {
                    if (line.startsWith("--" + boundary)) {
                        String nameStr = br.readLine();
                        if (!TextUtils.isEmpty(nameStr) && !nameStr.contains("filename=\"")) {    //不管文件
                            String key = nameStr.substring(nameStr.indexOf("name=\"") + 6, nameStr.length() - 1);
                            br.readLine();  //注意空行

                            StringBuilder value = new StringBuilder(br.readLine());

                            line = br.readLine();
                            while (!line.startsWith("--" + boundary)) {
                                value.append("\r\n" + line);
                                line = br.readLine();
                            }

                            parameters.put(URLDecoder.decode(key, "utf-8"), value.toString());
                        }
                    } else {
                        line = br.readLine();
                    }
                }
            }

            br.close();
            isr.close();
        }
    }

	private String decodePercent(String str) {
		String decoded = null;
		try {
			decoded = URLDecoder.decode(str, "UTF8");
		} catch (UnsupportedEncodingException e) {
		}
		
		return decoded;
	}
	
	private void decodeParams(String params, Map<String, String> p) {
		if (params == null) {
			return;
		}
		
		StringTokenizer st = new StringTokenizer(params, "&");
		while(st.hasMoreTokens()) {
			String e = st.nextToken();
			System.out.println("e的值：" + e);
			int sep = e.indexOf('=');
			if (sep >= 0) {
				p.put(decodePercent(e.substring(0, sep)).trim(), decodePercent(e.substring(sep + 1)));
			} else {
				p.put(decodePercent(e).trim(), "");
			}
		}
	}


}
