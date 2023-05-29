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
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.data.modular.baseConfig.dto.UserDistrictDTO;
import utry.data.modular.baseConfig.model.UserDistrict;
import utry.data.modular.baseConfig.service.UserDistrictService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 人员大区配置管理类
 */
@Controller
@RequestMapping("/userDistrict")
@Api(tags = "人员大区配置管理")
public class UserDistrictController extends CommonController {

    @Resource
    private UserDistrictService userDistrictService;

    @PostMapping("/selectDistrictConfig/{currentPage}/{pageSize}")
    @ApiOperation("查询配置")
    @ResponseBody
    public RetResult selectDistrictConfig(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                          @PathVariable @ApiParam(value = "当前页数") String currentPage) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<UserDistrictDTO> list = userDistrictService.selectDistrict();
        PageInfo<UserDistrictDTO> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/select")
    @ApiOperation("查询所有未配置的大区")
    @ResponseBody
    public RetResult select() {
        List<UserDistrictDTO> list = userDistrictService.select();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/editDistrictConfig")
    @ApiOperation("修改配置")
    @ResponseBody
    public RetResult editDistrictConfig(@RequestBody UserDistrict userDistrict) {
        if(StringUtils.isEmpty(userDistrict.getDistrictId())){
            return RetResponse.makeErrRsp("编辑失败，未选择片区");
        }
        if(StringUtils.isEmpty(userDistrict.getAccountId())){
            return RetResponse.makeErrRsp("编辑失败，未选择用户");
        }
//        //判断是否是未配置的大区
//        if(userDistrictService.ifExist(userDistrict.getDistrictId(),userDistrict.getList())){
//            return RetResponse.makeErrRsp("编辑失败，该配置片区含有已关联用户");
//        }
        //删除配置
        userDistrictService.deleteDistrictConfig(userDistrict.getDistrictId());
        //新增配置
        int i = userDistrictService.addDistrictConfig(userDistrict);
        return RetResponse.makeOKRsp(i);
    }

/*    @PostMapping("/addDistrictConfig")
    @ApiOperation("新增配置")
    @ResponseBody
    public RetResult addDistrictConfig(@RequestBody UserDistrict userDistrict) {
        if(StringUtils.isEmpty(userDistrict.getAccountId())){
            return RetResponse.makeErrRsp("编辑失败，未选择用户");
        }
        if(CollectionUtils.isEmpty(userDistrict.getList())){
            return RetResponse.makeErrRsp("编辑失败，未选择大区");
        }
        //判断是否是未配置的大区
        if(userDistrictService.ifExistDistrict(userDistrict.getAccountId())){
            return RetResponse.makeErrRsp("编辑失败，该片区已经关联用户");
        }
        int i = userDistrictService.addDistrictConfig(userDistrict);
        return RetResponse.makeOKRsp(i);
    }*/
}
