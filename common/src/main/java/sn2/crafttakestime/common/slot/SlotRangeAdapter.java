package sn2.crafttakestime.common.slot;

import com.google.gson.*;

import java.lang.reflect.Type;

public class SlotRangeAdapter implements JsonSerializer<SlotRange>, JsonDeserializer<SlotRange> {

    @Override
    public JsonElement serialize(SlotRange src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.toString());
    }

    @Override
    public SlotRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return SlotRange.fromString(json.getAsString());
    }
}
