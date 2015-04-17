package aisuu.com.remoteBag.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.ironchest.TileEntityIronChest;

public final class MessageOpenGui implements IMessage,
IMessageHandler<MessageOpenGui, IMessage> {
	public int currentWindow, x, y, z, id;
	public MessageOpenGui() { }

	public MessageOpenGui(int currentWindowId, int x, int y, int z, int id) {
		this.currentWindow = currentWindowId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	@Override
	public IMessage onMessage(MessageOpenGui message, MessageContext ctx) {
		if ( ctx.side.isClient() ) {
			Minecraft mc = Minecraft.getMinecraft();

			EntityClientPlayerMP player = mc.thePlayer;
			WorldClient world = mc.theWorld;
			TileEntity ironchest = world.getTileEntity(message.x, message.y, message.z);
			if ( !(ironchest != null && ironchest instanceof TileEntityIronChest) ) {
				return null;
			}

			Object c = NetworkRegistry.INSTANCE.getLocalGuiContainer(FMLCommonHandler.instance().findContainerFor("IronChest"), player, message.id, world, message.x, message.y, message.z);
			if ( c != null && c instanceof GuiScreen ) {
				mc.displayGuiScreen((GuiScreen) c);
				player.openContainer.windowId = message.currentWindow;
			}
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentWindow = buf.readInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(currentWindow);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}



}
