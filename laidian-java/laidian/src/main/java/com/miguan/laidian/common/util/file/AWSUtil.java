package com.miguan.laidian.common.util.file;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.YmlUtil;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import tool.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

/**
 * 封装白山云常用操作
 */
public class AWSUtil {
    private static Logger logger = LoggerFactory.getLogger(AWSUtil.class);

    public static String bscUrl;
    public static String accessKey;
    public static String secretKey;
    public static String endPoint;
    public static String bucketName;

    public static AmazonS3 s3;

    static {
        try {
            bscUrl = YmlUtil.getCommonYml("aws.bscUrl");
            endPoint = YmlUtil.getCommonYml("aws.endPoint");
            accessKey = YmlUtil.getCommonYml("aws.accessKey");
            secretKey = YmlUtil.getCommonYml("aws.secretKey");
            bucketName = YmlUtil.getCommonYml("aws.bucketName");
            //初始化
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            ClientConfiguration clientconfiguration = new ClientConfiguration();
            clientconfiguration.setSocketTimeout(60 * 60 * 1000);
            clientconfiguration.setConnectionTimeout(60 * 60 * 1000);
            s3 = new AmazonS3Client(awsCreds, clientconfiguration);
            s3.setEndpoint(endPoint);
            s3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).disableChunkedEncoding().build());

        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    /**
     * 上传图片到BSC,并返回上传图片的URL
     * @return
     */
    public static String uploadFileToAWS(InputStream is,String fileName){

        //上传开始
        String key = fileName;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        //设置文件类型
        objectMetadata.setContentType(getContentType(fileName));
        PutObjectRequest putObjectrequest = new PutObjectRequest(bucketName, key, is, objectMetadata);

        //您可以使用下面两种方式为上传的文件指定ACL，后一种已被注释
        putObjectrequest.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        s3.putObject(putObjectrequest);

        URL url= s3.generatePresignedUrl(bucketName,key,new Date(119,00,22));
        String urlString = url.toString();
        String[] splitStr = urlString.split("\\?");
        return splitStr[0];
    }

    /**
     * 根据后缀名获取图片MIME类型
     * @param fileName
     * @return
     */
    public static String getContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if ("gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if ("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension) || "png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if ("html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if ("txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if ("vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if ("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if ("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if ("xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        if ("mp4".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if ("m4a".equalsIgnoreCase(fileExtension)) {
            return "audio/m4a";
        }
        if ("aac".equalsIgnoreCase(fileExtension)) {
            return "audio/aac";
        }
        return ".*";
    }
    /**
     * @description 图片上传
     * @param file
     * @param prefix 文件名称前缀
     * @param folder 文件夹名称
     * @param type(head 头像  opinion 反馈意见)
     * @return

     */
    public static UploadFileModel upload(MultipartFile file, String prefix, String folder , String type,Date currDate) {

        UploadFileModel model = new UploadFileModel();
        model.setCreateTime(DateUtil.getNow());
        // 文件名称-特定前缀
        model.setOldName(file.getOriginalFilename());

        CommonsMultipartFile cf = (CommonsMultipartFile) file;
        DiskFileItem fi = (DiskFileItem) cf.getFileItem();
        // 文件格式
        String fileType = getFileType(file.getOriginalFilename());
        String picName ="";
        if("head".equals(type)){
            prefix = StringUtil.isBlank(prefix) ? "" : prefix;
            picName = "user_"+prefix+ "." + fileType;
        } else {
            prefix = StringUtil.isBlank(prefix) ? "" : prefix + "_";
            picName = prefix + DateUtil.dateStr(currDate,DateUtil.DATEFORMAT_STR_016) + "." + fileType;
        }
        if (StringUtil.isBlank(fileType) || !isImage(fileType)) {
            model.setErrorMsg("图片格式错误或内容不规范");
            return model;
        }
        // 校验图片大小
        Long picSize = file.getSize();
        if (picSize.compareTo(20971520L) > 0) {
            model.setErrorMsg("文件超出20M大小限制");
            return model;
        }
        // 保存文件
        String s = "/";
        String filePath = folder+s+DateUtil.dateStr(currDate, DateUtil.DATEFORMAT_STR_013) + s + picName;
        if ("vedio".equals(type)){
            filePath = folder+s+"uploadImgs"+s+DateUtil.dateStr(currDate, DateUtil.DATEFORMAT_STR_013) + s + picName;
        }
        filePath = uploadToAws(fi,filePath);

        // 转存文件
        model.setResPath(filePath);
        model.setFileName(picName);
        model.setFileFormat(fileType);
        model.setFileSize(new BigDecimal(picSize));
        return model;
    }

    /**
     * 上传到白山云
     * @param fi
     * @param filePath
     */
    private static String uploadToAws(DiskFileItem fi, String filePath) {
        try{
            return AWSUtil.uploadFileToAWS(fi.getInputStream(),filePath);
        } catch (Exception e) {
            logger.error("上传图片失败，filePath = " + filePath);
        }
        return filePath;
    }

    /**
     * 上传到白山云
     * @param fi
     * @param filePath
     */
    private static String uploadVideoToAws(File fi, String filePath) {
        try{
            return AWSUtil.uploadFileToAWS(new FileInputStream(fi),filePath);
        } catch (Exception e) {
            logger.error("上传图片视频，filePath = " + filePath);
        }
        return filePath;
    }

    public static final String getFileType(String fileName) {
        String filetype = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length());
        return filetype;
    }

    /**
     *
     * 是否为图片类型
     * @param fileType
     * @return
     */
    public static boolean isImage(String fileType) {
        if ("jpeg".equals(fileType) || "jpg".equals(fileType) || "png".equals(fileType) || "gif".equals(fileType)) {
            return true;
        }
        return false;
    }

    /**
     * @description 视频上传
     * @param file
     * @param prefix 文件名称前缀
     * @param folder 文件夹名称
     * @param currDate 当前时间
     * @return
     */
    public static UploadFileModel uploadVideos(MultipartFile file, String prefix, String folder,Date currDate,String tmpFilePath) {

        UploadFileModel model = new UploadFileModel();
        model.setCreateTime(DateUtil.getNow());
        // 文件名称-特定前缀
        model.setOldName(file.getOriginalFilename());

//        CommonsMultipartFile cf = (CommonsMultipartFile) file;
//        DiskFileItem fi = (DiskFileItem) cf.getFileItem();

        // 文件格式
        String fileType = getFileType(file.getOriginalFilename());
        prefix = StringUtil.isBlank(prefix) ? "" : prefix + "_";
        String picName = prefix + DateUtil.dateStr(currDate,DateUtil.DATEFORMAT_STR_016) + "." + fileType;
        //校验大小
        Long picSize = file.getSize();
        if (picSize.compareTo(104857600L) > 0) {
            model.setErrorMsg("文件超出100M大小限制");
            return model;
        }
        // 保存文件
        String s = "/";
        String filePath = folder+s+"uploadVideos"+s+DateUtil.dateStr(currDate, DateUtil.DATEFORMAT_STR_013) + s + picName;
        File tmpFile = new File(tmpFilePath);
        filePath = uploadVideoToAws(tmpFile,filePath);
        tmpFile.delete();

        // 转存文件
        model.setResPath(filePath);
        model.setFileName(picName);
        model.setFileFormat(fileType);
        model.setFileSize(new BigDecimal(picSize));
        return model;
    }
}
