package com.okina.fxcraft.account;

import java.util.Calendar;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class FXPosition implements Cloneable {

	public static final FXPosition NO_INFO;
	static{
		NO_INFO = new FXPosition();
		NO_INFO.contractDate = Calendar.getInstance();
		NO_INFO.currencyPair = "No Info";
		NO_INFO.lot = 1000;
	}

	public static final int FIELD_DATE = 0;
	public static final int FIELD_PAIR = 1;
	public static final int FIELD_LOT = 2;
	public static final int FIELD_DEPOSIT = 3;
	public static final int FIELD_ASK_BID = 4;
	public static final int FIELD_RATE = 5;
	public static final int FIELD_ID = 6;

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

	public FXPosition(Calendar date, String pair, double lot, double deposit, double rate, boolean askOrBid) {
		this();
		this.contractDate = date;
		this.currencyPair = pair;
		this.lot = lot;
		this.depositLot = deposit;
		this.contractRate = rate;
		this.askOrBid = askOrBid;
	}

	public FXPosition(Calendar date, double rate, GetPositionOrder order) {
		this(date, order.currencyPair, order.lot, order.depositLot, rate, order.askOrBid);
	}

	public String getField(int field) {
		switch (field) {
		case FIELD_DATE:
			return String.valueOf(contractDate);
		case FIELD_PAIR:
			return String.valueOf(currencyPair);
		case FIELD_LOT:
			return String.valueOf(lot);
		case FIELD_DEPOSIT:
			return String.valueOf(depositLot);
		case FIELD_ASK_BID:
			return askOrBid ? "ASK" : "BID";
		case FIELD_RATE:
			return String.valueOf(contractRate);
		case FIELD_ID:
			return String.valueOf(positionID);
		default:
			return null;
		}
	}

	public double getLeverage() {
		if(depositLot <= 0){
			return 0;
		}
		return lot / depositLot;
	}

	public double getGain(double nowRate) {
		if(nowRate <= 0) return 0;
		return lot * (nowRate - contractRate) / nowRate * (askOrBid ? 1 : -1);
	}

	public double getValue(double nowRate) {
		if(nowRate <= 0) return depositLot;
		return depositLot + getGain(nowRate);
	}

	public FXPosition split(double dealLot) {
		if(lot < dealLot) throw new IllegalArgumentException();
		double ratio = dealLot / lot;

		FXPosition clone = clone();
		clone.positionID = UUID.randomUUID().toString();
		clone.lot = dealLot;
		clone.depositLot = clone.depositLot * ratio;

		lot -= dealLot;
		depositLot -= depositLot * ratio;
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
		pos.positionID = positionID;
		return pos;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof FXPosition && positionID.equals(((FXPosition) o).positionID);
	}

	public static FXPosition getFXPositionFromNBT(NBTTagCompound tag) {
		FXPosition position = new FXPosition();
		position.readFromNBT(tag);
		return position;
	}

}
