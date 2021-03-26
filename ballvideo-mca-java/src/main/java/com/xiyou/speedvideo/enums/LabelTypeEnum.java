package com.xiyou.speedvideo.enums;

public enum LabelTypeEnum {
    keyword(200, "keyword", "关键字标签"),
    figure(300, "figure", "人脸标签"),
    scenario(400, "scenario", "场景标签"),
    knowledgeGraph(500, "knowledge_graph", "知识图谱标签");

    Integer type;
    String code;
    String name;

    LabelTypeEnum(int type, String code, String name) {
        this.type = type;
        this.code = code;
        this.name = name;
    }

    public static String getLabelName(String code) {
        for (LabelTypeEnum labelEnum : LabelTypeEnum.values()) {
            if(labelEnum.code.equals(code)) {
                return labelEnum.name;
            }
        }
        return "";
    }

    public static Integer getLabelType(String code) {
        for (LabelTypeEnum labelEnum : LabelTypeEnum.values()) {
            if(labelEnum.code.equals(code)) {
                return labelEnum.type;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
