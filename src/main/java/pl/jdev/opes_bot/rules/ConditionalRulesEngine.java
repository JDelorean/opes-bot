package pl.jdev.opes_bot.rules;

import org.jeasy.rules.api.*;
import org.jeasy.rules.core.RulesEngineParameters;

import java.util.*;

public class ConditionalRulesEngine implements RulesEngine {

    private RulesEngineParameters parameters;
    private List<RuleListener> ruleListeners;
    private List<RulesEngineListener> rulesEngineListeners;

    public ConditionalRulesEngine() {
        this(new RulesEngineParameters());
    }

    public ConditionalRulesEngine(final RulesEngineParameters parameters) {
        this.parameters = parameters;
        this.ruleListeners = new ArrayList<>();
//        this.ruleListeners.add(new DefaultRuleListener());
        this.rulesEngineListeners = new ArrayList<>();
//        this.rulesEngineListeners.add(new DefaultRulesEngineListener(parameters));
    }

    @Override
    public RulesEngineParameters getParameters() {
        return parameters;
    }

    @Override
    public List<RuleListener> getRuleListeners() {
        return ruleListeners;
    }

    @Override
    public List<RulesEngineListener> getRulesEngineListeners() {
        return rulesEngineListeners;
    }

    @Override
    public void fire(Rules rules, Facts facts) {
        triggerListenersBeforeRules(rules, facts);
        doFire(rules, facts);
        triggerListenersAfterRules(rules, facts);
    }

    void doFire(Rules rules, Facts facts) {
        boolean shouldProceed = true;
        Iterator<Rule> ruleIterator = rules.iterator();
        do {
            Rule rule = ruleIterator.next();
            final String name = rule.getName();
            final int priority = rule.getPriority();
            if (priority > parameters.getPriorityThreshold()) {
//                LOGGER.info("Rule priority threshold ({}) exceeded at rule '{}' with priority={}, next rules will be skipped",
//                parameters.getPriorityThreshold(), name, priority);
                break;
            }
            if (!shouldBeEvaluated(rule, facts)) {
//                LOGGER.info("Rule '{}' has been skipped before being evaluated", name);
                continue;
            }
            if (rule.evaluate(facts)) {
                triggerListenersAfterEvaluate(rule, facts, true);
                try {
                    triggerListenersBeforeExecute(rule, facts);
                    rule.execute(facts);
                    triggerListenersOnSuccess(rule, facts);
                    if (parameters.isSkipOnFirstAppliedRule()) {
//                        LOGGER.info("Next rules will be skipped since parameter skipOnFirstAppliedRule is set");
                        break;
                    }
                } catch (Exception e) {
                    triggerListenersOnFailure(rule, e, facts);
                    if (parameters.isSkipOnFirstFailedRule()) {
//                        LOGGER.info("Next rules will be skipped since parameter skipOnFirstFailedRule is set");
                        break;
                    }
                }
            } else {
                triggerListenersAfterEvaluate(rule, facts, false);
                if (parameters.isSkipOnFirstNonTriggeredRule()) {
//                    LOGGER.info("Next rules will be skipped since parameter skipOnFirstNonTriggeredRule is set");
                    break;
                }
                shouldProceed = false;
            }
        } while (ruleIterator.hasNext() && !shouldProceed);
    }

    @Override
    public Map<Rule, Boolean> check(Rules rules, Facts facts) {
        triggerListenersBeforeRules(rules, facts);
        Map<Rule, Boolean> result = doCheck(rules, facts);
        triggerListenersAfterRules(rules, facts);
        return result;
    }

    private Map<Rule, Boolean> doCheck(Rules rules, Facts facts) {
        Map<Rule, Boolean> result = new HashMap<>();
        for (Rule rule : rules) {
            if (shouldBeEvaluated(rule, facts)) {
                result.put(rule, rule.evaluate(facts));
            }
        }
        return result;
    }

    private void triggerListenersOnFailure(final Rule rule, final Exception exception, Facts facts) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.onFailure(rule, facts, exception);
        }
    }

    private void triggerListenersOnSuccess(final Rule rule, Facts facts) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.onSuccess(rule, facts);
        }
    }

    private void triggerListenersBeforeExecute(final Rule rule, Facts facts) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.beforeExecute(rule, facts);
        }
    }

    private boolean triggerListenersBeforeEvaluate(Rule rule, Facts facts) {
        for (RuleListener ruleListener : ruleListeners) {
            if (!ruleListener.beforeEvaluate(rule, facts)) {
                return false;
            }
        }
        return true;
    }

    private void triggerListenersAfterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.afterEvaluate(rule, facts, evaluationResult);
        }
    }

    private void triggerListenersBeforeRules(Rules rule, Facts facts) {
        for (RulesEngineListener rulesEngineListener : rulesEngineListeners) {
            rulesEngineListener.beforeEvaluate(rule, facts);
        }
    }

    private void triggerListenersAfterRules(Rules rule, Facts facts) {
        for (RulesEngineListener rulesEngineListener : rulesEngineListeners) {
            rulesEngineListener.afterExecute(rule, facts);
        }
    }

    private boolean shouldBeEvaluated(Rule rule, Facts facts) {
        return triggerListenersBeforeEvaluate(rule, facts);
    }

    /**
     * Register a rule listener.
     *
     * @param ruleListener to register
     */
    public void registerRuleListener(RuleListener ruleListener) {
        ruleListeners.add(ruleListener);
    }

    /**
     * Register a list of rule listener.
     *
     * @param ruleListeners to register
     */
    public void registerRuleListeners(List<RuleListener> ruleListeners) {
        this.ruleListeners.addAll(ruleListeners);
    }

    /**
     * Register a rules engine listener.
     *
     * @param rulesEngineListener to register
     */
    public void registerRulesEngineListener(RulesEngineListener rulesEngineListener) {
        rulesEngineListeners.add(rulesEngineListener);
    }

    /**
     * Register a list of rules engine listener.
     *
     * @param rulesEngineListeners to register
     */
    public void registerRulesEngineListeners(List<RulesEngineListener> rulesEngineListeners) {
        this.rulesEngineListeners.addAll(rulesEngineListeners);
    }
}