package sn2.timecraft.networking;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sn2.timecraft.TimeCraft;

public class PacketCraftingDifficulty implements IMessage {

	public int item;
	public float difficulty;

	public PacketCraftingDifficulty() {
	}
	
	public PacketCraftingDifficulty(int item, float difficulty) {
		this.item = item;
		this.difficulty = difficulty;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.item = buf.readInt();
		this.difficulty = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.item);
		buf.writeFloat(this.difficulty);
	}
	
	public static class Handler implements IMessageHandler<PacketCraftingDifficulty, IMessage> {

		@Override
		public IMessage onMessage(PacketCraftingDifficulty message, MessageContext ctx) {
			if(ctx.side == Side.CLIENT) {
				TimeCraft.map.setDifficulty(message.item, message.difficulty);
			}
			return null;
		}

	}
}
