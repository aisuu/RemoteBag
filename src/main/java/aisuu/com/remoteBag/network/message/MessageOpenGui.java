package aisuu.com.remoteBag.network.message;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import aisuu.com.remoteBag.RemoteBagMod;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import cpw.mods.ironchest.client.GUIChest;

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
		if ( ctx.side.isClient() && RemoteBagMod.isLoadedIronChest ) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityClientPlayerMP player = mc.thePlayer;
			TileEntityIronChest ironchest = null;
			try {
				Constructor<TileEntityIronChest> tileConst = TileEntityIronChest.class.getDeclaredConstructor(IronChestType.class);
				tileConst.setAccessible(true);
				ironchest = tileConst.newInstance(IronChestType.values()[message.id]);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if ( ironchest != null ) {
				GUIChest screen = GUIChest.GUI.buildGUI(IronChestType.values()[message.id], player.inventory, ironchest);

				if ( screen != null ) {
					mc.displayGuiScreen(screen);
					player.openContainer.windowId = message.currentWindow;

				}
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
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(currentWindow);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}



}
