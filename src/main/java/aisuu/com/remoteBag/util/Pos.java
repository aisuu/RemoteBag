package aisuu.com.remoteBag.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public final class Pos {
	public static final String POS_X = "targetX", POS_Y = "targetY", POS_Z = "targetZ", DIMENSION = "dimensionID";
	protected World world;
	protected int x, y, z;
	public Pos( World world, int x, int y, int z ) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getDistanceEntity(Entity entity) {
		return entity.getDistance(x, y, z);
	}

	public NBTTagCompound getNBT() {
		return this.getNBT(new NBTTagCompound());
	}

	public NBTTagCompound getNBT(NBTTagCompound nbt) {
		nbt.setInteger(POS_X, x);
		nbt.setInteger(POS_Y, y);
		nbt.setInteger(POS_Z, z);
		nbt.setInteger(DIMENSION, this.world.provider.dimensionId);
		return nbt;
	}

	public static Pos getPosOnNBT(NBTTagCompound nbt) {
		return getPosOnNBT(nbt, DimensionManager.getWorld(nbt.getInteger(DIMENSION)));
	}


	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Pos && ((Pos) obj).getX() == this.getX() && ((Pos) obj).getY() == this.getY() && ((Pos) obj).getZ() == this.getZ() && ((Pos) obj).getWorld().provider.dimensionId == this.getWorld().provider.dimensionId;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Pos(world, x, y, z);
	}

	public static Pos getPosOnNBT(NBTTagCompound nbt, World world) {
		return new Pos(world, nbt.getInteger(POS_X), nbt.getInteger(POS_Y), nbt.getInteger(POS_Z));
	}

	public Block getBlock() {
		return this.world.getBlock(this.x, this.y, this.z);
	}

	public TileEntity getTileEntity() {
		return this.world.getTileEntity(this.x, this.y, this.z);
	}

	public static boolean isSetedPosOnNBT(NBTTagCompound nbt) {
		return nbt != null && nbt.hasKey(POS_X) && nbt.hasKey(POS_Y) && nbt.hasKey(POS_Z) && nbt.hasKey(DIMENSION);
	}

	public World getWorld() {
		return this.world;
	}

	public void addPos(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}


}
