package com.miguan.advert.common.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictionariesProperty {
    public static List<DictionariesTable> dictionaries_table_list = new ArrayList<>();

    static {
        dictionaries_table_list.add(new DictionariesTable("广告位管理","ad_advert_position"));
        dictionaries_table_list.add(new DictionariesTable("代码位管理","ad_advert_code"));
        dictionaries_table_list.add(new DictionariesTable("广告配置管理","ad_advert_config"));
        dictionaries_table_list.add(new DictionariesTable("代码位报表","ad_advert_code_report"));
    }


    public static class DictionariesTable{
        private String name;
        private String table;

        public DictionariesTable(String name, String table) {
            this.name = name;
            this.table = table;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }
    }
}
