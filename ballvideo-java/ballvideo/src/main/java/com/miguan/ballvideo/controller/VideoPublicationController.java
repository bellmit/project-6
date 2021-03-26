package com.miguan.ballvideo.controller;

import com.cgcg.context.thread.ThreadPoolManager;
import com.cgcg.context.util.StringUtils;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.YmlUtil;
import com.miguan.ballvideo.common.util.file.AWSUtil4Video;
import com.miguan.ballvideo.dto.FirstVideosDeleteDto;
import com.miguan.ballvideo.dto.FirstVideosDto;
import com.miguan.ballvideo.dto.UserCenterDto;
import com.miguan.ballvideo.service.FirstVideosService;
import com.miguan.ballvideo.springTask.thread.UploadVideoThread;
import com.miguan.ballvideo.vo.UserCenterVo;
import com.miguan.ballvideo.vo.VideoPublicationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tool.util.BigDecimalUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(value = "发布视频接口", tags = {"发布视频接口"})
@RequestMapping("/api/own/video")
@RestController
public class VideoPublicationController {

    @Autowired
    private FirstVideosService firstVideosService;

    @ApiOperation(value = "白山云SDK初始化配置信息(客户端上传视频)")
    @GetMapping("/config")
    public ResultMap config() {
        String appPoint = YmlUtil.getCommonYml("aws.appPoint");
        String accessKey = YmlUtil.getCommonYml("aws.accessKey");
        String secretKey = YmlUtil.getCommonYml("aws.secretKey");
        String appEnvironment = Global.getValue("app_environment");
        String bucketName = YmlUtil.getCommonYml("aws.bucketName");
        String secondDirectory = null;
        if ("prod".equals(appEnvironment)) {
            secondDirectory = YmlUtil.getCommonYml("aws.video_prefix_prod");
        } else {
            secondDirectory = YmlUtil.getCommonYml("aws.video_prefix_dev");
        }
        String str = DateUtil.dateStr(new Date(), DateUtil.DATEFORMAT_STR_013);
        String videoKey = AWSUtil4Video.FLOAD_USER_VIDEO + "/" + str;
        String videoImgKey = AWSUtil4Video.FLOAD_USER_VIDEO + "/" + str;

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("url", appPoint);
        result.put("accessKey", accessKey);
        result.put("secretKey", secretKey);
        result.put("bucket",  bucketName + "/" + secondDirectory);
        result.put("videoKey", videoKey);
        result.put("videoImgKey", videoImgKey);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "发布视频(客户端上传到白山云)")
    @PostMapping("/publication")
    public ResultMap publication(@ModelAttribute FirstVideosDto firstVideosDto) {
        /*String errorMsg = checkParamIsNull(firstVideosDto);
        if(StringUtils.isNotBlank(errorMsg)){
            return ResultMap.error(400, errorMsg);
        }
        if(StringUtils.isBlank(firstVideosDto.getBsyUrl())){
            return ResultMap.error(400, "白山云视频地址不能为空");
        }
        return firstVideosService.videoPublication(firstVideosDto);*/
        return ResultMap.success();
    }

    @ApiOperation(value = "发布视频图片(服务端上传到白山云)")
    @PostMapping("/publication/img")
    public ResultMap<VideoPublicationVo> publicationImg(@ApiParam(value = "用户ID", required = true) @RequestParam(value = "userId") String userId,
                                                        @ApiParam(value = "视频图片", required = true) @RequestParam(value = "imgFile") MultipartFile imgFile) {

        if(StringUtils.isBlank(userId)){
            return ResultMap.error(400, "用户ID不能为空");
        }
        if(imgFile == null){
            return ResultMap.error(400, "视频图片格式错误");
        }

        Date currdate = DateUtil.getNow();
        String originalFilename = imgFile.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (imgFile != null) {
            // 上传到白山云
            ThreadPoolManager.execute(new UploadVideoThread(imgFile, userId, AWSUtil4Video.FLOAD_USER_VIDEO, AWSUtil4Video.FILE_TYPE_VIDEO_SCREEN, currdate));
        }
        // 返回图片上传路径
        String bsyImgUrl = AWSUtil4Video.getBsyUrl(userId, AWSUtil4Video.FLOAD_USER_VIDEO, fileType, currdate);
        VideoPublicationVo result = new VideoPublicationVo();
        result.setBsyImgUrl(bsyImgUrl);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "发布视频(服务端上传到白山云)")
    @PostMapping("/publication/source")
    public ResultMap<VideoPublicationVo> publicationSource(@ModelAttribute FirstVideosDto firstVideosDto,
                                       @ApiParam(value = "视频源文件", required = true) @RequestParam(value = "videoFile") MultipartFile videoFile) {
        String errorMsg = checkParamIsNull(firstVideosDto);
        if(StringUtils.isNotBlank(errorMsg)){
            return ResultMap.error(400, errorMsg);
        }
        if(videoFile == null){
            return ResultMap.error(400, "视频图片格式错误");
        }


        log.info("发布视频(服务端上传到白山云)");
        Date currdate = DateUtil.getNow();
        String originalFilename = videoFile.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (videoFile != null) {
            // 上传到白山云
            ThreadPoolManager.execute(new UploadVideoThread(videoFile, firstVideosDto.getUserId(), AWSUtil4Video.FLOAD_USER_VIDEO, AWSUtil4Video.FILE_TYPE_VIDEO_SOURCE, currdate));
        }

        // 保存到数据库
        String bsyUrl = AWSUtil4Video.getBsyUrl(firstVideosDto.getUserId(), AWSUtil4Video.FLOAD_USER_VIDEO, fileType, currdate);
        firstVideosDto.setBsyUrl(bsyUrl);
        firstVideosDto.setVideoSize(BigDecimalUtil.getBigDecimal(String.valueOf(videoFile.getSize() / 1048576f)).toString());
        return firstVideosService.videoPublication(firstVideosDto);

    }

    @ApiOperation(value = "用户删除发布视频")
    @PostMapping("/delete")
    public ResultMap delete(@ApiParam FirstVideosDeleteDto deleteDto) {
        if (deleteDto.getUserId() == null || deleteDto.getId() == null) {
            return ResultMap.error(400, "用户ID或视频ID不能为空");
        }
        return firstVideosService.videoDelete(deleteDto);
    }

    @ApiOperation(value = "用户个人主页")
    @PostMapping("/center")
    public ResultMap<UserCenterVo> center(@ModelAttribute UserCenterDto userCenterDto) {
        return firstVideosService.center(userCenterDto);
    }

    private String checkParamIsNull(FirstVideosDto firstVideosDto){
        if(StringUtils.isBlank(firstVideosDto.getUserId())){
            return "用户ID不能为空";
        }
        if(StringUtils.isBlank(firstVideosDto.getTitle())){
            return "标题不能为空";
        }
        if(StringUtils.isBlank(firstVideosDto.getVideoTime())){
            return "时长不能为空";
        }
        return null;
    }
}
