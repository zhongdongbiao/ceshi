package utry.data.modular.baseConfig.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.core.common.LoginInfoParams;
import utry.data.modular.api.DepartmentDataApi;
import utry.data.modular.baseConfig.dto.DepartmentStationLetterDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.dto.UserComplaintDTO;
import utry.data.modular.baseConfig.model.DepartmentData;
import utry.data.modular.baseConfig.model.Target;
import utry.data.modular.baseConfig.service.UserComplaintService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/15 9:59
 * description 人员投诉配置管理
 */
@RestController
@RequestMapping("/userComplaint")
@Api(tags = "人员投诉配置管理")
public class UserComplaintController extends CommonController {

    @Resource
    private UserComplaintService userComplaintService;
    @Resource
    private DepartmentDataApi departmentDataApi;

    @PostMapping("/selectUser")
    @ApiOperation("查询用户")
    public RetResult selectUser() {
        List<HrmAccountInfoDTO> list = userComplaintService.selectUser();
        return RetResponse.makeOKRsp(list);
    }

    @PostMapping("/associatedUser")
    @ApiOperation("关联用户")
    public RetResult associatedUser(@RequestBody UserComplaintDTO userComplaintDTO) {
        if (StringUtils.isBlank(userComplaintDTO.getAccountId())) {
            return RetResponse.makeErrRsp("新增失败，未选择用户");
        }
        if (userComplaintService.queryAssumeUserByAccountId(userComplaintDTO.getAccountId()) > 0) {
            return RetResponse.makeErrRsp("新增失败，该用户已被关联");
        }
        int j = userComplaintService.associatedUser(userComplaintDTO);
        return RetResponse.makeOKRsp(j);
    }

    @PostMapping("/queryAssumeUser")
    @ApiOperation("查询担当用户列表")
    public RetResult queryAssumeUser(@ApiParam("页码") String pageIndex, @ApiParam("页的大小") String pageSize) {
        PageBean pageBean = this.getPageBean(pageIndex, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<UserComplaintDTO> userComplaintDTOList = userComplaintService.queryAssumeUser();
        PageInfo<UserComplaintDTO> pageInfo = new PageInfo<>(userComplaintDTOList);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/deleteAssumeUser")
    @ApiOperation("删除担当用户")
    public RetResult deleteAssumeUser(@RequestParam String accountId) {
        int j = userComplaintService.deleteAssumeUser(accountId);
        return RetResponse.makeOKRsp(j);
    }

    @PostMapping("/editAssumeUser")
    @ApiOperation("编辑担当用户")
    public RetResult editAssumeUser(@RequestBody UserComplaintDTO userComplaintDTO) {
        if (StringUtils.isBlank(userComplaintDTO.getAccountId())) {
            return RetResponse.makeErrRsp("编辑失败，未选择用户");
        }
        if (StringUtils.isBlank(userComplaintDTO.getOldAccountId())) {
            return RetResponse.makeErrRsp("编辑失败，不知道编辑的用户");
        }
        if (userComplaintService.queryAssumeUserByAccountId(userComplaintDTO.getAccountId()) > 0
                && !userComplaintDTO.getAccountId().equals(userComplaintDTO.getOldAccountId())) {
            return RetResponse.makeErrRsp("编辑失败，该用户已被关联");
        }
        int j = userComplaintService.editAssumeUser(userComplaintDTO);
        return RetResponse.makeOKRsp(j);
    }

    @PostMapping("/queryStationLetter")
    @ApiOperation("查询部门站内信")
    public RetResult queryStationLetter(@ApiParam("页码") String pageIndex, @ApiParam("页的大小") String pageSize) {
        PageBean pageBean = this.getPageBean(pageIndex, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<DepartmentStationLetterDTO> list = userComplaintService.queryStationLetter();
        PageInfo<DepartmentStationLetterDTO> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/editStationLetter")
    @ApiOperation("编辑部门站内信")
    public RetResult editStationLetter(@RequestBody DepartmentStationLetterDTO stationLetterDTO) {
        int j = userComplaintService.editStationLetter(stationLetterDTO);
        return RetResponse.makeOKRsp(j);
    }

    @PostMapping("/insertStationLetter")
    @ApiOperation("插入部门站内信信息")
    public RetResult insertStationLetter() {
//        //切换数据源
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        // 调用硕德接口
        RetResult department = departmentDataApi.getDepartment();
        if (200 == department.getCode()) {
            String data = (String) department.getData();
            JSONObject jsonObject = JSONObject.fromObject(data);
            String str = jsonObject.get("data").toString();
            List<DepartmentData> list = JSON.parseArray(str,DepartmentData.class);
            int j = userComplaintService.insertDepartmentStationLetter(list);
            return RetResponse.makeOKRsp("插入了" + j + "条数据");
        }
        return RetResponse.makeOKRsp("fail");
    }
}
