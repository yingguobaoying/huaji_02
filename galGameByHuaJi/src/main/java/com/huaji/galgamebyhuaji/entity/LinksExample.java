package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LinksExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LinksExample() {
        oredCriteria = new ArrayList<>();
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
            criteria = new ArrayList<>();
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

        public Criteria andLinkIdIsNull() {
            addCriterion("link_id is null");
            return (Criteria) this;
        }

        public Criteria andLinkIdIsNotNull() {
            addCriterion("link_id is not null");
            return (Criteria) this;
        }

        public Criteria andLinkIdEqualTo(Long value) {
            addCriterion("link_id =", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotEqualTo(Long value) {
            addCriterion("link_id <>", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThan(Long value) {
            addCriterion("link_id >", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdGreaterThanOrEqualTo(Long value) {
            addCriterion("link_id >=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThan(Long value) {
            addCriterion("link_id <", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdLessThanOrEqualTo(Long value) {
            addCriterion("link_id <=", value, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdIn(List<Long> values) {
            addCriterion("link_id in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotIn(List<Long> values) {
            addCriterion("link_id not in", values, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdBetween(Long value1, Long value2) {
            addCriterion("link_id between", value1, value2, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkIdNotBetween(Long value1, Long value2) {
            addCriterion("link_id not between", value1, value2, "linkId");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserIsNull() {
            addCriterion("link_up_user is null");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserIsNotNull() {
            addCriterion("link_up_user is not null");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserEqualTo(Integer value) {
            addCriterion("link_up_user =", value, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserNotEqualTo(Integer value) {
            addCriterion("link_up_user <>", value, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserGreaterThan(Integer value) {
            addCriterion("link_up_user >", value, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserGreaterThanOrEqualTo(Integer value) {
            addCriterion("link_up_user >=", value, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserLessThan(Integer value) {
            addCriterion("link_up_user <", value, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserLessThanOrEqualTo(Integer value) {
            addCriterion("link_up_user <=", value, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserIn(List<Integer> values) {
            addCriterion("link_up_user in", values, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserNotIn(List<Integer> values) {
            addCriterion("link_up_user not in", values, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserBetween(Integer value1, Integer value2) {
            addCriterion("link_up_user between", value1, value2, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkUpUserNotBetween(Integer value1, Integer value2) {
            addCriterion("link_up_user not between", value1, value2, "linkUpUser");
            return (Criteria) this;
        }

        public Criteria andLinkRIsNull() {
            addCriterion("link_r is null");
            return (Criteria) this;
        }

        public Criteria andLinkRIsNotNull() {
            addCriterion("link_r is not null");
            return (Criteria) this;
        }

        public Criteria andLinkREqualTo(Integer value) {
            addCriterion("link_r =", value, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRNotEqualTo(Integer value) {
            addCriterion("link_r <>", value, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRGreaterThan(Integer value) {
            addCriterion("link_r >", value, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRGreaterThanOrEqualTo(Integer value) {
            addCriterion("link_r >=", value, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRLessThan(Integer value) {
            addCriterion("link_r <", value, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRLessThanOrEqualTo(Integer value) {
            addCriterion("link_r <=", value, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRIn(List<Integer> values) {
            addCriterion("link_r in", values, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRNotIn(List<Integer> values) {
            addCriterion("link_r not in", values, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRBetween(Integer value1, Integer value2) {
            addCriterion("link_r between", value1, value2, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkRNotBetween(Integer value1, Integer value2) {
            addCriterion("link_r not between", value1, value2, "linkR");
            return (Criteria) this;
        }

        public Criteria andLinkStateIsNull() {
            addCriterion("link_state is null");
            return (Criteria) this;
        }

        public Criteria andLinkStateIsNotNull() {
            addCriterion("link_state is not null");
            return (Criteria) this;
        }

        public Criteria andLinkStateEqualTo(String value) {
            addCriterion("link_state =", value, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateNotEqualTo(String value) {
            addCriterion("link_state <>", value, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateGreaterThan(String value) {
            addCriterion("link_state >", value, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateGreaterThanOrEqualTo(String value) {
            addCriterion("link_state >=", value, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateLessThan(String value) {
            addCriterion("link_state <", value, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateLessThanOrEqualTo(String value) {
            addCriterion("link_state <=", value, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateLike(String value) {
            addCriterion("link_state like", value, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateNotLike(String value) {
            addCriterion("link_state not like", value, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateIn(List<String> values) {
            addCriterion("link_state in", values, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateNotIn(List<String> values) {
            addCriterion("link_state not in", values, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateBetween(String value1, String value2) {
            addCriterion("link_state between", value1, value2, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinkStateNotBetween(String value1, String value2) {
            addCriterion("link_state not between", value1, value2, "linkState");
            return (Criteria) this;
        }

        public Criteria andLinksPublicIsNull() {
            addCriterion("links_public is null");
            return (Criteria) this;
        }

        public Criteria andLinksPublicIsNotNull() {
            addCriterion("links_public is not null");
            return (Criteria) this;
        }

        public Criteria andLinksPublicEqualTo(String value) {
            addCriterion("links_public =", value, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicNotEqualTo(String value) {
            addCriterion("links_public <>", value, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicGreaterThan(String value) {
            addCriterion("links_public >", value, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicGreaterThanOrEqualTo(String value) {
            addCriterion("links_public >=", value, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicLessThan(String value) {
            addCriterion("links_public <", value, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicLessThanOrEqualTo(String value) {
            addCriterion("links_public <=", value, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicLike(String value) {
            addCriterion("links_public like", value, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicNotLike(String value) {
            addCriterion("links_public not like", value, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicIn(List<String> values) {
            addCriterion("links_public in", values, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicNotIn(List<String> values) {
            addCriterion("links_public not in", values, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicBetween(String value1, String value2) {
            addCriterion("links_public between", value1, value2, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andLinksPublicNotBetween(String value1, String value2) {
            addCriterion("links_public not between", value1, value2, "linksPublic");
            return (Criteria) this;
        }

        public Criteria andUpTimeIsNull() {
            addCriterion("up_time is null");
            return (Criteria) this;
        }

        public Criteria andUpTimeIsNotNull() {
            addCriterion("up_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpTimeEqualTo(Date value) {
            addCriterion("up_time =", value, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeNotEqualTo(Date value) {
            addCriterion("up_time <>", value, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeGreaterThan(Date value) {
            addCriterion("up_time >", value, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("up_time >=", value, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeLessThan(Date value) {
            addCriterion("up_time <", value, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeLessThanOrEqualTo(Date value) {
            addCriterion("up_time <=", value, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeIn(List<Date> values) {
            addCriterion("up_time in", values, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeNotIn(List<Date> values) {
            addCriterion("up_time not in", values, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeBetween(Date value1, Date value2) {
            addCriterion("up_time between", value1, value2, "upTime");
            return (Criteria) this;
        }

        public Criteria andUpTimeNotBetween(Date value1, Date value2) {
            addCriterion("up_time not between", value1, value2, "upTime");
            return (Criteria) this;
        }

        public Criteria andLinkPointerIsNull() {
            addCriterion("link_pointer is null");
            return (Criteria) this;
        }

        public Criteria andLinkPointerIsNotNull() {
            addCriterion("link_pointer is not null");
            return (Criteria) this;
        }

        public Criteria andLinkPointerEqualTo(String value) {
            addCriterion("link_pointer =", value, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerNotEqualTo(String value) {
            addCriterion("link_pointer <>", value, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerGreaterThan(String value) {
            addCriterion("link_pointer >", value, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerGreaterThanOrEqualTo(String value) {
            addCriterion("link_pointer >=", value, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerLessThan(String value) {
            addCriterion("link_pointer <", value, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerLessThanOrEqualTo(String value) {
            addCriterion("link_pointer <=", value, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerLike(String value) {
            addCriterion("link_pointer like", value, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerNotLike(String value) {
            addCriterion("link_pointer not like", value, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerIn(List<String> values) {
            addCriterion("link_pointer in", values, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerNotIn(List<String> values) {
            addCriterion("link_pointer not in", values, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerBetween(String value1, String value2) {
            addCriterion("link_pointer between", value1, value2, "linkPointer");
            return (Criteria) this;
        }

        public Criteria andLinkPointerNotBetween(String value1, String value2) {
            addCriterion("link_pointer not between", value1, value2, "linkPointer");
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