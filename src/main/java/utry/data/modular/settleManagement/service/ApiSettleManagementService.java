package utry.data.modular.settleManagement.service;

import utry.data.modular.settleManagement.dto.SettleDataDto;
import utry.data.modular.settleManagement.model.SettleDataVo;

import java.util.List;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/28 16:33
 */
public interface ApiSettleManagementService {

    /**
     * 获取结算的数据
     * @return
     */
    String getSettleData() throws Exception;

    /**
     * 获取未结算的数据
     * @return
     */
    String getMissSettleData() throws Exception;
}
