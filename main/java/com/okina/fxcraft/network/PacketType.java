package com.okina.fxcraft.network;

import net.minecraft.nbt.NBTTagCompound;

public enum PacketType {

	UNKNOWN(0, null),

	ACCOUNT_CHECK(1, String.class),

	ACCOUNT_MAKE(2, NBTTagCompound.class),

	ACCOUNT_LOGIN(3, NBTTagCompound.class),

	ACCOUNT_LOGOUT(4, Integer.class),

	ACCOUNT_DISPOSE(5, NBTTagCompound.class),

	ACCOUNT_REALIZE(6, NBTTagCompound.class),

	ACCOUNT_LIMIT_RELEASE(7, NBTTagCompound.class),

	ACCOUNT_REWARD(8, NBTTagCompound.class),

	ACCOUNT_UPDATE(9, NBTTagCompound.class),

	ACCOUNT_REQUEST(10, NBTTagCompound.class),

	FX_GET_POSITION(11, NBTTagCompound.class),

	FX_SETTLE_POSITION(12, NBTTagCompound.class),

	FX_ORDER_GET_POSITION(13, NBTTagCompound.class),

	FX_ORDER_SETTLE_POSITION(14, NBTTagCompound.class),

	FX_DELETE_GET_ORDER(15, NBTTagCompound.class),

	FX_DELETE_SETTLE_ORDER(16, NBTTagCompound.class),

	MESSAGE(17, String.class);

	public final int id;
	public final Class<?> valueClass;

	private PacketType(int id, Class<?> valueClass) {
		this.id = id;
		this.valueClass = valueClass;
	}

	public static PacketType getFromId(int id) {
		for (PacketType type : PacketType.values()){
			if(type.id == id) return type;
		}
		return UNKNOWN;
	}

}

/*for 1.8
@Override
public IMessage onMessage(PacketClockPulser message, MessageContext ctx) {
    IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
    mainThread.addScheduledTask(new Runnable() {
        @Override
        public void run() {
            System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        }
    });
    return null; // no response in this case
}
*/