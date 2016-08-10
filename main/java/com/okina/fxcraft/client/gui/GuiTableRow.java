package com.okina.fxcraft.client.gui;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

public class GuiTableRow {

	public final int sizeX;
	public final int sizeY;
	public final int fieldCount;
	public final int[] rowSize;
	public final int[] rowPosition;
	public final String[] contents;

	public GuiTableRow(int sizeY, int[] rowSize, String[] contents) {
		if(rowSize.length != contents.length) throw new IllegalArgumentException();
		this.sizeY = sizeY;
		fieldCount = rowSize.length;
		this.rowSize = rowSize;
		this.contents = contents;
		rowPosition = new int[rowSize.length + 1];
		int x = 0;
		for (int i = 0; i < rowSize.length; i++){
			rowPosition[i] = x;
			x += rowSize[i];
		}
		rowPosition[rowSize.length] = x;
		sizeX = x;
	}

	public void setContent(int field, String content) {
		contents[field] = Objects.requireNonNull(content);
	}

	public String getContent(int field) {
		return contents[field];
	}

	/**Mouse  points param field.*/
	public List<String> getTips(int field) {
		return Lists.newArrayList();
	}

}