package utry.data.modular.sharding.service.impl;

import org.springframework.stereotype.Service;
import utry.data.modular.settleManagement.dao.MissSettleManagementDao;
import utry.data.modular.settleManagement.dao.SettleManagementDao;
import utry.data.modular.sharding.dao.ShardingDao;
import utry.data.modular.sharding.model.OrderDetail;
import utry.data.modular.sharding.service.IShardingService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 15:49
 */
@Service
public class ShardingServiceImpl implements IShardingService {

    @Resource
    private ShardingDao shardingDao;


    @Override
    public Integer addDetail(OrderDetail detail) {
        return shardingDao.addDetail(detail);
    }

    @Override
    public List<OrderDetail> getList() {
        return  shardingDao.getList();
    }
}
