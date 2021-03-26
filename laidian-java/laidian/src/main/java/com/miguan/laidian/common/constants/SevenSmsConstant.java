package com.miguan.laidian.common.constants;

import java.util.HashMap;
import java.util.Map;

public class SevenSmsConstant {
    
    public static final Map<Integer, String> MESSAGE = new HashMap<>();
    
    static {
        MESSAGE.put(0, "发送成功");
        MESSAGE.put(101, "无此用户");
        MESSAGE.put(102, "短信密码错误");
        MESSAGE.put(103, "提交过快（提交速度超过流速限制）");
        MESSAGE.put(104, "系统忙（因平台侧原因，暂时无法处理提交的短信）");
        MESSAGE.put(105, "敏感短信（短信内容包含敏感词）");
        MESSAGE.put(106, "消息长度错误（>700或<=0）");
        MESSAGE.put(107, "包含错误的手机号码");
        MESSAGE.put(108, "手机号码个数错（>50000或<=0）");
        MESSAGE.put(109, "无发送额度（该用户可用短信条数为0）");
        MESSAGE.put(110, "不在发送时间内");
        MESSAGE.put(111, "超出该账户当月发送额度限制");
        MESSAGE.put(112, "无此产品，用户没有订购该产品");
        MESSAGE.put(113, "extno格式错（非数字或者长度不对）");
        MESSAGE.put(115, "自动审核驳回");
        MESSAGE.put(116, "签名不合法，未带签名（用户必须带签名的前提下）");
        MESSAGE.put(117, "IP地址认证错,请求调用的IP地址不是系统登记的IP地址");
        MESSAGE.put(118, "用户没有相应的发送权限");
        MESSAGE.put(119, "用户已过期");
        MESSAGE.put(120, "内容不在白名单模板中");
    }
}
