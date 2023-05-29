package utry.data.modular.calendar.service.impl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utry.core.common.BusinessException;
import utry.data.modular.calendar.dao.CalendarMapper;
import utry.data.modular.calendar.model.CalendarDto;
import utry.data.modular.calendar.service.ICalendarService;
import utry.data.util.DateTimeUtil;
import utry.data.util.ExcelUtil;
import utry.data.modular.calendar.vo.AttendanceCalendarVo ;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:
 * @author: lvlb
 * @Date: 2020/9/23 13:26
 */
@Service
public class CalendarServiceImpl implements ICalendarService {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Resource
    private CalendarMapper calendarMapper;


    /***
     * 获取日历数据
     * @param calendarDto
     * @return
     */
    @Override
    public List<CalendarDto> getCalendarList(CalendarDto calendarDto) throws Exception {
        /** 添加同步锁，优化并发导致生成两次数据的问题 */
        synchronized (this) {
            if (null != calendarDto) {
                //1、查询当前年月初始化数据
                int yearDataCount = calendarMapper.selectCountByYear(calendarDto);
                //2、若数据记录数少于365条，需要清空数据，重新初始化
                if (yearDataCount < 365) {
                    calendarMapper.deleteCountByYear(calendarDto);
                    DateFormat fmt = new SimpleDateFormat("yyyy");
                    Date initDate = fmt.parse(calendarDto.getYear());
                    Calendar cal = Calendar.getInstance();
                    cal.clear();
                    // 设置制定日期
                    cal.setTime(initDate);
                    // 设置月份从1月开始
                    cal.set(Calendar.MONTH, 0);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    //获取设置的日期年份
                    Integer year = cal.get(Calendar.YEAR);
                    for (int i = 0; i < 12; i++) {
                        Integer month = cal.get(Calendar.MONTH) + 1;
                        // 计算每个月有多少天
                        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        for (int j = 0; j < days; j++, cal.add(Calendar.DATE, 1)) {
                            Integer day = cal.get(Calendar.DATE);
                            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                            String fullDate = sf.format(cal.getTime());
                            //假期类型：0工作日1非工作日
                            int type = 0;
                            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                type = 1;
                            }
                            CalendarDto dto = new CalendarDto();
                            dto.setYear(String.valueOf(year));
                            dto.setMonth(String.valueOf(month));
                            dto.setDay(String.valueOf(day));
                            dto.setFullDate(fullDate);
                            dto.setType(type);
                            SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
                            String fullDate2 = sf2.format(cal.getTime());
                            String currentWeek = DateTimeUtil.getCurrentWeek(fullDate2);
                            dto.setDayOfWeek(currentWeek);
                            calendarMapper.insertCalendarData(dto);
                            //cal.add()方法设置每次增加一个月
                            cal.add(Calendar.MONTH, 0);
                        }
                    }
                }
            }
        }
        return calendarMapper.getCalendarList(calendarDto);
    }

    /***
     * 更新假期类型
     * @param calendarDto
     * @return
     */
    @Override
    public int updateCalendarType(CalendarDto calendarDto) {
        return calendarMapper.updateCalendarType(calendarDto);
    }

    /***
     * 批量更新假期类型
     * @param calendarList
     */
    @Override
    public void batchUpdateCalendarType(List<CalendarDto> calendarList) {
        if (null != calendarList && calendarList.size() > 0) {
            for (CalendarDto calendarDto : calendarList) {
                calendarMapper.updateCalendarType(calendarDto);
            }
        }
    }

    @Override
    public CalendarDto selectTypeByYear(String fullDate) {
        String[] split = fullDate.split("-");
        CalendarDto calendarDto = new CalendarDto();
        calendarDto.setYear(split[0]);
        calendarDto.setMonth(split[1]);
        calendarDto.setDay(split[2]);
        try {
            this.getCalendarList(calendarDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarMapper.selectTypeByYear(fullDate);
    }

    @Override
    public List<AttendanceCalendarVo> getCalendarVoList(CalendarDto calendarDto) {
        return calendarMapper.getCalendarVoList(calendarDto);
    }

    @Override
    public void importCalendarTemplate(MultipartFile file) {
        List<CalendarDto> calendarList = new ArrayList<>();
        Workbook workbook = null;
        String filename = file.getOriginalFilename();
        String type = filename.indexOf(".") != -1 ? filename.substring(filename.lastIndexOf(".") + 1, filename.length()) : null;
        if (!"xls".equals(type) && !"xlsx".equals(type)) {
            throw new RuntimeException("文件类型不是excel!");
        }
        try {
            if (type.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else if (type.endsWith("xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheetAt = workbook.getSheetAt(0);
        int lastRowNum = sheetAt.getLastRowNum();
        for (int i = 2; i < lastRowNum + 1; i++) {
            CalendarDto calendarDto = new CalendarDto();
            Row row = sheetAt.getRow(i);
            if (null != row) {
                int firstCellNum = row.getFirstCellNum();
                int lastCellNum = row.getLastCellNum();
                for (int j = firstCellNum; j < lastCellNum; j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = getStrCellFormat(cell, false);
                    if (0 == j) {
                        calendarDto.setFullDate(cellValue);
                    }
                    if (1 == j) {
                        calendarDto.setTypeStatus("节假日".equals(cellValue) ? "1" : "0");
                        calendarDto.setType("节假日".equals(cellValue) ? 1 : 0);
                    }
                    if (2 == j) {
                        calendarDto.setDepict(cellValue);
                    }
                }
                calendarList.add(calendarDto);
            }
        }
        this.batchUpdateCalendarTypeByDate(calendarList);
    }

    @Override
    public void batchUpdateCalendarTypeByDate(List<CalendarDto> calendarList) {
        if (null != calendarList && calendarList.size() > 0) {
            for (CalendarDto calendarDto : calendarList) {
                String fullDate = calendarDto.getFullDate();
                if (fullDate.contains("/")) {
                    String year = "";
                    String month = "";
                    String day = "";
                    String[] split = fullDate.split("/");
                    for (int i = 0; i < split.length; i++) {
                        year = split[0];
                        String s = split[i];
                        if (Integer.parseInt(s) < 10) {
                            if (1 == i) {
                                month = "0" + s;
                            }
                            if (2 == i) {
                                day = "0" + s;
                            }
                        }
                    }
                    fullDate = year + "-" + month + "-" + day;
                }
                calendarDto.setFullDate(fullDate);
                calendarMapper.updateCalendarTypeByDate(calendarDto);
            }
        }
    }

    @Override
    public void exportCalendarTemplate(HttpServletResponse response) {
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.createNewSheet();
        excelUtil.setTitle("节假日模板", 3);
        excelUtil.createRow(1);
        excelUtil.setColumnCell(0, "日期");
        excelUtil.setColumnCell(1, "类型(节假日/补班)");
        excelUtil.setColumnCell(2, "描述");
        excelUtil.createRow(2);
        excelUtil.setDataCell(0, "2022/1/1");
        excelUtil.setDataCell(1, "节假日");
        excelUtil.setDataCell(2, "元旦");
        excelUtil.createRow(3);
        excelUtil.setDataCell(0, "2022/1/2");
        excelUtil.setDataCell(1, "节假日");
        excelUtil.setDataCell(2, "元旦");
        excelUtil.createRow(4);
        excelUtil.setDataCell(0, "2022/1/3");
        excelUtil.setDataCell(1, "节假日");
        excelUtil.setDataCell(2, "元旦");
        try {
            //excelUtil.downloadExcle(response, "节假日模板");
            excelUtil.exportExcel(response, "节假日模板.xls");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getWhetherWorkTime(String time) {
        String day = time.substring(0,10);
        int type = calendarMapper.selectTypeByYear(day).getType();
        String result = "";
        if (type == 1) {    // 非工作日
            result = "1";
        } else {
            // 查询冬夏令时
//            Map<String, Object> dayLightTimeData = attendanceDataService.getDayLightTimeData();
            Map<String, Object> dayLightTimeData = null;
            String morningStart = ((String) dayLightTimeData.get("morningTime")).substring(0,8);
            String morningEnd = ((String) dayLightTimeData.get("morningTime")).substring(9,17);
            String afternoonStart = ((String) dayLightTimeData.get("afternoonTime")).substring(0,8);
            String afternoonEnd = ((String) dayLightTimeData.get("afternoonTime")).substring(9,17);
            String t = time.substring(11,19);
            if (t.compareTo(morningStart)<0 || t.compareTo(afternoonEnd)>0 || (t.compareTo(morningEnd)>0 && t.compareTo(afternoonStart)<0)) {
                result = "1";
            } else {
                result = "0";
            }
        }
        return result;
    }

    public static String getStrCellFormat(Cell cell, boolean allowBlank) {
        if (cell == null || "".equals(cell.toString())) {
            if (allowBlank) {
                return "";
            } else {
                throw new BusinessException("存在不允许为空的空字段");
            }
        } else {
            switch (cell.getCellType()) {
//                case Cell.CELL_TYPE_NUMERIC:
                case NUMERIC:
                    return sdf.format(cell.getDateCellValue());
//                case Cell.CELL_TYPE_STRING:
                case STRING:
                    return cell.getStringCellValue();
                default:
                    return cell.toString().trim();
            }
        }
    }

    @Override
    public Map<String, Object> getDayLightTimeData() {
        return calendarMapper.getDayLightTimeData();
    }

    @Override
    public void setDayLightTimeData(String type, String name) {
        //当这个启用的时候，自动把另外一个改为停用。
        //type 1启用 0 不启用
        calendarMapper.setDayLightTimeData(type, name);
        String type2 = "1".equals(type) ? "0" : "1";
        String name2 = "夏令时".equals(name) ? "冬令时" : "夏令时";

        calendarMapper.setDayLightTimeData(type2, name2);
    }
}
