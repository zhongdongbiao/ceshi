package utry.data.modular.account.controller;
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import utry.data.modular.account.service.ApiUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.ISysConfService;
import utry.data.modular.api.UserDataApi;
import utry.data.modular.partsManagement.model.UserData;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 人员管理Controller
 * @author WJ
 */
@Controller
@RequestMapping("/user")
@Api(tags = "人员信息全刪全增")
public class UserController extends CommonController {
    @Autowired
    ISysConfService iSysConfService;
    @Resource
    ApiUserService apiUserService;
    @Resource
    private UserDataApi userDataApi;

    @PostMapping("/getUser")
    @ResponseBody
    public RetResult getUser() {
        int i,j = 0;
        //切换数据源
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        //查询人员信息
        List<UserData> oldUserList = apiUserService.selectAllUser();
        String IP = iSysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/lhms/getUserData";
        //调用硕德接口
        RetResult retResult = userDataApi.getUser();
        if (200 == retResult.getCode()) {
        //数据处理
        String res = (String) retResult.getData();
        JSONObject jsonObject = JSONObject.fromObject(res);
        String str = jsonObject.get("data").toString();
        List<UserData> list = JSON.parseArray(str,UserData.class);
        //对比数据并站内信通知
        List<UserData> info = apiUserService.sendMessage(list,oldUserList);
        apiUserService.sendMessage(info);
        //删除全部数据
        i = apiUserService.batchDelete();
        //保存数据
        j = apiUserService.batchUserData(list);
        return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }
}
