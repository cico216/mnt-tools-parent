package com.mnt.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 时间工具类
 *
 * @author jiangbiao
 * @Date 2017年4月19日上午9:11:44
 */
public class TimeUtils {

	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static long getCurrentTime()
	{
		return System.currentTimeMillis();
	}
	
	/**
	 * 获取当前日期
	 * @return
	 */
	public static Date getCurrentDate()
	{
		return new Date();
	}
	
	/**
	 * 处理搜索结束时间 方便搜索
	 * @param endTime
	 */
	public static Date handleSearchEndTime(Date endTime)
	{
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(endTime);
		 calendar.set(Calendar.HOUR, 23);
		 calendar.set(Calendar.MINUTE, 59);
		 calendar.set(Calendar.SECOND, 59);
		 calendar.set(Calendar.MILLISECOND, 999);
		 return calendar.getTime();
	}
	
	/**
	 * 格式化为指定日期
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern)
	{
		if(pattern == null || pattern.isEmpty())
		{
			pattern = "yyyy-MM-dd";
		}
		if(date == null)
		{
			date = getCurrentDate();
		}
		
		DateFormat format = new SimpleDateFormat(pattern);
		
		return format.format(date);
	}
	
	/**
	 * 获取月初时间
	 * @return
	 */
	public static Date getMonthStart() {
		Calendar cDay = Calendar.getInstance();     
        cDay.setTime(getCurrentDate());     
        cDay.set(Calendar.DAY_OF_MONTH, 1);//老外将周日定位第一天，周一取第二天  
        cDay.set(Calendar.HOUR_OF_DAY, 0);
        cDay.set(Calendar.MINUTE, 0);
        cDay.set(Calendar.MILLISECOND, 0);
        cDay.set(Calendar.SECOND, 0);
        return cDay.getTime();
	}
	
	
	public static void main(String[] args) {
		System.err.println(getMonthStart());
	}
}
