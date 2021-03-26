package com.miguan.xuanyuan.controller.common;

import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.common.util.file.FileUtil;
import com.miguan.xuanyuan.common.util.file.UploadFileModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/api/common/uploadImg")
public class UploadImgController {

    @ApiOperation("上传图片到白山云")
    @PostMapping("/baiShanCloud")
    public ResultMap<String> baiShanCloud(
            @RequestPart MultipartFile file) {
        try {
            String fileType = FileUtil.getFileType(Objects.requireNonNull(file.getOriginalFilename()));
            if (StringUtil.isBlank(fileType)) {
                return ResultMap.error("图片格式错误或内容不规范");
            }
            if(!FileUtil.isImage(fileType)){
                return ResultMap.error("目前仅支持:jpeg,jpg,png,gif格式");
            }
            //目前仅看到1M以内.
            int size = 10;

            UploadFileModel fileModel = FileUtil.upload(file, "", "image",size);
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
