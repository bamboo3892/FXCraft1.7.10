package com.okina.fxcraft.account;

import java.util.Calendar;
import java.util.UUID;

import com.okina.fxcraft.rate.RateData;

import net.minecraft.nbt.NBTTagCompound;

public class GetPositionOrder implements Cloneable {

	public static final GetPositionOrder NO_INFO;
	static{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		NO_INFO = new GetPositionOrder(calendar, "No Info", 1000, 1000, true, 0);
		NO_INFO.orderID = "No Info";
	}

	public Calendar contractDate;
	public String currencyPair;
	public double lot;
	public double depositLot;
	public boolean askOrBid = true;//True: Ask, False: Bid
	public double limits;
	public String orderID;

	private GetPositionOrder() {
		orderID = UUID.randomUUID().toString();
	}

	public GetPositionOrder(Calendar date, String pair, double lot, double deposit, boolean askOrBid, double limits) {
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
		tag.setDouble("lot", lot);
		tag.setDouble("depositLot", depositLot);
		tag.setBoolean("askOrBid", askOrBid);
		tag.setDouble("limits", limits);
		tag.setString("id", orderID);
	}

	public void readFromNBT(NBTTagCompound tag) {
		contractDate = Calendar.getInstance();
		contractDate.setTimeInMillis(tag.getLong("date"));
		currencyPair = tag.getString("currencyPair");
		lot = tag.getDouble("lot");
		depositLot = tag.getDouble("depositLot");
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

	@Override
	public boolean equals(Object o) {
		return o instanceof GetPositionOrder && orderID.equals(((GetPositionOrder) o).orderID);
	}

	public static GetPositionOrder getGetPositionOrderFromNBT(NBTTagCompound tag) {
		GetPositionOrder order = new GetPositionOrder();
		order.readFromNBT(tag);
		return order;
	}

	public boolean checkConstruct(RateData data) {
		//		System.out.println(FXRateGetHelper.getCalendarString(contractDate, 0) + ", " + FXRateGetHelper.getCalendarString(data.calendar, 0));
		if(contractDate.compareTo(data.calendar) <= 0){
			if(askOrBid){
				return limits >= data.open;
			}else{
				return limits <= data.open;
			}
		}
		return false;
	}

}
