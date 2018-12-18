package pl.jdev.opes_bot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import pl.jdev.opes_bot.strategy.Strategy;
import pl.jdev.opes_bot.strategy.StrategyFileDeserializer;
import pl.jdev.opes_bot.strategy.StrategyPolicy;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static pl.jdev.opes_bot.strategy.StrategyPolicy.DISABLE_ALL_ON_START;

@Configuration
public class StrategyConfig {

    @Value("${strategies.path:strategies/}")
    private String strategiesPath;

    @Value("${strategies.policy:DISABLE_ALL_ON_START}")
    private StrategyPolicy strategiesPolicy;


    @Bean
    public Map<UUID, Strategy> strategies() throws IOException {
        Map<UUID, Strategy> strategies = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Strategy.class, new StrategyFileDeserializer());
        mapper.registerModule(module);
        for (Resource file : getStrategyFiles()) {
            Strategy strategy = mapper.readValue(new FileReader(file.getFile()), Strategy.class);
            setEnabledByPolicy(strategy);
            strategies.put(strategy.getId(), strategy);
        }
        return strategies;
    }

    private void setEnabledByPolicy(Strategy strategy) {
        if (strategiesPolicy.equals(DISABLE_ALL_ON_START)) {
            strategy.disable();
        } else {
            strategy.enable();
        }
    }

    private Resource[] getStrategyFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:" + strategiesPath + "**/*.yml");
    }
}
