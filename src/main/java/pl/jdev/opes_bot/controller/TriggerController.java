package pl.jdev.opes_bot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jdev.opes_bot.service.signaler.EventBasedSignalProducer;
import pl.jdev.opes_commons.rest.message.Event;

import java.util.Collection;

@RestController
@RequestMapping("/trigger")
public class TriggerController {
    @PostMapping
    public void eventBasedTrigger(Event event, @Autowired Collection<EventBasedSignalProducer> eventBasedSignalProducers) {
        eventBasedSignalProducers.parallelStream()
                .forEach(producer -> producer.trigger(event));
    }
}
