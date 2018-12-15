package pl.jdev.opes_bot.config;

import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import pl.jdev.opes_bot.rules.ConditionalRulesEngine;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class RulesConfig {

    @Value("${rules.path:rules/}")
    private String rulesPath;

    @Bean
    @Scope("prototype")
    public RulesEngine rulesEngine() throws IOException {
        return new ConditionalRulesEngine();
    }

//    @Bean
//    @Scope("prototype")
//    public RulesSession rulesSession() {
//        return new RulesSession();
//    }

    @Bean
    public Map<UUID, Rule> rules() throws IOException {
        Map rulesMap = new HashMap();
        for (Resource file : getRuleFiles()) {
            Rules rules = MVELRuleFactory.createRulesFrom(new FileReader(file.getFile()));
            rules.iterator().forEachRemaining(rule -> rulesMap.put(UUID.fromString(rule.getName()), rule));
        }
        return rulesMap;
    }

    private Resource[] getRuleFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:" + rulesPath + "**/*rules.yml");
    }
}