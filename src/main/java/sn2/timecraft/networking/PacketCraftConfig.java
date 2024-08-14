package sn2.timecraft.networking;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.timecraft.config.CraftConfig;
import sn2.timecraft.core.CraftManager;

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
        ObjectMapper mapper = new ObjectMapper();
        try {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String json = new String(bytes);
            config = mapper.readValue(json, CraftConfig.class);
        } catch (Exception e) {
            log.error("Failed to read config from bytes");
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(config);
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
