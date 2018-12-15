package pl.jdev.opes_bot.service.signaler;

import pl.jdev.opes_commons.rest.message.event.Event;

public interface Producer {
    void send(Event event);
}
