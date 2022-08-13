package com.alibaba.csp.sentinel.demo.annotation.aop.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author maofangyun
 * @date 2022/8/13 11:42
 */
@Component
public class SentinelConfig {

    private static final String KEY="query";

    @PostConstruct
    private void init(){
        initDegradeRule();
        initFlowQpsRule();
        initSystemRule();
        initAuthorityRule();
        initParamFlowRule();
    }

    // 熔断降级规则
    private void initDegradeRule(){
        List<DegradeRule> rules=new ArrayList<>();
        DegradeRule rule=new DegradeRule();
        rule.setResource(KEY);
        // 80s内调用接口出现异常,次数超过5的时候,进行熔断
        rule.setCount(5);
        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        rule.setTimeWindow(80);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }

    // 流量控制规则
    private void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule(KEY);
        rule.setCount(20);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    // 系统保护规则
    private void initSystemRule() {
        List<SystemRule> rules = new ArrayList<>();
        SystemRule rule = new SystemRule();
        rule.setHighestSystemLoad(10);
        rules.add(rule);
        SystemRuleManager.loadRules(rules);
    }

    //黑白名单控制
    private void initAuthorityRule(){
        List<AuthorityRule> rules=new ArrayList<>();

        AuthorityRule rule = new AuthorityRule();
        rule.setResource(KEY);
        rule.setStrategy(RuleConstant.AUTHORITY_BLACK);
        rule.setLimitApp("nacos-consumer");
        rules.add(rule);
        AuthorityRuleManager.loadRules(rules);
    }

    // 热点参数规则
    private void initParamFlowRule(){
        ParamFlowRule rule = new ParamFlowRule(KEY)
                .setParamIdx(0)
                .setCount(20);
        ParamFlowItem item = new ParamFlowItem().setObject(String.valueOf("4"))
                .setClassType(String.class.getName())
                .setCount(2);
        rule.setParamFlowItemList(Collections.singletonList(item));
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }
}
