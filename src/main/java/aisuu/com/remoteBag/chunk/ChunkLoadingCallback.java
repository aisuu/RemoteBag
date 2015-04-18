package aisuu.com.remoteBag.chunk;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public final class ChunkLoadingCallback implements LoadingCallback {

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		for ( ForgeChunkManager.Ticket tic : tickets ) {
			NBTTagCompound nbt = tic.getModData();
			ChunkLoading load = ChunkLoading.getInstance(nbt.getInteger("LoaderID"));
			Chunk chunk = world.getChunkFromChunkCoords(nbt.getInteger("coordX"), nbt.getInteger("coordZ"));

			load.forceChunk(tic, new ChunkCoordIntPair(nbt.getInteger("coordX"), nbt.getInteger("coordZ")));
		}

	}

}
