package aisuu.com.remoteBag.network;

import io.netty.buffer.ByteBuf;

public interface IPacket {
	public void readBytes(ByteBuf buf);
	public void writeBytes(ByteBuf buf);
}
