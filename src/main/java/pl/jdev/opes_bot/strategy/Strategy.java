package pl.jdev.opes_bot.strategy;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public final class Strategy {
    @NonNull
    private String name;
    @NonNull
    private UUID id;
    @NonNull
    private String description;
    @NonNull
    private Collection<UUID> rules;
    @NonNull
    private StrategyType type;
    private boolean isEnabled;

    public void enable() {
        if (!isEnabled) this.isEnabled = true;
    }

    public void disable() {
        if (isEnabled) this.isEnabled = false;
    }

    public void update(Map<String, Object> updates) {
        if (updates.containsKey("name")) this.name = (String) updates.get("name");
        if (updates.containsKey("description")) this.description = (String) updates.get("description");
//        if (updates.containsKey("rules")) this.rules
    }
}
