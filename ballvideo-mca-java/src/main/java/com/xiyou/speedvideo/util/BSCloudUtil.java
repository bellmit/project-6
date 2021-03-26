package com.xiyou.speedvideo.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/15 4:23 下午
 */
public class BSCloudUtil {

    private static String endPoint = "http://ss.bscstorage.com";
    private static String accessKey = "u2lj6mtnafb3vw7zr45x";
    private static String secretKey = "4EOjVeF5BGQKcPo8H22GjgM9HVlQL4TvP4hLwM3/";

    private static volatile AmazonS3 instance = null;

    public static AmazonS3 getS3Instance(){
        if(instance == null){
            synchronized(BSCloudUtil.class){
                if(instance == null){
                    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
                    ClientConfiguration clientconfiguration = new ClientConfiguration();
                    clientconfiguration.setSocketTimeout(60 * 60 * 1000);
                    clientconfiguration.setConnectionTimeout(60 * 60 * 1000);
                    instance = new AmazonS3Client(awsCreds, clientconfiguration);
                    instance.setRegion(Region.getRegion(Regions.US_EAST_1));
                    instance.setEndpoint(endPoint);
                    instance.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true)
                            .disableChunkedEncoding().build());
                }
            }
        }
        return instance;
    }

    public static boolean uploadFile(String filePath){
        File  file = new File(filePath);
        try {
            InputStream inputStream = new FileInputStream(file);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(getContentType(filePath));
            PutObjectRequest putObjectrequest = new PutObjectRequest(
                    "xiyou", "speed-video/"+getFileName(filePath), inputStream, objectMetadata);
            putObjectrequest.setCannedAcl(CannedAccessControlList.PublicReadWrite);
            PutObjectResult putObjectResult = getS3Instance().putObject(putObjectrequest);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取文件名
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath){
        return filePath.substring(filePath.lastIndexOf("/")+1);
    }

    /**
     * 获取文件类型
     * @param filePath
     * @return
     */
    public static String getContentType(String filePath){
        return filePath.substring(filePath.lastIndexOf(".")+1);
    }


    public static void main( String[] args ){
        String filePath = "/usr/local/video/246177-15832150828176.mp4";
        System.out.println(getContentType(filePath));
        System.out.println(getFileName(filePath));

    }

}
