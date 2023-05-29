package utry.data.modular.complaints.service;

import utry.data.modular.complaints.dto.ComplaintDto;
import utry.data.util.RetResult;

import java.util.List;
import java.util.Map;

/**
 * 投诉直辖业务接口
 *
 * @author wanlei
 */
public interface ComplaintsService {


    /**
     * 投诉处理单推送SPI业务接口
     * @param map
     * @return
     */
    RetResult complaintDetail(Map map);

    /**
     * 热线服务单推送SPI业务接口
     * @param map
     * @return
     */
    RetResult hotLineDetail(Map map);

    /**
     * 投诉处理单拉取
     * @return
     */
    RetResult complaintAlonePull(String hotlineNumber);

    /**
     * 履历信息推送
     * @param resumeList
     * @return
     */
    RetResult resumeDetail(List<Map<String,Object>> resumeList);

    /**
     * 投诉升级记录添加
     * @param map
     * @return
     */
    Map<String,List<Map<String,String>>> complaintUpdateAdd(Map map);

    /**
     * 投诉7天解决率
     * @param complaintDto
     * @return
     */
    Map<String, Object> sevenDaySolveRate(ComplaintDto complaintDto);

    /**
     * 投诉率
     * @param complaintDto
     * @return
     */
    Map<String, Object> complaintRate(ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之投诉率
     * @param complaintDto
     * @return
     */
    Map<String, Object> complaintRateRate(ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之投诉原因
     * @param complaintDto
     * @return
     */
    Object complaintRateReason(ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之产品品类投诉率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintRateProductCategory(ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之大区投诉率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintRateAccountingCenter(ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之地域(省份)投诉率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintRateProvinces(ComplaintDto complaintDto);

    /**
     * 未结案
     * @param complaintDto
     * @return
     */
    Map<String, Object> notOverCase(ComplaintDto complaintDto);

    /**
     * 全国投诉地图
     * @param complaintDto
     * @return
     */
    Map<String, Object> nationalComplaintMap(ComplaintDto complaintDto);

    /**
     * 排名
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> rankMap(ComplaintDto complaintDto);

    /**
     * 投诉异常监控
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintAbnormalMonitor(ComplaintDto complaintDto);

    /**
     * 查询单据列表
     * @param complaintDto
     * @return
     */
    Object selectDocumentsList(ComplaintDto complaintDto,Integer pageNum,Integer pageSize);

    /**
     * 投诉原因分析
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintReasonAnalysis(ComplaintDto complaintDto);

    /**
     * 内部投诉7天解决率
     * @param complaintDto
     * @return
     */
    Map<String, Object> innerSevenDaySolveRate(ComplaintDto complaintDto);

    /**
     * N+1解决方案及时提交率
     * @param complaintDto
     * @return
     */
    Map<String, Object> complaintSolveSubmissionRate(ComplaintDto complaintDto);

    /**
     * 投诉来源分析
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintSourceAnalysis(ComplaintDto complaintDto);

    /**
     * 品类投诉分析
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> categoryComplaintAnalysis(ComplaintDto complaintDto);

    /**
     * 全部大区
     * @return
     */
    List<Map<String, Object>> selectAllRegion();

    /**
     * 根据投诉单号查询投诉详情
     * @return
     */
    Map<String, Object> selectComplaintDetailByNumber(Map<String,Object> map);

    /**
     * 根据投诉单号查询投诉留言信息
     * @param map
     * @return
     */
    List<Map<String, Object>> selectComplaintMessageByNumber(Map<String, Object> map);

    /**
     * 根据投诉单号查询投诉处理明细
     * @param map
     * @return
     */
    List<Map<String, Object>> selectComplaintProcessDetailByNumber(Map<String, Object> map);

    /**
     * 全省投诉地图
     * @param complaintDto
     * @return
     */
    Map<String, Object> provinceComplaintMap(ComplaintDto complaintDto);

    /**
     * 门店排名
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> storeRank(ComplaintDto complaintDto);

    /**
     * 投诉7天解决率
     * @param map
     * @return
     */
    Map<String, Object> apiSevenSolveRate(Map<String, Object> map);

    /**
     * 投诉7天解决率排行
     * @param map
     * @return
     */
    List<Map<String, Object>> apiSevenSolveRank(Map<String, Object> map);

    /**
     * 投诉率
     * @param map
     * @return
     */
    Map<String, Object> apiComplaintRate(Map<String, Object> map);

    /**
     * 未结案
     * @param map
     * @return
     */
    Map<String, Object> apiNotOverCase(Map<String, Object> map);

    /**
     * 投诉异常监控
     * @param map
     * @return
     */
    List<Map<String, Object>> apiComplaintAbnormalMonitor(Map<String, Object> map);

    /**
     * N+1解决方案提交率
     * @param map
     * @return
     */
    Map<String, Object> apiSolveSubmitRate(Map<String, Object> map);

    /**
     * N+1解决方案提交率排名
     * @param map
     * @return
     */
    List<Map<String, Object>> apiSolveSubmitRank(Map<String, Object> map);

    /**
     * 结算费用及台量
     * @param map
     * @return
     */
    Map<String, Object> apiSettleData(Map<String, Object> map);
}
