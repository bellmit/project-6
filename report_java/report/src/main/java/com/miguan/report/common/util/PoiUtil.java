package com.miguan.report.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**poi工具
 * @author zhongli
 * @date 2020-06-29 
 *
 */
public final class PoiUtil {

    public static String getStringValue(XSSFRow xlsxRow, int cellNum) {
        XSSFCell dateCell = xlsxRow.getCell(cellNum);
        if (dateCell == null) {
            return null;
        }
        String v = dateCell.getStringCellValue();
        return StringUtils.isBlank(v) ? null : v.trim();
    }

    public static int getIntergeValue(XSSFRow xlsxRow, int cellNum) {
        return Double.valueOf(getNumberValue(xlsxRow, cellNum)).intValue();
    }

    public static double getNumberValue(XSSFRow xlsxRow, int cellNum) {
        XSSFCell dateCell = xlsxRow.getCell(cellNum);
        if (dateCell == null) {
            return 0;
        }
        CellType cellStyle = dateCell.getCellTypeEnum();
        if (cellStyle == CellType.STRING) {
            String vstr = getStringValue(xlsxRow, cellNum);
            return vstr == null ? 0.0 : Double.valueOf(vstr);
        }
        return dateCell.getNumericCellValue();
    }
}
