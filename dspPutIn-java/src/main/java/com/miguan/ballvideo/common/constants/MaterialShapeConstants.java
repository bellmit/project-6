package com.miguan.ballvideo.common.constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class MaterialShapeConstants {
    public static final Map<Integer,String> materialShapeMap = Maps.newConcurrentMap();
    public static final Map<Integer,String> materialShapeNameMap = Maps.newConcurrentMap();
    public static final List<ShapeNameParam> materialShapeNameList = Lists.newArrayList();
    //新旧对应
    public static final Map<Integer,Integer> oldMaterialTypeMap = Maps.newConcurrentMap();


    //定义常量
    static {
        materialShapeMap.put(1,"9*16");
        materialShapeMap.put(2,"16*9");
        materialShapeMap.put(3,"6*1");
        materialShapeMap.put(4,"1.5*1");
        materialShapeMap.put(5,"1.5*1");
        materialShapeMap.put(6,"9*16");
        materialShapeMap.put(7,"16*9");

        materialShapeNameMap.put(1,"竖版大图9:16");
        materialShapeNameMap.put(2,"横版大图16:9");
        materialShapeNameMap.put(3,"横版长图6:1");
        materialShapeNameMap.put(4,"左图右文1.5:1");
        materialShapeNameMap.put(5,"右图左文1.5:1");
        materialShapeNameMap.put(6,"竖版视频9:16");
        materialShapeNameMap.put(7,"横版视频16:9");

        materialShapeNameList.add(new ShapeNameParam(1,"竖版大图9:16"));
        materialShapeNameList.add(new ShapeNameParam(2,"横版大图16:9"));
        materialShapeNameList.add(new ShapeNameParam(3,"横版长图6:1"));
        materialShapeNameList.add(new ShapeNameParam(4,"左图右文1.5:1"));
        materialShapeNameList.add(new ShapeNameParam(5,"右图左文1.5:1"));
        materialShapeNameList.add(new ShapeNameParam(6,"竖版视频9:16"));
        materialShapeNameList.add(new ShapeNameParam(7,"横版视频16:9"));


        oldMaterialTypeMap.put(1,1);
        oldMaterialTypeMap.put(2,1);
        oldMaterialTypeMap.put(3,3);
        oldMaterialTypeMap.put(4,4);
        oldMaterialTypeMap.put(5,5);
        oldMaterialTypeMap.put(6,2);
        oldMaterialTypeMap.put(7,2);
    }
}
