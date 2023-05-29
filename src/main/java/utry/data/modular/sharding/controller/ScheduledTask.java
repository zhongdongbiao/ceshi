package utry.data.modular.sharding.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import utry.core.bo.ResponseEntity;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.core.websocket.bo.UserInfo;
import utry.data.util.CrossSubStationUtil;
import utry.data.util.MessageUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : ldk
 * @date : 14:54 2022/6/9
 * @desc : 站内信测试demo
 */
@SuppressWarnings("all")
@Api(tags = "定时任务执行类-部品库存数据拉取")
@ServiceApi
@RestController
public class ScheduledTask {


    @Autowired
    private CrossSubStationUtil crossSubStationUtil;

    @PostMapping("/api/task/100060/scheduleTaskDem")
    @ResponseBody
    public RetResult getUser(String param) {
        System.out.println("定时任务触发站内信测试");
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        // 随便查询一些接收者
        ResponseEntity call = crossSubStationUtil.getAccount();
        String res = (String) call.getData();
//        List<UserInfo> userInfos = JSONArray.parseArray(res,UserInfo.class);
        List userInfos = new ArrayList();
        UserInfo userInfo = new UserInfo();
        userInfo.setAccount("admin");
        userInfo.setAccountID("f0ac5c12b71011ecb435fe25b45e0cae");
        userInfo.setCompanyID("");
        userInfo.setCompanyName("");
        userInfo.setRealName("系统管理员");
        userInfos.add(userInfo);
        MessageUtil.send("人员变动通知","您好，人员数据数据状态发生变化，请自行排查系统~","auto",userInfos);
        return RetResponse.makeOKRsp("success");
    }
}
