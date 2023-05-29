package utry.data.modular.baseConfig.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.data.modular.baseConfig.dto.*;
import utry.data.modular.baseConfig.service.TargetUserConfigService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import javax.annotation.Resource;
import java.util.List;


/**
 * 目标配置管理类
 */
@Controller
@RequestMapping("/target/user")
@Api(tags = "担当目标管理")
public class TargetUserConfigController extends CommonController {

    @Resource
    private TargetUserConfigService targetUserConfigService;

    @PostMapping("/selectTarget/{currentPage}/{pageSize}")
    @ApiOperation("查询担当目标配置")
    @ResponseBody
    public RetResult selectTarget(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                  @PathVariable @ApiParam(value = "当前页数") String currentPage,@RequestBody TargetUserDTO targetUserDTO) {
        if(StringUtils.isEmpty(targetUserDTO.getBusinessCode())){
            return RetResponse.makeErrRsp("查询失败，未选择业务");
        }
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<TargetUserDTO> list = targetUserConfigService.selectTarget(targetUserDTO);
        PageInfo<TargetUserDTO> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/addTarget")
    @ApiOperation("新增担当目标")
    @Transactional
    @ResponseBody
    public RetResult addTarget(@RequestBody TargetAddDTO targetAddDTO) {
        if(StringUtils.isEmpty(targetAddDTO.getTargetMonth())){
            return RetResponse.makeErrRsp("新增失败，未选择目标月");
        }
        if(StringUtils.isEmpty(targetAddDTO.getTargetName())){
            return RetResponse.makeErrRsp("新增失败，未填写目标名称");
        }
        if(CollectionUtils.isEmpty(targetAddDTO.getUsers())){
            return RetResponse.makeErrRsp("新增失败，未选择关联用户");
        }
        targetAddDTO.setId(null);
        //判断数据库中用户是否创建了指标
        if(targetUserConfigService.ifExist(targetAddDTO)){
            return RetResponse.makeErrRsp("新增失败，用户当月已配置");
        }
        targetUserConfigService.addTarget(targetAddDTO);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/editQuery")
    @ApiOperation("编辑前查询")
    @ResponseBody
    public RetResult editQuery(@RequestBody TargetAddDTO targetAddDTO) {
        List<IndicatorUserDTO> list = targetUserConfigService.editQuery(targetAddDTO);
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/edit")
    @ApiOperation("编辑保存")
    @Transactional
    @ResponseBody
    public RetResult edit(@RequestBody TargetAddDTO targetAddDTO) {
        if(StringUtils.isEmpty(targetAddDTO.getId())){
            return RetResponse.makeErrRsp("编辑异常");
        }
        //参数校验
        if(CollectionUtils.isEmpty(targetAddDTO.getUsers())){
            return RetResponse.makeErrRsp("必要参数为空");
        }
        //判断数据库中用户是否创建了指标
        if(targetUserConfigService.ifExist(targetAddDTO)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
            return RetResponse.makeErrRsp("编辑失败，用户当月已配置");
        }
        //删除该目标下全部指标数据
        targetUserConfigService.deleteAll(targetAddDTO);
        //新增指标数据
        targetUserConfigService.addIndicator(targetAddDTO);
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
        targetUserConfigService.delete(targetAddDTO);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/selectUser")
    @ApiOperation("选择用户---查询已配置用户")
    @ResponseBody
    public RetResult selectUser(@RequestBody JSONObject jsonObject) {
        String businessCode = jsonObject.getString("businessCode");
        //参数校验
        if(StringUtils.isEmpty(businessCode)){
            return RetResponse.makeErrRsp("业务id为空");
        }
        List<HrmAccountInfoDTO> list = targetUserConfigService.selectUser(businessCode);
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/select")
    @ApiOperation("查询当月指标")
    @ResponseBody
    public RetResult select(@RequestParam String businessCode) {
        //参数校验
        if(StringUtils.isEmpty(businessCode)){
            return RetResponse.makeErrRsp("业务id为空");
        }
        List<IndicatorUserDTO> list = targetUserConfigService.select(businessCode);
        return RetResponse.makeOKRsp(list);
    }
}
