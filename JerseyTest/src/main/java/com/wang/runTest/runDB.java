package com.wang.runTest;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wang.common.DBAccess;
import com.wang.common.StringUtils;
import com.wang.model.FileBean;
import com.wang.model.FileEntryBean;
import com.wang.model.UserBean;

public class runDB {
	public static void main(String[] args) {
		try (DBAccess db = new DBAccess()) {
			UserBean name = db.queryForObject(UserBean.class, "select id, name FROM tbl_user where id = ?", 1000);
			System.out.println("单一检索:" + name);
			List<UserBean> list = db.searchExec(UserBean.class, "select id, name, age from tbl_user where id > ?", 100);
			System.out.println("List检索:" + list);
			int updateExec = db.updateExec("insert into tbl_user values(?,?,?)", 1003, "wang1", 2);
			System.out.println("单一插入:" + updateExec);
			// 插入(stream->oid;timestamp;bean->jsonStr)
			// stream
			FileInputStream fis = new FileInputStream("E:\\TestFile\\111.txt");
			byte[] data = new byte[1024];
			int read = fis.read(data);
			ByteArrayInputStream bis = new ByteArrayInputStream(data, 0, read);
			// object to bean
			FileEntryBean obj = new FileEntryBean();
			obj.setFileFlg(true);
			obj.setName("111");
			obj.setPrice(10);
			int updateExec2 = db.updateExec("insert into tbl_file values(?,?,?::JSONB,?,?,?,?,?)", 11, "112.txt",
					StringUtils.beanToJsonString(obj), bis, "wang", StringUtils.getCurrentUtc(), "hahaha2",
					"why so serious?");
			System.out.println("完整插入:" + updateExec2);
			FileBean file1 = db.queryForObject(FileBean.class,
					"select file_id, file_name, file_info, file_body, update_user, update_time, col1, col2 from tbl_file where file_id = ?",
					10);
			List<FileBean> files = db.searchExec(FileBean.class,
					"select file_id, file_name, file_info, file_body, update_user, update_time, col1, col2 from tbl_file where file_id = ?",
					10);
			System.out.println(file1);
			System.out.println(files);
			db.dbCommit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
