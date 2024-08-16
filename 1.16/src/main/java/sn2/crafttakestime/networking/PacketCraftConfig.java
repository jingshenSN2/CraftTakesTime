package sn2.crafttakestime.networking;

import com.google.gson.Gson;
import lombok.Data;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.crafttakestime.config.CraftConfig;
import sn2.crafttakestime.core.CraftManager;

import java.util.function.Supplier;

@Data
public class PacketCraftConfig {

    private static final Logger log = LogManager.getLogger(PacketCraftConfig.class);
    public CraftConfig config;

    public PacketCraftConfig(CraftConfig config) {
        this.config = config;
    }

    public static PacketCraftConfig fromBytes(PacketBuffer buf) {
        Gson gson = new Gson();
        try {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String json = new String(bytes);
            CraftConfig config = gson.fromJson(json, CraftConfig.class);
            return new PacketCraftConfig(config);
        } catch (Exception e) {
            log.error("Failed to read config from bytes");
        }
        return null;
    }

    public void toBytes(PacketBuffer buf) {
        Gson gson = new Gson();
        try {
            String json = gson.toJson(config);
            buf.writeBytes(json.getBytes());
        } catch (Exception e) {
            log.error("Failed to write config to bytes");
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> CraftManager.getInstance().setConfig(config));
        ctx.get().setPacketHandled(true);
    }
}
