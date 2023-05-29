package utry.data.modular.api;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.core.common.LoginInfoParams;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;



/**
 * 测试Controller
 *
 * @author lidakai
 */
@Controller
@RequestMapping("subApi/api")
@Api(tags = "分页测试")
public class ApiTestController extends CommonController {

    @PostMapping("test")
    @ResponseBody
    public RetResult getName(@RequestBody JSONObject jsonObject) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String currentPage = jsonObject.getString("currentPage");
        String pageSize = jsonObject.getString("pageSize");
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page pages = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        return RetResponse.makeOKRsp();
    }

}
