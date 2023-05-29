package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.data.base.BaseController;
import utry.data.modular.baseConfig.controller.UserComplaintController;
import utry.data.util.RetResult;

import javax.annotation.Resource;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/15 17:06
 * description
 */
@Api(tags = "拉取部门信息插入部门站内信")
@ServiceApi
@Controller
public class DepartmentStationLetterTask extends BaseController {

    @Resource
    private UserComplaintController userComplaintController;

    @ApiOperation(value = "插入部门站内信信息", notes = "插入部门站内信信息")
    @PostMapping("/api/task/100060/insertDepartmentStationLetter")
    public ResponseEntity insertDepartmentStationLetter() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = userComplaintController.insertStationLetter();
        if (200 != retResult.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

}
