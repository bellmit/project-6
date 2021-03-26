package com.miguan.laidian.service.impl;

import com.github.pagehelper.PageHelper;
import com.miguan.laidian.entity.CommonQuestion;
import com.miguan.laidian.entity.TelBrand;
import com.miguan.laidian.mapper.CommonQuestionMapper;
import com.miguan.laidian.mapper.TelBrandMapper;
import com.miguan.laidian.repositories.CommonQuestionJpaRepository;
import com.miguan.laidian.service.CommonQuestionService;
import com.miguan.laidian.vo.CommonQuestionVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommonQuestionServiceImpl implements CommonQuestionService {

    private static final String ALL = "all";        //默认全部

    private static final String OTHER = "other";    //默认其他

    private static List<String> stringList = new ArrayList<>(); //全部或者其他

    @Resource
    CommonQuestionJpaRepository commonQuestionJpaRepository;

    @Resource
    CommonQuestionMapper commonQuestionMapper;

    @Resource
    TelBrandMapper telBrandMapper;


    @Override
    public List<CommonQuestion> findAllCommonQuestion(CommonQuestion commonQuestion, int currentPage, int pageSize) {
        //telType前端传手机品牌+机型，比如 ViVO,VIVO830(后者包含前者)，或者HUAWEI,Z8(后者不包含前者)
        String[] split = commonQuestion.getTelType().split(",");
        if(split[1].contains(split[0])){
            commonQuestion.setTelType(split[1]);
        }else{
            commonQuestion.setTelType(commonQuestion.getTelType().replaceAll(","," "));
        }
        PageHelper.startPage(currentPage,pageSize);
        List<CommonQuestion> commonQuestionList = commonQuestionMapper.findAllCommonQuestionList(commonQuestion);
        //判断是否为空，如果为空。则根据机型拼接，查询品牌表，
        if (CollectionUtils.isEmpty(commonQuestionList)){
            //直接获取逗号截取的品牌 无须再次截取
            TelBrand telBrand = new TelBrand();
            telBrand.setTelKey(split[0]);
            telBrand = telBrandMapper.selectTelBrandByTelKey(telBrand);
            if(telBrand == null){
                return findAllCommonQuestionALLOther(currentPage, pageSize);
            }
            String s =","+telBrand.getId().toString()+",";
            commonQuestion.setTelBrandId(s);
            commonQuestion.setTelType(null);
            //根据品牌查询
            PageHelper.startPage(currentPage,pageSize);
            commonQuestionList = commonQuestionMapper.findAllCommonQuestionList(commonQuestion);
            //如果没有数据，根绝全部和其他查询
            if (commonQuestionList.size()==0){
                PageHelper.startPage(currentPage,pageSize);
                commonQuestionList = findAllCommonQuestionALLOther(currentPage, pageSize);
            }
        }
        return commonQuestionList;
    }


    public List<CommonQuestion> findAllCommonQuestionALLOther(int currentPage, int pageSize) {
        // 创建静态集合，减少bean的初始化
        if(stringList.size()==0 || stringList==null){
            stringList.add(ALL);
            stringList.add(OTHER);
        }
        //获取全部和其他的  ID
        List<TelBrand> telBrands = telBrandMapper.selectTelBrandByTelKeyList(stringList);
        CommonQuestionVO commonQuestionBean = new CommonQuestionVO();
        StringBuilder builder = new StringBuilder();
        //拼接字符串，
        for (int i = 0;i < telBrands.size();i++){
            builder.append("|").append(","+telBrands.get(i).getId().toString()+",");
        }
        commonQuestionBean.setTelBrandId(builder.toString().substring(1));
        //查询全部或者其他的问题
        PageHelper.startPage(currentPage,pageSize);
        List<CommonQuestion> allByCommonQuestionList = commonQuestionMapper.findAllByCommonQuestionList(commonQuestionBean);
        return allByCommonQuestionList;
    }

    @Override
    public int updateCommonQuestionNumber(CommonQuestionVO commonQuestionVO) {
        return commonQuestionMapper.updateCommonQuestionNumber(commonQuestionVO);
    }
}
