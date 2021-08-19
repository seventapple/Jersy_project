package com.wang.schdule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduleUtil {

	/**
	 * 每日设定时间执行定期任务
	 * 
	 * @param <T>
	 * @param exClass
	 * @param time    HH:mm
	 * @param future
	 * @return
	 */
	public static <T> ScheduledFuture<?> executeScheduleAtTimePerDayBase(T exClass, String time,
			ScheduledFuture<?> future) {
		// 前回定时任务取消
		if (future != null && !future.isCancelled()) {
			future.cancel(false);
		}
		int timeHour = 0;
		int timeMinute = 0;
		try {
			if (time != null) {
				timeHour = Integer.valueOf(time.substring(0, 2));
				timeMinute = Integer.valueOf(time.substring(3, 5));
			}
		} catch (NumberFormatException e) {
			timeHour = 0;
			timeMinute = 0;
		}

		long rate = 60 * 60 * 24;
		LocalDateTime localNow = LocalDateTime.now();
		ZoneId currentZone = ZoneId.systemDefault();
		// UTC
		ZonedDateTime zoneTimeNow = ZonedDateTime.of(localNow, currentZone);
		// 执行时间
		ZonedDateTime executeTime;
		executeTime = zoneTimeNow.withHour(timeHour).withMinute(timeMinute).withSecond(0);
		if (zoneTimeNow.compareTo(executeTime) > 0) {
			executeTime = executeTime.plusDays(1);
		}
		System.out.println("executeTime = " + executeTime);
		// 延迟时间
		Duration duration = Duration.between(zoneTimeNow, executeTime);
		long initDelay = duration.getSeconds();

		ScheduledExecutorService executor = ScheduledExecutorServiceManager.getExecutorInstance()
				.getScheduledExecutorService();
		future = executor.scheduleAtFixedRate((Runnable) exClass, initDelay, rate, TimeUnit.SECONDS);
		return future;
	}

	public static <T> ScheduledFuture<?> executeScheduleRateNoDeleyBase(T exClass, long rateMinute,
			ScheduledFuture<?> future) {
		return executeScheduleRateHaveDeleyBase(exClass, 0, rateMinute, future);
	}

	public static <T> ScheduledFuture<?> executeScheduleRateHaveDeleyBase(T exClass, long delayMinute, long rateMinute,
			ScheduledFuture<?> future) {
		// 前回定时任务取消
		if (future != null && !future.isCancelled()) {
			future.cancel(false);
		}
		ScheduledExecutorService executor = ScheduledExecutorServiceManager.getExecutorInstance()
				.getScheduledExecutorService();
		future = executor.scheduleAtFixedRate((Runnable) exClass, delayMinute, rateMinute, TimeUnit.MINUTES);
		return future;
	}
}
