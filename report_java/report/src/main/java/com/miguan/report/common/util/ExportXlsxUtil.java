package com.miguan.report.common.util;

import lombok.extern.slf4j.Slf4j;
import org.jxls.common.Context;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Map;

/**报表导出工具
 * @author zhongli
 * @date 2020-06-20 
 *
 */
@Slf4j
public class ExportXlsxUtil {

    /**
     *
     * @param response
     * @param resourceLoader
     * @param templePath 导出模板路径，模板需要存放在classpath:目录下
     * @param fileName   导出文件名称
     * @param exportData 导出数据
     * @param varMap     导出扩展数据
     */
    public static void export(HttpServletResponse response,
                              ResourceLoader resourceLoader,
                              String templePath,
                              String fileName,
                              Object exportData,
                              Map<String, Object> varMap) {

        Resource resource = resourceLoader.getResource("classpath:" + templePath);

        try (InputStream is = resource.getInputStream()) {
            Context context = CollectionUtils.isEmpty(varMap) ? new Context() : new Context(varMap);
            context.putVar("items", exportData);
            response.setHeader("Content-type", "application/vnd.ms-excel");
            // 解决导出文件名中文乱码
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            JxlsHelper jxlsHelper = JxlsHelper.getInstance();
            Transformer transformer = jxlsHelper.createTransformer(is, response.getOutputStream());
            JxlsHelper.getInstance().processTemplate(context, transformer);
        } catch (Exception e) {
            log.error("导出<{}>数据异常", fileName, e);
        }
    }
}
