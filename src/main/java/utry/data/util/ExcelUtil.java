package utry.data.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import utry.core.common.BusinessException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于导入、导出execel基本样式定义
 */
@SuppressWarnings("deprecation")
public class ExcelUtil {
    private HSSFWorkbook workbook;

    private HSSFSheet sheet;

    private HSSFRow row;

    private HSSFCellStyle titleStyle, columnHeadStyle, dataStyle;

    /**
     * 初始化Excel
     */
    public ExcelUtil() {
        this.workbook = new HSSFWorkbook();
        // 设置表头字体
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 20);// 字体大小
//        titleFont.setBold(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
        titleFont.setBold(true);// 加粗

        // 表头样式
        this.titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
//        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        titleStyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中
//        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);// 上下居下
        titleStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);// 上下居下

        // 设置列头字体
        HSSFFont columnHeadFont = workbook.createFont();
        columnHeadFont.setFontName("Calibri");
        columnHeadFont.setFontHeightInPoints((short) 10);
//        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 加粗
        columnHeadFont.setBold(true); // 加粗
        // 列头样式
        this.columnHeadStyle = workbook.createCellStyle();
        columnHeadStyle.setFont(columnHeadFont);
//        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 居中
        columnHeadStyle.setAlignment(HorizontalAlignment.CENTER);// 居中
        // 设置边框
//        columnHeadStyle.setBorderLeft((short) 1);
        columnHeadStyle.setBorderLeft(BorderStyle.THIN);
        columnHeadStyle.setBorderRight(BorderStyle.THIN);
        columnHeadStyle.setBorderTop(BorderStyle.THIN);
        columnHeadStyle.setBorderBottom(BorderStyle.THIN);
        //设置背景色
//        columnHeadStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
//        columnHeadStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // 设置数据字体
        HSSFFont dataFont = workbook.createFont();
        dataFont.setFontName("Calibri");
        dataFont.setFontHeightInPoints((short) 10);
        // 数据样式
        this.dataStyle = workbook.createCellStyle();
        dataStyle.setFont(dataFont);
//        dataStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        // 设置边框
//        dataStyle.setBorderLeft((short) 1);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
    }

    public void createNewSheet() {
        this.sheet = workbook.createSheet();
    }

    /**
     * 设置每列列宽
     *
     * @param columnWidths
     */
    public void setColumnWidths(int[] columnWidths) {
        for (int i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }
    }

    /**
     * 设置标题
     *
     * @param title     标题
     * @param columnNum 合并单元格的列数
     */
    public void setTitle(String title, int columnNum) {
        createRow(0);
        this.row.setHeight((short) 525);

        HSSFCell cell0 = this.row.createCell(0);
        cell0.setCellStyle(this.titleStyle);
        cell0.setCellValue(new HSSFRichTextString(title));
        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNum - 1));
    }

    /**
     * 设置表尾备注
     */
    public void setDescript(String title, int column, int columnNum) {
        createRow(column);
        this.row.setHeight((short) 1000);

        HSSFCell cell0 = this.row.createCell(0);
        cell0.setCellStyle(this.titleStyle);
        cell0.setCellValue(new HSSFRichTextString(title));
        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(column, column + 1, 0, columnNum - 1));
    }

    /**
     * 设置列头
     *
     * @param index
     * @param value
     */
    public void setColumnCell(int index, String value) {
        HSSFCell cell1 = this.row.createCell(index);
        cell1.setCellStyle(this.columnHeadStyle);
        cell1.setCellValue(new HSSFRichTextString(value));
    }

    /**
     * 增加一行
     *
     * @param index 行号
     */
    public void createRow(int index) {
        this.row = this.sheet.createRow(index);
    }

    /**
     * 设置数据单元格
     *
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setDataCell(int index, String value) {
        HSSFCell cell = this.row.createCell(index);
        cell.setCellStyle(this.dataStyle);
        cell.setCellValue(new HSSFRichTextString(value));
    }

    /**
     * 设置数据单元格
     *
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setDataCell(int index, int value) {
        HSSFCell cell = this.row.createCell(index);
//        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(this.dataStyle);
        cell.setCellValue(value);
    }

    /**
     * 导出excel文件
     */
    public void downloadExcle(HttpServletResponse response, String fileName) throws Exception {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            //String encodedFileName = new String(fileName.getBytes("gb2312"), "ISO8859-1");
            fileName = URLEncoder.encode(fileName, "UTF-8");
            String encodedFileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName);
            response.setContentType("application/vnd.ms-excel");
            this.workbook.write(out);
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    public void exportExcel(HttpServletResponse response, String fileName) {
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            String encodedFileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            OutputStream outputStream = response.getOutputStream();
            this.workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new BusinessException("导出Excel失败");
        }
    }

    /**
     * 导出Excel公共方法
     * 创建人：zhengshan
     * 创建时间：Aug 25, 2014 3:39:20 PM
     *
     * @param mainData    List<Map<String, String>> mainData 主表数据
     * @param mainTitle   String[][] mainTitle 主表列标题/主表列key
     * @param detailData  List<Map<String, String>> detailData 明细表数据
     * @param detailTitle String[][] detailTitle 明细表列标题/明细表列key
     * @return 导出Excel流
     */
    public static InputStream getExcelStream(
            List<HashMap<String, String>> mainData, String[][] mainTitle,
            List<HashMap<String, String>> detailData, String[][] detailTitle) {
        // 1.创建表格对象及设置边框
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFCellStyle setBorder = workbook.createCellStyle();
        setBorder.setBorderBottom(BorderStyle.THIN); //下边框
        setBorder.setBorderLeft(BorderStyle.THIN);//左边框
        setBorder.setBorderTop(BorderStyle.THIN);//上边框
        setBorder.setBorderRight(BorderStyle.THIN);//右边框
        setBorder.setWrapText(true);//设置自动换行

        // 2.设置主表格title
        HSSFRow row = sheet.createRow(0);// 创建第一行
        HSSFCell cell = null;// 第一列
        cell = row.createCell(0);
        cell.setCellValue("序号");
        for (int i = 0; i < mainTitle[0].length; i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(mainTitle[0][i]);
        }

        // 3.设置主表格数据
        for (int i = 0; i < mainData.size(); i++) {
            Map<String, String> map = mainData.get(i);
            row = sheet.createRow(i + 1);// 创建第i+1行

            cell = row.createCell(0);// 创建第一列
            cell.setCellValue(i + 1);
            for (int j = 0; j < mainTitle[1].length; j++) {
                cell = row.createCell(j + 1);
                cell.setCellValue(String.valueOf(map.get(mainTitle[1][j])));
            }
        }

        // 4.设置明细表格title
        row = sheet.createRow(mainData.size() + 1);
        cell = row.createCell(0);
        cell.setCellValue("序号");
        for (int i = 0; i < detailTitle[0].length; i++) {
            cell = row.createCell(i + 1); // 创建第1列
            cell.setCellValue(detailTitle[0][i]);
        }

        // 5.设置明细表格数据
        for (int i = 0; i < detailData.size(); i++) {
            Map<String, String> map = detailData.get(i);
            row = sheet.createRow(i + mainData.size() + 2);// 创建第i+1行

            cell = row.createCell(0);// 创建第一列
            cell.setCellValue(i + 1);
            for (int j = 0; j < detailTitle[1].length; j++) {
                cell = row.createCell(j + 1);
                cell.setCellValue(String.valueOf(map.get(detailTitle[1][j])));
            }
        }

        //调整列宽
        for (int i = 0; i < row.getLastCellNum(); i++) {
            sheet.autoSizeColumn((short) i);
        }

        // 6.写入输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            workbook.write(baos);// 写入
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] ba = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);

        return bais;
    }

    /**
     * 导出Excel公共方法
     * 创建人：zhengshan
     * 创建时间：Aug 25, 2014 3:39:20 PM
     *
     * @param mainData  List<Map<String, String>> mainData 主表数据
     * @param mainTitle String[][] mainTitle 主表列标题/主表列key
     * @return 导出Excel流
     */
    public static InputStream getExcelStream(
            List<Map<String, String>> mainData, String[][] mainTitle) {
        // 1.创建表格对象及设置边框
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFCellStyle setBorder = workbook.createCellStyle();
        setBorder.setBorderBottom(BorderStyle.THIN); //下边框
        setBorder.setBorderLeft(BorderStyle.THIN);//左边框
        setBorder.setBorderTop(BorderStyle.THIN);//上边框
        setBorder.setBorderRight(BorderStyle.THIN);//右边框
        setBorder.setWrapText(true);//设置自动换行

        // 2.设置主表格title
        HSSFRow row = sheet.createRow(0);// 创建第一行
        HSSFCell cell = null;// 第一列
        cell = row.createCell(0);
        cell.setCellValue("序号");
        for (int i = 0; i < mainTitle[0].length; i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(mainTitle[0][i]);
        }

        // 3.设置主表格数据
        for (int i = 0; i < mainData.size(); i++) {
            Map<String, String> map = mainData.get(i);
            row = sheet.createRow(i + 1);// 创建第i+1行

            cell = row.createCell(0);// 创建第一列
            cell.setCellValue(i + 1);
            for (int j = 0; j < mainTitle[1].length; j++) {
                cell = row.createCell(j + 1);
                cell.setCellValue(String.valueOf(map.get(mainTitle[1][j])));
            }
        }

        //调整列宽
        for (int i = 0; i < row.getLastCellNum(); i++) {
            sheet.autoSizeColumn((short) i);
        }

        // 4.写入输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            workbook.write(baos);// 写入
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] ba = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);

        return bais;
    }

    /**
     * 设置表头格式
     *
     * @param book
     * @return
     */
    public static HSSFCellStyle getHeadStyle(HSSFWorkbook book) {
        HSSFCellStyle style = book.createCellStyle();
        style.setWrapText(true);//自动换行
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        style.setAlignment(HorizontalAlignment.CENTER);// 左右居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
        // 设置单元格的背景颜色为淡蓝色
//        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
//        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        // 设置单元格边框为细线条
//        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        // 设置字体,大小,
        HSSFFont font = book.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 10);
//        font.setBoldweight(font.BOLDWEIGHT_BOLD);
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /**
     * 导出为Excel
     *
     * @param response
     * @param book
     */
    public static void exportExcel(HttpServletResponse response, HSSFWorkbook book, String fileName) {
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            String encodedFileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            OutputStream outputStream = response.getOutputStream();
            book.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new BusinessException("导出Excel失败");
        }
    }

}
