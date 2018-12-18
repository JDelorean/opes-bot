package pl.jdev.opes_bot.controller;

import org.jeasy.rules.api.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/rule")
public class RuleController {

    @Autowired
    private Map<UUID, Rule> rules;

    @PostMapping
    public void createRule(@RequestBody Rule rule) {
        rules.put(UUID.randomUUID(), rule);
    }

    @GetMapping
    @ResponseBody
    public List<Rule> getRules() {
        List<Rule> ruleList = new ArrayList<>();
        rules.forEach((uuid, rule) -> ruleList.add(rule));
        return ruleList;
    }

    @GetMapping("/{ruleId}")
    @ResponseBody
    public Rule getRuleDetails(@PathVariable("ruleId") UUID ruleUUID) {
        return rules.get(ruleUUID);
    }
}
