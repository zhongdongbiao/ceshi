package utry.data.modular.api;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import utry.core.base.controller.CommonController;
import utry.core.bo.LoginBean;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.util.HttpClientUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/15 15:40
 * description 获取部门数据
 */
@RestController
@RequestMapping("/subApi/departmentData")
@Api("获取部门数据")
public class DepartmentDataApi extends CommonController {

    @Resource
    private SysConfServiceImpl sysConfService;

    @PostMapping("/getDepartment")
    public RetResult getDepartment() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/GetDepartmentInfo";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }

}
