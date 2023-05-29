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
import utry.data.modular.baseConfig.dto.UserFactoryDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.service.UserFactoryService;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 人员工厂配置管理类
 */
@Controller
@RequestMapping("/userFactory")
@Api(tags = "人员工厂配置管理")
public class UserFactoryController extends CommonController {


    @Resource
    private UserFactoryService userFactoryService;

    @PostMapping("/selectUser")
    @ApiOperation("查询用户")
    @ResponseBody
    public RetResult selectUser() {
        List<HrmAccountInfoDTO> list = userFactoryService.selectUser();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectUserFactory")
    @ApiOperation("编辑---查询用户已经绑定的工厂")
    @ResponseBody
    public RetResult selectUserFactory(@RequestParam String accountId) {
        List<FactoryData> list = userFactoryService.selectUserFactory(accountId);
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectFactory/{currentPage}/{pageSize}")
    @ApiOperation("查看详情---查询未配置的工厂")
    @ResponseBody
    public RetResult selectFactory(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                   @PathVariable @ApiParam(value = "当前页数") String currentPage) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<FactoryData> list = userFactoryService.selectFactory();
        PageInfo<FactoryData> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectNotConfigFactory")
    @ApiOperation("查询未配置的工厂")
    @ResponseBody
    public RetResult selectNotConfigFactory() {
        List<FactoryData> list = userFactoryService.selectFactory();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/selectConfig/{currentPage}/{pageSize}")
    @ApiOperation("查询所有已绑定的用户")
    @ResponseBody
    public RetResult selectConfig(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                                  @PathVariable @ApiParam(value = "当前页数") String currentPage) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<HrmAccountInfoDTO> list = userFactoryService.selectConfig();
        PageInfo<HrmAccountInfoDTO> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/selectConfigNoPage")
    @ApiOperation("查询所有已绑定的用户---未分页")
    @ResponseBody
    public RetResult selectConfigNoPage() {
        List<HrmAccountInfoDTO> list = userFactoryService.selectConfig();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/addFactoryConfig")
    @ApiOperation("新增/保存---新增配置")
    @ResponseBody
    public RetResult addConfig(@RequestBody UserFactoryDTO userFactoryDTO) {
        if(StringUtils.isEmpty(userFactoryDTO.getAccountId())){
            return RetResponse.makeErrRsp("新增失败，未选择用户");
        }
        if(CollectionUtils.isEmpty(userFactoryDTO.getList())){
            return RetResponse.makeErrRsp("新增失败，未选择片区");
        }
        //判断该片区是否已经被配置
        if(userFactoryService.ifExist(userFactoryDTO.getList())){
            return RetResponse.makeErrRsp("新增失败，选择包含已配置片区");
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取操作时间
        userFactoryDTO.setCreateTime(dateFormat.format(new Date()));
        int j = userFactoryService.addConfig(userFactoryDTO);
        return RetResponse.makeOKRsp(j);
    }

    @PostMapping("/editFactoryConfig")
    @ApiOperation("修改配置")
    @ResponseBody
    public RetResult editConfig(@RequestBody UserFactoryDTO userFactoryDTO) {
        if(StringUtils.isEmpty(userFactoryDTO.getOldAccountId())){
            return RetResponse.makeErrRsp("编辑异常");
        }
        if(StringUtils.isEmpty(userFactoryDTO.getAccountId())){
            return RetResponse.makeErrRsp("编辑失败，未选择用户");
        }
        if(CollectionUtils.isEmpty(userFactoryDTO.getList())){
            return RetResponse.makeErrRsp("编辑失败，未选择片区");
        }
        //判断该片区是否已经被配置
        if(userFactoryService.ifExist(userFactoryDTO.getList(),userFactoryDTO.getOldAccountId())){
            return RetResponse.makeErrRsp("编辑失败，选择包含已配置片区");
        }
        //删除数据
        userFactoryService.deleteConfig(userFactoryDTO.getOldAccountId());
        //新增配置
        int i = userFactoryService.addConfig(userFactoryDTO);
        return RetResponse.makeOKRsp(i);
    }

    @PostMapping("/deleteFactoryConfig")
    @ApiOperation("删除配置")
    @ResponseBody
    public RetResult deleteConfig(@RequestParam String accountId) {
        int i = userFactoryService.deleteConfig(accountId);
        return RetResponse.makeOKRsp(i);
    }
}
