package com.wang.common;

import java.io.IOException;

public class ResourceUtil {
	private static PropertyLoader propert;
	private static String basePath=System.getenv("WA_HOME");
	private static void load(String path) throws IOException {
		if(propert==null) {
			synchronized(ResourceUtil.class){
				propert=new PropertyLoader(basePath+path);
			}
		}
	}
	
	public static String getProperty(String key,String defValue) throws IOException {
		load("/conf/wa.properties");
		return propert.getProperty(key, defValue);
	}
	
	public static int getProperty(String key,int defValue) throws IOException {
		load("/conf/wa.properties");
		return propert.getProperty(key, defValue);
	}
}
