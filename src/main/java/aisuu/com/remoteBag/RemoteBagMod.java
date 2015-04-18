package aisuu.com.remoteBag;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import aisuu.com.remoteBag.item.ItemRemoteBag;
import aisuu.com.remoteBag.item.ItemRemoteEnderBag;
import aisuu.com.remoteBag.network.PacketHandler;
import aisuu.com.remoteBag.util.Pos;
import aisuu.com.remoteBag.util.Util;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.ironchest.BlockIronChest;
import cpw.mods.ironchest.ContainerIronChest;

@Mod(modid = RemoteBagMod.MOD_ID, name = "Remote Bag Mod", version = "1.2.1", dependencies = "after:IronChest")
public final class RemoteBagMod {
    public static final String MOD_ID = "remote_bag";

    @Instance(MOD_ID)
    public static RemoteBagMod instance;

    public static Item remoteEnderBag;
    public static Item remoteBag;

    /** IronChestが導入されているか */
    public static boolean isLoadedIronChest = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	MinecraftForge.EVENT_BUS.register(this);


        remoteEnderBag = new ItemRemoteEnderBag();
        remoteBag = new ItemRemoteBag();
        GameRegistry.registerItem(remoteEnderBag, "enderremotebag");
        GameRegistry.registerItem(remoteBag, "remotebag");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.addRecipe( new ItemStack( remoteEnderBag ),
                new Object[] {  " # ",
                                "#$#",
                                " # ",
                                Character.valueOf('#'), Items.leather,
                                Character.valueOf('$'), Blocks.ender_chest } );

        GameRegistry.addRecipe(new ItemStack(remoteBag),
                new Object[] {  " # ",
                                "$&$",
                                " $ ",
                                Character.valueOf('#'), Items.redstone,
                                Character.valueOf('&'), new ItemStack(Blocks.planks, 1, 32767),
                                Character.valueOf('$'), Items.leather   } );

        // NBT初期化
        GameRegistry.addShapelessRecipe(new ItemStack(remoteBag), remoteBag);

    }

    /**
     * IronChestが導入されているかどうかの判定を行う。
     * @param event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.isLoadedIronChest = Loader.isModLoaded("IronChest");
        if ( isLoadedIronChest ) {
        	PacketHandler.init();
        }
    }

    /**
     * Guiを開けるかどうかの判定。距離で判定されると困るため。
     *
     *
     * @param event
     */
    @SubscribeEvent
    public void playerOpenContainer(PlayerOpenContainerEvent event) {
    	ItemStack currentStack = event.entityPlayer.getCurrentEquippedItem();
    	Container openContainer = event.entityPlayer.openContainer;

    	if ( ( openContainer instanceof ContainerChest || ( isLoadedIronChest && openContainer instanceof ContainerIronChest ) ) &&
    			currentStack != null && Util.isItemEqual(currentStack.getItem(), remoteBag) && Pos.isSetedPosOnNBT(currentStack.getTagCompound()) ) {
    		NBTTagCompound nbt = currentStack.getTagCompound();
    		Block block = Pos.getPosOnNBT(nbt, event.entityPlayer.worldObj).getBlock();
    		if ( block != null && ( block instanceof BlockChest || ( isLoadedIronChest && block instanceof BlockIronChest ) ) ) {
    			event.setResult(Result.ALLOW);
    		}
    	}
    }
}
