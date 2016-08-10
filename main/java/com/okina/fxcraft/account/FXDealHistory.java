package com.okina.fxcraft.account;

import net.minecraft.nbt.NBTTagCompound;

public class FXDealHistory {

	public void writeToNBT(NBTTagCompound tag) {}

	public void readFromNBT(NBTTagCompound tag) {}

	public static FXDealHistory getFXHistoryFromNBT(NBTTagCompound tag) {
		return new FXDealHistory();
	}

}
