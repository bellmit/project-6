package com.miguan.xuanyuan.common.util.file;


import com.cgcg.context.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import tool.util.DateUtil;
import tool.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 校验一个file文件是否是真实的文件类型
 * （避免是用户在上传时将后缀修改掉，伪装
 * 其他类型的文件进行上传操作）
 *
 * @author RDuser
 */
@Slf4j
public class FileUtil {

    public static String getFileType(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 是否为图片类型
     *
     * @param fileType
     * @return
     */
    public static boolean isImage(String fileType) {
        return "jpeg".equals(fileType) || "jpg".equals(fileType) || "png".equals(fileType) || "gif".equals(fileType);
    }

    /**
     * @param file
     * @param prefix 文件名称前缀
     * @param folder 文件夹名称，紧接在/data/image之后
     * @param size
     * @return UploadFileRes
     * @description 图片上传
     * @author mcwang
     * @since 1.0.0
     */
    public static UploadFileModel upload(MultipartFile file, String prefix, String folder, int size) {

        UploadFileModel model = new UploadFileModel();
        model.setCreateTime(DateUtil.getNow());
        // 文件名称-特定前缀
        model.setOldName(file.getOriginalFilename());

        CommonsMultipartFile cf = (CommonsMultipartFile) file;
        DiskFileItem fi = (DiskFileItem) cf.getFileItem();
        // 文件格式
        String fileType = getFileType(Objects.requireNonNull(file.getOriginalFilename()));
        prefix = StringUtil.isBlank(prefix) ? "" : prefix + "_";
        String picName = prefix + DateUtil.dateStr(DateUtil.getNow(), DateUtil.DATEFORMAT_STR_016) + "." + fileType;

        if (StringUtil.isBlank(fileType)) {
            log.error("图片格式错误或内容不规范");
            model.setErrorMsg("图片格式错误或内容不规范");
            return model;
        }

        // 校验图片大小
        Long picSize = file.getSize();
//        if (picSize.compareTo(size * 1024 * 1024L) > 0) {
//            log.error("文件超出"+size+"M大小限制");
//            model.setErrorMsg("文件超出"+size+"M大小限制");
//            return model;
//        }
        // 保存文件
        String s = "/";
        String filePath = "data" + s + s + folder + s +
                DateUtil.dateStr(DateUtil.getNow(), DateUtil.DATEFORMAT_STR_013) + s + picName;
        filePath = uploadToAws(fi, filePath);

        // 转存文件
        model.setResPath(filePath);
        model.setFileName(picName);
        model.setFileFormat(fileType);
        model.setFileSize(new BigDecimal(picSize));
        return model;
    }

    /**
     * 上传图片到白山云
     *
     * @param fi
     * @param filePath
     */
    private static String uploadToAws(DiskFileItem fi, String filePath) {
        try {
            return AWSUtil.uploadFileToAWS(fi.getInputStream(), filePath);
        } catch (Exception e) {
            log.error("上传图片失败，filePath = " + filePath);
            return null;
        }
    }

    public static FileItem getFileItem(byte[] fileByte) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[8192];
        final FileItemFactory factory = new DiskFileItemFactory(16, null);
        final String textFieldName = "textField";
        final FileItem item = factory.createItem(textFieldName, "text/plain", true, UUIDUtils.getUUID());
        final InputStream fis = new ByteArrayInputStream(fileByte);
        final OutputStream os = item.getOutputStream();
        while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        fis.close();
        return item;
    }
}  