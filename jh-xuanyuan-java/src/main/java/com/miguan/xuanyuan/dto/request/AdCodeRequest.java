package com.miguan.xuanyuan.dto.request;

import com.cgcg.context.util.StringUtils;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.regex.Pattern;

@ApiModel("广告源代码位")
@Data
public class AdCodeRequest {

    @ApiModelProperty("广告源id")
    private Long id;

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("广告位id")
    private Long positionId;

    @ApiModelProperty("广告源名称")
    private String codeName;

    @ApiModelProperty("第三方广告平台key")
    private String sourcePlatKey;

    @ApiModelProperty("第三方广告平台id")
    private Long sourcePlatAccountId;

    @ApiModelProperty("广告平台应用id")
    private String sourceAppId;

    @ApiModelProperty("广告平台代码位id")
    private String sourceCodeId;

    @ApiModelProperty("渲染方式")
    private String renderType;

    @ApiModelProperty("是否为阶梯广告")
    private Integer isLadder;

    @ApiModelProperty("阶梯价格")
    private Long ladderPrice;

    @ApiModelProperty("同一个用户，1个小时内最多展示限制")
    private Long showLimitHour;

    @ApiModelProperty("同一个用户，1天内最多展示限制")
    private Long showLimitDay;

    @ApiModelProperty("同一个用户，前后两次请求广告的间隔秒数")
    private Long showIntervalSec;

    @ApiModelProperty("版本操作")
    private String versionOperation;

    @ApiModelProperty("版本")
    @Size(max = 500,message = "版本不能超过500个字符")
    private String versions = "";

    @ApiModelProperty("渠道操作")
    private String channelOperation;

    @ApiModelProperty("渠道")
    @Size(max = 500,message = "渠道不能超过500个字符")
    private String channels = "";

    @ApiModelProperty("备注")
    private String note = "";

    @ApiModelProperty("状态，1投放，0未投放")
    private Integer status = 0;

    @ApiModelProperty("是否删除，0正常，1删除")
    private Integer isDel = 0;


    public void check() throws ValidateException {
        if (positionId == null) {
            throw new ValidateException("positionId不能为空");
        }

        if (codeName == null) {
            throw new ValidateException("codeName不能为空");
        }

        if (sourcePlatKey == null) {
            throw new ValidateException("sourcePlatKey不能为空");
        }


        if (sourceAppId == null) {
            throw new ValidateException("sourceAppId不能为空");
        }

        if (sourceCodeId == null) {
            throw new ValidateException("代码位ID(sourceCodeId)不能为空");
        }

        if (renderType == null) {
            throw new ValidateException("renderType不能为空");
        }

        if (isLadder == null) {
            throw new ValidateException("isLadder不能为空");
        }

        if (isLadder == 1 && ladderPrice == null) {
            throw new ValidateException("ladderPrice不能为空");
        }

        if (ladderPrice == null) {
            ladderPrice = 0l;
        }

        if (isLadder == 1 && ladderPrice < 0) {
            throw new ValidateException("ladderPrice不能为负数");
        }

        if (isLadder == 0) {
            ladderPrice = 0l;
        }

        if (StringUtils.isEmpty(versionOperation)) {
            versionOperation = "-1";
        }

        if (!XyConstant.VERSION_OPERATION_MAP.containsKey(versionOperation)) {
            throw new ValidateException("versionOperation数据错误");
        }

        if (versions == null || versionOperation.equals("-1")) {
            versions = "";
        }

        if (!versionOperation.equals("-1") && StringUtils.isEmpty(versions)) {
            throw new ValidateException("versions不能为空");
        }

        if (!versionOperation.equals(XyConstant.OPERA_IN) && !versionOperation.equals(XyConstant.OPERA_NOT_IN) && versions.contains(",")) {
            throw new ValidateException("versions参数错误");
        }

        if (StringUtils.isEmpty(channelOperation)) {
            channelOperation = "-1";
        }

        if (!XyConstant.CHANNEL_OPERATION_MAP.containsKey(channelOperation)) {
            throw new ValidateException("channelOperation数据错误");
        }

        if (channels == null || channelOperation.equals("-1")) {
            channels = "";
        }

        if (!channelOperation.equals("-1") && StringUtils.isEmpty(channels)) {
            throw new ValidateException("channels不能为空");
        }


//        String pattern = "^1\\d{10}$";
//        boolean isMatch = Pattern.matches(pattern, phone);
//        if (!isMatch) {
//            return ResultMap.error("请输入正确手机格式");
//        }


        if (note == null) {
            note = "";
        }
    }


}
