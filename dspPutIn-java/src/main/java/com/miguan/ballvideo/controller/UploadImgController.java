package com.miguan.ballvideo.controller;

import com.cgcg.context.util.StringUtils;
import com.miguan.ballvideo.common.constants.TypeConstant;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.file.FileUtil;
import com.miguan.ballvideo.common.util.file.UploadFileModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tool.util.StringUtil;

import java.util.Objects;


@Slf4j
@Api(value="上传文件 Controller",tags={"上传文件接口"})
@RestController
@RequestMapping("/api/uploadImg")
public class UploadImgController {

    @ApiOperation("上传图片到白山云")
    @PostMapping("/baiShanCloud")
    public ResultMap<String> baiShanCloud(
            @RequestPart MultipartFile file,@ApiParam(value = "上传类型：1：图片或者视频，2：logo图片") Integer type) {
        try {
            if(type == null){
                type = 1;
            }
            String fileType = FileUtil.getFileType(Objects.requireNonNull(file.getOriginalFilename()));
            String uploadType = TypeConstant.UPLOAD_FILE_TYPE_IMAGE;
            int size = 2;
            if(type == 1){
                // 文件格式
                if (StringUtil.isBlank(fileType)) {
                    return ResultMap.error("图片格式错误或内容不规范");
                }
                if(!FileUtil.isImageOrVideo(fileType)){
                    return ResultMap.error("目前仅支持:jpeg,jpg,png,gif,mp4,mov,avi格式");
                }
                if(FileUtil.isImage(fileType)){
                    uploadType = TypeConstant.UPLOAD_FILE_TYPE_IMAGE;
                    size = 10;
                } else {
                    uploadType = TypeConstant.UPLOAD_FILE_TYPE_VIDEO;
                    size = 50;
                }
            } else {
                if (StringUtil.isBlank(fileType)) {
                    return ResultMap.error("图片格式错误或内容不规范");
                }
                if(!FileUtil.isImage(fileType)){
                    return ResultMap.error("目前仅支持:jpeg,jpg,png,gif格式");
                }
            }

            UploadFileModel fileModel = FileUtil.upload(file, "", "imgAndVideo",size,uploadType);
            if(StringUtils.isNotEmpty(fileModel.getErrorMsg())){
                return ResultMap.error(fileModel.getErrorMsg());
            }
            return ResultMap.success(fileModel.getResPath());
        }  catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}
