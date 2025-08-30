package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserResourceRepositoryExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UserResourceRepositoryExample() {
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

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Integer value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Integer value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Integer value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Integer value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Integer value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Integer> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Integer> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Integer value1, Integer value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Integer value1, Integer value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andRIdIsNull() {
            addCriterion("r_id is null");
            return (Criteria) this;
        }

        public Criteria andRIdIsNotNull() {
            addCriterion("r_id is not null");
            return (Criteria) this;
        }

        public Criteria andRIdEqualTo(Integer value) {
            addCriterion("r_id =", value, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdNotEqualTo(Integer value) {
            addCriterion("r_id <>", value, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdGreaterThan(Integer value) {
            addCriterion("r_id >", value, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("r_id >=", value, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdLessThan(Integer value) {
            addCriterion("r_id <", value, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdLessThanOrEqualTo(Integer value) {
            addCriterion("r_id <=", value, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdIn(List<Integer> values) {
            addCriterion("r_id in", values, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdNotIn(List<Integer> values) {
            addCriterion("r_id not in", values, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdBetween(Integer value1, Integer value2) {
            addCriterion("r_id between", value1, value2, "rId");
            return (Criteria) this;
        }

        public Criteria andRIdNotBetween(Integer value1, Integer value2) {
            addCriterion("r_id not between", value1, value2, "rId");
            return (Criteria) this;
        }

        public Criteria andGetTypeIsNull() {
            addCriterion("get_type is null");
            return (Criteria) this;
        }

        public Criteria andGetTypeIsNotNull() {
            addCriterion("get_type is not null");
            return (Criteria) this;
        }

        public Criteria andGetTypeEqualTo(String value) {
            addCriterion("get_type =", value, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeNotEqualTo(String value) {
            addCriterion("get_type <>", value, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeGreaterThan(String value) {
            addCriterion("get_type >", value, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeGreaterThanOrEqualTo(String value) {
            addCriterion("get_type >=", value, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeLessThan(String value) {
            addCriterion("get_type <", value, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeLessThanOrEqualTo(String value) {
            addCriterion("get_type <=", value, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeLike(String value) {
            addCriterion("get_type like", value, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeNotLike(String value) {
            addCriterion("get_type not like", value, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeIn(List<String> values) {
            addCriterion("get_type in", values, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeNotIn(List<String> values) {
            addCriterion("get_type not in", values, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeBetween(String value1, String value2) {
            addCriterion("get_type between", value1, value2, "getType");
            return (Criteria) this;
        }

        public Criteria andGetTypeNotBetween(String value1, String value2) {
            addCriterion("get_type not between", value1, value2, "getType");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeIsNull() {
            addCriterion("expiration_time is null");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeIsNotNull() {
            addCriterion("expiration_time is not null");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeEqualTo(Date value) {
            addCriterion("expiration_time =", value, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeNotEqualTo(Date value) {
            addCriterion("expiration_time <>", value, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeGreaterThan(Date value) {
            addCriterion("expiration_time >", value, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("expiration_time >=", value, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeLessThan(Date value) {
            addCriterion("expiration_time <", value, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeLessThanOrEqualTo(Date value) {
            addCriterion("expiration_time <=", value, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeIn(List<Date> values) {
            addCriterion("expiration_time in", values, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeNotIn(List<Date> values) {
            addCriterion("expiration_time not in", values, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeBetween(Date value1, Date value2) {
            addCriterion("expiration_time between", value1, value2, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andExpirationTimeNotBetween(Date value1, Date value2) {
            addCriterion("expiration_time not between", value1, value2, "expirationTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeIsNull() {
            addCriterion("get_time is null");
            return (Criteria) this;
        }

        public Criteria andGetTimeIsNotNull() {
            addCriterion("get_time is not null");
            return (Criteria) this;
        }

        public Criteria andGetTimeEqualTo(Date value) {
            addCriterion("get_time =", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotEqualTo(Date value) {
            addCriterion("get_time <>", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeGreaterThan(Date value) {
            addCriterion("get_time >", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("get_time >=", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeLessThan(Date value) {
            addCriterion("get_time <", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeLessThanOrEqualTo(Date value) {
            addCriterion("get_time <=", value, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeIn(List<Date> values) {
            addCriterion("get_time in", values, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotIn(List<Date> values) {
            addCriterion("get_time not in", values, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeBetween(Date value1, Date value2) {
            addCriterion("get_time between", value1, value2, "getTime");
            return (Criteria) this;
        }

        public Criteria andGetTimeNotBetween(Date value1, Date value2) {
            addCriterion("get_time not between", value1, value2, "getTime");
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