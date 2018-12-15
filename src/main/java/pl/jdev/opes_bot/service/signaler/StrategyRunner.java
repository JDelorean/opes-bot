package pl.jdev.opes_bot.service.signaler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jdev.opes_bot.rules.RulesSession;
import pl.jdev.opes_bot.strategy.Strategy;
import pl.jdev.opes_commons.rest.message.event.Event;

import java.util.Collection;
import java.util.Map;

@Component
public class StrategyRunner implements PricingBasedProducer {

    @Autowired
    private Collection<Strategy> strategies;
    @Autowired
    RulesSession rulesSession;

    @Override
    public void trigger() {
        strategies.parallelStream()
                .forEach(strategy -> {
                    rulesSession.applyRules(strategy.getRules());
                    rulesSession.setSessionFacts(Map.of("prevShortMA", 1, "prevLongMA", 2, "currShortMA", 2, "currLongMA", 1));
                    rulesSession.run();
                });
    }

    @Override
    public void send(Event event) {

    }
}
