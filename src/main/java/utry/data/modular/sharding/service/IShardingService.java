package utry.data.modular.sharding.service;

import utry.data.modular.sharding.model.OrderDetail;

import java.util.List;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 15:49
 */
public interface IShardingService {


    Integer addDetail(OrderDetail order);

    List<OrderDetail> getList();

}
