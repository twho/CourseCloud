package com.coursecloud.util;

import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;

public class TimeUtilities {
	private static final String TAG = "TimeUtilities";

	/**
	 * get hour:minute
	 * 
	 * @return ex. 00:00, 12:32
	 */
	public static String getTimehhmm() {
		return (String) DateFormat.format("hh:mm", new Date());
	}

	/**
	 * get hour:minute
	 * 
	 * @return ex. 00:00, 12:32
	 */
	public static String getTimehhmm(Date d) {
		return (String) DateFormat.format("hh:mm", d);
	}

	/**
	 * get yyyyMMddhhmmss
	 * 
	 * @return ex. 20141105181533
	 */
	public static String getTimeyyyyMMddhhmmss(Date d) {
		return (String) DateFormat.format("yyyyMMddhhmmss", d);
	}

	/**
	 * get yyyyMMddhhmmss
	 * 
	 * @return ex. 20141105181533
	 */
	public static String getTimeyyyyMMddhhmmss() {
		return (String) DateFormat.format("yyyyMMddhhmmss", new Date());
	}

	public static String getTimeyyyyMMddHHmmss() {
		return (String) DateFormat.format("yyyyMMddHHmmss", new Date());
	}

	public static String getTimeyyyyMMddHHmm() {
		return (String) DateFormat.format("yyyyMMddHHmm", new Date());
	}

	public static String getTimeyyyy_MM_dd_HH_mm() {
		return (String) DateFormat.format("yyyy/MM/dd HH:mm", new Date());
	}

	public static String getTimeyyyyMMdd() {
		return (String) DateFormat.format("yyyyMMdd", new Date());
	}

	/**
	 * Sample : Date dateYesterday = new Date(new Date().getTime()- (1000 * 60 *
	 * 60 * 24)); String yesterday =
	 * TimeUtilities.getTimeyyyyMMddhhmmss(dateYesterday);
	 * TimeUtilities.isNowOverTime(yesterday);
	 * 
	 * @param deadlineTime
	 * @return
	 */
	public static boolean isNowOverTime(long deadlineTime) {
		if (StringToLong(getTimeyyyyMMddHHmm()) > deadlineTime) {
			return false;
		}
		return true;
	}

	public static boolean isNowOverTime(String deadlineTime) {
		return isNowOverTime(StringToLong(deadlineTime));
	}

	public static long StringToLong(String time) {
		return Long.parseLong(time);
	}

	public static String LongToString(long time) {
		return String.valueOf(time);
	}

}
