package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.List;

public class ResourceExtensionInformationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ResourceExtensionInformationExample() {
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

        public Criteria andLinkPriceIsNull() {
            addCriterion("link_price is null");
            return (Criteria) this;
        }

        public Criteria andLinkPriceIsNotNull() {
            addCriterion("link_price is not null");
            return (Criteria) this;
        }

        public Criteria andLinkPriceEqualTo(Integer value) {
            addCriterion("link_price =", value, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceNotEqualTo(Integer value) {
            addCriterion("link_price <>", value, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceGreaterThan(Integer value) {
            addCriterion("link_price >", value, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceGreaterThanOrEqualTo(Integer value) {
            addCriterion("link_price >=", value, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceLessThan(Integer value) {
            addCriterion("link_price <", value, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceLessThanOrEqualTo(Integer value) {
            addCriterion("link_price <=", value, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceIn(List<Integer> values) {
            addCriterion("link_price in", values, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceNotIn(List<Integer> values) {
            addCriterion("link_price not in", values, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceBetween(Integer value1, Integer value2) {
            addCriterion("link_price between", value1, value2, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andLinkPriceNotBetween(Integer value1, Integer value2) {
            addCriterion("link_price not between", value1, value2, "linkPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceIsNull() {
            addCriterion("download_locally_price is null");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceIsNotNull() {
            addCriterion("download_locally_price is not null");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceEqualTo(Integer value) {
            addCriterion("download_locally_price =", value, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceNotEqualTo(Integer value) {
            addCriterion("download_locally_price <>", value, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceGreaterThan(Integer value) {
            addCriterion("download_locally_price >", value, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceGreaterThanOrEqualTo(Integer value) {
            addCriterion("download_locally_price >=", value, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceLessThan(Integer value) {
            addCriterion("download_locally_price <", value, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceLessThanOrEqualTo(Integer value) {
            addCriterion("download_locally_price <=", value, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceIn(List<Integer> values) {
            addCriterion("download_locally_price in", values, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceNotIn(List<Integer> values) {
            addCriterion("download_locally_price not in", values, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceBetween(Integer value1, Integer value2) {
            addCriterion("download_locally_price between", value1, value2, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andDownloadLocallyPriceNotBetween(Integer value1, Integer value2) {
            addCriterion("download_locally_price not between", value1, value2, "downloadLocallyPrice");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyIsNull() {
            addCriterion("has_download_locally is null");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyIsNotNull() {
            addCriterion("has_download_locally is not null");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyEqualTo(String value) {
            addCriterion("has_download_locally =", value, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyNotEqualTo(String value) {
            addCriterion("has_download_locally <>", value, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyGreaterThan(String value) {
            addCriterion("has_download_locally >", value, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyGreaterThanOrEqualTo(String value) {
            addCriterion("has_download_locally >=", value, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyLessThan(String value) {
            addCriterion("has_download_locally <", value, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyLessThanOrEqualTo(String value) {
            addCriterion("has_download_locally <=", value, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyLike(String value) {
            addCriterion("has_download_locally like", value, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyNotLike(String value) {
            addCriterion("has_download_locally not like", value, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyIn(List<String> values) {
            addCriterion("has_download_locally in", values, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyNotIn(List<String> values) {
            addCriterion("has_download_locally not in", values, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyBetween(String value1, String value2) {
            addCriterion("has_download_locally between", value1, value2, "hasDownloadLocally");
            return (Criteria) this;
        }

        public Criteria andHasDownloadLocallyNotBetween(String value1, String value2) {
            addCriterion("has_download_locally not between", value1, value2, "hasDownloadLocally");
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