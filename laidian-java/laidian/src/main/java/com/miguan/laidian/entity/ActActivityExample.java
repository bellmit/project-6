package com.miguan.laidian.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActActivityExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ActActivityExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNull() {
            addCriterion("start_time is null");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNotNull() {
            addCriterion("start_time is not null");
            return (Criteria) this;
        }

        public Criteria andStartTimeEqualTo(Date value) {
            addCriterion("start_time =", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotEqualTo(Date value) {
            addCriterion("start_time <>", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThan(Date value) {
            addCriterion("start_time >", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("start_time >=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThan(Date value) {
            addCriterion("start_time <", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThanOrEqualTo(Date value) {
            addCriterion("start_time <=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeIn(List<Date> values) {
            addCriterion("start_time in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotIn(List<Date> values) {
            addCriterion("start_time not in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeBetween(Date value1, Date value2) {
            addCriterion("start_time between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotBetween(Date value1, Date value2) {
            addCriterion("start_time not between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNull() {
            addCriterion("end_time is null");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNotNull() {
            addCriterion("end_time is not null");
            return (Criteria) this;
        }

        public Criteria andEndTimeEqualTo(Date value) {
            addCriterion("end_time =", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotEqualTo(Date value) {
            addCriterion("end_time <>", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThan(Date value) {
            addCriterion("end_time >", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("end_time >=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThan(Date value) {
            addCriterion("end_time <", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("end_time <=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIn(List<Date> values) {
            addCriterion("end_time in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotIn(List<Date> values) {
            addCriterion("end_time not in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeBetween(Date value1, Date value2) {
            addCriterion("end_time between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("end_time not between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagIsNull() {
            addCriterion("pop_up_flag is null");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagIsNotNull() {
            addCriterion("pop_up_flag is not null");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagEqualTo(Integer value) {
            addCriterion("pop_up_flag =", value, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagNotEqualTo(Integer value) {
            addCriterion("pop_up_flag <>", value, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagGreaterThan(Integer value) {
            addCriterion("pop_up_flag >", value, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("pop_up_flag >=", value, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagLessThan(Integer value) {
            addCriterion("pop_up_flag <", value, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagLessThanOrEqualTo(Integer value) {
            addCriterion("pop_up_flag <=", value, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagIn(List<Integer> values) {
            addCriterion("pop_up_flag in", values, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagNotIn(List<Integer> values) {
            addCriterion("pop_up_flag not in", values, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagBetween(Integer value1, Integer value2) {
            addCriterion("pop_up_flag between", value1, value2, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andPopUpFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("pop_up_flag not between", value1, value2, "popUpFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagIsNull() {
            addCriterion("floating_window_flag is null");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagIsNotNull() {
            addCriterion("floating_window_flag is not null");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagEqualTo(Integer value) {
            addCriterion("floating_window_flag =", value, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagNotEqualTo(Integer value) {
            addCriterion("floating_window_flag <>", value, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagGreaterThan(Integer value) {
            addCriterion("floating_window_flag >", value, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("floating_window_flag >=", value, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagLessThan(Integer value) {
            addCriterion("floating_window_flag <", value, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagLessThanOrEqualTo(Integer value) {
            addCriterion("floating_window_flag <=", value, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagIn(List<Integer> values) {
            addCriterion("floating_window_flag in", values, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagNotIn(List<Integer> values) {
            addCriterion("floating_window_flag not in", values, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagBetween(Integer value1, Integer value2) {
            addCriterion("floating_window_flag between", value1, value2, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andFloatingWindowFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("floating_window_flag not between", value1, value2, "floatingWindowFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagIsNull() {
            addCriterion("banner_flag is null");
            return (Criteria) this;
        }

        public Criteria andBannerFlagIsNotNull() {
            addCriterion("banner_flag is not null");
            return (Criteria) this;
        }

        public Criteria andBannerFlagEqualTo(Integer value) {
            addCriterion("banner_flag =", value, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagNotEqualTo(Integer value) {
            addCriterion("banner_flag <>", value, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagGreaterThan(Integer value) {
            addCriterion("banner_flag >", value, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("banner_flag >=", value, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagLessThan(Integer value) {
            addCriterion("banner_flag <", value, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagLessThanOrEqualTo(Integer value) {
            addCriterion("banner_flag <=", value, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagIn(List<Integer> values) {
            addCriterion("banner_flag in", values, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagNotIn(List<Integer> values) {
            addCriterion("banner_flag not in", values, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagBetween(Integer value1, Integer value2) {
            addCriterion("banner_flag between", value1, value2, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andBannerFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("banner_flag not between", value1, value2, "bannerFlag");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumIsNull() {
            addCriterion("day_draw_num is null");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumIsNotNull() {
            addCriterion("day_draw_num is not null");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumEqualTo(Integer value) {
            addCriterion("day_draw_num =", value, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumNotEqualTo(Integer value) {
            addCriterion("day_draw_num <>", value, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumGreaterThan(Integer value) {
            addCriterion("day_draw_num >", value, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("day_draw_num >=", value, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumLessThan(Integer value) {
            addCriterion("day_draw_num <", value, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumLessThanOrEqualTo(Integer value) {
            addCriterion("day_draw_num <=", value, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumIn(List<Integer> values) {
            addCriterion("day_draw_num in", values, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumNotIn(List<Integer> values) {
            addCriterion("day_draw_num not in", values, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumBetween(Integer value1, Integer value2) {
            addCriterion("day_draw_num between", value1, value2, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andDayDrawNumNotBetween(Integer value1, Integer value2) {
            addCriterion("day_draw_num not between", value1, value2, "dayDrawNum");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagIsNull() {
            addCriterion("ldx_task_flag is null");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagIsNotNull() {
            addCriterion("ldx_task_flag is not null");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagEqualTo(Integer value) {
            addCriterion("ldx_task_flag =", value, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagNotEqualTo(Integer value) {
            addCriterion("ldx_task_flag <>", value, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagGreaterThan(Integer value) {
            addCriterion("ldx_task_flag >", value, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("ldx_task_flag >=", value, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagLessThan(Integer value) {
            addCriterion("ldx_task_flag <", value, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagLessThanOrEqualTo(Integer value) {
            addCriterion("ldx_task_flag <=", value, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagIn(List<Integer> values) {
            addCriterion("ldx_task_flag in", values, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagNotIn(List<Integer> values) {
            addCriterion("ldx_task_flag not in", values, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagBetween(Integer value1, Integer value2) {
            addCriterion("ldx_task_flag between", value1, value2, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLdxTaskFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("ldx_task_flag not between", value1, value2, "ldxTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagIsNull() {
            addCriterion("ls_task_flag is null");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagIsNotNull() {
            addCriterion("ls_task_flag is not null");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagEqualTo(Integer value) {
            addCriterion("ls_task_flag =", value, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagNotEqualTo(Integer value) {
            addCriterion("ls_task_flag <>", value, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagGreaterThan(Integer value) {
            addCriterion("ls_task_flag >", value, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("ls_task_flag >=", value, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagLessThan(Integer value) {
            addCriterion("ls_task_flag <", value, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagLessThanOrEqualTo(Integer value) {
            addCriterion("ls_task_flag <=", value, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagIn(List<Integer> values) {
            addCriterion("ls_task_flag in", values, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagNotIn(List<Integer> values) {
            addCriterion("ls_task_flag not in", values, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagBetween(Integer value1, Integer value2) {
            addCriterion("ls_task_flag between", value1, value2, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLsTaskFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("ls_task_flag not between", value1, value2, "lsTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagIsNull() {
            addCriterion("video_task_flag is null");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagIsNotNull() {
            addCriterion("video_task_flag is not null");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagEqualTo(Integer value) {
            addCriterion("video_task_flag =", value, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagNotEqualTo(Integer value) {
            addCriterion("video_task_flag <>", value, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagGreaterThan(Integer value) {
            addCriterion("video_task_flag >", value, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("video_task_flag >=", value, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagLessThan(Integer value) {
            addCriterion("video_task_flag <", value, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagLessThanOrEqualTo(Integer value) {
            addCriterion("video_task_flag <=", value, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagIn(List<Integer> values) {
            addCriterion("video_task_flag in", values, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagNotIn(List<Integer> values) {
            addCriterion("video_task_flag not in", values, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagBetween(Integer value1, Integer value2) {
            addCriterion("video_task_flag between", value1, value2, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andVideoTaskFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("video_task_flag not between", value1, value2, "videoTaskFlag");
            return (Criteria) this;
        }

        public Criteria andLinkUrlIsNull() {
            addCriterion("link_url is null");
            return (Criteria) this;
        }

        public Criteria andLinkUrlIsNotNull() {
            addCriterion("link_url is not null");
            return (Criteria) this;
        }

        public Criteria andLinkUrlEqualTo(String value) {
            addCriterion("link_url =", value, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlNotEqualTo(String value) {
            addCriterion("link_url <>", value, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlGreaterThan(String value) {
            addCriterion("link_url >", value, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlGreaterThanOrEqualTo(String value) {
            addCriterion("link_url >=", value, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlLessThan(String value) {
            addCriterion("link_url <", value, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlLessThanOrEqualTo(String value) {
            addCriterion("link_url <=", value, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlLike(String value) {
            addCriterion("link_url like", value, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlNotLike(String value) {
            addCriterion("link_url not like", value, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlIn(List<String> values) {
            addCriterion("link_url in", values, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlNotIn(List<String> values) {
            addCriterion("link_url not in", values, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlBetween(String value1, String value2) {
            addCriterion("link_url between", value1, value2, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andLinkUrlNotBetween(String value1, String value2) {
            addCriterion("link_url not between", value1, value2, "linkUrl");
            return (Criteria) this;
        }

        public Criteria andStateIsNull() {
            addCriterion("state is null");
            return (Criteria) this;
        }

        public Criteria andStateIsNotNull() {
            addCriterion("state is not null");
            return (Criteria) this;
        }

        public Criteria andStateEqualTo(Integer value) {
            addCriterion("state =", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotEqualTo(Integer value) {
            addCriterion("state <>", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThan(Integer value) {
            addCriterion("state >", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThanOrEqualTo(Integer value) {
            addCriterion("state >=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThan(Integer value) {
            addCriterion("state <", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThanOrEqualTo(Integer value) {
            addCriterion("state <=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateIn(List<Integer> values) {
            addCriterion("state in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotIn(List<Integer> values) {
            addCriterion("state not in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateBetween(Integer value1, Integer value2) {
            addCriterion("state between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotBetween(Integer value1, Integer value2) {
            addCriterion("state not between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andDrawNumIsNull() {
            addCriterion("draw_num is null");
            return (Criteria) this;
        }

        public Criteria andDrawNumIsNotNull() {
            addCriterion("draw_num is not null");
            return (Criteria) this;
        }

        public Criteria andDrawNumEqualTo(Integer value) {
            addCriterion("draw_num =", value, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumNotEqualTo(Integer value) {
            addCriterion("draw_num <>", value, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumGreaterThan(Integer value) {
            addCriterion("draw_num >", value, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("draw_num >=", value, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumLessThan(Integer value) {
            addCriterion("draw_num <", value, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumLessThanOrEqualTo(Integer value) {
            addCriterion("draw_num <=", value, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumIn(List<Integer> values) {
            addCriterion("draw_num in", values, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumNotIn(List<Integer> values) {
            addCriterion("draw_num not in", values, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumBetween(Integer value1, Integer value2) {
            addCriterion("draw_num between", value1, value2, "drawNum");
            return (Criteria) this;
        }

        public Criteria andDrawNumNotBetween(Integer value1, Integer value2) {
            addCriterion("draw_num not between", value1, value2, "drawNum");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("created_at is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("created_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(Date value) {
            addCriterion("created_at =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(Date value) {
            addCriterion("created_at <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(Date value) {
            addCriterion("created_at >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("created_at >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(Date value) {
            addCriterion("created_at <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(Date value) {
            addCriterion("created_at <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<Date> values) {
            addCriterion("created_at in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<Date> values) {
            addCriterion("created_at not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(Date value1, Date value2) {
            addCriterion("created_at between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(Date value1, Date value2) {
            addCriterion("created_at not between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNull() {
            addCriterion("updated_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(Date value) {
            addCriterion("updated_at =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(Date value) {
            addCriterion("updated_at <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(Date value) {
            addCriterion("updated_at >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("updated_at >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(Date value) {
            addCriterion("updated_at <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(Date value) {
            addCriterion("updated_at <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<Date> values) {
            addCriterion("updated_at in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<Date> values) {
            addCriterion("updated_at not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(Date value1, Date value2) {
            addCriterion("updated_at between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(Date value1, Date value2) {
            addCriterion("updated_at not between", value1, value2, "updatedAt");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}