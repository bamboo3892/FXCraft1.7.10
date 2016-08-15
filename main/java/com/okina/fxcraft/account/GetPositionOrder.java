package com.okina.fxcraft.account;

import java.util.Calendar;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class GetPositionOrder implements Cloneable {

	public static final GetPositionOrder NO_INFO;
	static{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		NO_INFO = new GetPositionOrder(calendar, "No Info", 1000, 1000, true, 0);
		NO_INFO.orderID = "No Info";
	}

	public static final int FIELD_DATE = 0;
	public static final int FIELD_PAIR = 1;
	public static final int FIELD_LOT = 2;
	public static final int FIELD_DEPOSIT = 3;
	public static final int FIELD_ASK_BID = 4;
	public static final int FIELD_LIMITS = 5;
	public static final int FIELD_ID = 6;

	public Calendar contractDate;
	public String currencyPair;
	public int lot;
	public int depositLot;
	public boolean askOrBid = true;//True: Ask, False: Bid
	public double limits;
	public String orderID;

	private GetPositionOrder() {
		orderID = UUID.randomUUID().toString();
	}

	public GetPositionOrder(Calendar date, String pair, int lot, int deposit, boolean askOrBid, double limits) {
		this();
		this.contractDate = date;
		this.currencyPair = pair;
		this.lot = lot;
		this.depositLot = deposit;
		this.askOrBid = askOrBid;
		this.limits = limits;
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
			return String.valueOf(askOrBid);
		case FIELD_LIMITS:
			return String.valueOf(limits);
		case FIELD_ID:
			return String.valueOf(orderID);
		default:
			return null;
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong("date", contractDate.getTimeInMillis());
		tag.setString("currencyPair", currencyPair);
		tag.setInteger("lot", lot);
		tag.setInteger("depositLot", depositLot);
		tag.setBoolean("askOrBid", askOrBid);
		tag.setDouble("limits", limits);
		tag.setString("id", orderID);
	}

	public void readFromNBT(NBTTagCompound tag) {
		contractDate = Calendar.getInstance();
		contractDate.setTimeInMillis(tag.getLong("date"));
		currencyPair = tag.getString("currencyPair");
		lot = tag.getInteger("lot");
		depositLot = tag.getInteger("depositLot");
		askOrBid = tag.getBoolean("askOrBid");
		limits = tag.getDouble("limits");
		orderID = tag.getString("id");
	}

	@Override
	public GetPositionOrder clone() {
		GetPositionOrder order = new GetPositionOrder();
		order.contractDate = (Calendar) contractDate.clone();
		order.currencyPair = currencyPair;
		order.lot = lot;
		order.depositLot = depositLot;
		order.askOrBid = askOrBid;
		order.limits = limits;
		order.orderID = orderID;
		return order;
	}

	public static GetPositionOrder getGetPositionOrderFromNBT(NBTTagCompound tag) {
		GetPositionOrder order = new GetPositionOrder();
		order.readFromNBT(tag);
		return order;
	}

}
