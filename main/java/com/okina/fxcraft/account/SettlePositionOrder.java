package com.okina.fxcraft.account;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;

public class SettlePositionOrder implements Cloneable {

	public static final SettlePositionOrder NO_INFO = new SettlePositionOrder(FXPosition.NO_INFO, 0);

	public static final int FIELD_DATE = 0;
	public static final int FIELD_PAIR = 1;
	public static final int FIELD_LOT = 2;
	public static final int FIELD_DEPOSIT = 3;
	public static final int FIELD_ASK_BID = 4;
	public static final int FIELD_RATE = 5;
	public static final int FIELD_ID = 6;
	public static final int FIELD_LIMITS = 7;

	public FXPosition position;
	public double limits;

	private SettlePositionOrder() {}

	public SettlePositionOrder(FXPosition position, double limits) {
		this.position = Objects.requireNonNull(position);
		this.limits = limits;
	}

	public String getField(int field) {
		switch (field) {
		case FIELD_DATE:
			return String.valueOf(position.contractDate);
		case FIELD_PAIR:
			return String.valueOf(position.currencyPair);
		case FIELD_LOT:
			return String.valueOf(position.lot);
		case FIELD_DEPOSIT:
			return String.valueOf(position.depositLot);
		case FIELD_ASK_BID:
			return String.valueOf(position.askOrBid);
		case FIELD_RATE:
			return String.valueOf(position.contractRate);
		case FIELD_ID:
			return String.valueOf(position.positionID);
		case FIELD_LIMITS:
			return String.valueOf(limits);
		default:
			return null;
		}
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

	@Override
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
