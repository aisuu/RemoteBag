package aisuu.com.remoteBag.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aisuu.com.remoteBag.RemoteBagMod;
import aisuu.com.remoteBag.network.MessageOpenGui;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public final class Util {
	public static World getWorld( World world, EntityPlayerMP player, int dim ) {
		World result = world;

		if ( result.provider.dimensionId != dim ) {
			result = player.mcServer.worldServerForDimension(dim);
		}
		return result;
	}

	public static boolean isItemEqual(Item item1, Item item2) {
		return item1 != null && item2 != null ? item1.getUnlocalizedName().equals(item2.getUnlocalizedName()) : false;
	}

	public static void openGui( int ID, EntityPlayerMP player, World world, Pos pos) {
        if (player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
        player.getNextWindowId();
        NBTTagCompound nbt = new NBTTagCompound();
        pos.getTileEntity().writeToNBT(nbt);
        RemoteBagMod.instance.sendTo(new MessageOpenGui( player.currentWindowId, ID, nbt), player);
        player.openContainer = NetworkRegistry.INSTANCE.getRemoteGuiContainer(FMLCommonHandler.instance().findContainerFor("IronChest"), player, ID, world, pos.getX(), pos.getY(), pos.getZ());
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
	}

	public static boolean isEqualCoordTile(TileEntity tile, Pos pos) {
		return tile.xCoord == pos.getX() && tile.yCoord == pos.getY() && tile.zCoord == pos.getZ();
	}

	public static void writeString(String str, ByteBuf buf) {
		char[] c = str.toCharArray();
		buf.writeInt(c.length);
		for ( char ch : c) {
			buf.writeChar(ch);
		}
	}

	public static String readString(ByteBuf buf) {
		int length = buf.readInt();
		char[] chars = new char[length];
		for ( int i = 0; i < length; i++ ) {
			chars[i] = buf.readChar();
		}
		return String.valueOf(chars);
	}
}
