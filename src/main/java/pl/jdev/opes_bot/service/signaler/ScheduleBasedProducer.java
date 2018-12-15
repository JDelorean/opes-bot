package pl.jdev.opes_bot.service.signaler;

import org.springframework.scheduling.annotation.Scheduled;
import pl.jdev.opes_commons.rest.message.event.Event;

public interface ScheduleBasedProducer extends Producer {
    @Override
    @Scheduled
    void send(Event event);
}
