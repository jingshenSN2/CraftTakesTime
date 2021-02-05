package sn2.timecraft.networking;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sn2.timecraft.TimeCraft;

public class PacketCraftingDifficulty implements IMessage {

	public int item;
	public int difficulty;

	public PacketCraftingDifficulty() {
	}
	
	public PacketCraftingDifficulty(int item, int difficulty) {
		this.item = item;
		this.difficulty = difficulty;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.item = buf.readInt();
		this.difficulty = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.item);
		buf.writeInt(this.difficulty);
	}
	
	public static class Handler implements IMessageHandler<PacketCraftingDifficulty, IMessage> {

		@Override
		public IMessage onMessage(PacketCraftingDifficulty message, MessageContext ctx) {
			TimeCraft.map.setDifficulty(message.item, message.difficulty);
			return null;
		}

	}

}
