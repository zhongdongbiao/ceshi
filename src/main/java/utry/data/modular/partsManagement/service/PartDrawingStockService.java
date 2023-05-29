package utry.data.modular.partsManagement.service;

import com.alibaba.fastjson.JSONObject;
import utry.data.modular.partsManagement.dto.FactoryAmountQueryDTO;
import utry.data.modular.partsManagement.dto.FactoryCountQueryDTO;
import utry.data.modular.partsManagement.dto.PartDrawingAmountQueryDTO;
import utry.data.modular.partsManagement.dto.PartDrawingCountQueryDTO;
import utry.data.modular.partsManagement.vo.FactoryAmountRingChartVO;
import utry.data.modular.partsManagement.vo.FactoryCountRingChartVO;
import utry.data.util.RetResult;

import java.util.List;

/**
 * @program: data
 * @description: 部品库存业务接口
 * @author: WangXinhao
 * @create: 2022-06-13 11:14
 **/

public interface PartDrawingStockService {

    /**
     * 部品库存工厂在库金额表格
     * @param factoryAmountQueryDTO 查询条件
     * @return 统一返回
     */
    RetResult<JSONObject> getFactoryAmount(FactoryAmountQueryDTO factoryAmountQueryDTO);

    /**
     * 部品库存工厂在库数量表格
     * @param factoryCountQueryDTO 查询条件
     * @return 统一返回
     */
    RetResult<JSONObject> getFactoryCount(FactoryCountQueryDTO factoryCountQueryDTO);

    /**
     * 工厂别在库金额环形图
     * @param date 日期
     * @return 统一返回
     */
    RetResult<List<FactoryAmountRingChartVO>> getFactoryAmountRingChart(String date);

    /**
     * 工厂别在库数量环形图
     * @param date 日期
     * @return 统一返回
     */
    RetResult<List<FactoryCountRingChartVO>> getFactoryCountRingChart(String date);

    /**
     * 部品库存部品图号在库金额表格
     * @param partDrawingAmountQueryDTO 查询条件
     * @return 统一返回
     */
    RetResult<JSONObject> getPartDrawingNoAmount(PartDrawingAmountQueryDTO partDrawingAmountQueryDTO);

    /**
     * 部品库存部品图号在库数量表格
     * @param partDrawingCountQueryDTO 查询条件
     * @return 统一返回
     */
    RetResult<JSONObject> getPartDrawingNoCount(PartDrawingCountQueryDTO partDrawingCountQueryDTO);
}
