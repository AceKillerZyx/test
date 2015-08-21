package com.education.zhxy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	private static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 获得默认的 date pattern
	 */
	public static String getDatePattern() {
		return defaultDatePattern;
	}

	/**
	 * 返回预设Format的当前日期字符串
	 */
	public static String getToday() {
		Date today = new Date();
		return format(today);
	}

	/**
	 * 使用预设Format格式化Date成字符串
	 */
	public static String format(Date date) {
		return date == null ? " " : format(date, getDatePattern());
	}

	/**
	 * 使用参数Format格式化Date成字符串
	 */
	public static String format(Date date, String pattern) {
		return date == null ? " " : new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 使用预设格式将字符串转为Date
	 */
	public static Date parse(String strDate) throws ParseException {
		return StringUtil.isEmpty(strDate) ? null : parse(strDate,
				getDatePattern());
	}

	/**
	 * 使用参数Format将字符串转为Date
	 */
	public static Date parse(String strDate, String pattern)
			throws ParseException {
		return StringUtil.isEmpty(strDate) ? null : new SimpleDateFormat(
				pattern).parse(strDate);
	}

	/**
	 * 在日期上增加数个整月
	 */
	public static Date addMonth(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, n);
		return cal.getTime();
	}

	public static String getLastDayOfMonth(String year, String month) {
		Calendar cal = Calendar.getInstance();
		// 年
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		// 月，因为Calendar里的月是从0开始，所以要-1
		// cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		// 日，设为一号
		cal.set(Calendar.DATE, 1);
		// 月份加一，得到下个月的一号
		cal.add(Calendar.MONTH, 1);
		// 下一个月减一为本月最后一天
		cal.add(Calendar.DATE, -1);
		return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));// 获得月末是几号
	}

	public static Date getDate(String year, String month, String day)
			throws ParseException {
		String result = year + "- "
				+ (month.length() == 1 ? ("0 " + month) : month) + "- "
				+ (day.length() == 1 ? ("0 " + day) : day);
		return parse(result);
	}

	/**
	 * 计算两个时间差
	 * 
	 * @param endDate
	 * @param nowDate
	 * @return
	 */
	public static String getDatePoor(Date endDate, Date nowDate) {
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		long ns = 1000;// 一秒钟的毫秒数
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		long sec = diff % nd % nh % nm / ns ;
		return day + "*" + hour + "-" + min + "&" + sec;
	}

	
	public static String getWeek(String date){
		String week = "星期";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 也可将此值当参数传进来
			Date curDate = parse(date);
			String pTime = format.format(curDate);
			Calendar c = Calendar.getInstance();
			try {
				c.setTime(format.parse(pTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			switch (c.get(Calendar.DAY_OF_WEEK)) {
				case 1:
					week += "天";
					break;
				case 2:
					week += "一";
					break;
				case 3:
					week += "二";
					break;
				case 4:
					week += "三";
					break;
				case 5:
					week += "四";
					break;
				case 6:
					week += "五";
					break;
				case 7:
					week += "六";
					break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return week;
	}

}
