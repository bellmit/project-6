package com.miguan.reportview.vo;

import com.cgcg.context.util.StringUtils;
import com.miguan.reportview.common.enmus.RenderTypeEnmu;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;


@Setter
@Getter
public class AdBehaviorSonDataVo {
    /**
     * 日期
     */
    private String date;

    /**
     * 应用
     */
    private String packageName;

    /**
     * 平台
     */
    private String adSource;

    /**
     * 广告位
     */
    private String adPostion;

    /**
     * 位置
     */
    private String qId;

    /**
     * 渲染方式
     */
    private String renderType;

    /**
     * 素材
     */
    private String adcType;

    /**
     * 库存
     */
    private double stock;

    /**
     * 请求量
     */
    private double reqNum;

    /**
     * 展示量
     */
    private double showNum;

    /**
     * 点击量
     */
    private double clickNum;

    /**
     * 有效曝光量
     */
    private Integer validShowNum;

    /**
     * 有效点击量
     */
    private Integer validClickNum;

    /**
     * 展现用户数
     */
    private double showUser;

    /**
     * 日活
     */
    private double activeUser;

    public void setRenderType(String renderType) {
        if(StringUtils.isBlank(renderType)) {
            this.renderType = renderType;
        }

        renderType = renderType.replace("[", "").replace("]", "").replace("'","");
        if(renderType.indexOf(",") == 0) {
            renderType = renderType.substring(1);
        }
        String[] renderTypes = renderType.split(",");
        for(int i=0;i<renderTypes.length;i++) {
            renderTypes[i] = RenderTypeEnmu.getValue(renderTypes[i]);
        }
        Arrays.sort(renderTypes);
        this.renderType = String.join("/", renderTypes);
    }

    public void setAdcType(String adcType) {
        if (StringUtils.isBlank(renderType)) {
            this.adcType = adcType;
        }
        adcType = adcType.replace("[", "").replace("]", "").replace("'", "");
        if(adcType.indexOf(",") == 0) {
            adcType = adcType.substring(1);
        }
        String[] adcTypes = adcType.split(",");
        Arrays.sort(adcTypes);
        this.adcType = String.join("/", adcTypes);
    }
}
