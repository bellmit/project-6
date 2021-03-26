package com.miguan.advert.domain.serviceimpl;

import com.miguan.advert.common.property.DictionariesProperty;
import com.miguan.advert.domain.mapper.PublicInfoMapper;
import com.miguan.advert.domain.mapper.TableInfoMapper;
import com.miguan.advert.domain.service.DictionariesService;
import com.miguan.advert.domain.vo.PageVo;
import com.miguan.advert.domain.vo.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DictionariesServiceImpl implements DictionariesService {

    @Resource
    private TableInfoMapper tableInfoMapper;

    @Override
    public PageVo getList(Integer page, Integer page_size) {
        int curIndex = (page - 1) * page_size;
        int lastIndex = DictionariesProperty.dictionaries_table_list.size() > (curIndex + page_size) ? curIndex + page_size : DictionariesProperty.dictionaries_table_list.size();

        List<Map<String,Object>> data = new ArrayList();
        for (int i = curIndex; i < lastIndex ; i++) {
            Map<String,Object> resultMap = new HashMap<>();
            DictionariesProperty.DictionariesTable table = DictionariesProperty.dictionaries_table_list.get(i);
            resultMap.put("name",table.getName());
            resultMap.put("table",table.getTable());
            List<Map<String,String>> clonm = new ArrayList<>();
            List<TableInfo> tableInfo = tableInfoMapper.findTableInfo(table.getTable());
            fillClonm(tableInfo,clonm);
            resultMap.put("clonm",clonm);
            data.add(resultMap);
        }

        return new PageVo(page,page_size ,DictionariesProperty.dictionaries_table_list.size(),data);
    }

    private void fillClonm(List<TableInfo> tableInfo, List<Map<String, String>> clonm) {
        for (TableInfo ti:tableInfo) {
            Map<String,String> map = new HashMap<>();
            map.put("china",ti.getColumnComment());
            map.put("english",ti.getColumnName());
            clonm.add(map);
        }
    }
}
