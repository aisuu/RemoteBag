package hakase.com.remoteBag.item;

import hakase.com.remoteBag.RemoteBagMod;
import hakase.com.remoteBag.chunk.ChunkLoading;
import hakase.com.remoteBag.util.Pos;
import hakase.com.remoteBag.util.Util;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
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

	@Override
	public void onUpdate(ItemStack stack, World world,
			Entity entity, int slot, boolean flag) {
		if ( stack != null && entity != null && entity instanceof EntityPlayer ) {
			EntityPlayer player = (EntityPlayer)entity;
			NBTTagCompound nbt = stack.getTagCompound();
			if ( !Pos.isSetedPosOnNBT(nbt) ) {
				return ;
			}

			ChunkLoading chunkLoader = getChunkLoading(nbt);
			int currentItem = player.inventory.currentItem;
			if ( currentItem == chunkLoader.prevSlot ) {
				return ;
			}
			chunkLoader.prevSlot = currentItem;
			if ( currentItem == slot ) {
				chunkLoader.startChunkLoading(world, nbt.getInteger(Pos.POS_X), nbt.getInteger(Pos.POS_Z));
			} else {
				chunkLoader.stopChunkLoading();
			}
		}
	}

	private ChunkLoading getChunkLoading(NBTTagCompound nbt) {
		ChunkLoading chunkLoader;
		if ( nbt.hasKey("ChunkID") ) {
			chunkLoader = ChunkLoading.getInstance(nbt.getInteger("ChunkID"));
		} else {
			chunkLoader = new ChunkLoading(ChunkLoading.getId());
		}
		nbt.setInteger("ChunkID", chunkLoader.id);
		return chunkLoader;
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
        if ( nbt == null ) {
            nbt = new NBTTagCompound();
            stack.setTagCompound( nbt );
        } else if ( Pos.isSetedPosOnNBT( nbt ) ) {
            return true;
        } else if ( nbt.hasKey( Pos.POS_X ) || nbt.hasKey( Pos.POS_Y ) || nbt.hasKey( Pos.POS_Z ) || nbt.hasKey(Pos.DIMENSION) ) {
            nbt = new NBTTagCompound();
            stack.setTagCompound( nbt );
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


    			if ( RemoteBagMod.isLoadedIronChest && block instanceof BlockIronChest && tile instanceof TileEntityIronChest ) {
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
            int dimID = nbt.getInteger(Pos.DIMENSION);

            tooltip.add(WorldProvider.getProviderForDimension(dimID).getDimensionName());
            tooltip.add("[ X : " + x + ", Y : " + y + ", Z : " + z + "]");
        }

    }
}
