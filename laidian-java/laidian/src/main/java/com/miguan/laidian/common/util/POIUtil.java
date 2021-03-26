package com.miguan.laidian.common.util;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * POI 工具类
 */
public class POIUtil {

    public static List<Map<String,String>> readExecl(MultipartFile opinionImageFile,String columns[]) {
        Workbook wb = null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String, String>> list = null;
        String cellData = null;
        try{
            InputStream inputStream = opinionImageFile.getInputStream();
            String[] split = opinionImageFile.getOriginalFilename().split("\\.");
            if ("xls".equals(split[1])) {
                wb = new HSSFWorkbook(inputStream);
            } else if ("xlsx".equals(split[1])) {
                wb = new XSSFWorkbook(inputStream);
            }
            if (wb != null) {
                //用来存放表中数据
                list = new ArrayList<Map<String, String>>();
                //获取第一个sheet
                sheet = wb.getSheetAt(0);
                //获取最大行数
                int rownum = sheet.getPhysicalNumberOfRows();
                //获取第一行
                row = sheet.getRow(0);
                //获取最大列数
                int colnum = row.getPhysicalNumberOfCells();
                for (int i = 1; i < rownum; i++) {
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    row = sheet.getRow(i);
                    if (row != null) {
                        for (int j = 0; j < colnum; j++) {
                            cellData = (String) getCellFormatValue(row.getCell(j));
                            map.put(columns[j], cellData);
                        }
                    } else {
                        break;
                    }
                    list.add(map);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
                case Cell.CELL_TYPE_NUMERIC:{
                    // 判断是日期时间类型还是数值类型
                    if (DateUtil.isCellDateFormatted(cell)) {
                        short format = cell.getCellStyle().getDataFormat();
                        SimpleDateFormat sdf = null;
                        /* 所有日期格式都可以通过getDataFormat()值来判断
                         *     yyyy-MM-dd----- 14
                         *    yyyy年m月d日----- 31
                         *    yyyy年m月--------57
                         *    m月d日  --------- 58
                         *    HH:mm---------- 20
                         *    h时mm分  --------- 32
                         */
                        if(format == 14 || format == 31 || format == 57 || format == 58){
                            //日期
                            sdf = new SimpleDateFormat("yyyy-MM-dd");
                        }else if (format == 20 || format == 32) {
                            //时间
                            sdf = new SimpleDateFormat("HH:mm");
                        }
                        cellValue = sdf.format(cell.getDateCellValue());
                    } else {
                        // 对整数进行判断处理
                        double cur = cell.getNumericCellValue();
                        long longVal = Math.round(cur);
                        Object inputValue = null;
                        if(Double.parseDouble(longVal + ".0") == cur) {
                            inputValue = longVal;
                        }
                        else {
                            inputValue = cur;
                        }
                        cellValue = String.valueOf(inputValue);
                    }
                    break;
                }
                case Cell.CELL_TYPE_FORMULA:{
                    //判断cell是否为日期格式
                    if(DateUtil.isCellDateFormatted(cell)){
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    }else{
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:{
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }


}
