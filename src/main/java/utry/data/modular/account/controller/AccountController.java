package utry.data.modular.account.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.data.modular.partsManagement.model.UserData;
import utry.data.modular.account.service.IAccountService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 账号管理管理类
 */
@Controller
@RequestMapping("/account")
@Api(tags = "账号基本信息管理")
public class AccountController extends CommonController {


    @Resource
    private IAccountService iAccountService;

    @PostMapping("/selectAccountInfoList")
    @ApiOperation("账号模糊搜索")
    @ResponseBody
    public RetResult selectAccountInfoList(@RequestParam String name) {
        List<UserData> list = iAccountService.fuzzySearch(name);
        return RetResponse.makeOKRsp(list);
    }
}
