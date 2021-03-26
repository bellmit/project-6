package com.xiyou.speedvideo.util;

import com.xiyou.speedvideo.constant.FFMpegConstant;
import com.xiyou.speedvideo.entity.FirstVideosMca;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/15 10:45 上午
 */
public class CommonUtil {

    public static void main(String[] args){
//        System.out.println(renameLocalPath("/usr/local/video/246177-15832150828176.mp4","-4x"));
//        String cmd = "ffmpeg -i /usr/local/video/246177-15832150828176.mp4 -filter_complex \"[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]\" -map \"[v]\" -map \"[a]\" /usr/local/video/246177-15832150828176-2x.mp4";
//        System.out.println(getSpeedLocalPath(cmd));
//        System.out.println(getSpeed(cmd));
//        String videoIds = "111,123";
//        System.out.println(string2List(videoIds,","));
        FirstVideosMca mca = new FirstVideosMca();
        mca.setVideoTime("11:00");
        mca.setLocalPath("/usr/local/video/246177-15832150828176.mp4");
        System.out.println(get1xCmd(mca));

    }


    /**
     * 以特定字符分隔的字符串转成List
     * @param videoIds
     * @return
     */
    public static List<String> string2List(String videoIds,String splitStr){
        if(StringUtils.isEmpty(videoIds)){
            return null;
        }
        return Arrays.asList(videoIds.split(splitStr));
    }

    private static String renameLocalPath(String localPath,String addString){
        int index = localPath.lastIndexOf('.');
        String prefix = localPath.substring(0,index);
        String end = localPath.substring(index);
        return prefix+addString+end;
    }

    public static String speed2LocalPath(String speedPath){
        return speedPath.replace("-1x","").replace("-2x","").replace("-4x","").replace("-8x","");
    }

    /**
     * 1分半钟以内 原速
     * 1分半~3分钟 两倍速
     * 3 ~ 6 分钟 四倍速
     * 6~ 10分钟 8倍速
     * 原速先不跑
     * @return
     */
    public static String getCmdByRule(FirstVideosMca mca){
        String videoTime = mca.getVideoTime();
        String result = null;
        if(!StringUtils.isEmpty(videoTime)){
            String[] strs = videoTime.split(":");
            Integer minute = Integer.parseInt(strs[0]);
            Integer seconds = Integer.parseInt(strs[1]);
            Integer secondsCount = minute*60+seconds;
            if(secondsCount>360 & secondsCount<=600){
                result = get8xCmd(mca);
            }else if(secondsCount>180){
                result = get4xCmd(mca);
            }else if(secondsCount>90){
                result = get2xCmd(mca);
            }else if(secondsCount>0 && secondsCount<=90){
                result = get1xCmd(mca);
            }
        }
        return result;

    }

    public static String get1xCmd(FirstVideosMca mca){
        return "cp "+mca.getLocalPath() +" "+renameLocalPath(mca.getLocalPath(),"-1x");
    }

    /**
     * 获取2倍速ffmpeg脚本
     * @param mca
     * @return
     */
    public static String get2xCmd(FirstVideosMca mca){
        return String.format(FFMpegConstant.FFMPEG_2X, mca.getLocalPath(),renameLocalPath(mca.getLocalPath(),"-2x"));
    }

    /**
     * 获取4倍速ffmpeg脚本
     * @param mca
     * @return
     */
    public static String get4xCmd(FirstVideosMca mca){
        return String.format(FFMpegConstant.FFMPEG_4X, mca.getLocalPath(),renameLocalPath(mca.getLocalPath(),"-4x"));
    }

    /**
     * 获取8倍速ffmpeg脚本
     * @param mca
     * @return
     */
    public static String get8xCmd(FirstVideosMca mca){
        return String.format(FFMpegConstant.FFMPEG_8X, mca.getLocalPath(),renameLocalPath(mca.getLocalPath(),"-8x"));
    }

    /**
     * 根据ffmpeg脚本获得本地视频地址
     * @param cmd
     * @return
     */
    public static String getSpeedLocalPath(String cmd){
        String[] splits = cmd.split(" ");
        return splits[splits.length-1];
    }

    public static Integer getSpeed(String cmd){
        return Integer.parseInt(cmd.substring(cmd.lastIndexOf("-")+1,cmd.lastIndexOf(".")-1));
    }

}
