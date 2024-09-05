package sn2.crafttakestime.networking;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.crafttakestime.common.config.CraftConfig;
import sn2.crafttakestime.common.core.CraftManager;

@Data
public class PacketCraftConfig implements IMessage {

    private static final Logger log = LogManager.getLogger(PacketCraftConfig.class);
    public CraftConfig config;

    public PacketCraftConfig() {
    }

    public PacketCraftConfig(CraftConfig config) {
        this.config = config;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Gson gson = new Gson();
        try {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String json = new String(bytes);
            config = gson.fromJson(json, CraftConfig.class);
        } catch (Exception e) {
            log.error("Failed to read config from bytes");
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Gson gson = new Gson();
        try {
            String json = gson.toJson(config);
            buf.writeBytes(json.getBytes());
        } catch (Exception e) {
            log.error("Failed to write config to bytes");
        }
    }

    public static class Handler implements IMessageHandler<PacketCraftConfig, IMessage> {

        @Override
        public IMessage onMessage(PacketCraftConfig message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                CraftManager.getInstance().setConfig(message.config);
            }
            return null;
        }

    }
}
