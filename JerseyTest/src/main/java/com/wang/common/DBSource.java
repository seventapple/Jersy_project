package com.wang.common;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DBSource {
	private static DataSource source = null;
	private static DBSource dbSource = null;
	private static final String BASE = System.getenv("WA_HOME");

	private DBSource() throws Exception {
		try {
			PoolProperties prop = getPoolPorperties(new PropertyLoader(BASE + "/conf/db.properties"));
			source = new DataSource();
			source.setPoolProperties(prop);
		} catch (Exception e) {
			throw new Exception();
		}
	}

	public static synchronized DBSource getInstance() throws Exception {
		if (dbSource == null) {
			try {
				dbSource = new DBSource();
			} catch (Exception e) {
				throw new Exception(e);
			}
		}
		return dbSource;
	}

	public synchronized Connection getConnection() throws SQLException {
		try {
			Connection conn = source.getConnection();
			conn.setAutoCommit(false);
			return conn;
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	private PoolProperties getPoolPorperties(PropertyLoader prop) throws Exception {
		PoolProperties pop = new PoolProperties();
		String jdbcDriver = "org.postgresql.Driver";
		String serverName = prop.getProperty("db.connectconfig.postgresqlservername", "localhost");
		int portNumber = Integer.parseInt(prop.getProperty("db.connectconfig.postgresqlportnumber", "5432"));
		String dbName = prop.getProperty("db.connectconfig.postgresqldbname", "postgres");
		String userName = prop.getProperty("db.connectconfig.postgresqluser", "postgres");
		String userPwd = prop.getProperty("db.connectconfig.postgresqlpassword", "postgres");
		int maxconnections = StringUtils.parseInt(prop.getProperty("db.connectconfig.postgresqlmaxconnections", "100"));
		int timeout = StringUtils.parseInt(prop.getProperty("db.connectconfig.postgresqltimeout", "300"));
		int validationInterval = StringUtils.parseInt(prop.getProperty("db.connectconfig.validationinterval", "2"));
		int timeBetweenEvictionRunsMillis = StringUtils
				.parseInt(prop.getProperty("db.connectconfig.timebetweenevictionruns", "10")) * 1000;
		int removeAbandonedTimeout = StringUtils
				.parseInt(prop.getProperty("db.connectconfig.removeabandonedtimeouts", "300"));
		int minEvictableIdleTime = StringUtils
				.parseInt(prop.getProperty("db.connectconfig.minevictableidletime", "300"));
		int minIdle = StringUtils.parseInt(prop.getProperty("db.connectconfig.minidle", "1"));
		// int maxIdle=StringUtils.parseInt(prop.getProperty("db.connectconfig.minidle",
		// "10"));
		String url = new StringBuffer("jdbc:postgresql://").append(serverName).append(":").append(portNumber)
				.append("/").append(dbName).toString();
		pop.setUrl(url);
		pop.setDriverClassName(jdbcDriver);
		pop.setUsername(userName);
		pop.setPassword(userPwd);
		pop.setTestWhileIdle(true);
		pop.setTestOnBorrow(true);
		pop.setTestOnConnect(true);
		pop.setValidationQuery("select 1");
		pop.setValidationInterval(validationInterval);
		pop.setMaxActive(maxconnections);
		pop.setInitialSize(minIdle);
		pop.setMaxWait(timeout);
		pop.setRemoveAbandoned(false);
		pop.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		pop.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		pop.setMinEvictableIdleTimeMillis(minEvictableIdleTime);
		pop.setMinIdle(minIdle);
		pop.setMaxIdle(maxconnections);
		pop.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;");
		pop.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;");
		return pop;
	}
}
