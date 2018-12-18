package pl.jdev.opes_bot.controller;

import org.jeasy.rules.api.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.jdev.opes_bot.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private Map<UUID, Strategy> strategies;
    @Autowired
    private Map<UUID, Rule> rules;

    @PostMapping
    public void createStrategy(@RequestBody Strategy strategy) {
        strategy.disable();
        strategies.put(UUID.randomUUID(), strategy);
    }

    @GetMapping
    @ResponseBody
    public List<Strategy> getStrategies(@RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<Strategy> strategyList = new ArrayList<>();
        strategies.forEach((uuid, strategy) -> strategyList.add(strategy));
        return strategyList;
    }

    @GetMapping("/{strategyId}")
    @ResponseBody
    public Strategy getStrategyDetails(@PathVariable("strategyId") UUID strategyUUID) {
        return strategies.get(strategyUUID);
    }

    @PutMapping("/{strategyId}/enable")
    public void enableStrategy(@PathVariable("strategyId") UUID strategyUUID) {
        strategies.get(strategyUUID).enable();
    }

    @PutMapping("/{strategyId}/disable")
    public void disableStrategy(@PathVariable("strategyId") UUID strategyUUID) {
        strategies.get(strategyUUID).disable();
    }

    @DeleteMapping("/{strategyId}")
    public void deleteStrategy(@PathVariable("strategyId") UUID strategyUUID) {
        strategies.remove(strategyUUID);
    }
}
