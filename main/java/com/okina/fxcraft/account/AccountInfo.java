package com.okina.fxcraft.account;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class AccountInfo {

	public static final int[] DEAL_LIMIT = { 5000, 20000, 50000, 100000, 500000, 1000000 };
	public static final int[] LEVERAGE_LIMIT = { 1, 5, 10, 25, 50, 500 };
	public static final int[] POSITION_LIMIT = { 2, 3, 5, 7, 10, 12 };

	public String name;
	public int balance;
	public int totalDeal;
	public int totalLimitsDeal;
	public long totalGain;
	public long totalLoss;
	/**0 - 5*/
	public int dealLotLimit;
	/**0 - 5*/
	public int leverageLimit;
	/**0 - 5*/
	public int positionLimit;
	public List<FXPosition> positionList;
	public List<GetPositionOrder> getPositionOrder = Lists.newArrayList();
	public List<SettlePositionOrder> settlePositionOrder = Lists.newArrayList();
	public List<FXDealHistory> history = Lists.newArrayList();

	public AccountInfo(String name) {
		this.name = name;
		balance = 0;
		totalDeal = 0;
		totalLimitsDeal = 0;
		totalGain = 0;
		totalLoss = 0;
		positionList = Lists.newArrayList();
		leverageLimit = 0;
		positionLimit = 0;
		dealLotLimit = 0;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("name", name);
		tag.setInteger("balance", balance);
		tag.setInteger("totalDeal", totalDeal);
		tag.setInteger("totalLimitsDeal", totalLimitsDeal);
		tag.setLong("totalGain", totalGain);
		tag.setLong("totalLoss", totalLoss);
		tag.setInteger("leverageLimit", leverageLimit);
		tag.setInteger("positionLimit", positionLimit);
		tag.setInteger("dealLotLimit", dealLotLimit);

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
			getOrderTags.appendTag(settleOrderTag);
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
		balance = tag.getInteger("balance");
		totalDeal = tag.getInteger("totalDeal");
		totalLimitsDeal = tag.getInteger("totalLimitsDeal");
		totalGain = tag.getLong("totalGain");
		totalLoss = tag.getLong("totalLoss");
		leverageLimit = tag.getInteger("leverageLimit");
		positionLimit = tag.getInteger("positionLimit");
		dealLotLimit = tag.getInteger("dealLotLimit");

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

	public boolean equals(Object o) {
		return o instanceof AccountInfo && name.equals(((AccountInfo) o).name);
	}

}
