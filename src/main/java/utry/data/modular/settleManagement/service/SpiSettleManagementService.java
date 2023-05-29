package utry.data.modular.settleManagement.service;

import utry.data.modular.settleManagement.dto.SettleDataDto;

import java.util.List;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/26 16:21
 */
public interface SpiSettleManagementService {

    /**
     * 未结算数据修改、删除
     * @param settleList
     */
    void missSettleModify(List<SettleDataDto> settleList) throws Exception;

    /**
     * 接收结算数据
     * @param settleList
     * @return
     */
    void settleModify(List<SettleDataDto> settleList) throws Exception;
}
