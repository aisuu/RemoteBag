package aisuu.com.remoteBag.chunk;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import aisuu.com.remoteBag.RemoteBagMod;

/**
 * チャンクロードのメイン
 *
 * NBTにidを記憶させることで、
 * インスタンスを個別に持たせるみたいな感じ
 *
 */
public class ChunkLoading {
	public final int id;
	public int prevSlot;
	public ForgeChunkManager.Ticket ticket;

	private static Map<Integer, ChunkLoading> instanceMap;
	static {
		instanceMap = new HashMap<Integer, ChunkLoading>();
	}

	public ChunkLoading(int id) {
		this.id = id;
		this.prevSlot = -1;
		instanceMap.put(this.id, this);
	}

	public static ChunkLoading getInstance(int id) {
		if ( instanceMap.containsKey(id) ) {
			ChunkLoading i = instanceMap.get(id);
			if ( i != null ) {
				return i;
			}
		}
		return new ChunkLoading(getId());
	}

	public static int getId() {
		int i = 0;
		while(true) {
			if ( !instanceMap.containsKey(i)) {
				return i;
			}
			i++;
		}
	}
	public void startChunkLoading(World world, int x, int z) {
		if ( this.ticket == null ) {
			ForgeChunkManager.Ticket t = ForgeChunkManager.requestTicket(RemoteBagMod.instance, world, ForgeChunkManager.Type.NORMAL);
			if ( t != null ) {
				ChunkCoordIntPair coord = world.getChunkFromBlockCoords(x, z).getChunkCoordIntPair();
				this.forceChunk(t, coord);
			}
		}
	}

	public void forceChunk(ForgeChunkManager.Ticket t, ChunkCoordIntPair coord) {
		this.stopChunkLoading();
		this.ticket = t;
		ticket.getModData().setInteger("LoaderID", this.id);
		ticket.getModData().setInteger("coordX", coord.chunkXPos);
		ticket.getModData().setInteger("coordZ", coord.chunkZPos);
		ForgeChunkManager.forceChunk(this.ticket, coord);
	}

	public void stopChunkLoading() {
		if ( this.ticket != null ) {
			ForgeChunkManager.releaseTicket(this.ticket);
			this.ticket = null;
		}
	}

}
