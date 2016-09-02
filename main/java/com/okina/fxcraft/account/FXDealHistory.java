package com.okina.fxcraft.account;

import java.util.Calendar;

import net.minecraft.nbt.NBTTagCompound;

public class FXDealHistory {

	public Calendar date;
	public String dealType;
	public boolean isLimits;
	public String pair;
	public double lot;
	public double deposit;
	public double rate;
	public double gain;//ask, bid : -1

	private FXDealHistory() {}

	public FXDealHistory(Calendar date, String dealType, boolean isLimits, String pair, double lot, double deposit, double rate, double gain) {
		this.date = date;
		this.dealType = dealType;
		this.isLimits = isLimits;
		this.pair = pair;
		this.lot = lot;
		this.deposit = deposit;
		this.rate = rate;
		this.gain = gain;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong("date", date.getTimeInMillis());
		tag.setString("type", dealType);
		tag.setBoolean("isLimits", isLimits);
		tag.setString("pair", pair);
		tag.setDouble("lot", lot);
		tag.setDouble("deposit", deposit);
		tag.setDouble("rate", rate);
		tag.setDouble("gain", gain);
	}

	public void readFromNBT(NBTTagCompound tag) {
		date = Calendar.getInstance();
		date.setTimeInMillis(tag.getLong("date"));
		dealType = tag.getString("type");
		isLimits = tag.getBoolean("isLimits");
		pair = tag.getString("pair");
		lot = tag.getDouble("lot");
		deposit = tag.getDouble("deposit");
		rate = tag.getDouble("rate");
		gain = tag.getDouble("gain");
	}

	@Override
	public FXDealHistory clone() {
		FXDealHistory history = new FXDealHistory();
		history.date = date;
		history.dealType = dealType;
		history.isLimits = isLimits;
		history.pair = pair;
		history.lot = lot;
		history.deposit = deposit;
		history.rate = rate;
		history.gain = gain;
		return history;
	}

	public static FXDealHistory getFXHistoryFromNBT(NBTTagCompound tag) {
		FXDealHistory history = new FXDealHistory();
		history.readFromNBT(tag);
		return history;
	}

}
