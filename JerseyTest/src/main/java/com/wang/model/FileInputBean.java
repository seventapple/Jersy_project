package com.wang.model;

import java.sql.Timestamp;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

public class FileInputBean {
	@FormDataParam("fileId")
	private int fileId;
	@FormDataParam("fileName")
	private String fileName;
	@FormDataParam("fileInfo")
	private FileBean fileInfo;
	@FormDataParam("fileBody")
	private FormDataBodyPart fileBody;
	@FormDataParam("updateUser")
	private String updateUser;
	@FormDataParam("updateTime")
	private Timestamp updateTime;
	@FormDataParam("col1")
	private String col1;
	@FormDataParam("col2")
	private String col2;

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

	public FileBean getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileBean fileInfo) {
		this.fileInfo = fileInfo;
	}

	public FormDataBodyPart getFileBody() {
		return fileBody;
	}

	public void setFileBody(FormDataBodyPart fileBody) {
		this.fileBody = fileBody;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
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
