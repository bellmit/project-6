package com.miguan.ballvideo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类-字符串处理
 * 
 * @author xx
 * @version 2.0
 * @since 2014年1月28日
 */
@Slf4j
public final class StringUtil extends tool.util.StringUtil {
	  
//    // 每位加权因子
//    private static int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
  
	/**
	 * 构造函数
	 */
	private StringUtil() {

	}
	/**
	 * 判断输入的手机号码是否有效
	 * 
	 * @param str 手机号码
	 * @return 检验结果（true：有效 false：无效）
	 */
	public static boolean isPhone(String str) {
		String phone = isNull(str);
		int length = phone.length();
		if(length != 11 && length != 13 && length != 14){
			return false;
		}
		if(phone.startsWith("+86")) {
			phone = phone.substring(3,length);
		} else if(phone.startsWith("86")) {
			phone = phone.substring(2,length);
		}
		Pattern regex = Pattern.compile("^\\d{11}$");
		Matcher matcher = regex.matcher(phone);
		boolean isMatched = matcher.matches();
		if(!isMatched){
			return false;
		}
//		String segment = phone.substring(0, 3);
//		String segments = Global.getValue("phone_number_segment");
//		if(segments.contains(segment)){
//			return true;
//		}
		return true;
	}
	
//	/**
//	 * 判断邮箱是否有效
//	 * @param str 邮箱
//	 * @return 检验结果（true：有效 false：无效）
//	 */
//	public static boolean isMail(String str) {
//		String mail = isNull(str);
//		Pattern regex = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
//		Matcher matcher = regex.matcher(mail);
//		boolean isMatched = matcher.matches();
//		return isMatched;
//	}

//	/**
//	 * 判断输入的身份证号码是否有效
//	 *
//	 * @param str 身份证号码
//	 * @return 检验结果（true：有效 false：无效）
//	 */
//	public static boolean isCard(String str) {
//		String cardId = isNull(str);
//		// 身份证正则表达式(15位)
//		Pattern isIDCard1 = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
//		// 身份证正则表达式(18位)
//		Pattern isIDCard2 = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$");
//		Matcher matcher1 = isIDCard1.matcher(cardId);
//		Matcher matcher2 = isIDCard2.matcher(cardId);
//		boolean isMatched = matcher1.matches() || matcher2.matches();
//		return isMatched;
//	}
	
	
	
//	/**
//	 * 根据身份证Id获取性别
//	 *
//	 * @param cardId
//	 * @return 性别: '男' / '女'
//	 */
//	public static String getSex(String cardId) {
//		int sexNum;
//		// 15位的最后一位代表性别，18位的第17位代表性别，奇数为男，偶数为女
//		if (cardId.length() == 15) {
//			sexNum = cardId.charAt(cardId.length() - 1);
//		} else {
//			sexNum = cardId.charAt(cardId.length() - 2);
//		}
//
//		if (sexNum % 2 == 1) {
//			return "男";
//		} else {
//			return "女";
//		}
//	}
	
    public static String toString(String separate,int...objs){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objs.length; i++) {
			if (i > 0)
				sb.append(separate);
			sb.append(objs[i]);
        }
        return sb.toString();
    }


//	public static String toStringArray(Object... list){
//		StringBuilder sb=new StringBuilder();
//		int index=0;
//
//		for (Object o : list) {
//			if(index>0)sb.append(",");
//			sb.append(o.toString());
//			index++;
//		}
//		return sb.toString();
//	}

	@SuppressWarnings("rawtypes")
	public static String toString(Collection list){
		return toString(list,",");
	}
	
    @SuppressWarnings("rawtypes")
	public static String toString(Collection list,String delim){
        StringBuilder sb=new StringBuilder();
		int index=0;

		for (Object o : list) {
			if(index>0)sb.append(delim);
			sb.append(o.toString());
			index++;
		}
        return sb.toString();
    }

//    public static String getRelativePath(File file,File parentFile){
//        return file.getAbsolutePath().replaceFirst("^\\Q"+parentFile.getAbsolutePath()+"\\E","").replace("\\","/");
//    }
//
//    @SuppressWarnings("deprecation")
//	public static String getFileUri(HttpServletRequest request, File file){
//        String pre=request.getRealPath("/");
//        String fullpath=file.getAbsolutePath();
//        return fullpath.replace(pre.replaceFirst("[\\\\/]$",""),"").replace("\\","/");
//    };
//
//    public static String getRepairedFileUri(String fullpath){
//        return fullpath.replaceFirst("[\\\\/]$","").replace("\\","/").replace("//","/");
//    };
	
//    /**
//     * 格式化金额数字为千分位显示；
//     * @param str
//     * @return
//     */
//	public static String fmtMicrometer(String str) {
//		DecimalFormat df;
//		if (str.indexOf(".") > -1) {
//			if (str.length() - str.indexOf(".") - 1 == 0) {
//				df = new DecimalFormat("###,##0.");
//			} else if (str.length() - str.indexOf(".") - 1 == 1) {
//				df = new DecimalFormat("###,##0.0");
//			} else {
//				df = new DecimalFormat("###,##0.00");
//			}
//		} else {
//			df = new DecimalFormat("###,##0");
//		}
//		double number = Double.parseDouble(str);
//		return df.format(number);
//	}
	
//	/**
//	 * 根据身份证获取年龄
//	 * @param idNo
//	 * @return
//	 * @throws ParseException
//	 */
//	public static Integer getAge(String idNo) throws ParseException {
//        String dates;
//        if(idNo != null && idNo.length()==15){
//        	idNo=convertIdcarBy15bit(idNo);
//        }
//        if (idNo != null && idNo.length() == 18) {
//            dates = idNo.substring(6, 14);
//            int year = DateUtil.daysBetween(DateUtil.valueOf(dates,DateUtil.DATEFORMAT_STR_012),DateUtil.getNow())/365;
//            return year;
//        }
//        return 0;
//	}
//	 /**
//     * 将15位的身份证转成18位身份证
//     *
//     * @param idcard
//     * @return
//	 * @throws ParseException
//     */
//    public static String convertIdcarBy15bit(String idcard) throws ParseException {
//        String idcard17;
//        // 非15位身份证
//        if (idcard.length() != 15) {
//            return null;
//        }
//
//        if (isDigital(idcard)) {
//            // 获取出生年月日
//            String birthday = idcard.substring(6, 12);
//            Date birthdate = new SimpleDateFormat("yyMMdd").parse(birthday);
//            Calendar cday = Calendar.getInstance();
//            cday.setTime(birthdate);
//            String year = String.valueOf(cday.get(Calendar.YEAR));
//
//            idcard17 = idcard.substring(0, 6) + year + idcard.substring(8);
//
//            char c[] = idcard17.toCharArray();
//            String checkCode;
//
//            if (null != c) {
////                int bit[] = new int[idcard17.length()];
//            	int bit[];
//                // 将字符数组转为整型数组
//                bit = converCharToInt(c);
//                int sum17 = getPowerSum(bit);
//
//                // 获取和值与11取模得到余数进行校验码
//                checkCode = getCheckCodeBySum(sum17);
//                // 获取不到校验位
//                if (null == checkCode) {
//                    return null;
//                }
//
//                // 将前17位与第18位校验码拼接
//                idcard17 += checkCode;
//            }
//        } else { // 身份证包含数字
//            return null;
//        }
//        return idcard17;
//    }
//    /**
//     * 将和值与11取模得到余数进行校验码判断
//     *
//     * @param sum17
//     * @return 校验位
//     */
//    public static String getCheckCodeBySum(int sum17) {
//        String checkCode = null;
//        switch (sum17 % 11) {
//        case 10:
//            checkCode = "2";
//            break;
//        case 9:
//            checkCode = "3";
//            break;
//        case 8:
//            checkCode = "4";
//            break;
//        case 7:
//            checkCode = "5";
//            break;
//        case 6:
//            checkCode = "6";
//            break;
//        case 5:
//            checkCode = "7";
//            break;
//        case 4:
//            checkCode = "8";
//            break;
//        case 3:
//            checkCode = "9";
//            break;
//        case 2:
//            checkCode = "x";
//            break;
//        case 1:
//            checkCode = "0";
//            break;
//        case 0:
//            checkCode = "1";
//            break;
//        }
//        return checkCode;
//    }
  
//    /**
//     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
//     *
//     * @param bit
//     * @return
//     */
//    public static int getPowerSum(int[] bit) {
//
//        int sum = 0;
//
//        if (power.length != bit.length) {
//            return sum;
//        }
//
//        for (int i = 0; i < bit.length; i++) {
//            for (int j = 0; j < power.length; j++) {
//                if (i == j) {
//                    sum = sum + bit[i] * power[j];
//                }
//            }
//        }
//        return sum;
//    }
//    /**
//     * 数字验证
//     *
//     * @param str
//     * @return
//     */
//    public static boolean isDigital(String str) {
//        return str == null || "".equals(str) ? false : str.matches("^[0-9]*$");
//    }
//    /**
//     * 将字符数组转为整型数组
//     *
//     * @param c
//     * @return
//     * @throws NumberFormatException
//     */
//    public static int[] converCharToInt(char[] c) throws NumberFormatException {
//        int[] a = new int[c.length];
//        int k = 0;
//        for (char temp : c) {
//            a[k++] = Integer.parseInt(String.valueOf(temp));
//        }
//        return a;
//    }
   
//    /**
//     * 字符转成map类型的
//     * 字符串："current=1&mobileType=1&pageSize=10"
//     */
//    public static Map<String,Object> convertStringToMap(String s){
//    	Map<String,Object> m=new HashMap<String,Object>();
//    	String[] couple=s.split("&");
//    	for (int i = 0; i < couple.length; i++) {
//			String[] single=couple[i].split("=");
//			if(single.length<2){
//				m.put(single[0], "");
//				continue;
//			}
//			m.put(single[0], single[1]);
//		}
//		return m;
//    }
//
//    /**
//	 * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
//	 * @param version1
//	 * @param version2
//	 * @return
//	 */
//	public static int compareVersion(String version1, String version2) throws Exception {
//	    if (version1 == null || version2 == null) {
//	        throw new Exception("compareVersion error:illegal params.");
//	    }
//	    String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
//	    String[] versionArray2 = version2.split("\\.");
//	    int idx = 0;
//	    int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
//	    int diff = 0;
//	    while (idx < minLength
//	            && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
//	            && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
//	        ++idx;
//	    }
//	    //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
//	    diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
//	    return diff;
//	}
	
	static int[] DAYS = { 0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };  
	  
//	/**
//	 * @param date yyyy-MM-dd HH:mm:ss  / yyyy-MM-dd
//	 * @return
//	 */
//	public static boolean isValidDate(String date) {
//	    try {
//	    	int year=0;
//	    	int month=0;
//	    	int day=0;
//	    	if(date.length()>5){
//		         year = Integer.parseInt(date.substring(0, 4));
//		        if (year <= 0)  return false;
//	    	}else{
//	    		return false;
//	    	}
//	    	if(date.length()>8){
//		         month = Integer.parseInt(date.substring(5, 7));
//		        if (month <= 0 || month > 12)
//		            return false;
//	    	}else{
//	    		return false;
//	    	}
//	    	if(date.length()>11){
//		         day = Integer.parseInt(date.substring(8, 10));
//		        if (day <= 0 || day > DAYS[month])
//		            return false;
//	    	}else{
//	    		return false;
//	    	}
//	        if (month == 2 && day == 29 && !isGregorianLeapYear(year)) {
//		            return false;
//		        }
//	        if(date.length()>20){
//		        int hour = Integer.parseInt(date.substring(11, 13));
//		        if (hour < 0 || hour > 23)
//		            return false;
//		        int minute = Integer.parseInt(date.substring(14, 16));
//		        if (minute < 0 || minute > 59)
//		            return false;
//		        int second = Integer.parseInt(date.substring(17, 19));
//		        if (second < 0 || second > 59)
//		            return false;
//	        }
//	    } catch (Exception e) {
//	        log.error(e.getMessage(),e);
//	        return false;
//	    }
//	    return true;
//	}
//	public static final boolean isGregorianLeapYear(int year) {
//	    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
//	}
	
 	public static String toString(Integer value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}
 	
//	/**
//	 * 生成len位数的随机码
//	 */
//	public static String randomNumAlph(int len) {
//        Random random = new Random();
//
//        StringBuilder sb = new StringBuilder();
//        byte[][] list = {
//            {48, 57},
//            {97, 122},
//            {65, 90}
//        };
//        for (int i = 0; i < len; i++) {
//            byte[] o = list[random.nextInt(list.length)];
//            byte value = (byte) (random.nextInt(o[1] - o[0] + 1) + o[0]);
//            sb.append((char) value);
//        }
//        return sb.toString();
//    }

//	/**
//	 * 判断字符串是否汉字数字大小写字母
//	 * @param str
//	 * @return
//	 */
//	public static boolean isLetterDigitOrChinese(String str) {
//		  String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";//其他需要，直接修改正则表达式就好
//		  return str.matches(regex);
//	}

//	/**
//	 * 生成随机数
//	 * @param aLength 随机数长度
//	 * @return
//	 */
//	public static String generateRandomDigitString(int aLength) {
//		SecureRandom tRandom = new SecureRandom();
//		long tLong;
//		tRandom.nextLong();
//		tLong = Math.abs(tRandom.nextLong());
//		String aString = (String.valueOf(tLong)).trim();
//		while (aString.length() < aLength) {
//			tLong = Math.abs(tRandom.nextLong());
//			aString += (String.valueOf(tLong)).trim();
//		}
//		aString = aString.substring(0, aLength);
//		return aString;
//	}
//
//	/**
//	 * 构造微信请求参数
//	 * @param payParam
//	 * @return
//	 */
//	public static String getWeixinPayXml(Map<String, String> payParam) {
//		String template = "<xml>\n" +
//				"<mch_appid>${mch_appid}</mch_appid>\n" +
//				"<mchid>${mchid}</mchid>\n" +
//				"<nonce_str>${nonce_str}</nonce_str>\n" +
//				"<partner_trade_no>${partner_trade_no}</partner_trade_no>\n" +
//				"<openid>${openid}</openid>\n" +
//				"<check_name>${check_name}</check_name>\n" +
//				"<amount>${amount}</amount>\n" +
//				"<desc>${desc}</desc>\n" +
//				"<spbill_create_ip>${spbill_create_ip}</spbill_create_ip>\n" +
//				"<sign>${sign}</sign>\n" +
//				"</xml>";
//		for(String key : payParam.keySet()) {
//			template = template.replace("${" + key + "}",payParam.get(key));
//		}
//
//		return template.trim();
//	}
//
//
//	public static String strConvertBase(String str) {
//		if (null != str) {
//			Base64.Encoder encoder = Base64.getEncoder();
//			return encoder.encodeToString(str.getBytes());
//		}
//		return null;
//
//	}
//
//
//	public static String baseConvertStr(String str) {
//		if (null != str) {
//			Base64.Decoder decoder = Base64.getDecoder();
//			try {
//				return new String(decoder.decode(str.getBytes()), "utf-8");
//			} catch (UnsupportedEncodingException e) {
//				return null;
//			}
//		}
//		return null;
//	}
//
//
//	public static boolean isBase64(String str) {
//		String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
//		return Pattern.matches(base64Pattern, str);
//	}

	/**
	 * <pre>
	 * 数字格式化显示
	 * 小于万默认显示 大于万以1.7万方式显示最大是9999.9万
	 * </pre>
	 * @param num 格式化的数字
	 * @return
	 */
	public static String formatNum(String num) {
		StringBuffer sb = new StringBuffer();
		if (!StringUtils.isNumeric(num)) {
			return "0";
		}
		BigDecimal b1 = new BigDecimal("10000");
		BigDecimal b3 = new BigDecimal(num);
		String formatNumStr = "";
		String nuit = "";
		// 以万为单位处理
		if (b3.compareTo(b1) == -1) {
			sb.append(b3.toString());
		} else if (b3.compareTo(b1) == 1) {
			formatNumStr = b3.divide(b1).toString();
			nuit = "万";
		}
		if (!"".equals(formatNumStr)) {
			int i = formatNumStr.indexOf(".");
			if (i == -1) {
				sb.append(formatNumStr).append(nuit);
			} else {
				i = i + 1;
				String v = formatNumStr.substring(i, i + 1);
				if (!v.equals("0")) {
					sb.append(formatNumStr.substring(0, i + 1)).append(nuit);
				} else {
					sb.append(formatNumStr.substring(0, i - 1)).append(nuit);
				}
			}
		}
		return sb.toString();
	}

	/**递归拆分数组
	 * @param source
	 * @param target
	 * @param num
	 * @param result
	 * @return
	 */
	public static boolean splitStringArray(String source,String target,int num,List<String> result ){
		int index =0;
		int sum = 0;
		if(source.indexOf(target)==-1){
			result.add(source);
			return true;
		}
		while((index=source.indexOf(target,index))!=-1){
			sum++;
			index++;
			if(sum > 0 && sum%num==0){
				String s =source.substring(0, index);
				s=s.substring(0, s.lastIndexOf(target));
				result.add(s);
				if(source.length()<index){
					result.add(source);
					return true;
				}
				source = source.substring(index);
				boolean t =splitStringArray(source, target, num, result);
				if(t){
					return true;
				}
			}
		}
		if(source.length()%num<num){
			result.add(source);
			return true;
		}
		return false;
	}

	public static List<String> strToList(String str){
		if(StringUtils.isEmpty(str))return null;
		String [] arrA = str.split(",");
		return Arrays.asList(arrA);
	}

	public static String listToString(List<String> strList) {
		if(strList == null || strList.size() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strList.size() ; i++) {
			if(i == 0){
				sb.append(strList.get(i));
			} else {
				sb.append(","+strList.get(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 获得UUID
	 * */
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-","");
	}

	/**
	 * 获取属性名数组
	 * */
	public static List<String> getFiledName(Object o){
		Field[] fields=o.getClass().getDeclaredFields();
		List<String> fieldNames=new ArrayList<>();
		for(int i=0;i<fields.length;i++){
			fieldNames.add(fields[i].getName());
		}
		return fieldNames;
	}

	public static boolean containStr(String[] pm, String flag) {
		if(pm == null || pm.length == 0){
			return false;
		}
		for ( String p :pm) {
			if(flag.equals(p)){
				return true;
			}
		}
		return false;
	}

	public static boolean existCatId(String videoCat, String catId) {
		if (StringUtil.isEmpty(catId)) {
			return false;
		}
		String[] videoCats = videoCat.split(",");
		for (String vc : videoCats) {
			if (catId.equals(vc)) return true;
		}
		return false;
	}

	public static boolean existGgCatId(String videoCatGg, String catId) {
		if(StringUtil.isEmpty(catId)){
			return false;
		}
		String[] videoCatGgs = videoCatGg.split(",");
		for (String vc:videoCatGgs) {
			if(catId.equals(vc)) return true;
		}
		return false;
	}

	//开关配置提取查询Ids
	public static String getIdString(String faChannelId, String name) {
		faChannelId = faChannelId + ":";
		String idStr = Global.getValue(name);
		String queryIds = "";
		if (idStr.contains(faChannelId)) {
			List<String> channelInfoList = Arrays.asList(idStr.split(";"));
			for (String channelInfo : channelInfoList) {
				if (channelInfo.contains(faChannelId)) {
					String[] str = channelInfo.split(":");
					queryIds = str[1];
					break;
				}
			}
		}
		return queryIds;
	}

	public static void printErrorLog(HttpServletRequest request, int type) {
		String deviceId = request.getParameter("deviceId");
		String paramStr = request.getRequestURL().toString() + "?";
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paramName = (String) enu.nextElement();
			String value = request.getParameter(paramName);
			paramStr = paramStr + paramName + "=" + value + "&";
		}
		String publicInfo = request.getHeader("Public-Info");
		if (type == 1) {
			if (StringUtils.isNotEmpty(publicInfo)) {
				log.info("设备号：{},publicInfo：{}", deviceId, publicInfo);
			}
			log.info("设备号：{},接口调用参数：{}", deviceId, paramStr);
		} else {
			if (StringUtils.isNotEmpty(publicInfo)) {
				log.error("设备号：{},publicInfo：{}", deviceId, publicInfo);
			}
			log.error("设备号：{},接口调用参数：{}", deviceId, paramStr);
		}
	}

}
