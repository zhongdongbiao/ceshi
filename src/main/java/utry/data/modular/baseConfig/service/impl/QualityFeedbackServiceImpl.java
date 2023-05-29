package utry.data.modular.baseConfig.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utry.core.common.BusinessException;
import utry.data.modular.baseConfig.dao.QualityFeedbackConfigDao;
import utry.data.modular.baseConfig.dao.TargetCoreConfigDao;
import utry.data.modular.baseConfig.dto.*;
import utry.data.modular.baseConfig.model.Target;
import utry.data.modular.baseConfig.service.QualityFeedbackService;
import utry.data.modular.baseConfig.service.TargetCoreConfigService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.apache.poi.ss.usermodel.DateUtil.getJavaDate;
import static org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted;

/**
 * 目标配置管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class QualityFeedbackServiceImpl implements QualityFeedbackService {
    @Resource
    private QualityFeedbackConfigDao qualityFeedbackConfigDao;

    @Override
    public void export(HttpServletResponse response, HSSFWorkbook book, String fileName) {
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            String encodedFileName = new String(fileName.getBytes("ISO-8859-1"), "utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            OutputStream outputStream = response.getOutputStream();
            book.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new BusinessException("导出Excel失败");
        }
    }

    @Override
    public HSSFCellStyle getHeadStyle(HSSFWorkbook book) {
        HSSFCellStyle style = book.createCellStyle();
        style.setWrapText(true);//自动换行
        style.setAlignment(HorizontalAlignment.CENTER);// 左右居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
        // 设置单元格的背景颜色为淡蓝色
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置单元格边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        // 设置字体,大小
        HSSFFont font = book.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    @Override
    public void validParams(MultipartFile file) throws IOException {
        List<QualityFeedbackDTO> list = new ArrayList<>();
        //判断导入excel是xls格式还是xlsx格式
        boolean ret = file.getOriginalFilename().endsWith("xls");
        boolean retSecond = file.getOriginalFilename().endsWith("xlsx");
        Workbook workbook;
        if (ret) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else if (retSecond) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else {
            throw new BusinessException("请上传正确的文件格式");
        }
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null || sheet.getLastRowNum()==0) {
            throw new BusinessException("文件为空，导入失败");
        }
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        //原来的数据
        List<QualityFeedbackDTO> oldList = qualityFeedbackConfigDao.selectAllInformation();
        Row headerRow = sheet.getRow(0);
        if(!("产品型号".equals(getCellValue(headerRow.getCell(0),evaluator))&&("新品说明书上传日期".equals(getCellValue(headerRow.getCell(1),evaluator)))&&("新品维修手册上传日期".equals(getCellValue(headerRow.getCell(2),evaluator))))){
            throw new BusinessException("导入失败，请检查导入表头是否与模板表头一致");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isRowEmpty(row)) {
                continue;
            }
            //接收参数对象
            String productModel = getCellValue(sheet.getRow(r).getCell(0),evaluator);
            String manualTime = getCellValue(sheet.getRow(r).getCell(1),evaluator);
            String serviceManualTime = getCellValue(sheet.getRow(r).getCell(2),evaluator);
            if(StringUtils.isEmpty(productModel)||
                    (StringUtils.isEmpty(manualTime)&&StringUtils.isEmpty(serviceManualTime))){
                throw new BusinessException("必要参数不能为空");
            }
            productModel = getCellValue(sheet.getRow(r).getCell(0),evaluator).trim();
            try {
                if(StringUtils.isNotEmpty(manualTime)){
                    Date m = sdf.parse(manualTime.trim());
                    manualTime = sdf.format(m);
                }
                if(StringUtils.isNotEmpty(serviceManualTime)){
                    Date s = sdf.parse(serviceManualTime.trim());
                    serviceManualTime = sdf.format(s);
                }
            }catch (Exception e){
                throw new BusinessException("导入的日期（yyyy-MM-dd）不规范，请检查");
            }
            List<String> errList = new ArrayList<>();
            for(QualityFeedbackDTO oq :oldList){
                if(oq.getProductModel().equals(productModel)){
                    errList.add(productModel);
                }
            }
            if(CollectionUtils.isEmpty(errList)){
                throw new BusinessException("导入的产品型号"+productModel+"不存在");
            }
            QualityFeedbackDTO qualityFeedbackDTO = new QualityFeedbackDTO();
            qualityFeedbackDTO.setProductModel(productModel);
            qualityFeedbackDTO.setManualTime(manualTime);
            qualityFeedbackDTO.setServiceManualTime(serviceManualTime);
            list.add(qualityFeedbackDTO);
        }
            qualityFeedbackConfigDao.updateTime(list);

    }

    @Override
    public List<ProductInformationDTO> select(ModelTimeDTO modelTimeDTO) {
        return qualityFeedbackConfigDao.select(modelTimeDTO);
    }

    @Override
    public void update(QualityFeedbackDTO qualityFeedbackDTO) {
        qualityFeedbackConfigDao.update(qualityFeedbackDTO);
    }

    /**
     * 判断行是否为空
     *
     * @param row 行对象
     * @return 判断结果
     */
    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取单元格的数据,类型判断，统一返回为String
     */
    public String getCellValue(Cell cell,FormulaEvaluator evaluator) {

        //处理科学计数法
        NumberFormat nf = NumberFormat.getInstance();
        //判断是否为null或空串
        if (cell == null || "".equals(cell.toString().trim())) {
            return "";
        }
        String cellValue = "";
        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = evaluator.evaluate(cell).getCellType();
        }
        switch (cellType) {
            //字符串类型
            case STRING:
                cellValue = cell.getStringCellValue().trim();
                cellValue = StringUtils.isEmpty(cellValue) ? "" : cellValue;
                break;
            //数值类型
            case NUMERIC:
                if (isCellDateFormatted(cell)) {
                    //注：format格式 yyyy-MM-dd hh:mm:ss 中小时为12小时制，若要24小时制，则把小h变为H即可，yyyy-MM-dd HH:mm:ss
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    cellValue=sdf.format(getJavaDate(cell.
                            getNumericCellValue()));
                    break;
                } else {
                    cellValue = new DecimalFormat("0").format(cell.getNumericCellValue());
                }
                break;
            //其它类型
            default:
                cellValue = "";
                break;
        }
        return cellValue;
    }
}

