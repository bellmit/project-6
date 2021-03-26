package com.miguan.ballvideo.dao.mysql;

import com.github.pagehelper.util.StringUtil;
import com.miguan.ballvideo.common.util.dsp.DspUtils;
import com.miguan.ballvideo.dao.DspAdvUserDao;
import com.miguan.ballvideo.dynamicquery.Dynamic3Query;
import com.miguan.ballvideo.entity.dsp.IdeaAdvertUserVo;
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
public class DspAdvUserDaoImpl implements DspAdvUserDao {

    @Resource
    private Dynamic3Query dynamic3Query;

    public List<Map<String, Object>> getList(String name, String type, String adUserId){
        StringBuffer sqlPutStr = new StringBuffer("SELECT usr.id,usr.name,usr.from,usr.link_man,usr.link_phone,usr.link_email,usr.remake,usr.state,usr.type, usr.id ad_user_id,");
        sqlPutStr.append(" DATE_FORMAT(usr.created_at,'%Y-%m-%d %h:%i:%s') created_at, ");
        sqlPutStr.append(" DATE_FORMAT(usr.updated_at,'%Y-%m-%d %h:%i:%s') updated_at ");
        sqlPutStr.append(" from idea_advert_user usr");
        sqlPutStr.append(" where 1 = 1 ");
        if(StringUtil.isNotEmpty(adUserId)){
            adUserId = DspUtils.sqlStrChg(adUserId);
            sqlPutStr.append(" and usr.id in (").append(adUserId).append(")");
        }
        if(StringUtil.isNotEmpty(type)){
            sqlPutStr.append(" and usr.type = '").append(type).append("'");

        }
        if(StringUtil.isNotEmpty(name)){
            sqlPutStr.append(" and usr.name = '").append(name).append("'");
        }
        sqlPutStr.append(" order by usr.id ");
        List<Map<String,Object>> advList =  dynamic3Query.nativeQueryListMap(sqlPutStr.toString());
        return advList;
    }

    @Override
    public List<Map<String, Object>> findPlanLst(String adUserId) {
        StringBuffer sqlPutStr = new StringBuffer("SELECT id from idea_advert_plan");
        sqlPutStr.append(" where 1 = 1 ");
        adUserId = DspUtils.sqlStrChg(adUserId);
        if(StringUtil.isNotEmpty(adUserId)){
            sqlPutStr.append(" and advert_user_id in (").append(adUserId).append(")");
        }
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
        List<Map<String, Object>>  lst = getList(userVo.getName(),null,null);
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

}
