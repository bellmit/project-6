package com.miguan.recommend.common.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lsk on 2017/2/14.
 */
@Slf4j
public class DateUtil extends tool.util.DateUtil{
	
//	public static final SimpleDateFormat FORMAT_yyyyMMddHHmmss =  new SimpleDateFormat("yyyyMMddHHmmss");
    public static final String DATEFORMAT_STR_010 = "yyyy-MM-dd HH:mm:ss";
	
	public static String format(SimpleDateFormat dateFormat, Date date){
		return dateFormat.format(date);
	}

//    @SuppressWarnings("deprecation")
//	public static Date dateAddMins(Date date, int minCnt) {
//        Date d = new Date(date.getTime());
//        d.setMinutes(d.getMinutes() + minCnt);
//        return d;
//    }
    
    /**
     * 计算时间差,单位分
     * @param date1
     * @param date2
     * @return
     */
    public static int minuteBetween(Date date1, Date date2){
		DateFormat sdf=new SimpleDateFormat(DATEFORMAT_STR_001);
		Calendar cal = Calendar.getInstance();
		try {
			Date d1 = sdf.parse(DateUtil.dateStr4(date1));
			Date d2 = sdf.parse(DateUtil.dateStr4(date2));
			cal.setTime(d1);
			long time1 = cal.getTimeInMillis();
			cal.setTime(d2);
			long time2 = cal.getTimeInMillis();
			return Integer.parseInt(String.valueOf((time2 - time1) / 60000));
		} catch (ParseException e) {
			log.error(e.getMessage(),e);
		}
		return 0;
	}

//	/**
//	 * 计算两个日期是否在day天以内(开始日期取当前天00:00:00)
//	 * @param start 开始日期
//	 * @param end   结束日期
//	 * @param day   天数
//	 * @return
//	 */
//	public static boolean dateSuperLong(Date start, Date end,int day) {
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(start);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);// 时
//		calendar.set(Calendar.MINUTE, 0);// 分
//		calendar.set(Calendar.SECOND, 0);// 秒
//		calendar.set(Calendar.MILLISECOND, 0); // 毫秒
//		long startLong = calendar.getTimeInMillis();
//		long dateLong = 1000l *60l*60l*24l  * (long)day;
// 		long value = end.getTime() - startLong - dateLong;
//		return value < 0;
//	}


//	public static Date getLastSecIntegralTime(Date d) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(d.getTime());
//		cal.set(11, 23);
//		cal.set(13, 59);
//		cal.set(12, 59);
//		cal.set(14, 0);
//		return cal.getTime();
//	}
    
//	/**
//	 * 获取指定时间天的开始时间
//	 *
//	 * @param date
//	 * @return
//	 */
//	public static Date getDayStartTime(Date date) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(date.getTime());
//		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
//				cal.get(Calendar.DATE), 0, 0, 0);
//		return cal.getTime();
//	}

	/**
	 * 获取指定时间天的结束时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDayEndTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE), 23, 59, 59);
		return cal.getTime();
	}

//	/**
//	 * String转化Date格式
//	 * @param date
//	 * @param type
//	 * @return
//	 */
//	public static Date parse(String date,String type){
//		SimpleDateFormat formatter = new SimpleDateFormat(type);
//		ParsePosition pos = new ParsePosition(0);
//		Date strtodate = formatter.parse(date, pos);
//		return strtodate;
//
//	}
	
//	/**
//	 * 得到指定日期之间的天数集合
//	 * @param startDate
//	 * @param endDate
//	 * @return
//	 * @throws Exception
//	 */
//	public static List<Date> dateSplit(Date startDate, Date endDate)
//	        throws Exception {
//	    if (!startDate.before(endDate))
//	        throw new Exception("开始时间应该在结束时间之后");
//	    Long spi = endDate.getTime() - startDate.getTime();
//	    Long step = spi / (24 * 60 * 60 * 1000);// 相隔天数
//
//	    List<Date> dateList = new ArrayList<Date>();
//	    dateList.add(endDate);
//	    for (int i = 1; i <= step; i++) {
//	        dateList.add(new Date(dateList.get(i - 1).getTime()
//	                - (24 * 60 * 60 * 1000)));// 比上一天减一
//	    }
//	    return dateList;
//	}
	
//	/**
//	 * 得到指定日期之间的月数集合
//	 * @param minDate
//	 * @param maxDate
//	 * @return
//	 * @throws ParseException
//	 */
//	public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException{
//	    ArrayList<String> result = new ArrayList<String>();
//	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
//
//	    Calendar min = Calendar.getInstance();
//	    Calendar max = Calendar.getInstance();
//
//	    min.setTime(sdf.parse(minDate));
//	    min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
//
//	    max.setTime(sdf.parse(maxDate));
//	    max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
//
//	    Calendar curr = min;
//	    while (curr.before(max)) {
//	     result.add(sdf.format(curr.getTime()));
//	     curr.add(Calendar.MONTH, 1);
//	    }
//
//	    return result;
//	  }
	
	/**
	 * 得到指定之前的前后几天
	 * @param day  把日期往后增加一天.整数往后推,负数往前移动
	 * @param date
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getDateStrBefore(int day,Date date,String type){
		SimpleDateFormat sdf = new SimpleDateFormat(type);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, day);
		return sdf.format(c.getTime());
	}
//
//	@SuppressWarnings("deprecation")
//	public static Date dateAddDays(Date date, int days) {
//		Date d = new Date(date.getTime());
//		d.setDate(d.getDate() + days);
//		return d;
//	}
//
//	public static Date dateAddMonths(Date date, int months) {
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		c.add(Calendar.MONTH, months);
//		return c.getTime();
//	}
	
	
//	/**
//	 *  分钟转天、时、分
//	 * @param minute
//	 * @return
//	 */
//	public static String minuteToTimes(int minute){
//		String DateTimes = null;
//		long days = minute / ( 60 * 24);
//		long hours = (minute % ( 60 * 24)) / 60;
//		long minutes = minute % 60;
//		if(days>0){
//		   DateTimes= days + "天" + hours + "小时" + minutes + "分钟";
//		}else if(hours>0){
//		   DateTimes=hours + "小时" + minutes + "分钟";
//		}else if(minutes>0){
//		   DateTimes=minutes + "分钟";
//		}
//
//		return DateTimes;
//	}
	/**
     * 格式化Date时间
     * @param time Date类型时间
     * @param timeFromat String类型格式
     * @return 格式化后的字符串
     */
    public static String parseDateToStr(Date time, String timeFromat){
    	DateFormat dateFormat=new SimpleDateFormat(timeFromat);
    	return dateFormat.format(time);
    }

	/**
	 * 是否是当天
	 *
	 * @param date
	 * @return
	 */
	public static boolean isTheSameDay(Date date) {
		return DateUtil.parseDateToStr(new Date(), "yyyy-MM-dd").equals(DateUtil.parseDateToStr(date, "yyyy-MM-dd"));
	}

  /**
   * 获取当天剩余时间
   * @param currentDate
   * @return
   */
  public static Integer getLastSecondsByDay(Date currentDate) {
		LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
				ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
				.withSecond(0).withNano(0);
		LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
				ZoneId.systemDefault());
		long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
		return (int) seconds;
	}

  /**
   * 获取往期最近的周六
   * @param date
   * @return
   */
  public static String getLatelySaturday(Date date) {
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
			return DateUtil.parseDateToStr(date,"MMdd");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);//当前时间减去一年，即一年前的时间
		return getLatelySaturday(calendar.getTime());
    }

//	/**
//	 * 判断是否是同一天
//	 * @param day1
//	 * @param day2
//	 * @return
//	 */
//	public static boolean isSameDay(Date day1, Date day2) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		String ds1 = sdf.format(day1);
//		String ds2 = sdf.format(day2);
//		return ds1.equals(ds2);
//	}

//	/**
//	 * 获取展期日期
//	 * @param oldRepayTime
//	 * @param timeLimit
//	 * @return
//	 */
//	public static Date getRenewDay(Date oldRepayTime,String timeLimit) {
//		Date now = getNow();
//		Date repayTime = null;
//		// 当前时间在还款之前 ，则认为未逾期
//		if (now.before(oldRepayTime)) {
//			repayTime = rollDay(oldRepayTime, NumberUtil.getInt(timeLimit));
//		} else { // 项目逾期展期
//			repayTime = rollDay(now, NumberUtil.getInt(timeLimit) - 1);
//			repayTime = getDayEndTime(repayTime);
//		}
//
//		return repayTime;
//	}

//	/**
//	 * 获取 10位时间戳
//	 *
//	 * @return
//	 */
//	public static Integer getDateInt() {
//		return (int) (System.currentTimeMillis() / 1000);
//	}

	/**
	 * 计算每天还剩多少秒
	 */
	public static Integer caluRedisExpiredTime() {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		final double seconds = (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000d;
		return (int) (seconds);
	}

	/**
	 * 获取当天日期 日期格式：'2011-12-03'.
	 * @return
	 */
	public static String yyyy_MM_dd() {
		return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
	}
}
