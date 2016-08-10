package com.okina.fxcraft.account;

import net.minecraft.nbt.NBTTagCompound;

public class SettlePositionOrder {

	public FXPosition position;
	public double limits;

	private SettlePositionOrder() {}

	public SettlePositionOrder(FXPosition position, double limits) {
		this.position = position;
		this.limits = limits;
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagCompound positionTag = new NBTTagCompound();
		position.writeToNBT(positionTag);
		tag.setTag("position", positionTag);
		tag.setDouble("limits", limits);
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagCompound positionTag = tag.getCompoundTag("position");
		position = FXPosition.getFXPositionFromNBT(positionTag);
		limits = tag.getDouble("limits");
	}

	public SettlePositionOrder clone() {
		SettlePositionOrder order = new SettlePositionOrder();
		order.position = position.clone();
		order.limits = limits;
		return order;
	}

	public static SettlePositionOrder getSettlePositionOrderFromNBT(NBTTagCompound tag) {
		SettlePositionOrder order = new SettlePositionOrder();
		order.readFromNBT(tag);
		return order;
	}

}
