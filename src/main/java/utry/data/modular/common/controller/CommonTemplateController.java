package utry.data.modular.common.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.data.modular.common.dto.TemplateQueryDataDto;
import utry.data.modular.common.dto.TemplateQueryDto;
import utry.data.modular.common.dto.TemplateResultDto;
import utry.data.modular.common.service.CommonTemplateService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


/**
 * 公共Controller
 *
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Controller
@RequestMapping("/subApi/common")
@Api(tags = "公共模板Controller")
public class CommonTemplateController extends CommonController {

    @Resource
    private CommonTemplateService commonTemplateService;

    @PostMapping("/selectTemplateKey")
    @ApiOperation("根据手机号查询key")
    @ResponseBody
    public RetResult selectTemplateKey(@RequestBody TemplateQueryDto templateQueryDto) {
        List<TemplateResultDto> list = commonTemplateService.selectTemplateKey(templateQueryDto);
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectDataByKey")
    @ApiOperation("通过key和name解析数据")
    @ResponseBody
    public RetResult selectDataByKey(@RequestBody @Valid TemplateQueryDataDto templateQueryDto) {
        JSONObject jsonObject = commonTemplateService.selectDataByKey(templateQueryDto);
        return RetResponse.makeOKRsp(jsonObject);
    }
}
