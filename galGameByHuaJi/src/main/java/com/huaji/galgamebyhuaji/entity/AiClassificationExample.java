package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.List;

public class AiClassificationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AiClassificationExample() {
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

        public Criteria andClientIdIsNull() {
            addCriterion("client_id is null");
            return (Criteria) this;
        }

        public Criteria andClientIdIsNotNull() {
            addCriterion("client_id is not null");
            return (Criteria) this;
        }

        public Criteria andClientIdEqualTo(Long value) {
            addCriterion("client_id =", value, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdNotEqualTo(Long value) {
            addCriterion("client_id <>", value, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdGreaterThan(Long value) {
            addCriterion("client_id >", value, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdGreaterThanOrEqualTo(Long value) {
            addCriterion("client_id >=", value, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdLessThan(Long value) {
            addCriterion("client_id <", value, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdLessThanOrEqualTo(Long value) {
            addCriterion("client_id <=", value, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdIn(List<Long> values) {
            addCriterion("client_id in", values, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdNotIn(List<Long> values) {
            addCriterion("client_id not in", values, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdBetween(Long value1, Long value2) {
            addCriterion("client_id between", value1, value2, "clientId");
            return (Criteria) this;
        }

        public Criteria andClientIdNotBetween(Long value1, Long value2) {
            addCriterion("client_id not between", value1, value2, "clientId");
            return (Criteria) this;
        }

        public Criteria andGaveUserIsNull() {
            addCriterion("gave_user is null");
            return (Criteria) this;
        }

        public Criteria andGaveUserIsNotNull() {
            addCriterion("gave_user is not null");
            return (Criteria) this;
        }

        public Criteria andGaveUserEqualTo(Integer value) {
            addCriterion("gave_user =", value, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserNotEqualTo(Integer value) {
            addCriterion("gave_user <>", value, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserGreaterThan(Integer value) {
            addCriterion("gave_user >", value, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserGreaterThanOrEqualTo(Integer value) {
            addCriterion("gave_user >=", value, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserLessThan(Integer value) {
            addCriterion("gave_user <", value, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserLessThanOrEqualTo(Integer value) {
            addCriterion("gave_user <=", value, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserIn(List<Integer> values) {
            addCriterion("gave_user in", values, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserNotIn(List<Integer> values) {
            addCriterion("gave_user not in", values, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserBetween(Integer value1, Integer value2) {
            addCriterion("gave_user between", value1, value2, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andGaveUserNotBetween(Integer value1, Integer value2) {
            addCriterion("gave_user not between", value1, value2, "gaveUser");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionIsNull() {
            addCriterion("need_jurisdiction is null");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionIsNotNull() {
            addCriterion("need_jurisdiction is not null");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionEqualTo(Integer value) {
            addCriterion("need_jurisdiction =", value, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionNotEqualTo(Integer value) {
            addCriterion("need_jurisdiction <>", value, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionGreaterThan(Integer value) {
            addCriterion("need_jurisdiction >", value, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionGreaterThanOrEqualTo(Integer value) {
            addCriterion("need_jurisdiction >=", value, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionLessThan(Integer value) {
            addCriterion("need_jurisdiction <", value, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionLessThanOrEqualTo(Integer value) {
            addCriterion("need_jurisdiction <=", value, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionIn(List<Integer> values) {
            addCriterion("need_jurisdiction in", values, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionNotIn(List<Integer> values) {
            addCriterion("need_jurisdiction not in", values, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionBetween(Integer value1, Integer value2) {
            addCriterion("need_jurisdiction between", value1, value2, "needJurisdiction");
            return (Criteria) this;
        }

        public Criteria andNeedJurisdictionNotBetween(Integer value1, Integer value2) {
            addCriterion("need_jurisdiction not between", value1, value2, "needJurisdiction");
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