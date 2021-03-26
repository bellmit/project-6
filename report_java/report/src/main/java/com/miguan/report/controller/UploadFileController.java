package com.miguan.report.controller;

import com.miguan.report.dataimport.ChuanShanJiaXlsx;
import com.miguan.report.dataimport.GuangDianTongCsv;
import com.miguan.report.dataimport.KuaiShouCsv;
import com.miguan.report.dataimport.TotalCostXlsx;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.NotSupportedException;
import java.io.InputStreamReader;

/**
 * @author zhongli
 * @date 2020-06-20 
 *
 */
@Api(value = "/api/upload", tags = "报表导入")
@RestController
@RequestMapping("/api/upload")
@Slf4j
public class UploadFileController {
    private static final String XLS = ".xls";
    private static final String XLSX = ".xlsx";
    private static final String CSV = ".csv";

    @Autowired
    private TotalCostXlsx totalCostXlsx;
    @Autowired
    private ChuanShanJiaXlsx chuanShanJiaXlsx;
    @Autowired
    private GuangDianTongCsv guangDianTongCsv;
    @Autowired
    private KuaiShouCsv kuaiShouCsv;

    /**
     *
     * @param file
     * @param dataType
     * @throws Exception
     */
    @PostMapping("/xlsx/file")
    @ApiOperation(value = "xlsx类型文件导入")
    public void xlsxFile(@RequestParam("file")
                                 MultipartFile file,
                         @ApiParam(value = "导入的数据类型：1=总成本 2=穿山甲", required = true)
                         @RequestParam("dataType") int dataType) throws Exception {

        // 文件名
        String filename = file.getOriginalFilename();
        // 后缀名
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (XLS.equals(suffix.toLowerCase())) {
            throw new NotSupportedException("目前不支持xls文件类型的数据导入");
        }
        if (!XLSX.equals(suffix.toLowerCase())) {
            throw new NotSupportedException("目前只支持xlsx文件类型的数据导入");
        }
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        switch (dataType) {
            case 1: {
                totalCostXlsx.doXlsxImport(workbook);
                break;
            }
            case 2: {
                chuanShanJiaXlsx.doXlsxImport(workbook);
                break;
            }
            default: {
                log.warn("无法识别要导入的数据类");
            }
        }
    }

    @PostMapping("/csv/file")
    @ApiOperation(value = "csv类型文件导入")
    public void csvFile(@RequestParam("file")
                                MultipartFile file,
                        @ApiParam(value = "导入的数据类型：3=广点通 4=快手", required = true)
                        @RequestParam("dataType") int dataType) throws Exception {

        // 文件名
        String filename = file.getOriginalFilename();
        // 后缀名
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (!CSV.equals(suffix.toLowerCase())) {
            throw new NotSupportedException("目前只支持CSV文件类型的数据导入");
        }
        InputStreamReader reader = new InputStreamReader(file.getInputStream());
        switch (dataType) {
            case 3: {
                guangDianTongCsv.doCsvImport(reader);
                break;
            }
            case 4: {
                kuaiShouCsv.doCsvImport(reader);
                break;
            }
            default: {
                log.warn("无法识别要导入的数据类");
            }
        }
    }
}
