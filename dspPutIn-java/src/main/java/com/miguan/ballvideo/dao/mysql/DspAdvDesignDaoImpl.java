package com.miguan.ballvideo.dao.mysql;

import com.github.pagehelper.util.StringUtil;
import com.miguan.ballvideo.common.util.dsp.DspUtils;
import com.miguan.ballvideo.dao.DspAdvDesignDao;
import com.miguan.ballvideo.dao.DspAdvUserDao;
import com.miguan.ballvideo.dynamicquery.Dynamic3Query;
import com.miguan.ballvideo.entity.dsp.IdeaAdvertUserVo;
import com.miguan.ballvideo.service.dsp.DspAdvDesignService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @program: dspPutIn-java
 * @description: 广告主列表DAO
 * @author: suhj
 * @create: 2020-09-22 20:06
 **/
@Repository
public class DspAdvDesignDaoImpl implements DspAdvDesignDao {

    @Resource
    private Dynamic3Query dynamic3Query;

    public List<Map<String, Object>> getLst(String advertDesignId, String adUserId){
        StringBuffer sqlPutStr = new StringBuffer("select des.id,des.name,des.advert_user_id,des.material_type,");
        sqlPutStr.append(" DATE_FORMAT(des.created_at,'%Y-%m-%d %h:%i:%s') created_at, ");
        sqlPutStr.append(" DATE_FORMAT(des.updated_at,'%Y-%m-%d %h:%i:%s') updated_at, ");
        sqlPutStr.append(" des.state,des.copy,des.button_copy,des.logo_url,des.put_in_method, ");
        sqlPutStr.append(" des.put_in_value,des.position_type,des.id advert_design_id,");
        sqlPutStr.append(" usr.name advert_user_name,tp.name advert_type_name");
        sqlPutStr.append(" from idea_advert_design des");
        sqlPutStr.append(" LEFT JOIN idea_advert_user usr on usr.id = des.advert_user_id");
        sqlPutStr.append(" LEFT JOIN idea_advert_type tp on tp.key = des.position_type");
        sqlPutStr.append(" where 1 = 1 ");
        if(StringUtil.isNotEmpty(advertDesignId)){
            sqlPutStr.append(" and des.id = '").append(advertDesignId).append("'");
        }
        if(StringUtil.isNotEmpty(adUserId)){
            sqlPutStr.append(" and des.advert_user_id = '").append(adUserId).append("'");

        }
        sqlPutStr.append(" order by des.id desc ");
        List<Map<String,Object>> advList =  dynamic3Query.nativeQueryListMap(sqlPutStr.toString());
        return advList;
    }

    @Override
    public String save(IdeaAdvertUserVo userVo) {
        String insertSql = "insert into idea_advert_user (name, link_man, link_phone, link_email, remake, created_at, type) VALUES (?,?,?,?,?,NOW(),?)";
        dynamic3Query.nativeExecuteUpdate(insertSql,
                new Object[]{
                        userVo.getName(),userVo.getLink_man(),userVo.getLink_phone(),
                        userVo.getLink_email(),userVo.getRemake(), userVo.getType()
                });
        List<Map<String, Object>>  lst = null;//getLst(userVo.getName(),null,null);
        return "" + lst.get(0).get("id");
    }

    @Override
    public void updateById(IdeaAdvertUserVo userVo) {

        String updateSql = "update idea_advert_user " +
                "set name = ?, link_man = ?, link_phone = ?, link_email = ?, remake = ?, updated_at = NOW(), type = ? where id = ?";
        dynamic3Query.nativeExecuteUpdate(updateSql,
                new Object[]{
                        userVo.getName(),userVo.getLink_man(),userVo.getLink_phone(),
                        userVo.getLink_email(),userVo.getRemake(), userVo.getType(),
                        userVo.getAd_user_id()
                });

    }

    @Override
    public List<Map<String, Object>> findPositionLst(String designId) {
        StringBuffer sqlPutStr = new StringBuffer();
        sqlPutStr.append(" select  DISTINCT concat(app.name,'-',posl.position_name) position_name");
        sqlPutStr.append(" from idea_code_design_relation derl");
        sqlPutStr.append(" LEFT JOIN idea_advert_code cd on cd.id = derl.code_id");
        sqlPutStr.append(" LEFT JOIN idea_adv_position_relation posl on cd.rela_id = posl.id");
        sqlPutStr.append(" LEFT JOIN idea_advert_app app on cd.app_id = app.id");
        sqlPutStr.append(" where 1 = 1 ");
        if(StringUtil.isNotEmpty(designId)){
            sqlPutStr.append(" and advert_user_id = '").append(designId).append("'");
        }
        List<Map<String,Object>> advList =  dynamic3Query.nativeQueryListMap(sqlPutStr.toString());
        return advList;
    }

}
