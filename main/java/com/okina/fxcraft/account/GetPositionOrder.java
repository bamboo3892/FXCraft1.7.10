package com.okina.fxcraft.account;

import java.util.Calendar;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class GetPositionOrder {

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
