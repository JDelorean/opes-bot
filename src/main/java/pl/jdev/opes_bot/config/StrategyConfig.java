package pl.jdev.opes_bot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import pl.jdev.opes_bot.strategy.Strategy;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class StrategyConfig {

    @Value("${strategies.path:strategies/}")
    private String strategiesPath;

    @Bean
    public Collection<Strategy> strategies() throws IOException {
        List<Strategy> strategies = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        for (Resource file : getStrategyFiles()) {
            Strategy strategy = mapper.readValue(new FileReader(file.getFile()), Strategy.class);
            strategies.add(strategy);
        }
        return strategies;
    }

    private Resource[] getStrategyFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:" + strategiesPath + "**/*.yml");
    }
}
