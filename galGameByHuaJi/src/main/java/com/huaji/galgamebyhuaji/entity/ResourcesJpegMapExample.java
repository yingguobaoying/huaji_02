package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.List;

public class ResourcesJpegMapExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ResourcesJpegMapExample() {
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

        public Criteria andResourcesIdIsNull() {
            addCriterion("resources_id is null");
            return (Criteria) this;
        }

        public Criteria andResourcesIdIsNotNull() {
            addCriterion("resources_id is not null");
            return (Criteria) this;
        }

        public Criteria andResourcesIdEqualTo(Integer value) {
            addCriterion("resources_id =", value, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdNotEqualTo(Integer value) {
            addCriterion("resources_id <>", value, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdGreaterThan(Integer value) {
            addCriterion("resources_id >", value, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("resources_id >=", value, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdLessThan(Integer value) {
            addCriterion("resources_id <", value, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdLessThanOrEqualTo(Integer value) {
            addCriterion("resources_id <=", value, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdIn(List<Integer> values) {
            addCriterion("resources_id in", values, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdNotIn(List<Integer> values) {
            addCriterion("resources_id not in", values, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdBetween(Integer value1, Integer value2) {
            addCriterion("resources_id between", value1, value2, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andResourcesIdNotBetween(Integer value1, Integer value2) {
            addCriterion("resources_id not between", value1, value2, "resourcesId");
            return (Criteria) this;
        }

        public Criteria andJpegNameIsNull() {
            addCriterion("jpeg_name is null");
            return (Criteria) this;
        }

        public Criteria andJpegNameIsNotNull() {
            addCriterion("jpeg_name is not null");
            return (Criteria) this;
        }

        public Criteria andJpegNameEqualTo(String value) {
            addCriterion("jpeg_name =", value, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameNotEqualTo(String value) {
            addCriterion("jpeg_name <>", value, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameGreaterThan(String value) {
            addCriterion("jpeg_name >", value, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameGreaterThanOrEqualTo(String value) {
            addCriterion("jpeg_name >=", value, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameLessThan(String value) {
            addCriterion("jpeg_name <", value, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameLessThanOrEqualTo(String value) {
            addCriterion("jpeg_name <=", value, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameLike(String value) {
            addCriterion("jpeg_name like", value, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameNotLike(String value) {
            addCriterion("jpeg_name not like", value, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameIn(List<String> values) {
            addCriterion("jpeg_name in", values, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameNotIn(List<String> values) {
            addCriterion("jpeg_name not in", values, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameBetween(String value1, String value2) {
            addCriterion("jpeg_name between", value1, value2, "jpegName");
            return (Criteria) this;
        }

        public Criteria andJpegNameNotBetween(String value1, String value2) {
            addCriterion("jpeg_name not between", value1, value2, "jpegName");
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