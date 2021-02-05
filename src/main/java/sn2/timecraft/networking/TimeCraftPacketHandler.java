package sn2.timecraft.networking;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import sn2.timecraft.Constants;

public class TimeCraftPacketHandler {

	private static int packetId = 0;
	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE
			.newSimpleChannel(Constants.DIFFICULTY_TABLE_PACKET_ID.toString());

	public static int nextID() {
		return packetId++;
	}

	public static void registerMessage() {
		INSTANCE.registerMessage(PacketCraftingDifficulty.Handler.class, PacketCraftingDifficulty.class, nextID(),
				Side.CLIENT);
	}
}
