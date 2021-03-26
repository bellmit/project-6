package com.xiyou.speedvideo.util;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
public class DownloadURLFile {  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
  
//        String res = downloadFromUrl("http://xiyou.sc.diyixin.com/dev-video-xiyou/20190820/15662683247909.mp4","/Users/huangjx/Downloads/test/");
        String res = getFileNameFromUrl("/usr/local/video/257145-15836342641875-2x.mp4");
        System.out.println(res);
    }

    public static String downloadFromUrl(String url,String dir,String prefix) {
        try {
            URL httpurl = new URL(url);
            String fileName = prefix+"-"+getFileNameFromUrl(url);
            System.out.println(fileName);
            String filePath = dir + File.separator + fileName;
            File f = new File(filePath);
            FileUtils.copyURLToFile(httpurl, f);
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String downloadFromUrl(String url,String dir) {
  
        try {  
            URL httpurl = new URL(url);  
            String fileName = getFileNameFromUrl(url);  
            System.out.println(fileName);
            String filePath = dir + File.separator + fileName;
            File f = new File(filePath);
            FileUtils.copyURLToFile(httpurl, f);
            return filePath;
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;
        }
    }  
      
    public static String getFileNameFromUrl(String url){  
        String name = new Long(System.currentTimeMillis()).toString() + ".X";  
        int index = url.lastIndexOf("/");  
        if(index > 0){  
            name = url.substring(index + 1);  
            if(name.trim().length()>0){  
                return name;  
            }  
        }  
        return name;  
    }

} 