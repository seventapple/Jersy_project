package com.wang.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyLoader {
	private static final String COMMENT="#!";
	private static final char SEPARATOR='=';
	private ConfigProperties properties;
	
	public PropertyLoader(String path) throws IOException{
		File file=new File(path);
		RandomAccessFile os=null;
		FileLock lock=null;
		FileChannel channel=null;
		try {
			os=new RandomAccessFile(file,"r");
			channel=os.getChannel();
			while(true) {
				try {
					lock=channel.tryLock(0,Long.MAX_VALUE,true);
					if(lock!=null) {
						break;
					}
				}catch(Exception e) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
				}
			}
		}catch(FileNotFoundException e) {
			throw new FileNotFoundException();}
		try(FileInputStream is =new FileInputStream(path)){
			properties=new ConfigProperties();
			properties.load(is);
		}catch(IOException e) {
			throw new IOException();
		}finally {
			try{
				if(lock!=null) {
					lock.release();
				}
				if(channel!=null) {
					channel.close();
				}
				if(os!=null) {
					os.close();
				}
			}catch(IOException e) {
				throw new IOException();
			}
		}
	}
	
	public Map<String,String> getGroupMap(String groupName){
	Map<String,String>groups=new HashMap<String,String>();
	Iterator<?>it=properties.keySet().iterator();
	while(it.hasNext()) {
		String key=(String)it.next();
		if(key.startsWith(groupName)) {
			String id=key.substring(groupName.length()+1);;
			groups.put(id,properties.getProperty(key));
		}
	}
		return groups;
	}
	
	public String getOrigiinProperty(String key) {
		return properties.getProperty(key);
	}
	public void setProperty(String key,String value) {
		properties.setProperties(key, value);
	}
	
	public int getProperty(String key,int defaultvalue) {
		int ref=defaultvalue;
		String val=properties.getProperty(key);
		if(StringUtils.isNull(val)) {
			return ref;
		}
		try {
			ref=Integer.valueOf(val);
		}catch(Exception e) {
			ref=defaultvalue;
		}
		return ref;
	}
	
	public String getProperty(String key,String defaultvalue) {
		String val=properties.getProperty(key);
		if(StringUtils.isNull(val)) {
			return defaultvalue;
		}
		return val;
	}
	
	public static enum SearchMode{
		END,
	}
	
	public Map<String ,String >search(String val){
		return search(val,SearchMode.END);
	}
	
	private Map<String,String > search(String val,SearchMode mode){
		Map<String,String>groups=new HashMap<String ,String>();
		for(Object obj:properties.keySet()) {
			String k=(String)obj;
			String v=(String)properties.get(k);
			if(SearchMode.END.equals(mode)) {
				if(v.endsWith(val)) {
					groups.put((String)k,v);
				}
			}
		}
		return groups;
	}
	
	public List<String> getAllGroups(){
		List<String>group=new ArrayList<>();
		Iterator<?>it=properties.keySet().iterator();
		while(it.hasNext()) {
			group.add((String)it.next());
		}
		return group;
	}
	
	public boolean sortedStore(String path) {
		if(properties instanceof ConfigProperties ) {
			try {
				properties.sortedStore(path);
				return true;
			}catch(Exception e) {
				return false;
			}
		}else {
			return false;
		}
	}
	
	public List<String> getPlugins(){
		return getGroups("plugin.");
	}
	
	public List<String> getGroups(String groupName){
		List<String>groups =new ArrayList<String>();
		Iterator<?>it=properties.keySet().iterator();
		while(it.hasNext()) {
			String key=(String)it.next();
			if(key.startsWith(groupName)) {
				groups.add(properties.getProperty(key));
			}
		}
		return groups;
	}
	
	class ConfigProperties extends Properties{
//		private static final long serialVersionUID=5246153840399256989L;
		private List<Property>properties=new ArrayList<Property>();
		public List<Property> getProperties(){
			return properties;
		}
		public void setProperties(List<Property>properties) {
			this.properties=properties;
		}
		public void load(InputStream in) throws  IOException {
			load(new BufferedReader(new InputStreamReader(in,"UTF-8")));
		}
		public synchronized Object setProperties(String key,String value) {
			value=(value==null?"":value);
			Object obj =super.setProperty(key,value);
			updateProperty(key,value);
			return obj;
		}
		public void updateProperty(String key,String value) {
			if(StringUtils.isNull(key)) {
				return;
			}
			for(Property pro:properties) {
				if(key.equals(pro.key)) {
					pro.setValue(value);
				}
			}
		}
		public void sortedStore(String path) throws IOException {
			File file=new File(path);
			RandomAccessFile os=null;
			FileLock lock=null;
			FileChannel channel=null;
			try {
				os=new RandomAccessFile(file,"r");
				channel=os.getChannel();
				while(true) {
					try {
						lock=channel.tryLock(0,Long.MAX_VALUE,true);
						if(lock!=null) {
							break;
						}
					}catch(Exception e) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
						}
					}
				}
			}catch(FileNotFoundException e) {
				throw new FileNotFoundException();
			}
			try{
				if(lock!=null) {
					byte[]newLine="\r\n".getBytes("utf-8");
					for(Property pro:properties) {
						if(StringUtils.isNull(pro.key)) {
							os.write(pro.getValue().getBytes("utf-8"));
						}else {
							byte[]line =(pro.getKey()+SEPARATOR+pro.getValue()).getBytes("utf-8");
							os.write(line);
						}
						os.write(newLine);
					}
				}
			}catch(IOException e) {
				throw new IOException();
			}finally {
				try{
					if(lock!=null) {
						lock.release();
					}
					if(channel!=null) {
						channel.close();
					}
					if(os!=null) {
						os.close();
					}
				}catch(IOException e) {
					throw new IOException();
				}
			}
		}
		private synchronized void load (BufferedReader in) throws IOException {
			String line;
			while((line=in.readLine())!=null) {
				line=line.trim();
				if(line.length()==0||COMMENT.indexOf(line.charAt(0))>-1) {
					properties.add(new Property("",line));
					continue;
				}
				int pos=line.indexOf(SEPARATOR);
				if(pos<0) {
					properties.add(new Property("",line));
					continue;
				}
				String key=line.substring(0,pos).trim();
				String value=line.substring(pos+1).trim();
				if(key.length()==0) {
					properties.add(new Property(key,value));
					continue;
				}
				put(key,value);
				properties.add(new Property(key,value));
			}
		}
	}
	
	class Property{
		private String key=null;
		private String value=null;
		public Property(String key,String value) {
			this.key=key;
			this.value=value;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
	}
}
