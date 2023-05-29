package utry.data.modular.region.controller.dto;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegionVisitMonitoringRequest {
	
	@ApiModelProperty(value = "查询日期范围：开始时间")
	private String startTime;
	
	@ApiModelProperty(value = "查询日期范围：结束时间")
	private String endTime;
	
	@ApiModelProperty(value = "核算服务中心，大区编码")
	private String accountingCenterCode;
	
	@ApiModelProperty(value = "产品品类代码")
	private List<String> productCategoryCode;
	
	@ApiModelProperty(value = "产品类型代码")
	private List<String> productTypeCode;
	
	@ApiModelProperty(value = "服务店编号")
	private String storeNumber;
	
	/**
	 * 查询来自投诉处理的表单， 0表无，1表示是来自投诉的表单
	 */
	@ApiModelProperty(value = "查询来自投诉处理的表单， 0表无，1表示是来自投诉的表单")
	private Integer dispatchingFromComplaint;
	
	/**
	 * 查询当前派单出现如超时未接单、超过一些时间进度异常的,  0表无，1表示是异常的
	 */
	@ApiModelProperty(value = "查询来自投诉处理的表单， 0表无，1表示是来自投诉的表单")
	private Integer dispatchingOfAbnormal;
	
	/**
	 * 工程师id
	 */
	@ApiModelProperty(value = "工程师id")
	private String engineerId;
	
	/**
	 * 是否仅查询投诉
	 */
	@ApiModelProperty(value = "是否仅查询投诉； 1是， 0不是")
	private Integer isComplaint;
	
	/**
	 * 是否仅查询异常
	 */
	@ApiModelProperty(value = "是否仅查询异常； 1是， 0不是")
	private Integer isErr;
	
	/**
	 * 将对象转化成map
	 * @param obj
	 * @return
	 */
	public Map<String, Object> objectToMap() {
		Map<String, Object> map = new HashMap<>();
        Class<?> clazz = this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
				map.put(field.getName(), field.get(this));
			} catch (IllegalAccessException e) {
				// 基本忽略此问题
			}
        }
        return map;
	}

}
