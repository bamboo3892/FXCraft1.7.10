package com.okina.fxcraft.account;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.okina.fxcraft.rate.RateData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

public class AccountInfo {

	public static final int[] DEAL_LIMIT = { 5000, 20000, 50000, 100000, 500000, 1000000 };
	public static final int[] LEVERAGE_LIMIT = { 1, 5, 10, 25, 50, 500 };
	public static final int[] POSITION_LIMIT = { 2, 3, 5, 7, 10, 11 };

	public String name;
	public double balance;
	public long totalDeal;
	public long totalMovedDeposit;
	public long totalLimitsDeal;
	public double totalGain;
	public double totalLoss;
	/**0 - 5*/
	public int dealLotLimit;
	/**0 - 5*/
	public int leverageLimit;
	/**0 - 5*/
	public int positionLimit;
	public boolean limitsTradePermission;
	public List<String> receivableReward = Lists.newArrayList();
	public List<String> receivedReward = Lists.newArrayList();
	public List<FXPosition> positionList = Lists.newArrayList();
	public List<GetPositionOrder> getPositionOrder = Lists.newArrayList();
	public List<SettlePositionOrder> settlePositionOrder = Lists.newArrayList();
	public List<FXDealHistory> history = Lists.newArrayList();

	public AccountInfo(String name) {
		this.name = name;
		balance = 0;
		totalDeal = 0;
		totalMovedDeposit = 0;
		totalLimitsDeal = 0;
		totalGain = 0;
		totalLoss = 0;
		leverageLimit = 0;
		positionLimit = 0;
		dealLotLimit = 0;
		limitsTradePermission = false;
	}

	public double getTotalBalence(Map<String, RateData> rateMap) {
		double value = balance;
		value += getPosiitionsValue(rateMap);
		value += getOrdersValue(rateMap);
		return value;
	}

	public double getPosiitionsValue(Map<String, RateData> rateMap) {
		double value = 0;
		for (FXPosition position : positionList){
			if(rateMap.containsKey(position.currencyPair)){
				value += position.getValue(rateMap.get(position.currencyPair).open);
			}else{
				value += position.depositLot;
			}
		}
		return value;
	}

	public double getOrdersValue(Map<String, RateData> rateMap) {
		double value = 0;
		for (GetPositionOrder order : getPositionOrder){
			value += order.depositLot;
		}
		for (SettlePositionOrder order : settlePositionOrder){
			if(rateMap.containsKey(order.position.currencyPair)){
				value += order.position.getValue(rateMap.get(order.position.currencyPair).open);
			}else{
				value += order.position.depositLot;
			}
		}
		return value;
	}

	public FXPosition getPosition(String positionID) {
		for (FXPosition position : positionList){
			if(positionID.equals(position.positionID)){
				return position;
			}
		}
		return null;
	}

	public GetPositionOrder getGetOrder(String orderID) {
		for (GetPositionOrder order : getPositionOrder){
			if(orderID.equals(order.orderID)){
				return order;
			}
		}
		return null;
	}

	public SettlePositionOrder getSettleOrder(String orderID) {
		for (SettlePositionOrder order : settlePositionOrder){
			if(orderID.equals(order.position.positionID)){
				return order;
			}
		}
		return null;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("name", name);
		tag.setDouble("balance", balance);
		tag.setLong("totalDeal", totalDeal);
		tag.setLong("totalMovedDeposit", totalMovedDeposit);
		tag.setLong("totalLimitsDeal", totalLimitsDeal);
		tag.setDouble("totalGain", totalGain);
		tag.setDouble("totalLoss", totalLoss);
		tag.setInteger("leverageLimit", leverageLimit);
		tag.setInteger("positionLimit", positionLimit);
		tag.setInteger("dealLotLimit", dealLotLimit);
		tag.setBoolean("limitsTradePermission", limitsTradePermission);

		NBTTagList receivableTabs = new NBTTagList();
		for (String reward : receivableReward){
			receivableTabs.appendTag(new NBTTagString(reward));
		}
		tag.setTag("receivable", receivableTabs);

		NBTTagList receivedTabs = new NBTTagList();
		for (String reward : receivedReward){
			receivedTabs.appendTag(new NBTTagString(reward));
		}
		tag.setTag("received", receivedTabs);

		NBTTagList positionTags = new NBTTagList();
		for (FXPosition position : positionList){
			NBTTagCompound positionTag = new NBTTagCompound();
			position.writeToNBT(positionTag);
			positionTags.appendTag(positionTag);
		}
		tag.setTag("position", positionTags);

		NBTTagList getOrderTags = new NBTTagList();
		for (GetPositionOrder getOrder : getPositionOrder){
			NBTTagCompound getOrderTag = new NBTTagCompound();
			getOrder.writeToNBT(getOrderTag);
			getOrderTags.appendTag(getOrderTag);
		}
		tag.setTag("getOrder", getOrderTags);

		NBTTagList settleOrderTags = new NBTTagList();
		for (SettlePositionOrder settleOrder : settlePositionOrder){
			NBTTagCompound settleOrderTag = new NBTTagCompound();
			settleOrder.writeToNBT(settleOrderTag);
			settleOrderTags.appendTag(settleOrderTag);
		}
		tag.setTag("settleOrder", settleOrderTags);

		NBTTagList historyTags = new NBTTagList();
		for (FXDealHistory h : history){
			NBTTagCompound historyTag = new NBTTagCompound();
			h.writeToNBT(historyTag);
			historyTags.appendTag(historyTag);
		}
		tag.setTag("history", historyTags);
	}

	public void readFromNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		balance = tag.getDouble("balance");
		totalDeal = tag.getLong("totalDeal");
		totalMovedDeposit = tag.getLong("totalMovedDeposit");
		totalLimitsDeal = tag.getLong("totalLimitsDeal");
		totalGain = tag.getDouble("totalGain");
		totalLoss = tag.getDouble("totalLoss");
		leverageLimit = tag.getInteger("leverageLimit");
		positionLimit = tag.getInteger("positionLimit");
		dealLotLimit = tag.getInteger("dealLotLimit");
		limitsTradePermission = tag.getBoolean("limitsTradePermission");

		receivableReward.clear();
		NBTTagList receivableTabs = tag.getTagList("receivable", Constants.NBT.TAG_STRING);
		if(receivableTabs != null){
			for (int i = 0; i < receivableTabs.tagCount(); i++){
				receivableReward.add(receivableTabs.getStringTagAt(i));
			}
		}

		receivedReward.clear();
		NBTTagList receivedTabs = tag.getTagList("received", Constants.NBT.TAG_STRING);
		if(receivedTabs != null){
			for (int i = 0; i < receivedTabs.tagCount(); i++){
				receivedReward.add(receivedTabs.getStringTagAt(i));
			}
		}

		positionList.clear();
		NBTTagList positionTags = tag.getTagList("position", Constants.NBT.TAG_COMPOUND);
		if(positionTags != null){
			for (int i = 0; i < positionTags.tagCount(); i++){
				NBTTagCompound positionTag = positionTags.getCompoundTagAt(i);
				FXPosition position = FXPosition.getFXPositionFromNBT(positionTag);
				positionList.add(position);
			}
		}

		getPositionOrder.clear();
		NBTTagList getOrderTags = tag.getTagList("getOrder", Constants.NBT.TAG_COMPOUND);
		if(getOrderTags != null){
			for (int i = 0; i < getOrderTags.tagCount(); i++){
				NBTTagCompound getOrderTag = getOrderTags.getCompoundTagAt(i);
				GetPositionOrder order = GetPositionOrder.getGetPositionOrderFromNBT(getOrderTag);
				getPositionOrder.add(order);
			}
		}

		settlePositionOrder.clear();
		NBTTagList settleOrderTags = tag.getTagList("settleOrder", Constants.NBT.TAG_COMPOUND);
		if(settleOrderTags != null){
			for (int i = 0; i < settleOrderTags.tagCount(); i++){
				NBTTagCompound settleOrderTag = settleOrderTags.getCompoundTagAt(i);
				SettlePositionOrder order = SettlePositionOrder.getSettlePositionOrderFromNBT(settleOrderTag);
				settlePositionOrder.add(order);
			}
		}

		history.clear();
		NBTTagList historyTags = tag.getTagList("position", Constants.NBT.TAG_COMPOUND);
		if(positionTags != null){
			for (int i = 0; i < historyTags.tagCount(); i++){
				NBTTagCompound historyTag = historyTags.getCompoundTagAt(i);
				FXDealHistory h = FXDealHistory.getFXHistoryFromNBT(historyTag);
				h.readFromNBT(historyTag);
				history.add(h);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof AccountInfo && name.equals(((AccountInfo) o).name);
	}

}
