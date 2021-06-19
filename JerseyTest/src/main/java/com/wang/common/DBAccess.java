package com.wang.common;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import jdk.nashorn.internal.ir.ObjectNode;

public class DBAccess implements AutoCloseable {
	private static final int FETCHSIZE = 100;
	private static final Object InputStream = null;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet resultSet = null;
	private int limit = 1;

	public DBAccess() throws Exception {
		init();
	}

	private void init() throws Exception {
		try {
			conn = DBSource.getInstance().getConnection();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public void reInitialize() throws Exception {
		try {
			if (conn.isClosed()) {
				init();
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public int updateExec(String sql, Object... args) throws SQLException {
		try {
			String perparedSQL = sql;
			pstmt = conn.prepareStatement(perparedSQL);
			setParameters(args);
			int count;
			boolean execResult = pstmt.execute();
			if (execResult) {
				resultSet = pstmt.getResultSet();
				if (resultSet.next()) {
					count = resultSet.getInt(1);
				} else {
					count = -99;
				}
			} else {
				count = pstmt.getUpdateCount();
			}
			return count;
		} catch (SQLException e) {
			if ("23505".equals(e.getSQLState())) {
				return -1;
			} else {
				throw new SQLException(e);
			}
		}
	}

	@SuppressWarnings("uncheck")
	public <T> T queryForObject(Class<?> clazz, String sql, Object... args) throws Exception {
		doQuery(sql, args);
		if (resultSet.next()) {
			if (String.class.equals(clazz)) {
				return (T) resultSet.getString(1);
			} else if (Integer.class.equals(clazz)) {
				return (T) Integer.valueOf(resultSet.getInt(1));
			} else {
				return reslutSetToBean(resultSet, clazz, getColumnName(resultSet));
			}
		}
		if (Integer.class.equals(clazz)) {
			return (T) Integer.valueOf(0);
		} else {
			return (T) clazz.newInstance();
		}
	}

	public <T> List<T> searchExec(Class<?> clazz, String sql, Object... args) throws Exception {
		doQuery(sql, args);
		@SuppressWarnings("uncheck")
		List<T> list = (List<T>) resultSetToList(resultSet);
		return list;
	}

	private void doQuery(String sql, Object... args) throws SQLException {
		try {
			ArrayList<Object> arrayList = new ArrayList(Arrays.asList(args));
			if (limit > 0) {
				sql = sql + " limit ?";
				arrayList.add(limit);
			}
			pstmt = conn.prepareStatement(sql);
			if (args != null) {
				setParameters(arrayList.toArray());
			}
			pstmt.setFetchSize(FETCHSIZE);
			resultSet = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	public <T> T reslutSetToBean(ResultSet rs, Class<?> dateBean, String[] columnNames) throws Exception {
		String[] lclColumnnName = columnNames;
		try {
			if (lclColumnnName == null) {
				lclColumnnName = getColumnName(rs);
			}
			int columnCount = lclColumnnName.length;
			@SuppressWarnings("uncheck")
			T bean = (T) dateBean.newInstance();
			for (int i = 0; i < columnCount; i++) {
				String methodName = StringUtils.underLineWord2Upper(lclColumnnName[i]);
				PropertyDescriptor pd = new PropertyDescriptor(methodName, dateBean);
				Method currentMethod = pd.getWriteMethod();
				Class<?>[] pClass = currentMethod.getParameterTypes();
				try {
					if (String.class.equals(pClass[0])) {
						currentMethod.invoke(bean, rs.getString(lclColumnnName[i]));
					} else if (BigDecimal.class.equals(pClass[0])) {
						currentMethod.invoke(bean, rs.getBigDecimal(lclColumnnName[i]));
					} else if (boolean.class.equals(pClass[0])) {
						currentMethod.invoke(bean, rs.getBoolean(lclColumnnName[i]));
					} else if (int.class.equals(pClass[0])) {
						currentMethod.invoke(bean, rs.getInt(lclColumnnName[i]));
					} else if (long.class.equals(pClass[0])) {
						currentMethod.invoke(bean, rs.getLong(lclColumnnName[i]));
					} else if (InputStream.class.equals(pClass[0])) {
						Object oidObject = rs.getObject(i);
						if (oidObject != null) {
							LargeObjectManager lom = conn.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
							long oid = lom.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
							LargeObject obj = lom.open(oid, LargeObjectManager.READ);
							currentMethod.invoke(bean, new ByteArrayInputStream(obj.read(obj.size())));
							obj.close();
						} else {
							currentMethod.invoke(bean, oidObject);
						}
					} else if (ObjectNode.class.equals(pClass[0])) {
						currentMethod.invoke(bean, StringUtils.strToObjectNode(rs.getString(lclColumnnName[i])));
					} else {
						currentMethod.invoke(bean, StringUtils.strToBean(rs.getString(lclColumnnName[i]), pClass[0]));
					}
				} catch (Exception e) {
					throw new Exception(e);
				}
			}
			return bean;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<Map<String, Object>> resultSetToList(ResultSet rs) throws Exception {
		try {
			if (rs == null) {
				return Collections.emptyList();
			}
			ResultSetMetaData mataData = rs.getMetaData();
			int columnCount = mataData.getColumnCount();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> rowData;
			int currentNum = 0;
			while (rs.next()) {
				currentNum++;
				rowData = new HashMap<String, Object>(columnCount);
				for (int i = 1; i < columnCount; i++) {
					rowData.put(mataData.getColumnName(i), rs.getObject(i));
				}
				list.add(rowData);
				if (limit > 0 && currentNum >= limit) {
					break;
				}
			}
			return list;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			setLimit(-1);
		}
	}

	private <T> List<T> resultSetToList(ResultSet rs, Class<?> dateBean) throws Exception {
		try {
			List<T> list = new ArrayList<T>();
			String[] columnNames = getColumnName(rs);
			if (String.class.equals(dateBean)) {
				while (rs.next()) {
					list.add((T) rs.getString(1));
				}
			} else {
				while (rs.next()) {
					list.add((T) reslutSetToBean(rs, dateBean, columnNames));
				}
			}
			return list;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			setLimit(-1);
		}
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	protected void setParameters(Object... args) throws SQLException {
		int index = 1;
		for (int i = 0; i < args.length; i++) {
			Object value = args[i];
			if (value != null && value.getClass().isArray()) {
				Object[] values = (Object[]) value;
				for (int j = 0; j < values.length; j++) {
					Object ao = values[j];
					setParameter(ao, index);
					index++;
				}
			} else {
				setParameter(value, index);
				index++;
			}
		}
	}

	protected void setParameter(Object value, int index) throws SQLException {
		if (value == null) {
			pstmt.setObject(index, null);
		}
		if (value instanceof String) {
			if (((String) value).length() == 0) {
				pstmt.setObject(index, null);
			} else {
				pstmt.setString(index, (String) value);
			}
		} else if (value instanceof Integer) {
			pstmt.setInt(index, (Integer) value);
		} else if (value instanceof Long) {
			pstmt.setLong(index, (Long) value);
		} else if (value instanceof Boolean) {
			pstmt.setBoolean(index, (Boolean) value);
		} else if (value instanceof Timestamp) {
			pstmt.setTimestamp(index, (Timestamp) value);
		} else if (value instanceof Double) {
			pstmt.setDouble(index, (Double) value);
		} else if (value instanceof List) {
			Array array = conn.createArrayOf("VARCHER", ((List<?>) value).toArray());
			pstmt.setArray(index, array);
		} else if (value instanceof InputStream) {
			InputStream data = (InputStream) value;
			LargeObjectManager lom = conn.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
			long oid = lom.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
			LargeObject obj = lom.open(oid, LargeObjectManager.WRITE);
			byte[] buf = new byte[1024];
			int s = 0;
			try {
				while ((s = data.read(buf)) != -1) {
					obj.write(buf, 0, s);
				}
			} catch (IOException e) {
				throw new SQLException(e);
			} finally {
				obj.close();
			}
			pstmt.setLong(index, oid);
		} else {
			pstmt.setObject(index, null);
		}
	}

	private String[] getColumnName(ResultSet rs) throws SQLException {
		try {
			ResultSetMetaData metaDate = rs.getMetaData();
			int columnCount = metaDate.getColumnCount();
			String[] columnNames = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				columnNames[i - 1] = metaDate.getColumnName(i);
			}
			return columnNames;
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	// 获取自增主键值
	public long getSequence(String seqName) {
		long seqence = 0;
//		seqence=(long)searchExec("SELECT nextval('"+seqName+"')").get(0).get("nextval");
		return seqence;
	}

	public void setBatchSql(String sql) throws SQLException {
		try {
			String preparedSQL = sql;
			pstmt = conn.prepareStatement(preparedSQL);
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	public void setBatchSqlParameter(Object... args) throws SQLException {
		try {
			setParameters(args);
			pstmt.addBatch();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	public int runBatchInsertIndex() throws Exception {
		try {
			int[] result = pstmt.executeBatch();
			for (int i = 0; i < result.length; i++) {
				if (result[i] == 0) {
					return i;
				}
			}
			return result.length;
		} catch (BatchUpdateException e) {
			if ("23505".equals(e.getSQLState())) {
				int[] rst = e.getUpdateCounts();
				return rst.length;
			}
			throw new BatchUpdateException(e);
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

//	public <T>List<T> searchByRowMapper(String sql,RowMapper<T> mapper,Object...args){
//		List<T> result=new ArrayList<T>();
//		doQuery(sql,args);
//	}

	public int runBatchUpdateIndex() throws Exception {
		try {
			int[] result = pstmt.executeBatch();
			for (int i = 0; i < result.length; i++) {
				if (result[i] == 0) {
					return i;
				}
			}
			return result.length;
		} catch (BatchUpdateException e) {
			throw new BatchUpdateException(e);
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	public int runBatchUpdate() throws Exception {
		try {
			int[] result = pstmt.executeBatch();
			return result.length;
		} catch (BatchUpdateException e) {
			if ("23505".equals(e.getSQLState())) {
				int[] rst = e.getUpdateCounts();
				return rst.length;
			}
			throw new BatchUpdateException(e);
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	public boolean dbRollback() throws SQLException {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.rollback();
			}
			return true;
		} catch (SQLException e) {
			throw new SQLException(e);
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean dbCommit() throws SQLException {
		try {
			conn.commit();
			return true;
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new SQLException(e);
			}
			return false;
		}
	}

	@Override
	public void close() throws SQLException {
		try {
			if (conn == null && conn.isClosed()) {
				return;
			}
			dbRollback();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				throw new SQLException(e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				throw new SQLException(e);
			}
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}
}
