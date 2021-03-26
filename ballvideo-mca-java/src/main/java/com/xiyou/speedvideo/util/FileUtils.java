package com.xiyou.speedvideo.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileUtils {
    public static void writeLineToFile(List<String> rows, String fileName) throws IOException {
        //写入中文字符时解决中文乱码问题
        //简写如下：
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(new File(fileName)), "UTF-8"));
        for (int i = 0; i < rows.size(); i++) {
            writer.write(rows.get(i) + "\n");
        }
        //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
        writer.close();
    }

    public static List<String> readLineFromFile(String name) {
        // 使用ArrayList来存储每行读取到的字符串
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(name);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * 迭代删除文件夹
     *
     * @param dirPath 文件夹路径
     */

    public static void deleteDir(String dirPath) {
        File file = new File(dirPath);// 读取
        if (file.isFile()) { // 判断是否是文件夹
            file.delete();// 删除
        } else {
            File[] files = file.listFiles(); // 获取文件
            if (files == null) {
                file.delete();// 删除
            } else {
                for (int i = 0; i < files.length; i++) {// 循环
                    deleteDir(files[i].getAbsolutePath());
                }
                file.delete();// 删除
            }
        }
    }

    public static void mkdir(String dirName){
        File dir = new File(dirName);
        if(!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static String execCmd(String cmd) {
        StringBuffer sb = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            log.info("exe");
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void main(String[] args){
        String cmd = "sh /tmp/ffmpeg_speed/1602741657335722/speed.sh";
        execCmd(cmd);
    }
}

