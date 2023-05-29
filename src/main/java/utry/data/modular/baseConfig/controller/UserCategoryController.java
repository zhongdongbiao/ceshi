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
import utry.data.modular.baseConfig.dto.CategoryRootDTO;
import utry.data.modular.baseConfig.dto.UserCategoryConfigDTO;
import utry.data.modular.baseConfig.dto.UserTypeDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.service.UserCategoryService;
import utry.data.modular.partsManagement.model.ProductType;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 人员品类配置管理类
 */
@Controller
@RequestMapping("/userCategory")
@Api(tags = "人员品类配置管理")
public class
UserCategoryController extends CommonController {

    @Resource
    private UserCategoryService userCategoryService;

    @PostMapping("/selectUser")
    @ApiOperation("新增---查询用户")
    @ResponseBody
    public RetResult selectUser() {
        List<HrmAccountInfoDTO> list = userCategoryService.selectUser();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectCategory")
    @ApiOperation("查询已配置的类型")
    @ResponseBody
    public RetResult selectCategory() {
        List<String> list = userCategoryService.selectCategory();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectType")
    @ApiOperation("查询全部的树")
    @ResponseBody
    public RetResult selectTypeTree() {
        List<CategoryRootDTO> list = userCategoryService.selectTypeTree();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectUserType")
    @ApiOperation("编辑-查询用户绑定信息")
    @ResponseBody
    public RetResult selectUserType(@RequestParam String accountId) {
        List<String> list = userCategoryService.selectUserType(accountId);
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectConfig/{currentPage}/{pageSize}")
    @ApiOperation("分页查询所有已绑定的用户")
    @ResponseBody
    public RetResult selectConfig(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                  @PathVariable @ApiParam(value = "当前页数") String currentPage) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<UserCategoryConfigDTO> list = userCategoryService.selectConfig();
        PageInfo<UserCategoryConfigDTO> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectConfig")
    @ApiOperation("查询所有已绑定的用户")
    @ResponseBody
    public RetResult selectConfigList() {
        List<UserCategoryConfigDTO> list = userCategoryService.selectConfig();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/addCategory")
    @ApiOperation("新增配置")
    @ResponseBody
    public RetResult addCategory(@RequestBody UserTypeDTO userTypeDTO) {
        if(StringUtils.isEmpty(userTypeDTO.getAccountId())){
            return RetResponse.makeErrRsp("新增失败，未选择用户");
        }
        if(CollectionUtils.isEmpty(userTypeDTO.getList())){
            return RetResponse.makeErrRsp("新增失败，未选择品类");
        }
        //判断该品类是否已经被配置
        if(userCategoryService.ifExist(userTypeDTO.getList(),userTypeDTO.getAccountId())){
            return RetResponse.makeErrRsp("新增失败，该用户已配置此类型");
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取操作时间
        userTypeDTO.setCreateTime(dateFormat.format(new Date()));
        int i = userCategoryService.addCategory(userTypeDTO);
        return RetResponse.makeOKRsp(i);
    }

    @PostMapping("/editCategory")
    @Transactional
    @ApiOperation("修改配置")
    @ResponseBody
    public RetResult editCategory(@RequestBody UserTypeDTO userTypeDTO) {
        if(StringUtils.isEmpty(userTypeDTO.getAccountId())){
            return RetResponse.makeErrRsp("编辑异常");
        }
        if(StringUtils.isEmpty(userTypeDTO.getOldAccountId())){
            return RetResponse.makeErrRsp("编辑异常");
        }
        if(StringUtils.isEmpty(userTypeDTO.getCreateTime())){
            return RetResponse.makeErrRsp("编辑异常");
        }
        if(CollectionUtils.isEmpty(userTypeDTO.getList())){
            return RetResponse.makeErrRsp("编辑失败，未选择类型");
        }
        //判断该品类是否已经被配置
        if(!userTypeDTO.getAccountId().equals(userTypeDTO.getOldAccountId())) {
            if (userCategoryService.ifExist(userTypeDTO.getList(), userTypeDTO.getAccountId())) {
                return RetResponse.makeErrRsp("编辑失败，该用户已配置此类型");
            }
            //删除数据
            userCategoryService.deleteConfig(userTypeDTO.getOldAccountId());
        }else {
            //删除本人数据
            userCategoryService.deleteMyself(userTypeDTO.getAccountId());
        }
        //新增配置
        int i = userCategoryService.addCategory(userTypeDTO);
        return RetResponse.makeOKRsp(i);
    }

    @PostMapping("/deleteCategoryConfig")
    @ApiOperation("删除配置")
    @ResponseBody
    public RetResult deleteCategoryConfig(@RequestBody UserTypeDTO userTypeDTO) {
        int i = userCategoryService.deleteConfig(userTypeDTO.getAccountId());
        return RetResponse.makeOKRsp(i);
    }

    @PostMapping("/selectDefault")
    @ApiOperation("查询默认担当")
    @ResponseBody
    public RetResult selectDefault() {
        HrmAccountInfoDTO hrmAccountInfoDTO = userCategoryService.selectDefault();
        return RetResponse.makeOKRsp(hrmAccountInfoDTO);
    }

    @PostMapping("/insertOrUpdateDefault")
    @ApiOperation("保存默认担当")
    @ResponseBody
    public RetResult insertOrUpdateDefault(@RequestParam String accountId,@RequestParam String id) {
        userCategoryService.insertOrUpdateDefault(accountId,id);
        return RetResponse.makeOKRsp();
    }
}
