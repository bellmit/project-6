package com.miguan.laidian.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 音频类别表bean
 * @author xy.chen
 * @date 2020-05-22
 **/
@Data
@ApiModel("音频类别表实体")
public class AudioCatVo {

	@ApiModelProperty("主键")
	private Integer id;

	@ApiModelProperty("分类名称")
	private String name;

	@ApiModelProperty("排序")
	private Integer sort;
}
