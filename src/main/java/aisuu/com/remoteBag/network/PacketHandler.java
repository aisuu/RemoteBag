package aisuu.com.remoteBag.network;

import aisuu.com.remoteBag.RemoteBagMod;
import aisuu.com.remoteBag.network.message.MessageOpenGui;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(RemoteBagMod.MOD_ID);

	public static void init() {
		INSTANCE.registerMessage(MessageOpenGui.class, MessageOpenGui.class, 0, Side.CLIENT);
	}
}
