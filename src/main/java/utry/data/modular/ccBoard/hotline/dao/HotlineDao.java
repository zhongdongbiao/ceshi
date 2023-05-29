package utry.data.modular.ccBoard.hotline.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.ccBoard.hotline.vo.Evaluate;
import utry.data.modular.ccBoard.hotline.vo.HotlineVo;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zh
 * @Date 2022/11/2 13:43
 */
@Mapper
public interface HotlineDao {

    /**
     * 查询热线服务总量及服务投诉量
     * @param map
     * @return
     */
    Map<String, Object> selectHotlineData(Map<String, Object> map);

    /**
     * 产品品类维度
     * @param map
     * @return
     */
    List<Map<String, Object>> hotlineServiceTypeByCategory(Map<String, Object> map);

    /**
     * 热线服务单维度
     * @param map
     * @return
     */
    List<Map<String, Object>> hotlineServiceTypeByType(Map<String, Object> map);

    /**
     * 已受理工单未跟进
     * @param map
     * @return
     */
    List<Map<String, Object>> acceptWorkFollow(Map<String, Object> map);

    /**
     * 客户评价
     * @return
     */
    @DS("shuce_db")
    List<Map<String, Object>> customerEvaluation(Map<String, Object> map);

    /**
     * 投诉分析
     * @param map
     * @return
     */
    List<Map<String, Object>> complaintsAnalysis(Map<String, Object> map);

    /**
     * 话务明细
     * @param map
     * @return
     */
    List<HotlineVo> callDetail(Map<String, Object> map);

    /**
     * 通过录音文件查询通话记录数据
     * @param map
     * @return
     */
    @DS("shuce_db")
    List<HotlineVo> selectCallRecord(Map<String,Object> map);

    /**
     * 热线服务单详情
     * @param map
     * @return
     */
    Map<String, Object> hotlineServiceDetail(Map<String, Object> map);

    /**
     * 查询出所有的服务类型
     * @return
     */
    List<Map<String, Object>> getServiceType(Map<String,Object> map);

    /**
     * 查询所有的服务明细
     * @return
     */
    List<Map<String, Object>> getServiceDetails(Map<String,Object> map);

    /**
     * 查询出队列名称
     * @param map
     * @return
     */
    List<Map<String, Object>> selectQueueName(Map<String, Object> map);

    /**
     * 根据队列id获取核算中心
     * @param map
     * @return
     */
    List<String> getAccountingCenter(Map<String,Object> map);

    /**
     * 查询录音文件信息
     * @param map
     * @return
     */
    @DS("shuce_db")
    List<Evaluate> selectRecordFile(Map<String, Object> map);

    /**
     * 查询热线信息
     * @param map
     * @return
     */
    List<Evaluate> selectHotline(Map<String, Object> map);
}
