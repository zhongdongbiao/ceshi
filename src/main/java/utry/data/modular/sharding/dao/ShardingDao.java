package utry.data.modular.sharding.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.sharding.model.OrderDetail;

import java.util.List;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 11:29
 */
@SuppressWarnings("all")
@Mapper
public interface ShardingDao {

//    void addOrder(@Param("item") TOrder order);

    @DS("gits_sharding")
    Integer addDetail(@Param("item") OrderDetail order);
//    @DS("gits_sharding")
    List<OrderDetail> getList();


//    @DS("gits_sharding")
//    List<TOrder> getList();

//    void updateDetail(@Param("item") OrderDetail order);

//    int delDetail(@Param("item")OrderDetail order);

//    @DS("gits_sharding")
//    List<TOrder> queryDetailByPageHelper(@Param("item") OrderDetail order);

}
