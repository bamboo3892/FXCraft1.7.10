package com.okina.fxcraft.client.gui.fxdealer;

import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.client.gui.GuiTableRow;

public class GuiPositionTableRow extends GuiTableRow {

	protected FXPosition position = FXPosition.NO_INFO_POSITION;

	public GuiPositionTableRow(int sizeY, int[] rowSize, String[] contents) {
		super(sizeY, rowSize, contents);
	}

	public GuiPositionTableRow(FXPosition position, int sizeY, int[] rowSize, String[] contents) {
		super(sizeY, rowSize, contents);
		this.position = position;
	}

}
