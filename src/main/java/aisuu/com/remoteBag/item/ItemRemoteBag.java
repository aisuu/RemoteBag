package aisuu.com.remoteBag.item;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import aisuu.com.remoteBag.RemoteBagMod;
import aisuu.com.remoteBag.util.Pos;
import aisuu.com.remoteBag.util.Util;
import cpw.mods.ironchest.BlockIronChest;
import cpw.mods.ironchest.TileEntityIronChest;

public final class ItemRemoteBag extends Item {
	/**
     * コンストラクタ
     *
     * NBTを使うのでスタックできないようにする。
     */
    public ItemRemoteBag() {
        this.setUnlocalizedName("ItemRemoteBag");
        this.setTextureName(RemoteBagMod.MOD_ID + ":chest_bag");
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setMaxStackSize( 1 );
    }

	/**
     * 座標が設定されていればアイコンが暗くなる。
     */
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if ( Pos.isSetedPosOnNBT(stack.getTagCompound()) ) {
            return Color.GRAY.getRGB();
        }
        return super.getColorFromItemStack(stack, pass);
    }

    /**
     * 座標とディメンションの設定
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn,
            World worldIn, int x, int y, int z, int side, float hitX,
            float hitY, float hitZ) {
        if ( worldIn.isRemote ) {
            return true;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if ( Pos.isSetedPosOnNBT(nbt) ) {
        	this.onItemRightClick(stack, worldIn, playerIn);
        } else if ( nbt == null ) {
        	nbt = new NBTTagCompound();
        } else if (nbt.hasKey( Pos.POS_X ) || nbt.hasKey( Pos.POS_Y ) || nbt.hasKey( Pos.POS_Z ) || nbt.hasKey(Pos.DIMENSION)) {
        	nbt = new NBTTagCompound();
        }

        Block state = worldIn.getBlock(x, y, z);
        if ( (RemoteBagMod.isLoadedIronChest && state instanceof BlockIronChest) || state instanceof BlockChest ) {
        	Pos pos = new Pos(worldIn, x, y, z);
            stack.setTagCompound( pos.getNBT(nbt) );
        }
        return true;
    }
    /**
     * 座標が設定されていて、かつその座標にチェストがあればGUIを開く。
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
    	if ( !worldIn.isRemote ) {
    		NBTTagCompound nbt = itemStackIn.getTagCompound();
    		if ( Pos.isSetedPosOnNBT(nbt) ) {
    			if ( worldIn.provider.dimensionId != nbt.getInteger(Pos.DIMENSION) ) {
    				return itemStackIn;
    			}
    			Pos pos = Pos.getPosOnNBT(nbt);
    			Block block = pos.getBlock();
    			TileEntity tile = pos.getTileEntity();
    			if ( tile == null || block == null ) {
    				return itemStackIn ;
    			}


    			if ( RemoteBagMod.isLoadedIronChest && ( block instanceof BlockIronChest && tile instanceof TileEntityIronChest ) ) {
    				TileEntityIronChest chest = ((TileEntityIronChest)tile);
    				if ( playerIn instanceof EntityPlayerMP ) {
    					Util.openGui( chest.getType().ordinal(), ((EntityPlayerMP)playerIn), worldIn, pos);
    				}
    			} else if ( block instanceof BlockChest && tile instanceof TileEntityChest ) {
    				playerIn.displayGUIChest( ((BlockChest)block).func_149951_m(worldIn, pos.getX(), pos.getY(), pos.getZ()));

    			}
    		}
    	}
    	return itemStackIn;
    }

    /**
     * NBTに座標が設定されていれば、座標を表示する。
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        NBTTagCompound nbt = stack.getTagCompound();
        if ( Pos.isSetedPosOnNBT( nbt ) ) {
            int x = nbt.getInteger( Pos.POS_X );
            int y = nbt.getInteger( Pos.POS_Y );
            int z = nbt.getInteger( Pos.POS_Z );
            int dimID = nbt.getInteger( Pos.DIMENSION );

            tooltip.add( WorldProvider.getProviderForDimension(dimID).getDimensionName());
            tooltip.add( x + " : " + y + " : " + z );
        }

    }
}
