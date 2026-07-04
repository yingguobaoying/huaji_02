package com.huaji.galgamebyhuaji.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AiClientConfigExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AiClientConfigExample() {
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

        public Criteria andCodeIsNull() {
            addCriterion("code is null");
            return (Criteria) this;
        }

        public Criteria andCodeIsNotNull() {
            addCriterion("code is not null");
            return (Criteria) this;
        }

        public Criteria andCodeEqualTo(String value) {
            addCriterion("code =", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotEqualTo(String value) {
            addCriterion("code <>", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThan(String value) {
            addCriterion("code >", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThanOrEqualTo(String value) {
            addCriterion("code >=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThan(String value) {
            addCriterion("code <", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThanOrEqualTo(String value) {
            addCriterion("code <=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLike(String value) {
            addCriterion("code like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotLike(String value) {
            addCriterion("code not like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeIn(List<String> values) {
            addCriterion("code in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotIn(List<String> values) {
            addCriterion("code not in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeBetween(String value1, String value2) {
            addCriterion("code between", value1, value2, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotBetween(String value1, String value2) {
            addCriterion("code not between", value1, value2, "code");
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

        public Criteria andModelIsNull() {
            addCriterion("model is null");
            return (Criteria) this;
        }

        public Criteria andModelIsNotNull() {
            addCriterion("model is not null");
            return (Criteria) this;
        }

        public Criteria andModelEqualTo(String value) {
            addCriterion("model =", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelNotEqualTo(String value) {
            addCriterion("model <>", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelGreaterThan(String value) {
            addCriterion("model >", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelGreaterThanOrEqualTo(String value) {
            addCriterion("model >=", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelLessThan(String value) {
            addCriterion("model <", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelLessThanOrEqualTo(String value) {
            addCriterion("model <=", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelLike(String value) {
            addCriterion("model like", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelNotLike(String value) {
            addCriterion("model not like", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelIn(List<String> values) {
            addCriterion("model in", values, "model");
            return (Criteria) this;
        }

        public Criteria andModelNotIn(List<String> values) {
            addCriterion("model not in", values, "model");
            return (Criteria) this;
        }

        public Criteria andModelBetween(String value1, String value2) {
            addCriterion("model between", value1, value2, "model");
            return (Criteria) this;
        }

        public Criteria andModelNotBetween(String value1, String value2) {
            addCriterion("model not between", value1, value2, "model");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultIsNull() {
            addCriterion("key_is_vault is null");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultIsNotNull() {
            addCriterion("key_is_vault is not null");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultEqualTo(Boolean value) {
            addCriterion("key_is_vault =", value, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultNotEqualTo(Boolean value) {
            addCriterion("key_is_vault <>", value, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultGreaterThan(Boolean value) {
            addCriterion("key_is_vault >", value, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultGreaterThanOrEqualTo(Boolean value) {
            addCriterion("key_is_vault >=", value, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultLessThan(Boolean value) {
            addCriterion("key_is_vault <", value, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultLessThanOrEqualTo(Boolean value) {
            addCriterion("key_is_vault <=", value, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultIn(List<Boolean> values) {
            addCriterion("key_is_vault in", values, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultNotIn(List<Boolean> values) {
            addCriterion("key_is_vault not in", values, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultBetween(Boolean value1, Boolean value2) {
            addCriterion("key_is_vault between", value1, value2, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andKeyIsVaultNotBetween(Boolean value1, Boolean value2) {
            addCriterion("key_is_vault not between", value1, value2, "keyIsVault");
            return (Criteria) this;
        }

        public Criteria andMaxTokensIsNull() {
            addCriterion("max_tokens is null");
            return (Criteria) this;
        }

        public Criteria andMaxTokensIsNotNull() {
            addCriterion("max_tokens is not null");
            return (Criteria) this;
        }

        public Criteria andMaxTokensEqualTo(Integer value) {
            addCriterion("max_tokens =", value, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensNotEqualTo(Integer value) {
            addCriterion("max_tokens <>", value, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensGreaterThan(Integer value) {
            addCriterion("max_tokens >", value, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensGreaterThanOrEqualTo(Integer value) {
            addCriterion("max_tokens >=", value, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensLessThan(Integer value) {
            addCriterion("max_tokens <", value, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensLessThanOrEqualTo(Integer value) {
            addCriterion("max_tokens <=", value, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensIn(List<Integer> values) {
            addCriterion("max_tokens in", values, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensNotIn(List<Integer> values) {
            addCriterion("max_tokens not in", values, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensBetween(Integer value1, Integer value2) {
            addCriterion("max_tokens between", value1, value2, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andMaxTokensNotBetween(Integer value1, Integer value2) {
            addCriterion("max_tokens not between", value1, value2, "maxTokens");
            return (Criteria) this;
        }

        public Criteria andTemperatureIsNull() {
            addCriterion("temperature is null");
            return (Criteria) this;
        }

        public Criteria andTemperatureIsNotNull() {
            addCriterion("temperature is not null");
            return (Criteria) this;
        }

        public Criteria andTemperatureEqualTo(Integer value) {
            addCriterion("temperature =", value, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureNotEqualTo(Integer value) {
            addCriterion("temperature <>", value, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureGreaterThan(Integer value) {
            addCriterion("temperature >", value, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureGreaterThanOrEqualTo(Integer value) {
            addCriterion("temperature >=", value, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureLessThan(Integer value) {
            addCriterion("temperature <", value, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureLessThanOrEqualTo(Integer value) {
            addCriterion("temperature <=", value, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureIn(List<Integer> values) {
            addCriterion("temperature in", values, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureNotIn(List<Integer> values) {
            addCriterion("temperature not in", values, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureBetween(Integer value1, Integer value2) {
            addCriterion("temperature between", value1, value2, "temperature");
            return (Criteria) this;
        }

        public Criteria andTemperatureNotBetween(Integer value1, Integer value2) {
            addCriterion("temperature not between", value1, value2, "temperature");
            return (Criteria) this;
        }

        public Criteria andTopPIsNull() {
            addCriterion("top_p is null");
            return (Criteria) this;
        }

        public Criteria andTopPIsNotNull() {
            addCriterion("top_p is not null");
            return (Criteria) this;
        }

        public Criteria andTopPEqualTo(Integer value) {
            addCriterion("top_p =", value, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPNotEqualTo(Integer value) {
            addCriterion("top_p <>", value, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPGreaterThan(Integer value) {
            addCriterion("top_p >", value, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPGreaterThanOrEqualTo(Integer value) {
            addCriterion("top_p >=", value, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPLessThan(Integer value) {
            addCriterion("top_p <", value, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPLessThanOrEqualTo(Integer value) {
            addCriterion("top_p <=", value, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPIn(List<Integer> values) {
            addCriterion("top_p in", values, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPNotIn(List<Integer> values) {
            addCriterion("top_p not in", values, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPBetween(Integer value1, Integer value2) {
            addCriterion("top_p between", value1, value2, "topP");
            return (Criteria) this;
        }

        public Criteria andTopPNotBetween(Integer value1, Integer value2) {
            addCriterion("top_p not between", value1, value2, "topP");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyIsNull() {
            addCriterion("frequency_penalty is null");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyIsNotNull() {
            addCriterion("frequency_penalty is not null");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyEqualTo(Integer value) {
            addCriterion("frequency_penalty =", value, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyNotEqualTo(Integer value) {
            addCriterion("frequency_penalty <>", value, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyGreaterThan(Integer value) {
            addCriterion("frequency_penalty >", value, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyGreaterThanOrEqualTo(Integer value) {
            addCriterion("frequency_penalty >=", value, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyLessThan(Integer value) {
            addCriterion("frequency_penalty <", value, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyLessThanOrEqualTo(Integer value) {
            addCriterion("frequency_penalty <=", value, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyIn(List<Integer> values) {
            addCriterion("frequency_penalty in", values, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyNotIn(List<Integer> values) {
            addCriterion("frequency_penalty not in", values, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyBetween(Integer value1, Integer value2) {
            addCriterion("frequency_penalty between", value1, value2, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andFrequencyPenaltyNotBetween(Integer value1, Integer value2) {
            addCriterion("frequency_penalty not between", value1, value2, "frequencyPenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyIsNull() {
            addCriterion("presence_penalty is null");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyIsNotNull() {
            addCriterion("presence_penalty is not null");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyEqualTo(Integer value) {
            addCriterion("presence_penalty =", value, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyNotEqualTo(Integer value) {
            addCriterion("presence_penalty <>", value, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyGreaterThan(Integer value) {
            addCriterion("presence_penalty >", value, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyGreaterThanOrEqualTo(Integer value) {
            addCriterion("presence_penalty >=", value, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyLessThan(Integer value) {
            addCriterion("presence_penalty <", value, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyLessThanOrEqualTo(Integer value) {
            addCriterion("presence_penalty <=", value, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyIn(List<Integer> values) {
            addCriterion("presence_penalty in", values, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyNotIn(List<Integer> values) {
            addCriterion("presence_penalty not in", values, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyBetween(Integer value1, Integer value2) {
            addCriterion("presence_penalty between", value1, value2, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andPresencePenaltyNotBetween(Integer value1, Integer value2) {
            addCriterion("presence_penalty not between", value1, value2, "presencePenalty");
            return (Criteria) this;
        }

        public Criteria andThinkingIsNull() {
            addCriterion("thinking is null");
            return (Criteria) this;
        }

        public Criteria andThinkingIsNotNull() {
            addCriterion("thinking is not null");
            return (Criteria) this;
        }

        public Criteria andThinkingEqualTo(Boolean value) {
            addCriterion("thinking =", value, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingNotEqualTo(Boolean value) {
            addCriterion("thinking <>", value, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingGreaterThan(Boolean value) {
            addCriterion("thinking >", value, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingGreaterThanOrEqualTo(Boolean value) {
            addCriterion("thinking >=", value, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingLessThan(Boolean value) {
            addCriterion("thinking <", value, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingLessThanOrEqualTo(Boolean value) {
            addCriterion("thinking <=", value, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingIn(List<Boolean> values) {
            addCriterion("thinking in", values, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingNotIn(List<Boolean> values) {
            addCriterion("thinking not in", values, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingBetween(Boolean value1, Boolean value2) {
            addCriterion("thinking between", value1, value2, "thinking");
            return (Criteria) this;
        }

        public Criteria andThinkingNotBetween(Boolean value1, Boolean value2) {
            addCriterion("thinking not between", value1, value2, "thinking");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortIsNull() {
            addCriterion("reasoning_effort is null");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortIsNotNull() {
            addCriterion("reasoning_effort is not null");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortEqualTo(String value) {
            addCriterion("reasoning_effort =", value, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortNotEqualTo(String value) {
            addCriterion("reasoning_effort <>", value, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortGreaterThan(String value) {
            addCriterion("reasoning_effort >", value, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortGreaterThanOrEqualTo(String value) {
            addCriterion("reasoning_effort >=", value, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortLessThan(String value) {
            addCriterion("reasoning_effort <", value, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortLessThanOrEqualTo(String value) {
            addCriterion("reasoning_effort <=", value, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortLike(String value) {
            addCriterion("reasoning_effort like", value, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortNotLike(String value) {
            addCriterion("reasoning_effort not like", value, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortIn(List<String> values) {
            addCriterion("reasoning_effort in", values, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortNotIn(List<String> values) {
            addCriterion("reasoning_effort not in", values, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortBetween(String value1, String value2) {
            addCriterion("reasoning_effort between", value1, value2, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andReasoningEffortNotBetween(String value1, String value2) {
            addCriterion("reasoning_effort not between", value1, value2, "reasoningEffort");
            return (Criteria) this;
        }

        public Criteria andStreamIsNull() {
            addCriterion("stream is null");
            return (Criteria) this;
        }

        public Criteria andStreamIsNotNull() {
            addCriterion("stream is not null");
            return (Criteria) this;
        }

        public Criteria andStreamEqualTo(Boolean value) {
            addCriterion("stream =", value, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamNotEqualTo(Boolean value) {
            addCriterion("stream <>", value, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamGreaterThan(Boolean value) {
            addCriterion("stream >", value, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamGreaterThanOrEqualTo(Boolean value) {
            addCriterion("stream >=", value, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamLessThan(Boolean value) {
            addCriterion("stream <", value, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamLessThanOrEqualTo(Boolean value) {
            addCriterion("stream <=", value, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamIn(List<Boolean> values) {
            addCriterion("stream in", values, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamNotIn(List<Boolean> values) {
            addCriterion("stream not in", values, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamBetween(Boolean value1, Boolean value2) {
            addCriterion("stream between", value1, value2, "stream");
            return (Criteria) this;
        }

        public Criteria andStreamNotBetween(Boolean value1, Boolean value2) {
            addCriterion("stream not between", value1, value2, "stream");
            return (Criteria) this;
        }

        public Criteria andTimeoutIsNull() {
            addCriterion("timeout is null");
            return (Criteria) this;
        }

        public Criteria andTimeoutIsNotNull() {
            addCriterion("timeout is not null");
            return (Criteria) this;
        }

        public Criteria andTimeoutEqualTo(Integer value) {
            addCriterion("timeout =", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutNotEqualTo(Integer value) {
            addCriterion("timeout <>", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutGreaterThan(Integer value) {
            addCriterion("timeout >", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutGreaterThanOrEqualTo(Integer value) {
            addCriterion("timeout >=", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutLessThan(Integer value) {
            addCriterion("timeout <", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutLessThanOrEqualTo(Integer value) {
            addCriterion("timeout <=", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutIn(List<Integer> values) {
            addCriterion("timeout in", values, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutNotIn(List<Integer> values) {
            addCriterion("timeout not in", values, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutBetween(Integer value1, Integer value2) {
            addCriterion("timeout between", value1, value2, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutNotBetween(Integer value1, Integer value2) {
            addCriterion("timeout not between", value1, value2, "timeout");
            return (Criteria) this;
        }

        public Criteria andIsActiveIsNull() {
            addCriterion("is_active is null");
            return (Criteria) this;
        }

        public Criteria andIsActiveIsNotNull() {
            addCriterion("is_active is not null");
            return (Criteria) this;
        }

        public Criteria andIsActiveEqualTo(Boolean value) {
            addCriterion("is_active =", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotEqualTo(Boolean value) {
            addCriterion("is_active <>", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveGreaterThan(Boolean value) {
            addCriterion("is_active >", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_active >=", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveLessThan(Boolean value) {
            addCriterion("is_active <", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveLessThanOrEqualTo(Boolean value) {
            addCriterion("is_active <=", value, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveIn(List<Boolean> values) {
            addCriterion("is_active in", values, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotIn(List<Boolean> values) {
            addCriterion("is_active not in", values, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveBetween(Boolean value1, Boolean value2) {
            addCriterion("is_active between", value1, value2, "isActive");
            return (Criteria) this;
        }

        public Criteria andIsActiveNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_active not between", value1, value2, "isActive");
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

        public Criteria andMerchantIsNull() {
            addCriterion("merchant is null");
            return (Criteria) this;
        }

        public Criteria andMerchantIsNotNull() {
            addCriterion("merchant is not null");
            return (Criteria) this;
        }

        public Criteria andMerchantEqualTo(String value) {
            addCriterion("merchant =", value, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantNotEqualTo(String value) {
            addCriterion("merchant <>", value, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantGreaterThan(String value) {
            addCriterion("merchant >", value, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantGreaterThanOrEqualTo(String value) {
            addCriterion("merchant >=", value, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantLessThan(String value) {
            addCriterion("merchant <", value, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantLessThanOrEqualTo(String value) {
            addCriterion("merchant <=", value, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantLike(String value) {
            addCriterion("merchant like", value, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantNotLike(String value) {
            addCriterion("merchant not like", value, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantIn(List<String> values) {
            addCriterion("merchant in", values, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantNotIn(List<String> values) {
            addCriterion("merchant not in", values, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantBetween(String value1, String value2) {
            addCriterion("merchant between", value1, value2, "merchant");
            return (Criteria) this;
        }

        public Criteria andMerchantNotBetween(String value1, String value2) {
            addCriterion("merchant not between", value1, value2, "merchant");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
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