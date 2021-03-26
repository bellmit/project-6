package com.miguan.ballvideo.common.enums;

import com.miguan.ballvideo.dto.IssueInterestLabelDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum InterestLabelEnum {
    label_1(1,"生活妙招",2,"1012,3",1,1),
    label_2(2,"爆笑视频",2,"2",2,2),
    label_3(3,"健康养生",2,"1006",3,3),
    label_4(4,"民生热点",2,"194",4,4),
    label_5(5,"美食推荐",2,"242",5,5),
    label_6(6,"美女街拍",2,"196",0,6),
    label_7(7,"奇闻轶事",2,"1",6,7),
    label_8(8,"时尚车模",2,"196",0,8),
    label_9(9,"健身运动",2,"54,1011",7,9),
    label_10(10,"影视精选",2,"4",8,10),
    label_11(11,"旅游攻略",2,"48",9,11),
    label_12(12,"娱乐八卦",2,"197,1016",10,12),
    label_13(13,"经典老歌",2,"1038",11,0),
    label_14(14,"家庭情感",2,"1010",12,0);

    InterestLabelEnum(Integer labelId,String labelName,Integer examType,String catId,Integer sortA,Integer sortB){
        this.labelId = labelId;
        this.labelName = labelName;
        this.examType = examType;
        this.catId = catId;
        this.sortA = sortA;
        this.sortB = sortB;
    }

    private Integer labelId;
    private String labelName;
    private Integer examType;
    private String catId;
    private Integer sortA;
    private Integer sortB;

    public static int getId(String label){
        for (InterestLabelEnum s : InterestLabelEnum.values()){
            if(label.equals(s.labelName)){
                return s.labelId;
            }
        }
        return 0;
    }

    public static String getName(Integer id){
        for (InterestLabelEnum s : InterestLabelEnum.values()){
            if(id.equals(s.labelId)){
                return s.labelName;
            }
        }
        return "未设定标签";
    }

    public static String getCatId(Integer id){
        for (InterestLabelEnum s : InterestLabelEnum.values()){
            if(id.equals(s.labelId)){
                return s.catId;
            }
        }
        return "-1";
    }

    public static List<IssueInterestLabelDto> exportList(Integer experimentType){
        List<IssueInterestLabelDto> list = new ArrayList<IssueInterestLabelDto>();
        for (InterestLabelEnum s : InterestLabelEnum.values()){
            if(experimentType == 2 && s.sortA != 0){
                IssueInterestLabelDto dto = new IssueInterestLabelDto();
                dto.setLabelId(s.labelId+"");
                dto.setLabelName(s.labelName);
                dto.setSort(s.sortA);
                list.add(dto);
            }
            if(experimentType == 3 && s.sortB != 0){
                IssueInterestLabelDto dto = new IssueInterestLabelDto();
                dto.setLabelId(s.labelId+"");
                dto.setLabelName(s.labelName);
                dto.setSort(s.sortB);
                list.add(dto);
            }
        }
        List<IssueInterestLabelDto> listBySort = list.stream().sorted(Comparator.comparing(IssueInterestLabelDto::getSort)).collect(Collectors.toList());
        return listBySort;
    }
}
