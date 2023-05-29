package utry.data.modular.partsManagement.service;

import utry.data.util.RetResult;

/**
 * @program: data
 * @description: 部品库存数据拉取业务接口
 * @author: WangXinhao
 * @create: 2022-06-07 09:15
 **/

public interface ApiPartDrawingStockService {

    /**
     * 部品库存数据拉取并保存
     * @return 统一返回格式
     */
    RetResult<Void> getPartDrawingStock();
}
