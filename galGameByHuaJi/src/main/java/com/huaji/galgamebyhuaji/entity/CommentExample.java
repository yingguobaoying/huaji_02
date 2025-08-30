package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CommentExample() {
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

        public Criteria andCommentIdIsNull() {
            addCriterion("comment_id is null");
            return (Criteria) this;
        }

        public Criteria andCommentIdIsNotNull() {
            addCriterion("comment_id is not null");
            return (Criteria) this;
        }

        public Criteria andCommentIdEqualTo(Long value) {
            addCriterion("comment_id =", value, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdNotEqualTo(Long value) {
            addCriterion("comment_id <>", value, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdGreaterThan(Long value) {
            addCriterion("comment_id >", value, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("comment_id >=", value, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdLessThan(Long value) {
            addCriterion("comment_id <", value, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdLessThanOrEqualTo(Long value) {
            addCriterion("comment_id <=", value, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdIn(List<Long> values) {
            addCriterion("comment_id in", values, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdNotIn(List<Long> values) {
            addCriterion("comment_id not in", values, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdBetween(Long value1, Long value2) {
            addCriterion("comment_id between", value1, value2, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentIdNotBetween(Long value1, Long value2) {
            addCriterion("comment_id not between", value1, value2, "commentId");
            return (Criteria) this;
        }

        public Criteria andCommentUserIsNull() {
            addCriterion("comment_user is null");
            return (Criteria) this;
        }

        public Criteria andCommentUserIsNotNull() {
            addCriterion("comment_user is not null");
            return (Criteria) this;
        }

        public Criteria andCommentUserEqualTo(Integer value) {
            addCriterion("comment_user =", value, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserNotEqualTo(Integer value) {
            addCriterion("comment_user <>", value, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserGreaterThan(Integer value) {
            addCriterion("comment_user >", value, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserGreaterThanOrEqualTo(Integer value) {
            addCriterion("comment_user >=", value, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserLessThan(Integer value) {
            addCriterion("comment_user <", value, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserLessThanOrEqualTo(Integer value) {
            addCriterion("comment_user <=", value, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserIn(List<Integer> values) {
            addCriterion("comment_user in", values, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserNotIn(List<Integer> values) {
            addCriterion("comment_user not in", values, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserBetween(Integer value1, Integer value2) {
            addCriterion("comment_user between", value1, value2, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentUserNotBetween(Integer value1, Integer value2) {
            addCriterion("comment_user not between", value1, value2, "commentUser");
            return (Criteria) this;
        }

        public Criteria andCommentTimeIsNull() {
            addCriterion("comment_time is null");
            return (Criteria) this;
        }

        public Criteria andCommentTimeIsNotNull() {
            addCriterion("comment_time is not null");
            return (Criteria) this;
        }

        public Criteria andCommentTimeEqualTo(Date value) {
            addCriterion("comment_time =", value, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeNotEqualTo(Date value) {
            addCriterion("comment_time <>", value, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeGreaterThan(Date value) {
            addCriterion("comment_time >", value, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("comment_time >=", value, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeLessThan(Date value) {
            addCriterion("comment_time <", value, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeLessThanOrEqualTo(Date value) {
            addCriterion("comment_time <=", value, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeIn(List<Date> values) {
            addCriterion("comment_time in", values, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeNotIn(List<Date> values) {
            addCriterion("comment_time not in", values, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeBetween(Date value1, Date value2) {
            addCriterion("comment_time between", value1, value2, "commentTime");
            return (Criteria) this;
        }

        public Criteria andCommentTimeNotBetween(Date value1, Date value2) {
            addCriterion("comment_time not between", value1, value2, "commentTime");
            return (Criteria) this;
        }

        public Criteria andFatherCommentIsNull() {
            addCriterion("father_comment is null");
            return (Criteria) this;
        }

        public Criteria andFatherCommentIsNotNull() {
            addCriterion("father_comment is not null");
            return (Criteria) this;
        }

        public Criteria andFatherCommentEqualTo(Long value) {
            addCriterion("father_comment =", value, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentNotEqualTo(Long value) {
            addCriterion("father_comment <>", value, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentGreaterThan(Long value) {
            addCriterion("father_comment >", value, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentGreaterThanOrEqualTo(Long value) {
            addCriterion("father_comment >=", value, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentLessThan(Long value) {
            addCriterion("father_comment <", value, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentLessThanOrEqualTo(Long value) {
            addCriterion("father_comment <=", value, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentIn(List<Long> values) {
            addCriterion("father_comment in", values, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentNotIn(List<Long> values) {
            addCriterion("father_comment not in", values, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentBetween(Long value1, Long value2) {
            addCriterion("father_comment between", value1, value2, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andFatherCommentNotBetween(Long value1, Long value2) {
            addCriterion("father_comment not between", value1, value2, "fatherComment");
            return (Criteria) this;
        }

        public Criteria andCommentRIdIsNull() {
            addCriterion("comment_r_id is null");
            return (Criteria) this;
        }

        public Criteria andCommentRIdIsNotNull() {
            addCriterion("comment_r_id is not null");
            return (Criteria) this;
        }

        public Criteria andCommentRIdEqualTo(Integer value) {
            addCriterion("comment_r_id =", value, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdNotEqualTo(Integer value) {
            addCriterion("comment_r_id <>", value, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdGreaterThan(Integer value) {
            addCriterion("comment_r_id >", value, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("comment_r_id >=", value, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdLessThan(Integer value) {
            addCriterion("comment_r_id <", value, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdLessThanOrEqualTo(Integer value) {
            addCriterion("comment_r_id <=", value, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdIn(List<Integer> values) {
            addCriterion("comment_r_id in", values, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdNotIn(List<Integer> values) {
            addCriterion("comment_r_id not in", values, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdBetween(Integer value1, Integer value2) {
            addCriterion("comment_r_id between", value1, value2, "commentRId");
            return (Criteria) this;
        }

        public Criteria andCommentRIdNotBetween(Integer value1, Integer value2) {
            addCriterion("comment_r_id not between", value1, value2, "commentRId");
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