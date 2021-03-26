package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * jpa entity class for dw_ad_errors_model_dict
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class DwAdErrorsModelDict  implements Serializable {

	private String model;

}
