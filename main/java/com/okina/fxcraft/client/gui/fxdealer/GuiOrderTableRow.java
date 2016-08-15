package com.okina.fxcraft.client.gui.fxdealer;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;

import com.okina.fxcraft.account.GetPositionOrder;
import com.okina.fxcraft.account.SettlePositionOrder;
import com.okina.fxcraft.client.gui.GuiTableRow;

public class GuiOrderTableRow extends GuiTableRow {

	public static final Comparator COMPARATOR = new Comparator<GuiOrderTableRow>() {
		@Override
		public int compare(GuiOrderTableRow row1, GuiOrderTableRow row2) {
			Object o1 = row1.order;
			Object o2 = row2.order;
			Calendar c1 = o1 instanceof GetPositionOrder ? ((GetPositionOrder) o1).contractDate : ((SettlePositionOrder) o1).position.contractDate;
			Calendar c2 = o2 instanceof GetPositionOrder ? ((GetPositionOrder) o2).contractDate : ((SettlePositionOrder) o2).position.contractDate;
			return -c1.compareTo(c2);
		}
	};

	private boolean isTitle;
	private boolean isGetOrder;
	protected Object order;
	protected int[] getPositionFields;
	protected int[] settlePositionFields;

	/**For title row*/
	public GuiOrderTableRow(int sizeY, int[] rowSize, String[] column, int[] getPositionFields, int[] settlePositionFields) {
		super(sizeY, rowSize, column);
		if(getPositionFields.length != fieldCount || settlePositionFields.length != fieldCount) throw new IllegalArgumentException();
		order = GetPositionOrder.NO_INFO;
		this.getPositionFields = getPositionFields;
		this.settlePositionFields = settlePositionFields;
		isTitle = true;
	}

	/**For not title row*/
	public GuiOrderTableRow(GuiOrderTableRow templete, Object order) {
		super(templete.sizeY, templete.rowSize, new String[templete.rowSize.length]);
		if(!(order instanceof GetPositionOrder || order instanceof SettlePositionOrder)) throw new IllegalArgumentException();
		if(getPositionFields.length != fieldCount || settlePositionFields.length != fieldCount) throw new IllegalArgumentException();
		this.order = Objects.requireNonNull(order);
		this.getPositionFields = templete.getPositionFields;
		this.settlePositionFields = templete.settlePositionFields;
	}

	@Override
	public String getContent(int field) {
		if(isTitle){
			return super.getContent(field);
		}else{
			if(order instanceof GetPositionOrder){
				return ((GetPositionOrder) order).getField(getPositionFields[field]).toString();
			}else{
				return ((SettlePositionOrder) order).getField(settlePositionFields[field]).toString();
			}
		}
	}

}
