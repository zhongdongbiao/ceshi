package utry.data.modular.spi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.partsManagement.service.SpiPartsManagementService;
import utry.data.modular.settleManagement.dto.SettleDataDto;
import utry.data.modular.settleManagement.service.SpiSettleManagementService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/26 13:06
 */
@RestController
@RequestMapping("subApi/spiSettleManagement")
@Api(tags = "结算管理spi")
public class SpiSettleManagementController {

    @Resource
    private SpiSettleManagementService spiSettleManagementService;

    @ApiOperation(value = "未结算数据修改、删除", notes = "未结算数据修改、删除")
    @PostMapping("/missSettleModify")
    public RetResult missSettleModify (@RequestBody List<SettleDataDto> settleList) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try {
            spiSettleManagementService.missSettleModify(settleList);
        }catch (Exception e) {
            return RetResponse.makeErrRsp(e.getMessage());
        }
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "结算数据修改、删除", notes = "结算数据修改、删除")
    @PostMapping("/settleModify")
    public RetResult settleModify (@RequestBody List<SettleDataDto> settleList) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try {
            spiSettleManagementService.settleModify(settleList);
        }catch (Exception e) {
            return RetResponse.makeErrRsp(e.getMessage());
        }
        return RetResponse.makeOKRsp();
    }

}
