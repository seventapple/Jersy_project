package com.wang.model;

import java.io.InputStream;

public class FileBean {
	private int fileId;
	private String fileName;
	private FileEntryBean fileInfo;
	private InputStream fileBody;
	private String updateUser;
	private String updateTime;
	private String col1;
	private String col2;
	
	

	@Override
	public String toString() {
		return "FileBean [fileId=" + fileId + ", fileName=" + fileName + ", fileInfo=" + fileInfo + ", fileBody="
				+ fileBody + ", updateUser=" + updateUser + ", updateTime=" + updateTime + ", col1=" + col1 + ", col2="
				+ col2 + "]";
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FileEntryBean getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileEntryBean fileInfo) {
		this.fileInfo = fileInfo;
	}

	public InputStream getFileBody() {
		return fileBody;
	}

	public void setFileBody(InputStream fileBody) {
		this.fileBody = fileBody;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCol1() {
		return col1;
	}

	public void setCol1(String col1) {
		this.col1 = col1;
	}

	public String getCol2() {
		return col2;
	}

	public void setCol2(String col2) {
		this.col2 = col2;
	}
}
