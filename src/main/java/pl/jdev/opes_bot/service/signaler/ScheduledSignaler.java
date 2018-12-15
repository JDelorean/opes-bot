package pl.jdev.opes_bot.service.signaler;

import org.springframework.stereotype.Service;
import pl.jdev.opes_commons.rest.message.event.Event;

@Service
public class ScheduledSignaler implements ScheduleBasedProducer {
    @Override
    public void send(Event event) {
        System.out.println("scheduled trololo");
    }
}
