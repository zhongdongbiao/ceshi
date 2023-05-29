package utry.data.modular.spi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.ccBoard.visit.service.SpiVisitService;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhongdongbiao
 * @date 2022/10/25 13:56
 */
@RestController
@RequestMapping("subApi/spiVisit")
@Api(tags = "回访模块SPI")
public class SpiVisitController {

    @Resource
    private SpiVisitService spiVisitService;

    @ApiOperation(value = "回访任务数据创建", notes = "回访任务数据创建")
    @PostMapping("/create")
    public RetResult create(HttpServletRequest request) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return spiVisitService.create(request);
    }

    @ApiOperation(value = "回访任务数据修改", notes = "回访任务数据修改")
    @PostMapping("/updateVisitTask")
    public RetResult updateVisitTask(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return spiVisitService.updateVisitTask(request);
    }

    @ApiOperation(value = "服务回访记录审核数据创建", notes = "服务回访记录审核数据创建")
    @PostMapping("/createVisitAudit")
    public RetResult createVisitAudit(HttpServletRequest request) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return spiVisitService.createVisitAudit(request);
    }

    @ApiOperation(value = "服务回访记录审核数据修改", notes = "服务回访记录审核数据修改")
    @PostMapping("/updateVisitAudit")
    public RetResult updateVisitAudit(HttpServletRequest request) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return spiVisitService.updateVisitAudit(request);
    }

    @ApiOperation(value = "回访违约单数据创建", notes = "回访违约单数据创建")
    @PostMapping("/createVisitDefault")
    public RetResult createVisitDefault(HttpServletRequest request) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return spiVisitService.createVisitDefault(request);
    }

    @ApiOperation(value = "回访违约单数据修改", notes = "回访违约单数据修改")
    @PostMapping("/updateVisitDefault")
    public RetResult updateVisitDefault(HttpServletRequest request) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return spiVisitService.updateVisitDefault(request);
    }
}
