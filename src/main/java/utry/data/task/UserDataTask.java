package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import utry.core.bo.LoginBean;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.data.base.BaseController;
import utry.data.modular.account.controller.UserController;
import utry.data.modular.baseData.controller.BaseDataController;
import utry.data.util.RetResult;

import javax.annotation.Resource;

/**
 * @Author: WJ
 * @Date: 2021/3/8 12:17
 */
@Api(tags = "定时任务执行类-获取人员基本信息")
@ServiceApi
@Controller
public class UserDataTask extends BaseController {


    @Resource
    private UserController userController;

    @ApiOperation(value = "获取人员基本信息", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/userData", method = RequestMethod.POST)
    public ResponseEntity executeTask(String param) {
        RetResult user = userController.getUser();
        if (200 != user.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }
}
