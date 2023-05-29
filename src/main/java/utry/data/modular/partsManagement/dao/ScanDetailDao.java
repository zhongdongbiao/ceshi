package utry.data.modular.partsManagement.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.ScanDetail;

import java.util.List;

/**
 * 服务店收货单扫描明细
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface ScanDetailDao {

    /**
     *服务店收货单扫描明细删除
     */
    int batchScanDetailDelete();

    /**
     *工服务店收货单扫描明细批量新增
     * @param list
     */
    int batchScanDetail(List<ScanDetail> list);
}
