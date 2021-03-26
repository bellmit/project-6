package com.miguan.xuanyuan.common.enums;


/**
 * 素材类型
 *
 */
public enum MaterialShapeEnum {

    SHAPE_ONE("1", "9:16"),
    SHAPE_TWO("2", "16:9"),
    SHAPE_THREE("3", "3:2"),
    SHAPE_FOUR("4", "2:3"),
    SHAPE_FIVE("5", "2:1"),
    SHAPE_SIX("6", "1:1"),
    SHAPE_SEVEN("7", "9:16"),
    SHAPE_EIGHT("8", "16:9")
    ;



    MaterialShapeEnum(String type, String size) {
        this.type = type;
        this.size = size;
    }


    private String type;
    private String size;


    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public static String getValue(String type) {
        if(type == null){
            return "";
        }
        for (MaterialShapeEnum p : MaterialShapeEnum.values()) {
            if (type.equals(p.getType())) {
                return p.getSize();
            }
        }
        return "";
    }
}