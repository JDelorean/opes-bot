package pl.jdev.opes_bot.rules;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Component
@Scope("prototype")
public class RulesSession {
    @Autowired
    private RulesEngine rulesEngine;
    @Autowired
    private Map<UUID, Rule> rulesMap;
    private Rules rules;
    private Facts facts;
    private UUID id;

    public RulesSession() {
        this.id = UUID.randomUUID();
        this.rules = new Rules();
        this.facts = new Facts();
    }

    public void run() {
        rulesEngine.fire(rules, facts);
    }

    public void applyRules(Collection<UUID> ruleIDs) {
        for (UUID ruleID : ruleIDs) {
            rules.register(rulesMap.get(ruleID));
        }
    }

    public Map getSessionRules() {
        return rulesMap;
    }

    public void setSessionRules(Map<UUID, Rule> rulesMap) {
        this.rulesMap = rulesMap;
    }

    public Map getSessionFacts() {
        return facts.asMap();
    }

    public void setSessionFacts(Map<String, Object> factsMap) {
        factsMap.forEach((factName, factValue) -> facts.put(factName, factValue));
    }
}