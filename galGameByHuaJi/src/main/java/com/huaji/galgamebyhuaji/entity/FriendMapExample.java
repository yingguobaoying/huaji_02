package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.List;

public class FriendMapExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FriendMapExample() {
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

        public Criteria andFriendIdIsNull() {
            addCriterion("friend_id is null");
            return (Criteria) this;
        }

        public Criteria andFriendIdIsNotNull() {
            addCriterion("friend_id is not null");
            return (Criteria) this;
        }

        public Criteria andFriendIdEqualTo(Integer value) {
            addCriterion("friend_id =", value, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdNotEqualTo(Integer value) {
            addCriterion("friend_id <>", value, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdGreaterThan(Integer value) {
            addCriterion("friend_id >", value, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("friend_id >=", value, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdLessThan(Integer value) {
            addCriterion("friend_id <", value, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdLessThanOrEqualTo(Integer value) {
            addCriterion("friend_id <=", value, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdIn(List<Integer> values) {
            addCriterion("friend_id in", values, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdNotIn(List<Integer> values) {
            addCriterion("friend_id not in", values, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdBetween(Integer value1, Integer value2) {
            addCriterion("friend_id between", value1, value2, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendIdNotBetween(Integer value1, Integer value2) {
            addCriterion("friend_id not between", value1, value2, "friendId");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationIsNull() {
            addCriterion("friend_confirmation is null");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationIsNotNull() {
            addCriterion("friend_confirmation is not null");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationEqualTo(Boolean value) {
            addCriterion("friend_confirmation =", value, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationNotEqualTo(Boolean value) {
            addCriterion("friend_confirmation <>", value, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationGreaterThan(Boolean value) {
            addCriterion("friend_confirmation >", value, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationGreaterThanOrEqualTo(Boolean value) {
            addCriterion("friend_confirmation >=", value, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationLessThan(Boolean value) {
            addCriterion("friend_confirmation <", value, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationLessThanOrEqualTo(Boolean value) {
            addCriterion("friend_confirmation <=", value, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationIn(List<Boolean> values) {
            addCriterion("friend_confirmation in", values, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationNotIn(List<Boolean> values) {
            addCriterion("friend_confirmation not in", values, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationBetween(Boolean value1, Boolean value2) {
            addCriterion("friend_confirmation between", value1, value2, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andFriendConfirmationNotBetween(Boolean value1, Boolean value2) {
            addCriterion("friend_confirmation not between", value1, value2, "friendConfirmation");
            return (Criteria) this;
        }

        public Criteria andInitiateUserIsNull() {
            addCriterion("initiate_user is null");
            return (Criteria) this;
        }

        public Criteria andInitiateUserIsNotNull() {
            addCriterion("initiate_user is not null");
            return (Criteria) this;
        }

        public Criteria andInitiateUserEqualTo(Integer value) {
            addCriterion("initiate_user =", value, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserNotEqualTo(Integer value) {
            addCriterion("initiate_user <>", value, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserGreaterThan(Integer value) {
            addCriterion("initiate_user >", value, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserGreaterThanOrEqualTo(Integer value) {
            addCriterion("initiate_user >=", value, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserLessThan(Integer value) {
            addCriterion("initiate_user <", value, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserLessThanOrEqualTo(Integer value) {
            addCriterion("initiate_user <=", value, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserIn(List<Integer> values) {
            addCriterion("initiate_user in", values, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserNotIn(List<Integer> values) {
            addCriterion("initiate_user not in", values, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserBetween(Integer value1, Integer value2) {
            addCriterion("initiate_user between", value1, value2, "initiateUser");
            return (Criteria) this;
        }

        public Criteria andInitiateUserNotBetween(Integer value1, Integer value2) {
            addCriterion("initiate_user not between", value1, value2, "initiateUser");
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