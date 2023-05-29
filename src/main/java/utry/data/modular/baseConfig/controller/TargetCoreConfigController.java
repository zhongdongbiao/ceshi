package utry.data.modular.baseConfig.controller;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.data.modular.baseConfig.dto.IndicatorDTO;
import utry.data.modular.baseConfig.dto.TargetAddDTO;
import utry.data.modular.baseConfig.model.Target;
import utry.data.modular.baseConfig.service.TargetCoreConfigService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 目标配置管理类
 */
@Controller
@RequestMapping("/target/core")
@Api(tags = "核心目标管理")
public class TargetCoreConfigController extends CommonController {

    @Resource
    private TargetCoreConfigService targetCoreConfigService;

    @PostMapping("/selectTarget/{currentPage}/{pageSize}")
    @ApiOperation("查询核心目标配置")
    @ResponseBody
    public RetResult selectTarget(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                  @PathVariable @ApiParam(value = "当前页数") String currentPage,@RequestBody Target target) {
        if(StringUtils.isEmpty(target.getBusinessCode())){
            return RetResponse.makeErrRsp("查询失败，未选择业务");
        }
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<Target> list = targetCoreConfigService.selectTarget(target);
        PageInfo<Target> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/addTarget")
    @ApiOperation("新增核心目标")
    @Transactional
    @ResponseBody
    public RetResult addTarget(@RequestBody TargetAddDTO targetAddDTO) {
        if(StringUtils.isEmpty(targetAddDTO.getTargetMonth())){
            return RetResponse.makeErrRsp("新增失败，未选择目标月");
        }
        if(StringUtils.isEmpty(targetAddDTO.getTargetName())){
            return RetResponse.makeErrRsp("新增失败，未填写目标名称");
        }
        //判断数据库中是否含有已经配置的核心目标
        if(targetCoreConfigService.ifExist(targetAddDTO)){
            return RetResponse.makeErrRsp("新增失败，当月已配置");
        }
        targetCoreConfigService.addTarget(targetAddDTO);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/editQuery")
    @ApiOperation("编辑前查询")
    @ResponseBody
    public RetResult editQuery(@RequestBody TargetAddDTO targetAddDTO) {
        List<IndicatorDTO> list = targetCoreConfigService.editQuery(targetAddDTO);
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/edit")
    @ApiOperation("编辑保存")
    @ResponseBody
    public RetResult edit(@RequestBody TargetAddDTO targetAddDTO) {
        if(StringUtils.isEmpty(targetAddDTO.getId())){
            return RetResponse.makeErrRsp("编辑异常");
        }
        //参数校验
        for(IndicatorDTO i : targetAddDTO.getList()){
            if(StringUtils.isEmpty(i.getId())){
                return RetResponse.makeErrRsp("编辑异常");
            }
        }
        targetCoreConfigService.edit(targetAddDTO);
        return RetResponse.makeOKRsp();
    }

    @Transactional
    @PostMapping("/delete")
    @ApiOperation("删除")
    @ResponseBody
    public RetResult delete(@RequestBody TargetAddDTO targetAddDTO) {
        //参数校验
        if(StringUtils.isEmpty(targetAddDTO.getId())){
            return RetResponse.makeErrRsp("删除异常");
        }
        targetCoreConfigService.delete(targetAddDTO);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/select")
    @ApiOperation("查询当月指标")
    @ResponseBody
    public RetResult select(@RequestParam String businessCode) {
        //参数校验
        if(StringUtils.isEmpty(businessCode)){
            return RetResponse.makeErrRsp("业务id为空");
        }
        List<IndicatorDTO> list = targetCoreConfigService.select(businessCode);
        return RetResponse.makeOKRsp(list);
    }
}
