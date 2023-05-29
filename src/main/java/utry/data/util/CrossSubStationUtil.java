package utry.data.util;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utry.core.bo.ResponseEntity;
import utry.core.cloud.caller.CallerParam;
import utry.core.cloud.caller.IServiceCaller;
import utry.core.site.SiteCode;

import javax.annotation.Resource;

/**
 * @author lidakai
 */
@RestController
@RequestMapping("/subApi/callSubStation")
@Api(tags = "跨子站调用，定制化需求部分")
public class CrossSubStationUtil {

    /**
     * 获取字典项接口
     */
    private static final String ACCOUNT_ROLEID = "/api/hrm/account/getAccountInfoByDepartIdAndRoleId";



    @Resource
    private IServiceCaller serviceCaller;

    @ApiOperation("站内信查询系统管理员信息")
    @RequestMapping(value = "/getAccount", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public ResponseEntity getAccount() {
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        CallerParam callerParam = new CallerParam(SiteCode.HRM.code(), ACCOUNT_ROLEID);
        callerParam.setHttpMethod(HttpMethod.GET);
        callerParam.addParameter("departId","9602b645b08d11ec83cc005056bde1f1");
        callerParam.addParameter("roleId","7496cb46b70d11eca906005056bde1f1");
        ResponseEntity call = serviceCaller.call(callerParam, ResponseEntity.class);
        return call;
    }



}
