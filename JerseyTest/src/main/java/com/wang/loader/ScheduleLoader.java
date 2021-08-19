package com.wang.loader;

import java.util.concurrent.ScheduledFuture;

import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

import com.wang.schdule.ScheduleUtil;
import com.wang.schdule.ScheduledExecutorServiceManager;

public class ScheduleLoader implements ContainerLifecycleListener {

	@Override
	public void onStartup(Container container) {
		System.out.println("start lifecycle listener");
		ScheduledExecutorServiceManager executor = ScheduledExecutorServiceManager.getExecutorInstance();
		ScheduledFuture<?> future = executor.getReloadFuture();
		future = ScheduleUtil.executeScheduleAtTimePerDayBase(new Runnable() {
			@Override
			public void run() {
				System.out.println("Running!!!!");
			}
		}, "21:14", future);
	}

	@Override
	public void onReload(Container container) {

	}

	@Override
	public void onShutdown(Container container) {
	}

}
