package com.miguan.advert.domain.mapper;

import com.miguan.advert.domain.vo.interactive.AdTestCodeSortVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AdAutoSortMapper {

    /**
     * 查询出需要自动排序的代码位列表
     * @return
     */
    List<AdTestCodeSortVo> listAdAutoSort();

    /**
     * 批量更新默认分组中代码位的顺序
     * @param list
     */
    void batchUpdateOrderNum(@Param("list") List<AdTestCodeSortVo> list);

    /**
     * 判断代码位当前在配置中还是否存在
     * @param params
     * @return 0--不存在，大于1--存在
     */
    Integer ifExistAdId(Map<String, Object> params);

    /**
     * 修改同一个而配置下的其他代码位的排序
     * @param params
     */
    void updateOtherOrderNum(Map<String, Object> params);

    /**
     * 修改代码位的排序
     * @param params
     */
    void updateOrderNum(Map<String, Object> params);
}
