package com.education.zhxy.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringUtil {
	public static final String G = "-";

	public static final String AND = "&";

	public static final String EMPTY = "";

	public static final String COLON = ":";

	public static final String SEMICOLON = ";";

	public static final String EQUALS = "=";

	public static final String PROBLEM = "?";

	public static final String COMMA = ",";

	public static String getStringFromStream(InputStream is) {
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (null != is) {
				baos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int ch = -1;
				while ((ch = is.read(buf)) != -1) {
					baos.write(buf, 0, ch);
				}
				result = new String(baos.toByteArray(), "utf-8");
				is.close();
				baos.close();
			}
		} catch (Exception e) {

		} finally {
			try {
				if (null != is) {
					is.close();
				}

				if (null != baos) {
					baos.close();
				}
			} catch (IOException e) {
			}
		}

		return result;
	}

	public static boolean isEmpty(String param) {
		return null == param || "".equals(param) || "null".equals(param);
	}

	/** * 判断字符串是否是整数 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/*
	 * 判断字符串是否是有效的JSON
	 */
	public static boolean isGoodJson(String json){
		if(json.contains("status")  && json.contains("data") && json.contains("errMsg")){
			return true;
		}
		return false;
	}
	
	/*
	 * 判断是否包含某个字符（城市截取）
	 */
	public static boolean lastEqual(String str, char ch) {
		if (!StringUtil.isEmpty(str) && str.charAt(str.length() - 1) == ch) {
			return true;
		} else {
			return false;
		}
	}
}
