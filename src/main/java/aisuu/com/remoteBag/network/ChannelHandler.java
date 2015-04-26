package aisuu.com.remoteBag.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket> {
	public ChannelHandler() {
		this.addDiscriminator(0, MessageOpenGui.class);
	}
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IPacket msg,
			ByteBuf target) throws Exception {
		msg.writeBytes(target);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf source,
			IPacket msg) {
		msg.readBytes(source);
		if ( msg instanceof MessageOpenGui ) {
			MessageOpenGui.onMessage((MessageOpenGui)msg, ctx);
		}
	}

}
