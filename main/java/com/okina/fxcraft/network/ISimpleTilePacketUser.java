package com.okina.fxcraft.network;

import com.okina.fxcraft.utils.Position;

public interface ISimpleTilePacketUser {

	SimpleTilePacket getPacket(PacketType type);

	void processCommand(PacketType type, Object value);

	Position getPosition();

}
