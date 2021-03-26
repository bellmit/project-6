package com.miguan.report.dataimport;

import com.miguan.report.common.util.AppNameUtil;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.entity.AppCost;
import com.miguan.report.mapper.AppCostMapper;
import com.miguan.report.repository.AppCostRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.transaction.NotSupportedException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**总成本导入
 * @author zhongli
 * @date 2020-06-20 
 *
 */
@Component
public class TotalCostXlsx implements ImportInterface {
    @Resource
    private AppCostRepository appCostRepository;
    @Resource
    private AppCostMapper appCostMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doXlsxImport(XSSFWorkbook workbook) {
        XSSFSheet hssfSheet = workbook.getSheetAt(0);
        List<AppCost> datas = new ArrayList<>();
        //从第二行开始读取，第一行为表头
        /** 时间 应用名称  设备类型  app类型 父渠道 成本*/
        for (int rowNum = 1, h = hssfSheet.getLastRowNum(); rowNum <= h; rowNum++) {
            try {
                XSSFRow xlsxRow = hssfSheet.getRow(rowNum);
                if (xlsxRow == null) {
                    continue;
                }
                AppCost appCost = new AppCost();
                //时间
                XSSFCell dateCell = xlsxRow.getCell(0);
                String dateStr = dateCell.getStringCellValue();
                if (StringUtils.isBlank(dateStr)) {
                    continue;
                }
                dateStr = dateStr.trim();
                boolean b = DateUtil.isYyyy_MM_dd(dateStr);
                if (!b) {
                    throw new NotSupportedException(String.format("第%d行日期格式要为 'yyyy_MM_dd'", rowNum));
                }
                Date date = DateUtil.strToDate(dateStr, "yyyy-MM-dd");
                appCost.setDate(date);
                //应用名称
                XSSFCell nameCell = xlsxRow.getCell(1);
                appCost.setAppName(nameCell.getStringCellValue().trim());
                //设备类型
                XSSFCell deviceCell = xlsxRow.getCell(2);
                String device = deviceCell.getStringCellValue().toLowerCase().trim();
                int deviceType = "android".equals(device) ? 1 : "ios".equals(device) ? 2 : 0;
                if (deviceType == 0) {
                    throw new NotSupportedException(String.format("第%d行无效的设备类型", rowNum));
                }
                appCost.setClientId(deviceType);
                //app类型
                XSSFCell appTypeCell = xlsxRow.getCell(3);
                int appType = Double.valueOf(appTypeCell.getNumericCellValue()).intValue();
                if (appType != 1 && appType != 2) {
                    throw new NotSupportedException(String.format("第%d行无效的app类型", rowNum));
                }
                appCost.setAppType(appType);

                //父渠道
                XSSFCell channelCell = xlsxRow.getCell(4);
                appCost.setChannel(channelCell.getStringCellValue().trim());

                //成本
                XSSFCell costCell = xlsxRow.getCell(5);
                appCost.setCost(costCell.getNumericCellValue());
                int appId = AppNameUtil.getAppIdForName(appCost.getAppName());
                if (appId == -1) {
                    throw new NotSupportedException(String.format("第%d行无效的应用名称", rowNum));
                }
                appCost.setAppId(appId);
                datas.add(appCost);
            } catch (Exception e) {
                throw new RuntimeException(String.format("第%d行数据处理异常: %s", rowNum, e.getMessage()), e);
            }

        }
        //如果已经有的数据要进行删除再添加
        datas.forEach(d -> {
            long count = appCostRepository.countByDateAndAppNameAndAppTypeAndClientIdAndChannel(d.getDate(), d.getAppName(), d.getAppType(), d.getClientId(),d.getChannel());
            if (count > 0) {
                appCostRepository.deleteByDateAndAppNameAndAppTypeAndClientIdAndChannel(d.getDate(), d.getAppName(), d.getAppType(), d.getClientId(),d.getChannel());
            }
        });
        appCostMapper.addAppCost(datas);
    }

    @Override
    public void doCsvImport(InputStreamReader reader) {

    }
}
