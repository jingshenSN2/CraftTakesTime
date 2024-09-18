package sn2.crafttakestime.networking;

import com.google.gson.Gson;
import lombok.Data;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.crafttakestime.common.config.CraftConfig;

import static sn2.crafttakestime.CraftTakesTime.MODID;

@Data
public class PacketCraftConfig implements FabricPacket {

    private static final Logger log = LogManager.getLogger(PacketCraftConfig.class);
    public static final PacketType<PacketCraftConfig> TYPE = PacketType
            .create(new Identifier(MODID), PacketCraftConfig::new);
    public CraftConfig config;

    public PacketCraftConfig(CraftConfig config) {
        this.config = config;
    }

    public PacketCraftConfig(PacketByteBuf buf) {
        Gson gson = new Gson();
        try {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String json = new String(bytes);
            this.config = gson.fromJson(json, CraftConfig.class);
        } catch (Exception e) {
            log.error("Failed to read config from bytes");
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        Gson gson = new Gson();
        try {
            String json = gson.toJson(config);
            buf.writeBytes(json.getBytes());
        } catch (Exception e) {
            log.error("Failed to write config to bytes");
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
