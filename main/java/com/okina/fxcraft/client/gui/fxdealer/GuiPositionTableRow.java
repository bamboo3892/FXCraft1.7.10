package com.okina.fxcraft.client.gui.fxdealer;

import java.util.Comparator;
import java.util.Objects;

import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.client.gui.GuiTableRow;

public class GuiPositionTableRow extends GuiTableRow {

	public static final Comparator COMPARATOR = new Comparator<GuiPositionTableRow>() {
		@Override
		public int compare(GuiPositionTableRow row1, GuiPositionTableRow row2) {
			return row1.position.contractDate.compareTo(row2.position.contractDate);
		}
	};

	private boolean isTitle;
	protected FXPosition position;
	protected int[] fields;

	public GuiPositionTableRow(int sizeY, int[] rowSize, String[] column, int[] fields) {
		super(sizeY, rowSize, column);
		if(fields.length != fieldCount) throw new IllegalArgumentException();
		position = FXPosition.NO_INFO;
		this.fields = fields;
		isTitle = true;
	}

	public GuiPositionTableRow(GuiPositionTableRow templete, FXPosition position) {
		super(templete.sizeY, templete.rowSize, new String[templete.rowSize.length]);
		this.position = Objects.requireNonNull(position);
		this.fields = templete.fields;
	}

	@Override
	public String getContent(int field) {
		if(isTitle || position == null){
			return super.getContent(field);
		}else{
			return position.getField(fields[field]).toString();
		}
	}

}
