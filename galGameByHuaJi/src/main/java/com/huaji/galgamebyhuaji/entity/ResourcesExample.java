package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResourcesExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ResourcesExample() {
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

        public Criteria andRNameIsNull() {
            addCriterion("r_name is null");
            return (Criteria) this;
        }

        public Criteria andRNameIsNotNull() {
            addCriterion("r_name is not null");
            return (Criteria) this;
        }

        public Criteria andRNameEqualTo(String value) {
            addCriterion("r_name =", value, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameNotEqualTo(String value) {
            addCriterion("r_name <>", value, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameGreaterThan(String value) {
            addCriterion("r_name >", value, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameGreaterThanOrEqualTo(String value) {
            addCriterion("r_name >=", value, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameLessThan(String value) {
            addCriterion("r_name <", value, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameLessThanOrEqualTo(String value) {
            addCriterion("r_name <=", value, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameLike(String value) {
            addCriterion("r_name like", value, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameNotLike(String value) {
            addCriterion("r_name not like", value, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameIn(List<String> values) {
            addCriterion("r_name in", values, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameNotIn(List<String> values) {
            addCriterion("r_name not in", values, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameBetween(String value1, String value2) {
            addCriterion("r_name between", value1, value2, "rName");
            return (Criteria) this;
        }

        public Criteria andRNameNotBetween(String value1, String value2) {
            addCriterion("r_name not between", value1, value2, "rName");
            return (Criteria) this;
        }

        public Criteria andRManufacturerIsNull() {
            addCriterion("r_manufacturer is null");
            return (Criteria) this;
        }

        public Criteria andRManufacturerIsNotNull() {
            addCriterion("r_manufacturer is not null");
            return (Criteria) this;
        }

        public Criteria andRManufacturerEqualTo(String value) {
            addCriterion("r_manufacturer =", value, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerNotEqualTo(String value) {
            addCriterion("r_manufacturer <>", value, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerGreaterThan(String value) {
            addCriterion("r_manufacturer >", value, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerGreaterThanOrEqualTo(String value) {
            addCriterion("r_manufacturer >=", value, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerLessThan(String value) {
            addCriterion("r_manufacturer <", value, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerLessThanOrEqualTo(String value) {
            addCriterion("r_manufacturer <=", value, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerLike(String value) {
            addCriterion("r_manufacturer like", value, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerNotLike(String value) {
            addCriterion("r_manufacturer not like", value, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerIn(List<String> values) {
            addCriterion("r_manufacturer in", values, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerNotIn(List<String> values) {
            addCriterion("r_manufacturer not in", values, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerBetween(String value1, String value2) {
            addCriterion("r_manufacturer between", value1, value2, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRManufacturerNotBetween(String value1, String value2) {
            addCriterion("r_manufacturer not between", value1, value2, "rManufacturer");
            return (Criteria) this;
        }

        public Criteria andRTypeIsNull() {
            addCriterion("r_type is null");
            return (Criteria) this;
        }

        public Criteria andRTypeIsNotNull() {
            addCriterion("r_type is not null");
            return (Criteria) this;
        }

        public Criteria andRTypeEqualTo(String value) {
            addCriterion("r_type =", value, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeNotEqualTo(String value) {
            addCriterion("r_type <>", value, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeGreaterThan(String value) {
            addCriterion("r_type >", value, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeGreaterThanOrEqualTo(String value) {
            addCriterion("r_type >=", value, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeLessThan(String value) {
            addCriterion("r_type <", value, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeLessThanOrEqualTo(String value) {
            addCriterion("r_type <=", value, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeLike(String value) {
            addCriterion("r_type like", value, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeNotLike(String value) {
            addCriterion("r_type not like", value, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeIn(List<String> values) {
            addCriterion("r_type in", values, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeNotIn(List<String> values) {
            addCriterion("r_type not in", values, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeBetween(String value1, String value2) {
            addCriterion("r_type between", value1, value2, "rType");
            return (Criteria) this;
        }

        public Criteria andRTypeNotBetween(String value1, String value2) {
            addCriterion("r_type not between", value1, value2, "rType");
            return (Criteria) this;
        }

        public Criteria andRJpegIsNull() {
            addCriterion("r_jpeg is null");
            return (Criteria) this;
        }

        public Criteria andRJpegIsNotNull() {
            addCriterion("r_jpeg is not null");
            return (Criteria) this;
        }

        public Criteria andRJpegEqualTo(String value) {
            addCriterion("r_jpeg =", value, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegNotEqualTo(String value) {
            addCriterion("r_jpeg <>", value, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegGreaterThan(String value) {
            addCriterion("r_jpeg >", value, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegGreaterThanOrEqualTo(String value) {
            addCriterion("r_jpeg >=", value, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegLessThan(String value) {
            addCriterion("r_jpeg <", value, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegLessThanOrEqualTo(String value) {
            addCriterion("r_jpeg <=", value, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegLike(String value) {
            addCriterion("r_jpeg like", value, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegNotLike(String value) {
            addCriterion("r_jpeg not like", value, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegIn(List<String> values) {
            addCriterion("r_jpeg in", values, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegNotIn(List<String> values) {
            addCriterion("r_jpeg not in", values, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegBetween(String value1, String value2) {
            addCriterion("r_jpeg between", value1, value2, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andRJpegNotBetween(String value1, String value2) {
            addCriterion("r_jpeg not between", value1, value2, "rJpeg");
            return (Criteria) this;
        }

        public Criteria andREnterTimeIsNull() {
            addCriterion("r_enter_time is null");
            return (Criteria) this;
        }

        public Criteria andREnterTimeIsNotNull() {
            addCriterion("r_enter_time is not null");
            return (Criteria) this;
        }

        public Criteria andREnterTimeEqualTo(Date value) {
            addCriterion("r_enter_time =", value, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeNotEqualTo(Date value) {
            addCriterion("r_enter_time <>", value, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeGreaterThan(Date value) {
            addCriterion("r_enter_time >", value, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("r_enter_time >=", value, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeLessThan(Date value) {
            addCriterion("r_enter_time <", value, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeLessThanOrEqualTo(Date value) {
            addCriterion("r_enter_time <=", value, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeIn(List<Date> values) {
            addCriterion("r_enter_time in", values, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeNotIn(List<Date> values) {
            addCriterion("r_enter_time not in", values, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeBetween(Date value1, Date value2) {
            addCriterion("r_enter_time between", value1, value2, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andREnterTimeNotBetween(Date value1, Date value2) {
            addCriterion("r_enter_time not between", value1, value2, "rEnterTime");
            return (Criteria) this;
        }

        public Criteria andUpUserIsNull() {
            addCriterion("up_user is null");
            return (Criteria) this;
        }

        public Criteria andUpUserIsNotNull() {
            addCriterion("up_user is not null");
            return (Criteria) this;
        }

        public Criteria andUpUserEqualTo(Long value) {
            addCriterion("up_user =", value, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserNotEqualTo(Long value) {
            addCriterion("up_user <>", value, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserGreaterThan(Long value) {
            addCriterion("up_user >", value, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserGreaterThanOrEqualTo(Long value) {
            addCriterion("up_user >=", value, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserLessThan(Long value) {
            addCriterion("up_user <", value, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserLessThanOrEqualTo(Long value) {
            addCriterion("up_user <=", value, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserIn(List<Long> values) {
            addCriterion("up_user in", values, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserNotIn(List<Long> values) {
            addCriterion("up_user not in", values, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserBetween(Long value1, Long value2) {
            addCriterion("up_user between", value1, value2, "upUser");
            return (Criteria) this;
        }

        public Criteria andUpUserNotBetween(Long value1, Long value2) {
            addCriterion("up_user not between", value1, value2, "upUser");
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