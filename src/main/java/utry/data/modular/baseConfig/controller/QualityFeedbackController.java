package utry.data.modular.baseConfig.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.core.common.BusinessException;
import utry.core.log.UtryLogger;
import utry.core.log.UtryLoggerFactory;
import utry.data.modular.baseConfig.dto.ModelTimeDTO;
import utry.data.modular.baseConfig.dto.ProductInformationDTO;
import utry.data.modular.baseConfig.dto.QualityFeedbackDTO;
import utry.data.modular.baseConfig.model.Target;
import utry.data.modular.baseConfig.service.QualityFeedbackService;
import utry.data.modular.technicalQuality.controller.TechnicalQualityController;
import utry.data.modular.technicalQuality.dto.ConditionDTO;
import utry.data.modular.technicalQuality.dto.RepairRateDTO;
import utry.data.modular.technicalQuality.dto.RepairRateQueryDTO;
import utry.data.modular.technicalQuality.utils.Column;
import utry.data.modular.technicalQuality.utils.ExcelTool;
import utry.data.modular.technicalQuality.utils.TitleEntity;
import utry.data.util.DateConditionUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 品质反馈配置管理
 */
@Controller
@RequestMapping("/QualityFeedback")
@Api(tags = "技术品质配置管理")
public class QualityFeedbackController extends CommonController {

    @Resource
    private QualityFeedbackService qualityFeedbackService;
    private static final UtryLogger LOGGER = UtryLoggerFactory.getLogger(TechnicalQualityController.class);

    @PostMapping("/export")
    @ApiOperation("导出模板")
    @ResponseBody
    public RetResult writeExcel(HttpServletResponse response) throws IOException {
        // 固定表头
        String[] headers = {"产品型号", "新品说明书上传日期", "新品维修手册上传日期"};
        // 生成工作表，设置表名和列名
        HSSFWorkbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("新品导入样表");
        HSSFCellStyle cellStyle = qualityFeedbackService.getHeadStyle(book);
        Row row = sheet.createRow(0);
        Cell cell;
        int i;
        for (i = 0; i < headers.length; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(headers[i]);
        }
        // 导出excel模板
        String fileName = "新品导入样表" + ".xls";
        qualityFeedbackService.export(response, book, fileName);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/import")
    @ApiOperation("导入")
    @ResponseBody
    public RetResult readExcel(MultipartFile file) throws IOException {
        //参数校验
        qualityFeedbackService.validParams(file);
        return RetResponse.makeOKRsp("导入成功");
    }

    @PostMapping("/select/{currentPage}/{pageSize}")
    @ApiOperation("查询所有")
    @ResponseBody
    public RetResult select(@PathVariable @ApiParam(value = "分页每页条数") String pageSize,
                            @PathVariable @ApiParam(value = "当前页数") String currentPage,@RequestBody ModelTimeDTO modelTimeDTO) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(currentPage, pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        //参数校验
        List<ProductInformationDTO> list = qualityFeedbackService.select(modelTimeDTO);
        PageInfo<ProductInformationDTO> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);
    }

    @PostMapping("/update")
    @ApiOperation("编辑")
    @ResponseBody
    public RetResult update(@RequestBody QualityFeedbackDTO qualityFeedbackDTO) {
        if (StringUtils.isEmpty(qualityFeedbackDTO.getProductModel())){
            return RetResponse.makeErrRsp("产品类型不能为空");
        }
        //更新
        qualityFeedbackService.update(qualityFeedbackDTO);
        return RetResponse.makeOKRsp();
    }

    @PostMapping("/exportProductInformationList")
    @ApiOperation("导出---产品资料列表")
    @ResponseBody
    public void exportProductInformationList(HttpServletResponse response, @RequestBody ModelTimeDTO modelTimeDTO) {
        List<ProductInformationDTO> list = qualityFeedbackService.select(modelTimeDTO);
        // 固定表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("productModel", "产品型号");
        headerMap.put("productCategory", "产品品类");
        headerMap.put("productType", "产品类型");
        headerMap.put("listingDate", "上市日期");
        headerMap.put("manualTime", "新品说明书上传日期");
        headerMap.put("serviceManualTime", "新品维修手册上传日期");
        headerMap.put("updateTime", "更新时间");
        List<TitleEntity> titleList = new ArrayList<>();
        String pid = "-1";
        int i = 0;
        for(Map.Entry<String, String> entry:headerMap.entrySet()){
            TitleEntity entity = new TitleEntity(String.valueOf(i), pid, entry.getValue(), entry.getKey());
            titleList.add(entity);
            i++;
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        ExcelTool excelTool = new ExcelTool("产品资料列表"+operationTime+".xls",20,20, null, "产品资料");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, list,true);
        } catch (Exception e) {
            LOGGER.error("产品资料列表导出失败", e);
            throw new BusinessException("导出失败");
        }
    }
}

