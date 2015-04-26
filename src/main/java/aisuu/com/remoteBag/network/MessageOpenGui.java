package aisuu.com.remoteBag.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import cpw.mods.ironchest.client.GUIChest;

public class MessageOpenGui implements IPacket {
	public int currentWindow, id;
	public NBTTagCompound nbt;
	public MessageOpenGui() { }

	public MessageOpenGui(int currentWindow, int id, NBTTagCompound nbt) {
		this.currentWindow = currentWindow;
		this.id = id;
		this.nbt = nbt;
	}

	@Override
	public void readBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.currentWindow = buf.readInt();
		this.nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeInt(currentWindow);
		ByteBufUtils.writeTag(buf, nbt);
	}

	@SideOnly(Side.CLIENT)
	public static void onMessage(MessageOpenGui message, ChannelHandlerContext ctx) {
		if ( FMLCommonHandler.instance().getSide().isClient() ) {

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
				ironchest.setWorldObj(mc.theWorld);
				ironchest.readFromNBT(message.nbt);
				GUIChest screen = GUIChest.GUI.buildGUI(IronChestType.values()[message.id], player.inventory, ironchest);

				if ( screen != null ) {
					mc.displayGuiScreen(screen);
					player.openContainer.windowId = message.currentWindow;

				}
			}
		}
	}
}
