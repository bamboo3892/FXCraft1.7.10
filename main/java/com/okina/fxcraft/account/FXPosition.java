package com.okina.fxcraft.account;

import java.util.Calendar;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class FXPosition {

	public static final FXPosition NO_INFO_POSITION;
	static{
		NO_INFO_POSITION = new FXPosition();
		NO_INFO_POSITION.contractDate = Calendar.getInstance();
		NO_INFO_POSITION.currencyPair = "No Info";
		NO_INFO_POSITION.lot = 1000;
	}

	public Calendar contractDate;
	public String currencyPair;
	public double lot;
	public double depositLot;
	public boolean askOrBid = true;//True: Ask, False: Bid
	public double contractRate;
	public String positionID;

	private FXPosition() {
		positionID = UUID.randomUUID().toString();
	}

	public FXPosition(Calendar date, String pair, int lot, int deposit, double rate, boolean askOrBid) {
		this();
		this.contractDate = date;
		this.currencyPair = pair;
		this.lot = lot;
		this.depositLot = deposit;
		this.contractRate = rate;
		this.askOrBid = askOrBid;
	}

	public double getLeverage() {
		if(depositLot <= 0){
			return 0;
		}
		return lot / (double) depositLot;
	}

	public double getGain(double nowRate) {
		return lot * (contractRate - nowRate) * (askOrBid ? 1 : -1);
	}

	public double getValue(double nowRate) {
		return depositLot + getGain(nowRate);
	}

	public FXPosition split(int dealLot) {
		if(lot < dealLot) throw new IllegalArgumentException();
		double ratio = dealLot / (double) lot;
		lot -= dealLot;
		depositLot -= depositLot * ratio;

		FXPosition clone = clone();
		clone.positionID = UUID.randomUUID().toString();
		clone.lot = dealLot;
		clone.depositLot = clone.depositLot * ratio;
		return clone;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong("date", contractDate.getTimeInMillis());
		tag.setString("currencyPair", currencyPair);
		tag.setDouble("lot", lot);
		tag.setDouble("depositLot", depositLot);
		tag.setBoolean("askOrBid", askOrBid);
		tag.setDouble("contractRate", contractRate);
		tag.setString("id", positionID);
	}

	public void readFromNBT(NBTTagCompound tag) {
		contractDate = Calendar.getInstance();
		contractDate.setTimeInMillis(tag.getLong("date"));
		currencyPair = tag.getString("currencyPair");
		lot = tag.getDouble("lot");
		depositLot = tag.getDouble("depositLot");
		askOrBid = tag.getBoolean("askOrBid");
		contractRate = tag.getDouble("contractRate");
		positionID = tag.getString("id");
	}

	@Override
	public FXPosition clone() {
		FXPosition pos = new FXPosition();
		pos.contractDate = (Calendar) contractDate.clone();
		pos.currencyPair = currencyPair;
		pos.lot = lot;
		pos.depositLot = depositLot;
		pos.askOrBid = askOrBid;
		pos.contractRate = contractRate;
		return pos;
	}

	public static FXPosition getFXPositionFromNBT(NBTTagCompound tag) {
		FXPosition position = new FXPosition();
		position.readFromNBT(tag);
		return position;
	}

}
