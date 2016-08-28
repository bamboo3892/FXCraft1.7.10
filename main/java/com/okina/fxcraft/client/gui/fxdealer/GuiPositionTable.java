package com.okina.fxcraft.client.gui.fxdealer;

import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.account.IAccountInfoContainer;
import com.okina.fxcraft.client.gui.GuiTable;

import net.minecraft.client.Minecraft;

public class GuiPositionTable extends GuiTable<GuiPositionTableRow> {

	private IAccountInfoContainer accountContainer;
	private long lastAccountUpdate;

	public GuiPositionTable(IAccountInfoContainer accountContainer, int buttonID, int startX, int startY, GuiPositionTableRow titleRow, int row) {
		super(buttonID, startX, startY, titleRow, row);
		this.accountContainer = accountContainer;
		AccountInfo info = accountContainer.getAccountInfo();
		if(info != null){
			rowList.clear();
			for (FXPosition position : info.positionList){
				rowList.add(new GuiPositionTableRow(titleRow, position));
			}
			rowList.sort(GuiPositionTableRow.COMPARATOR);
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
					for (FXPosition position : info.positionList){
						rowList.add(new GuiPositionTableRow(titleRow, position));
					}
					rowList.sort(GuiPositionTableRow.COMPARATOR);
					rowList.add(0, titleRow);
					lastAccountUpdate = System.currentTimeMillis();
				}
			}
			super.drawButton(minecraft, mouseX, mouseY);
		}
	}

	public FXPosition getSelectedPosition() {
		return getSelectedRow().position;
	}

}
