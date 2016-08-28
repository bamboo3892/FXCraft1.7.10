package com.okina.fxcraft.client.gui.fxdealer;

import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.GetPositionOrder;
import com.okina.fxcraft.account.IAccountInfoContainer;
import com.okina.fxcraft.account.SettlePositionOrder;
import com.okina.fxcraft.client.gui.GuiTable;

import net.minecraft.client.Minecraft;

public class GuiOrderTable extends GuiTable<GuiOrderTableRow> {

	private IAccountInfoContainer accountContainer;
	private long lastAccountUpdate;

	public GuiOrderTable(IAccountInfoContainer accountContainer, int buttonID, int startX, int startY, GuiOrderTableRow titleRow, int row) {
		super(buttonID, startX, startY, titleRow, row);
		this.accountContainer = accountContainer;
		AccountInfo info = accountContainer.getAccountInfo();
		if(info != null){
			rowList.clear();
			for (GetPositionOrder order : info.getPositionOrder){
				rowList.add(new GuiOrderTableRow(titleRow, order));
			}
			for (SettlePositionOrder order : info.settlePositionOrder){
				rowList.add(new GuiOrderTableRow(titleRow, order));
			}
			rowList.sort(GuiOrderTableRow.COMPARATOR);
			rowList.add(0, titleRow);
			lastAccountUpdate = System.currentTimeMillis();
		}
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(visible){
			if(accountContainer.hasAccountUpdate(lastAccountUpdate)){
				AccountInfo info = accountContainer.getAccountInfo();
				if(info != null){
					rowList.clear();
					for (GetPositionOrder order : info.getPositionOrder){
						rowList.add(new GuiOrderTableRow(titleRow, order));
					}
					for (SettlePositionOrder order : info.settlePositionOrder){
						rowList.add(new GuiOrderTableRow(titleRow, order));
					}
					rowList.sort(GuiOrderTableRow.COMPARATOR);
					rowList.add(0, titleRow);
					lastAccountUpdate = System.currentTimeMillis();
				}
			}
			super.drawButton(minecraft, mouseX, mouseY);
		}
	}

}
