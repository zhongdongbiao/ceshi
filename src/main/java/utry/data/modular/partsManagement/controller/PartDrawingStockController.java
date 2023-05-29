package utry.data.modular.partsManagement.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utry.core.common.LoginInfoParams;
import utry.data.modular.partsManagement.dto.FactoryAmountQueryDTO;
import utry.data.modular.partsManagement.dto.FactoryCountQueryDTO;
import utry.data.modular.partsManagement.dto.PartDrawingAmountQueryDTO;
import utry.data.modular.partsManagement.dto.PartDrawingCountQueryDTO;
import utry.data.modular.partsManagement.service.ApiPartDrawingStockService;
import utry.data.modular.partsManagement.service.PartDrawingStockService;
import utry.data.modular.partsManagement.vo.FactoryAmountRingChartVO;
import utry.data.modular.partsManagement.vo.FactoryCountRingChartVO;
import utry.data.util.RetResult;

import javax.validation.Valid;
import java.util.List;

/**
 * @program: data
 * @description: 部品库存控制类
 * @author: WangXinhao
 * @create: 2022-06-08 11:07
 **/
@RestController
@RequestMapping("/partDrawingStock")
@Api(tags = "部品库存控制类")
public class PartDrawingStockController {

    @Autowired
    private ApiPartDrawingStockService apiPartDrawingStockService;

    @Autowired
    private PartDrawingStockService partDrawingStockService;

    @ApiOperation(value = "部品库存数据拉取")
    @RequestMapping(value = "/getPartDrawingStock", method = RequestMethod.POST)
    public RetResult<Void> getPartDrawingStock() {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return apiPartDrawingStockService.getPartDrawingStock();
    }

    @ApiOperation(value = "部品库存工厂在库金额表格")
    @RequestMapping(value = "/getFactoryAmount", method = RequestMethod.POST)
    public RetResult<JSONObject> getFactoryAmount(@Valid @RequestBody FactoryAmountQueryDTO factoryAmountQueryDTO) {
        return partDrawingStockService.getFactoryAmount(factoryAmountQueryDTO);
    }

    @ApiOperation(value = "部品库存工厂在库数量表格")
    @RequestMapping(value = "/getFactoryCount", method = RequestMethod.POST)
    public RetResult<JSONObject> getFactoryCount(@Valid @RequestBody FactoryCountQueryDTO factoryCountQueryDTO) {
        return partDrawingStockService.getFactoryCount(factoryCountQueryDTO);
    }

    @ApiOperation(value = "工厂别在库金额环形图")
    @RequestMapping(value = "/getFactoryAmountRingChart", method = RequestMethod.GET)
    public RetResult<List<FactoryAmountRingChartVO>> getFactoryAmountRingChart(@RequestParam("date") String date) {
        return partDrawingStockService.getFactoryAmountRingChart(date);
    }

    @ApiOperation(value = "工厂别在库数量环形图")
    @RequestMapping(value = "/getFactoryCountRingChart", method = RequestMethod.GET)
    public RetResult<List<FactoryCountRingChartVO>> getFactoryCountRingChart(@RequestParam("date") String date) {
        return partDrawingStockService.getFactoryCountRingChart(date);
    }

    @ApiOperation(value = "部品库存部品图号在库金额表格")
    @RequestMapping(value = "/getPartDrawingNoAmount", method = RequestMethod.POST)
    public RetResult<JSONObject> getPartDrawingNoAmount(@Valid @RequestBody PartDrawingAmountQueryDTO partDrawingAmountQueryDTO) {
        return partDrawingStockService.getPartDrawingNoAmount(partDrawingAmountQueryDTO);
    }

    @ApiOperation(value = "部品库存部品图号在库数量表格")
    @RequestMapping(value = "/getPartDrawingNoCount", method = RequestMethod.POST)
    public RetResult<JSONObject> getPartDrawingNoCount(@Valid @RequestBody PartDrawingCountQueryDTO partDrawingCountQueryDTO) {
        return partDrawingStockService.getPartDrawingNoCount(partDrawingCountQueryDTO);
    }
}
