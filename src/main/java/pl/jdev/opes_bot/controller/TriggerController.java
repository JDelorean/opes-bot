package pl.jdev.opes_bot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jdev.opes_bot.service.signaler.PricingBasedProducer;
import pl.jdev.opes_bot.service.signaler.ScheduleBasedProducer;

import java.util.List;

@RestController
@RequestMapping("/trigger")
public class TriggerController {
    @Autowired
    private List<PricingBasedProducer> eventBasedSignalProducers;
    @Autowired
    private List<ScheduleBasedProducer> scheduleBasedProducers;

    @PostMapping
    public void eventBasedTrigger(@RequestHeader(name = "Event_Type", required = false) final String eventType) {
        if (eventType.equalsIgnoreCase("pricing")) {
            eventBasedSignalProducers.parallelStream()
                    .forEach(producer -> producer.trigger());
        } else if (eventType.equalsIgnoreCase("other")) {

        }
    }
}
