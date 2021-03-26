package com.miguan.ballvideo.common.util.file;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.YmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import tool.util.StringUtil;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

/**
 * 封装白山云视频常用操作
 */
@Slf4j
public class AWSUtil4Video {

    /**
     * 视频源文件
     */
    public static final String FILE_TYPE_VIDEO_SOURCE = "SOURCE";
    /**
     * 视频图片(视频待播放时展示的图片)
     */
    public static final String FILE_TYPE_VIDEO_SCREEN = "SCREEN";
    /**
     * 用户发布视频文件夹
     */
    public final static String FLOAD_USER_VIDEO = "userVideos";

    public static String bscUrl;
    public static String accessKey;
    public static String secretKey;
    public static String endPoint;
    public static String bucketName;
    public static String appEnvironment;
    public static AmazonS3 s3;

    static {
        try {
            bscUrl = YmlUtil.getCommonYml("aws.bscUrl");
            endPoint = YmlUtil.getCommonYml("aws.endPoint");
            accessKey = YmlUtil.getCommonYml("aws.accessKey");
            secretKey = YmlUtil.getCommonYml("aws.secretKey");
            appEnvironment = Global.getValue("app_environment");
            if ("prod".equals(appEnvironment)) {
                bucketName = YmlUtil.getCommonYml("aws.video_prefix_prod");
            } else {
                bucketName = YmlUtil.getCommonYml("aws.video_prefix_dev");
            }
            //初始化
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            ClientConfiguration clientconfiguration = new ClientConfiguration();
            clientconfiguration.setSocketTimeout(60 * 60 * 1000);
            clientconfiguration.setConnectionTimeout(60 * 60 * 1000);
            s3 = new AmazonS3Client(awsCreds, clientconfiguration);
            s3.setEndpoint(endPoint);
            s3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).disableChunkedEncoding().build());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获取视频上传到白山云的地址
     *
     * @param userId
     * @param fload
     * @param fileType
     * @param currdate
     * @return
     */
    public static String getBsyUrl(String userId, String fload, String fileType, Date currdate) {
        String prefix = StringUtil.isBlank(userId) ? "" : userId + "_";
        String imgPicName = prefix + DateUtil.dateStr(currdate, DateUtil.DATEFORMAT_STR_016) + "." + fileType;
        return endPoint + "/" + bucketName + "/" + fload + "/" + DateUtil.dateStr(currdate, DateUtil.DATEFORMAT_STR_013) + "/" + imgPicName;
    }

    /**
     * @param file
     * @param prefix    文件名称前缀
     * @param folder    文件夹名称
     * @param type(head 头像 opinion 反馈意见)
     * @return
     * @description 图片上传
     */
    public static UploadFileModel upload(MultipartFile file, String prefix, String folder, String type, Date currdate) {

        UploadFileModel model = new UploadFileModel();
        model.setCreateTime(DateUtil.getNow());
        // 文件名称-特定前缀
        model.setOldName(file.getOriginalFilename());

        CommonsMultipartFile cf = (CommonsMultipartFile) file;
        DiskFileItem fi = (DiskFileItem) cf.getFileItem();
        // 文件格式
        String fileType = getFileType(file.getOriginalFilename());
        prefix = StringUtil.isBlank(prefix) ? "" : prefix + "_";
        String picName = prefix + DateUtil.dateStr(currdate, DateUtil.DATEFORMAT_STR_016) + "." + fileType;
        if(FILE_TYPE_VIDEO_SOURCE.equals(type)){
            if (StringUtil.isBlank(fileType) || !isVideo(fileType)) {
                model.setErrorMsg("视频格式错误或内容不规范");
                return model;
            }
        } else {
            if (StringUtil.isBlank(fileType) || !isImage(fileType)) {
                model.setErrorMsg("图片格式错误或内容不规范");
                return model;
            }
        }
        // 校验图片大小
        Long picSize = file.getSize();
        if (picSize.compareTo(104857600L) > 0) {
            model.setErrorMsg("文件超出20M大小限制");
            return model;
        }
        // 保存文件
        String s = "/";
        String filePath = folder + s + DateUtil.dateStr(currdate, DateUtil.DATEFORMAT_STR_013) + s + picName;
        filePath = uploadToAws(fi, filePath, file.getContentType());
        log.info("白山云上传视频地址>>{}", filePath);
        // 转存文件
        model.setResPath(filePath);
        model.setFileName(picName);
        model.setFileFormat(fileType);
        model.setFileSize(new BigDecimal(picSize));
        return model;
    }

    /**
     * 删除白山云对应的文件
     * @param url 链接地址
     * @param fload 所在文件夹
     */
    public static final void delete(String url, String fload){
        if(StringUtils.isNotBlank(url) && url.contains(fload)){
            String key = url.substring(url.indexOf(fload));
            s3.deleteObject(bucketName, key);
        }
    }

    public static final String getFileType(String fileName) {
        String filetype = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        return filetype;
    }

    /**
     * 上传到白山云
     *
     * @param fi
     * @param filePath
     */
    private static String uploadToAws(DiskFileItem fi, String filePath, String contentType) {
        try {
            return AWSUtil4Video.uploadFileToAWS(fi.getInputStream(), filePath, contentType);
        } catch (Exception e) {
            log.error("上传图片失败，filePath = " + filePath);
        }
        return filePath;
    }

    /**
     * 上传图片到BSC,并返回上传图片的URL
     *
     * @return
     */
    public static String uploadFileToAWS(InputStream is, String fileName, String contentType) {

        //上传开始
        String key = fileName;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        //设置文件类型
        objectMetadata.setContentType(contentType);
        PutObjectRequest putObjectrequest = new PutObjectRequest(bucketName, key, is, objectMetadata);

        //您可以使用下面两种方式为上传的文件指定ACL，后一种已被注释
        putObjectrequest.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        s3.putObject(putObjectrequest);

        URL url = s3.generatePresignedUrl(bucketName, key, new Date(119, 00, 22));
        String urlString = url.toString();
        String[] splitStr = urlString.split("\\?");
        return splitStr[0];
    }

    /**
     * 是否为图片类型
     *
     * @param fileType mp4|flv|avi|rm|rmvb|wmv|mov|m3u8|
     * @return
     */
    public static boolean isVideo(String fileType) {
        if ("mp4".equals(fileType) || "flv".equals(fileType) || "avi".equals(fileType) || "rm".equals(fileType)
                || "rmvb".equals(fileType) || "wmv".equals(fileType) || "mov".equals(fileType) || "m3u8".equals(fileType)) {
            return true;
        }
        return false;
    }

    /**
     *
     * 是否为图片类型
     * @param fileType jpeg|jpg|png|gif
     * @return
     */
    public static boolean isImage(String fileType) {
        if ("jpeg".equals(fileType) || "jpg".equals(fileType) || "png".equals(fileType) || "gif".equals(fileType)) {
            return true;
        }
        return false;
    }
}
