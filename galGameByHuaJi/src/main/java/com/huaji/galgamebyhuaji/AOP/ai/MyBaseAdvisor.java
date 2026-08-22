//package com.huaji.galgamebyhuaji.AOP.ai;
//
//import org.springframework.ai.chat.client.ChatClientRequest;
//import org.springframework.ai.chat.client.ChatClientResponse;
//import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
//import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
//import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
//import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
//import reactor.core.publisher.Flux;
//import reactor.core.scheduler.Scheduler;
//
//public abstract class MyBaseAdvisor implements BaseAdvisor {
//    protected Object getParam(ChatClientRequest request, String name) {
//        return request.context().get(name);
//    }
//
//    protected Object getParam(ChatClientResponse r, String name) {
//        return r.context().get(name);
//    }
//
//    protected <T> void saveData(ChatClientRequest request, String name, T value) {
//        request.context().put(name, value);
//    }
//
//    protected <T> void saveData(ChatClientResponse response, String name, T value) {
//        response.context().put(name, value);
//    }
//
//    @Override
//    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
//        return callAdvisorChain.nextCall(chatClientRequest);
//    }
//
//    @Override
//    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
//        return streamAdvisorChain.nextStream(chatClientRequest);
//    }
//
//    @Override
//    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
//        return chatClientRequest;
//    }
//
//    @Override
//    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
//        return chatClientResponse;
//    }
//
//    @Override
//    public Scheduler getScheduler() {
//        return BaseAdvisor.super.getScheduler();
//    }
//
//}