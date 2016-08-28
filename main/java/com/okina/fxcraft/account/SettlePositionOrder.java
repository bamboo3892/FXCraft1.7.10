package com.okina.fxcraft.account;

import java.util.Calendar;
import java.util.Objects;

import com.okina.fxcraft.rate.RateData;

import net.minecraft.nbt.NBTTagCompound;

public class SettlePositionOrder implements Cloneable {

	public static final SettlePositionOrder NO_INFO = new SettlePositionOrder(Calendar.getInstance(), FXPosition.NO_INFO, 0);
	static{
		NO_INFO.contractDate.setTimeInMillis(0);
	}

	public FXPosition position;
	public Calendar contractDate;
	public double limits;

	private SettlePositionOrder() {}

	public SettlePositionOrder(Calendar date, FXPosition position, double limits) {
		this.position = Objects.requireNonNull(position);
		this.contractDate = date;
		this.limits = limits;
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagCompound positionTag = new NBTTagCompound();
		position.writeToNBT(positionTag);
		tag.setTag("position", positionTag);
		tag.setLong("date", contractDate.getTimeInMillis());
		tag.setDouble("limits", limits);
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagCompound positionTag = tag.getCompoundTag("position");
		position = FXPosition.getFXPositionFromNBT(positionTag);
		contractDate = Calendar.getInstance();
		contractDate.setTimeInMillis(tag.getLong("date"));
		limits = tag.getDouble("limits");
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof SettlePositionOrder && position.equals(((SettlePositionOrder) o).position);
	}

	@Override
	public SettlePositionOrder clone() {
		SettlePositionOrder order = new SettlePositionOrder();
		order.position = position.clone();
		order.contractDate = (Calendar) contractDate.clone();
		order.limits = limits;
		return order;
	}

	public static SettlePositionOrder getSettlePositionOrderFromNBT(NBTTagCompound tag) {
		SettlePositionOrder order = new SettlePositionOrder();
		order.readFromNBT(tag);
		return order;
	}

	public boolean checkConstruct(RateData data) {
		if(contractDate.compareTo(data.calendar) <= 0){
			if(position.askOrBid){
				return limits <= data.open;
			}else{
				return limits >= data.open;
			}
		}
		return false;
	}

}
