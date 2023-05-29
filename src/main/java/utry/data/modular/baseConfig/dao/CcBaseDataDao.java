package utry.data.modular.baseConfig.dao;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.model.*;

import java.util.List;

/**
 * 
 * @author ldk
 */
@Mapper
//@SuppressWarnings("all")
public interface CcBaseDataDao {


    /**
     * 查询所有话务坐席的所有状态
     * @return
     */
    @DS("shuce_db")
    List<String> getCcStatus();

    Integer getIsExit(String status);

    void saveStatus(SeatStatusReminder seatStatusReminder);

    Integer isExitQueue(@Param("dto") CCSeatInfo ccQueueDept);

    /**
     * 查询所有状态
     *
     * @return SeatStatusReminder
     */
    List<SeatStatusReminder> selectSeatStatusReminder();
}
