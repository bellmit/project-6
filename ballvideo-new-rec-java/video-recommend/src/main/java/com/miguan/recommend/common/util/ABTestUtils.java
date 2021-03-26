package com.miguan.recommend.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import static org.apache.commons.lang3.StringUtils.isNoneEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public class ABTestUtils {


    public static boolean isABChannel(String channel) {
        String use_recommend_channel = Global.getValue("use_recommend_channel");
        if (isNoneEmpty(channel) && isNotEmpty(use_recommend_channel) && use_recommend_channel.contains(channel)) {
            return true;
        }
        return false;
    }

    public static boolean isABUUid(String channel) {
        String use_recommend_uuid = Global.getValue("use_recommend_uuid");
        if (use_recommend_uuid != null && use_recommend_uuid.contains(channel)) {
            return true;
        }
        return false;
    }

    /**
     * 是否使用新推荐算法(使用取模算法，把deviceId进行md5加密后，在转成hashcode，用这个hashcode进行取模，如果取模后值是0，则使用新推荐算法)
     *
     * @return true：使用新推荐算法，false：使用旧推荐算法
     */
    public static boolean isABTestUser(String uuid) {
        //判定是不是指定测试用户
        String use_recommend_test = Global.getValue("use_recommend_test");
        log.info("{} 推荐 AB: testUser>>{}", uuid, use_recommend_test);
        if (use_recommend_test != null && use_recommend_test.contains(uuid)) {
            return true;
        }
        //使用新推荐算法mod值。如果值为0，则都用旧算法；如果值为1，则都使用新算法
        int useRecommendMod = Global.getInt("use_recommend_mod");
        if (useRecommendMod == 0) {
            return false;
        }
        if (useRecommendMod == 1) {
            return true;
        }
        try {
            String firtsalt = Global.getValue("use_recommend_salt");
            String md5 = DigestUtils.md5Hex(uuid.concat(firtsalt)).substring(16, 16 + 8);
            long t = Long.parseLong(md5, 16);
            if (t % useRecommendMod == 0) {
                return true;
            }
        } catch (Exception e) {
            log.error("A/B推荐用户取模异常", e);
        }
        return false;
    }

    public static boolean checkSubABTest(String uuid) {
        boolean isABTest = false;
        try {
            //判定是不是指定测试用户
            String use_recommend_test = Global.getValue("use_recommend_test");
            log.info("{} 推荐 SUB-AB: testUser>>{}", uuid, use_recommend_test);
            if (use_recommend_test != null && use_recommend_test.contains(uuid)) {
                isABTest = true;
            }
            int subuseRecommendMod = Global.getInt("use_recommend_submod");
            if (subuseRecommendMod == 1) {
                isABTest = true;
            } else if (subuseRecommendMod != 0) {
                String secondsalt = Global.getValue("use_recommend_subsalt");
                String md5 = DigestUtils.md5Hex(uuid.concat(secondsalt)).substring(16, 16 + 8);
                long t = Long.parseLong(md5, 16);

                int subuseRecommendModValue = Global.getInt("use_recommend_submod_value");
                log.warn("{} 推荐 SUB-AB 用户计算>>{},{},{}", uuid, t, subuseRecommendMod, subuseRecommendModValue);
                isABTest = (t % subuseRecommendMod == subuseRecommendModValue);
            }
            //是否使用新推荐算法
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐 SUB-AB 用户是否命中：{}", uuid, isABTest);
            }
        } catch (Exception e) {
            log.error("Sub A/B推荐用户取模异常", e);
        }
        return isABTest;
    }
}
