package aisuu.com.remoteBag.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import aisuu.com.remoteBag.RemoteBagMod;

public final class ItemRemoteEnderBag extends Item {

    public ItemRemoteEnderBag() {
        this.setUnlocalizedName( "ItemRemoteEnderBag" );
        this.setCreativeTab( CreativeTabs.tabMisc );
        this.setTextureName(RemoteBagMod.MOD_ID + ":ender_bag");
        this.setMaxStackSize( 1 );
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn,
            EntityPlayer playerIn) {
        InventoryEnderChest enderChestInv = playerIn.getInventoryEnderChest();
        if(!worldIn.isRemote && enderChestInv != null)
            playerIn.displayGUIChest(enderChestInv);
        return itemStackIn;
    }
}
