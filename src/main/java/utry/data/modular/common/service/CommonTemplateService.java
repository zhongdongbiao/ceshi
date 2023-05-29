package utry.data.modular.common.service;

import com.alibaba.fastjson.JSONObject;
import utry.data.modular.common.dto.TemplateQueryDataDto;
import utry.data.modular.common.dto.TemplateQueryDto;
import utry.data.modular.common.dto.TemplateResultDto;

import java.util.List;

public interface CommonTemplateService {

    /**
     * 通过电话号获取key
     * @param templateQueryDto
     * @return
     */
    List<TemplateResultDto> selectTemplateKey(TemplateQueryDto templateQueryDto);

    /**
     * 通过key和name解析数据
     * @param templateQueryDto
     * @return
     */
    JSONObject selectDataByKey(TemplateQueryDataDto templateQueryDto);
}
