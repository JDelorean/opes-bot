package pl.jdev.opes_bot.strategy;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StrategyFileDeserializer extends StdDeserializer<Strategy> {

    public StrategyFileDeserializer() {
        super(Strategy.class);
    }

    @Override
    public Strategy deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        UUID id = UUID.fromString(node.get("id").asText());
        String name = node.get("name").asText();
        String description = node.get("description").asText();
        StrategyType type = StrategyType.valueOf(node.get("type").asText().toUpperCase());
        List<UUID> rules = new ArrayList<>();
        node.get("rules").elements().forEachRemaining(rule -> rules.add(UUID.fromString(rule.asText())));
        return new Strategy(name, id, description, rules, type);
    }
}
