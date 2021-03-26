package com.miguan.laidian.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActActivityConfigExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ActActivityConfigExample() {
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

        public Criteria andActivityIdIsNull() {
            addCriterion("activity_id is null");
            return (Criteria) this;
        }

        public Criteria andActivityIdIsNotNull() {
            addCriterion("activity_id is not null");
            return (Criteria) this;
        }

        public Criteria andActivityIdEqualTo(Long value) {
            addCriterion("activity_id =", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdNotEqualTo(Long value) {
            addCriterion("activity_id <>", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdGreaterThan(Long value) {
            addCriterion("activity_id >", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdGreaterThanOrEqualTo(Long value) {
            addCriterion("activity_id >=", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdLessThan(Long value) {
            addCriterion("activity_id <", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdLessThanOrEqualTo(Long value) {
            addCriterion("activity_id <=", value, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdIn(List<Long> values) {
            addCriterion("activity_id in", values, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdNotIn(List<Long> values) {
            addCriterion("activity_id not in", values, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdBetween(Long value1, Long value2) {
            addCriterion("activity_id between", value1, value2, "activityId");
            return (Criteria) this;
        }

        public Criteria andActivityIdNotBetween(Long value1, Long value2) {
            addCriterion("activity_id not between", value1, value2, "activityId");
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

        public Criteria andPriceIsNull() {
            addCriterion("price is null");
            return (Criteria) this;
        }

        public Criteria andPriceIsNotNull() {
            addCriterion("price is not null");
            return (Criteria) this;
        }

        public Criteria andPriceEqualTo(BigDecimal value) {
            addCriterion("price =", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotEqualTo(BigDecimal value) {
            addCriterion("price <>", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceGreaterThan(BigDecimal value) {
            addCriterion("price >", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("price >=", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceLessThan(BigDecimal value) {
            addCriterion("price <", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("price <=", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceIn(List<BigDecimal> values) {
            addCriterion("price in", values, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotIn(List<BigDecimal> values) {
            addCriterion("price not in", values, "price");
            return (Criteria) this;
        }

        public Criteria andPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("price between", value1, value2, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("price not between", value1, value2, "price");
            return (Criteria) this;
        }

        public Criteria andCostPriceIsNull() {
            addCriterion("cost_price is null");
            return (Criteria) this;
        }

        public Criteria andCostPriceIsNotNull() {
            addCriterion("cost_price is not null");
            return (Criteria) this;
        }

        public Criteria andCostPriceEqualTo(BigDecimal value) {
            addCriterion("cost_price =", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceNotEqualTo(BigDecimal value) {
            addCriterion("cost_price <>", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceGreaterThan(BigDecimal value) {
            addCriterion("cost_price >", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("cost_price >=", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceLessThan(BigDecimal value) {
            addCriterion("cost_price <", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("cost_price <=", value, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceIn(List<BigDecimal> values) {
            addCriterion("cost_price in", values, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceNotIn(List<BigDecimal> values) {
            addCriterion("cost_price not in", values, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("cost_price between", value1, value2, "costPrice");
            return (Criteria) this;
        }

        public Criteria andCostPriceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("cost_price not between", value1, value2, "costPrice");
            return (Criteria) this;
        }

        public Criteria andPicIsNull() {
            addCriterion("pic is null");
            return (Criteria) this;
        }

        public Criteria andPicIsNotNull() {
            addCriterion("pic is not null");
            return (Criteria) this;
        }

        public Criteria andPicEqualTo(String value) {
            addCriterion("pic =", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicNotEqualTo(String value) {
            addCriterion("pic <>", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicGreaterThan(String value) {
            addCriterion("pic >", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicGreaterThanOrEqualTo(String value) {
            addCriterion("pic >=", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicLessThan(String value) {
            addCriterion("pic <", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicLessThanOrEqualTo(String value) {
            addCriterion("pic <=", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicLike(String value) {
            addCriterion("pic like", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicNotLike(String value) {
            addCriterion("pic not like", value, "pic");
            return (Criteria) this;
        }

        public Criteria andPicIn(List<String> values) {
            addCriterion("pic in", values, "pic");
            return (Criteria) this;
        }

        public Criteria andPicNotIn(List<String> values) {
            addCriterion("pic not in", values, "pic");
            return (Criteria) this;
        }

        public Criteria andPicBetween(String value1, String value2) {
            addCriterion("pic between", value1, value2, "pic");
            return (Criteria) this;
        }

        public Criteria andPicNotBetween(String value1, String value2) {
            addCriterion("pic not between", value1, value2, "pic");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumIsNull() {
            addCriterion("prize_virtual_num is null");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumIsNotNull() {
            addCriterion("prize_virtual_num is not null");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumEqualTo(Integer value) {
            addCriterion("prize_virtual_num =", value, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumNotEqualTo(Integer value) {
            addCriterion("prize_virtual_num <>", value, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumGreaterThan(Integer value) {
            addCriterion("prize_virtual_num >", value, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("prize_virtual_num >=", value, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumLessThan(Integer value) {
            addCriterion("prize_virtual_num <", value, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumLessThanOrEqualTo(Integer value) {
            addCriterion("prize_virtual_num <=", value, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumIn(List<Integer> values) {
            addCriterion("prize_virtual_num in", values, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumNotIn(List<Integer> values) {
            addCriterion("prize_virtual_num not in", values, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumBetween(Integer value1, Integer value2) {
            addCriterion("prize_virtual_num between", value1, value2, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeVirtualNumNotBetween(Integer value1, Integer value2) {
            addCriterion("prize_virtual_num not between", value1, value2, "prizeVirtualNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumIsNull() {
            addCriterion("prize_real_num is null");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumIsNotNull() {
            addCriterion("prize_real_num is not null");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumEqualTo(Integer value) {
            addCriterion("prize_real_num =", value, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumNotEqualTo(Integer value) {
            addCriterion("prize_real_num <>", value, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumGreaterThan(Integer value) {
            addCriterion("prize_real_num >", value, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("prize_real_num >=", value, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumLessThan(Integer value) {
            addCriterion("prize_real_num <", value, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumLessThanOrEqualTo(Integer value) {
            addCriterion("prize_real_num <=", value, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumIn(List<Integer> values) {
            addCriterion("prize_real_num in", values, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumNotIn(List<Integer> values) {
            addCriterion("prize_real_num not in", values, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumBetween(Integer value1, Integer value2) {
            addCriterion("prize_real_num between", value1, value2, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeRealNumNotBetween(Integer value1, Integer value2) {
            addCriterion("prize_real_num not between", value1, value2, "prizeRealNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumIsNull() {
            addCriterion("prize_recive_num is null");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumIsNotNull() {
            addCriterion("prize_recive_num is not null");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumEqualTo(Integer value) {
            addCriterion("prize_recive_num =", value, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumNotEqualTo(Integer value) {
            addCriterion("prize_recive_num <>", value, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumGreaterThan(Integer value) {
            addCriterion("prize_recive_num >", value, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("prize_recive_num >=", value, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumLessThan(Integer value) {
            addCriterion("prize_recive_num <", value, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumLessThanOrEqualTo(Integer value) {
            addCriterion("prize_recive_num <=", value, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumIn(List<Integer> values) {
            addCriterion("prize_recive_num in", values, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumNotIn(List<Integer> values) {
            addCriterion("prize_recive_num not in", values, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumBetween(Integer value1, Integer value2) {
            addCriterion("prize_recive_num between", value1, value2, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andPrizeReciveNumNotBetween(Integer value1, Integer value2) {
            addCriterion("prize_recive_num not between", value1, value2, "prizeReciveNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumIsNull() {
            addCriterion("debris_reach_num is null");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumIsNotNull() {
            addCriterion("debris_reach_num is not null");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumEqualTo(Integer value) {
            addCriterion("debris_reach_num =", value, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumNotEqualTo(Integer value) {
            addCriterion("debris_reach_num <>", value, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumGreaterThan(Integer value) {
            addCriterion("debris_reach_num >", value, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("debris_reach_num >=", value, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumLessThan(Integer value) {
            addCriterion("debris_reach_num <", value, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumLessThanOrEqualTo(Integer value) {
            addCriterion("debris_reach_num <=", value, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumIn(List<Integer> values) {
            addCriterion("debris_reach_num in", values, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumNotIn(List<Integer> values) {
            addCriterion("debris_reach_num not in", values, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumBetween(Integer value1, Integer value2) {
            addCriterion("debris_reach_num between", value1, value2, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisReachNumNotBetween(Integer value1, Integer value2) {
            addCriterion("debris_reach_num not between", value1, value2, "debrisReachNum");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateIsNull() {
            addCriterion("debris_draw_rate is null");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateIsNotNull() {
            addCriterion("debris_draw_rate is not null");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateEqualTo(Integer value) {
            addCriterion("debris_draw_rate =", value, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateNotEqualTo(Integer value) {
            addCriterion("debris_draw_rate <>", value, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateGreaterThan(Integer value) {
            addCriterion("debris_draw_rate >", value, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateGreaterThanOrEqualTo(Integer value) {
            addCriterion("debris_draw_rate >=", value, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateLessThan(Integer value) {
            addCriterion("debris_draw_rate <", value, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateLessThanOrEqualTo(Integer value) {
            addCriterion("debris_draw_rate <=", value, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateIn(List<Integer> values) {
            addCriterion("debris_draw_rate in", values, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateNotIn(List<Integer> values) {
            addCriterion("debris_draw_rate not in", values, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateBetween(Integer value1, Integer value2) {
            addCriterion("debris_draw_rate between", value1, value2, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andDebrisDrawRateNotBetween(Integer value1, Integer value2) {
            addCriterion("debris_draw_rate not between", value1, value2, "debrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateIsNull() {
            addCriterion("last_debris_draw_rate is null");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateIsNotNull() {
            addCriterion("last_debris_draw_rate is not null");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateEqualTo(Integer value) {
            addCriterion("last_debris_draw_rate =", value, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateNotEqualTo(Integer value) {
            addCriterion("last_debris_draw_rate <>", value, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateGreaterThan(Integer value) {
            addCriterion("last_debris_draw_rate >", value, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateGreaterThanOrEqualTo(Integer value) {
            addCriterion("last_debris_draw_rate >=", value, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateLessThan(Integer value) {
            addCriterion("last_debris_draw_rate <", value, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateLessThanOrEqualTo(Integer value) {
            addCriterion("last_debris_draw_rate <=", value, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateIn(List<Integer> values) {
            addCriterion("last_debris_draw_rate in", values, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateNotIn(List<Integer> values) {
            addCriterion("last_debris_draw_rate not in", values, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateBetween(Integer value1, Integer value2) {
            addCriterion("last_debris_draw_rate between", value1, value2, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andLastDebrisDrawRateNotBetween(Integer value1, Integer value2) {
            addCriterion("last_debris_draw_rate not between", value1, value2, "lastDebrisDrawRate");
            return (Criteria) this;
        }

        public Criteria andSortIsNull() {
            addCriterion("sort is null");
            return (Criteria) this;
        }

        public Criteria andSortIsNotNull() {
            addCriterion("sort is not null");
            return (Criteria) this;
        }

        public Criteria andSortEqualTo(Integer value) {
            addCriterion("sort =", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotEqualTo(Integer value) {
            addCriterion("sort <>", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortGreaterThan(Integer value) {
            addCriterion("sort >", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortGreaterThanOrEqualTo(Integer value) {
            addCriterion("sort >=", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortLessThan(Integer value) {
            addCriterion("sort <", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortLessThanOrEqualTo(Integer value) {
            addCriterion("sort <=", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortIn(List<Integer> values) {
            addCriterion("sort in", values, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotIn(List<Integer> values) {
            addCriterion("sort not in", values, "sort");
            return (Criteria) this;
        }

        public Criteria andSortBetween(Integer value1, Integer value2) {
            addCriterion("sort between", value1, value2, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotBetween(Integer value1, Integer value2) {
            addCriterion("sort not between", value1, value2, "sort");
            return (Criteria) this;
        }

        public Criteria andRotarySortIsNull() {
            addCriterion("rotary_sort is null");
            return (Criteria) this;
        }

        public Criteria andRotarySortIsNotNull() {
            addCriterion("rotary_sort is not null");
            return (Criteria) this;
        }

        public Criteria andRotarySortEqualTo(Integer value) {
            addCriterion("rotary_sort =", value, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortNotEqualTo(Integer value) {
            addCriterion("rotary_sort <>", value, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortGreaterThan(Integer value) {
            addCriterion("rotary_sort >", value, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortGreaterThanOrEqualTo(Integer value) {
            addCriterion("rotary_sort >=", value, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortLessThan(Integer value) {
            addCriterion("rotary_sort <", value, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortLessThanOrEqualTo(Integer value) {
            addCriterion("rotary_sort <=", value, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortIn(List<Integer> values) {
            addCriterion("rotary_sort in", values, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortNotIn(List<Integer> values) {
            addCriterion("rotary_sort not in", values, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortBetween(Integer value1, Integer value2) {
            addCriterion("rotary_sort between", value1, value2, "rotarySort");
            return (Criteria) this;
        }

        public Criteria andRotarySortNotBetween(Integer value1, Integer value2) {
            addCriterion("rotary_sort not between", value1, value2, "rotarySort");
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