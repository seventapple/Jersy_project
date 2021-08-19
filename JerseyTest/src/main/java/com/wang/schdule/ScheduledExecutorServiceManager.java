package com.wang.schdule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ScheduledExecutorServiceManager {

	private static volatile ScheduledExecutorServiceManager instance = null;
	private ScheduledExecutorService scheduleEs = null;
	private ScheduledFuture<?> reloadFuture;

	private ScheduledExecutorServiceManager() {
	}

	public static ScheduledExecutorServiceManager getExecutorInstance() {
		if (instance == null) {
			synchronized (ScheduledExecutorServiceManager.class) {
				if (instance == null) {
					instance = new ScheduledExecutorServiceManager();
					instance.scheduleEs = Executors.newScheduledThreadPool(5);
				}
			}
		}
		return instance;
	}

	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduleEs;
	}

	public ScheduledFuture<?> getReloadFuture() {
		return reloadFuture;
	}

	public void setReloadFuture(ScheduledFuture<?> future) {
		this.reloadFuture = future;
	}

	public void destory() {
		if (this.reloadFuture != null) {
			this.reloadFuture.cancel(false);
		}
	}
}
