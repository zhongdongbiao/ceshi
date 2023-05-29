package utry.data.modular.complaints.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import utry.data.base.Page;
import utry.data.modular.complaints.dto.ComplaintDto;
import utry.data.modular.complaints.service.ComplaintsService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description 投诉直辖
 * @Author zh
 * @Date 2022/5/11 09:53
 */
@RestController
@RequestMapping("/complaints")
@Api(tags = "投诉直辖")
public class ComplaintController {

    @Resource
    private ComplaintsService complaintsService;

    @ApiOperation(value = "投诉7天解决率", notes = "投诉7天解决率")
    @RequestMapping(value = "/sevenDaySolveRate", method = RequestMethod.POST)
    public RetResult sevenDaySolveRate(@RequestBody ComplaintDto complaintDto) {
        Map<String,Object> message = complaintsService.sevenDaySolveRate(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "投诉率", notes = "投诉率")
    @RequestMapping(value = "/complaintRate", method = RequestMethod.POST)
    public RetResult complaintRate(@RequestBody ComplaintDto complaintDto) {
        Map<String,Object> message = complaintsService.complaintRate(complaintDto);
        return RetResponse.makeOKRsp(message);
    }
    
    @ApiOperation(value = "投诉率 - 下钻页面之投诉率", notes = "投诉率 - 下钻页面之投诉率")
    @RequestMapping(value = "/complaintRate/rate", method = RequestMethod.POST)
    public RetResult complaintRateRate(@RequestBody ComplaintDto complaintDto) {
        Map<String,Object> message = complaintsService.complaintRateRate(complaintDto);
        return RetResponse.makeOKRsp(message);
    }
    
    /**
     * 投诉率 - 下钻页面之投诉原因
     * @param complaintDto
     * @return
     */
    @ApiOperation(value = "投诉率 - 下钻页面之投诉原因", notes = "投诉率 - 下钻页面之投诉原因")
    @RequestMapping(value = "/complaintRate/reason", method = RequestMethod.POST)
    public RetResult complaintRateReason(@RequestBody ComplaintDto complaintDto) {
        Object result = complaintsService.complaintRateReason(complaintDto);
        return RetResponse.makeOKRsp(result);
    }
    
    /**
     * 投诉率 - 下钻页面之产品品类投诉率
     * @param complaintDto
     * @return
     */
    @ApiOperation(value = "投诉率 - 下钻页面之产品品类投诉率", notes = "投诉率 - 下钻页面之产品品类投诉率")
    @RequestMapping(value = "/complaintRate/productCategory", method = RequestMethod.POST)
    public RetResult complaintRateProductCategory(@RequestBody ComplaintDto complaintDto) {
    	List<Map<String, Object>> result = complaintsService.complaintRateProductCategory(complaintDto);
        return RetResponse.makeOKRsp(result);
    }
    
    /**
     * 投诉率 - 下钻页面之大区投诉率
     * @param complaintDto
     * @return
     */
    @ApiOperation(value = "投诉率 - 下钻页面之大区投诉率", notes = "投诉率 - 下钻页面之大区投诉率")
    @RequestMapping(value = "/complaintRate/accountingCenter", method = RequestMethod.POST)
    public RetResult complaintRateAccountingCenter(@RequestBody ComplaintDto complaintDto) {
    	List<Map<String, Object>> result = complaintsService.complaintRateAccountingCenter(complaintDto);
        return RetResponse.makeOKRsp(result);
    }
    
    /**
     * 投诉率 - 下钻页面之地域(省份)投诉率
     * @param complaintDto
     * @return
     */
    @ApiOperation(value = "投诉率 - 下钻页面之地域(省份)投诉率", notes = "投诉率 - 下钻页面之地域(省份)投诉率")
    @RequestMapping(value = "/complaintRate/provinces", method = RequestMethod.POST)
    public RetResult complaintRateProvinces(@RequestBody ComplaintDto complaintDto) {
    	List<Map<String, Object>> result = complaintsService.complaintRateProvinces(complaintDto);
        return RetResponse.makeOKRsp(result);
    }

    @ApiOperation(value = "未结案", notes = "未结案")
    @RequestMapping(value = "/notOverCase", method = RequestMethod.POST)
    public RetResult notOverCase(@RequestBody ComplaintDto complaintDto) {
        Map<String,Object> message = complaintsService.notOverCase(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "全国投诉地图", notes = "全国投诉地图")
    @RequestMapping(value = "/nationalComplaintMap", method = RequestMethod.POST)
    public RetResult nationalComplaintMap(@RequestBody ComplaintDto complaintDto) {
        Map<String,Object> message = complaintsService.nationalComplaintMap(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "排名", notes = "排名")
    @RequestMapping(value = "/rankMap", method = RequestMethod.POST)
    public RetResult rankMap(@RequestBody ComplaintDto complaintDto) {
        List<Map<String,Object>> message = complaintsService.rankMap(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "投诉异常监控", notes = "投诉异常监控")
    @RequestMapping(value = "/complaintAbnormalMonitor", method = RequestMethod.POST)
    public RetResult complaintAbnormalMonitor(@RequestBody ComplaintDto complaintDto) {
        List<Map<String,Object>> message = complaintsService.complaintAbnormalMonitor(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "查询单据列表", notes = "查询单据列表")
    @RequestMapping(value = "/selectDocumentsList", method = RequestMethod.POST)
    public Object selectDocumentsList(@RequestBody Page<ComplaintDto> page) {
        Object message = complaintsService.selectDocumentsList(page.getPageData(),page.getPage(),page.getSize());
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "投诉原因分析", notes = "投诉原因分析")
    @RequestMapping(value = "/complaintReasonAnalysis", method = RequestMethod.POST)
    public RetResult complaintReasonAnalysis(@RequestBody ComplaintDto complaintDto) {
        List<Map<String,Object>> message = complaintsService.complaintReasonAnalysis(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "内部投诉7天解决率", notes = "内部投诉7天解决率")
    @RequestMapping(value = "/innerSevenDaySolveRate", method = RequestMethod.POST)
    public RetResult innerSevenDaySolveRate(@RequestBody ComplaintDto complaintDto) {
        Map<String,Object> message = complaintsService.innerSevenDaySolveRate(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "N+1解决方案及时提交率", notes = "N+1解决方案及时提交率")
    @RequestMapping(value = "/complaintSolveSubmissionRate", method = RequestMethod.POST)
    public RetResult complaintSolveSubmissionRate(@RequestBody ComplaintDto complaintDto) {
        Map<String,Object> message = complaintsService.complaintSolveSubmissionRate(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "投诉来源分析", notes = "投诉来源分析")
    @RequestMapping(value = "/complaintSourceAnalysis", method = RequestMethod.POST)
    public RetResult complaintSourceAnalysis(@RequestBody ComplaintDto complaintDto) {
        List<Map<String,Object>> message = complaintsService.complaintSourceAnalysis(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "品类投诉分析", notes = "品类投诉分析")
    @RequestMapping(value = "/categoryComplaintAnalysis", method = RequestMethod.POST)
    public RetResult categoryComplaintAnalysis(@RequestBody ComplaintDto complaintDto) {
        List<Map<String,Object>> message = complaintsService.categoryComplaintAnalysis(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "全部大区", notes = "全部大区")
    @RequestMapping(value = "/selectAllRegion", method = RequestMethod.POST)
    public RetResult selectAllRegion() {
        List<Map<String,Object>> message = complaintsService.selectAllRegion();
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "根据投诉单号查询投诉详情", notes = "根据投诉单号查询投诉详情")
    @RequestMapping(value = "/selectComplaintDetailByNumber", method = RequestMethod.POST)
    public RetResult selectComplaintDetailByNumber(@RequestBody Map<String,Object> map) {
        Map<String,Object> message = complaintsService.selectComplaintDetailByNumber(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "根据投诉单号查询投诉留言信息", notes = "根据投诉单号查询投诉留言信息")
    @RequestMapping(value = "/selectComplaintMessageByNumber", method = RequestMethod.POST)
    public RetResult selectComplaintMessageByNumber(@RequestBody Map<String,Object> map) {
        List<Map<String,Object>> message = complaintsService.selectComplaintMessageByNumber(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "根据投诉单号查询投诉处理明细", notes = "根据投诉单号查询投诉处理明细")
    @RequestMapping(value = "/selectComplaintProcessDetailByNumber", method = RequestMethod.POST)
    public RetResult selectComplaintProcessDetailByNumber(@RequestBody Map<String,Object> map) {
        List<Map<String,Object>> message = complaintsService.selectComplaintProcessDetailByNumber(map);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "全省投诉地图", notes = "全省投诉地图")
    @RequestMapping(value = "/provinceComplaintMap", method = RequestMethod.POST)
    public RetResult provinceComplaintMap(@RequestBody ComplaintDto complaintDto) {
        Map<String,Object> message = complaintsService.provinceComplaintMap(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    @ApiOperation(value = "门店排名", notes = "门店排名")
    @RequestMapping(value = "/storeRank", method = RequestMethod.POST)
    public RetResult storeRank(@RequestBody ComplaintDto complaintDto) {
        List<Map<String,Object>> message = complaintsService.storeRank(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

}
