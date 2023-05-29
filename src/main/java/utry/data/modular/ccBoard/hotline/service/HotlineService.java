package utry.data.modular.ccBoard.hotline.service;

import utry.data.modular.ccBoard.hotline.vo.HotlineVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zh
 * @Date 2022/11/2 13:24
 */
public interface HotlineService {

    /**
     * 热线项目数据概览
     * @param map
     * @return
     */
    Map<String, Object> hotlineData(Map<String, Object> map);

    /**
     * 热线项目服务类型
     * @param map
     * @return
     */
    List<Map<String, Object>> hotlineServiceType(Map<String, Object> map);

    /**
     * 已受理工单未跟进
     * @param map
     * @return
     */
    List<Map<String, Object>> acceptWorkFollow(Map<String, Object> map);

    /**
     * 客户评价
     * @param map
     * @return
     */
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
    Object callDetail(Map<String, Object> map, Integer pageNum, Integer pageSize);

    /**
     * 热线服务单详情
     * @param map
     * @return
     */
    Map<String, Object> hotlineServiceDetail(Map<String, Object> map) throws Exception;

    /**
     * 未完成处理单
     * @param map
     * @return
     */
    Object noFinishOrder(Map<String, Object> map) throws Exception;

    /**
     * 热线服务单导出
     * @param response
     * @param map
     * @return
     */
    void hotlineChartExport(HttpServletResponse response, Map<String, Object> map) throws Exception;

    /**
     * 话务明细导出
     * @param response
     * @param map
     */
    void callDetailExport(HttpServletResponse response, Map<String, Object> map) throws Exception;

    /**
     * 已受理未跟进列表
     * @param pageData
     * @param page
     * @param size
     * @return
     */
    Object acceptWorkFollowList(Map<String, Object> pageData, Integer page, Integer size);
}
