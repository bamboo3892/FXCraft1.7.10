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
			return c1.compareTo(c2);
		}
	};

	public static final int FIELD_DATE = 0;
	public static final int FIELD_PAIR = 1;
	public static final int FIELD_LOT = 2;
	public static final int FIELD_DEPOSIT = 3;
	public static final int FIELD_ASK_BID = 4;
	public static final int FIELD_LIMITS = 5;
	public static final int FIELD_ID = 6;
	public static final int FIELD_RATE = 7;
	public static final int FIELD_ORDER_TYPE = 8;

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
		this.order = Objects.requireNonNull(order);
		this.getPositionFields = templete.getPositionFields;
		this.settlePositionFields = templete.settlePositionFields;
		if(getPositionFields.length != fieldCount || settlePositionFields.length != fieldCount) throw new IllegalArgumentException();
	}

	@Override
	public String getContent(int field) {
		if(isTitle){
			return super.getContent(field);
		}else{
			if(order instanceof GetPositionOrder){
				return getField(getPositionFields[field]).toString();
			}else{
				return getField(settlePositionFields[field]).toString();
			}
		}
	}

	private String getField(int field) {
		if(order instanceof GetPositionOrder){
			GetPositionOrder get = (GetPositionOrder) order;
			switch (field) {
			case FIELD_DATE:
				return String.valueOf(get.contractDate);
			case FIELD_PAIR:
				return String.valueOf(get.currencyPair);
			case FIELD_LOT:
				return String.valueOf(get.lot);
			case FIELD_DEPOSIT:
				return String.valueOf(get.depositLot);
			case FIELD_ASK_BID:
				return get.askOrBid ? "ASK" : "BID";
			case FIELD_LIMITS:
				return String.valueOf(get.limits);
			case FIELD_ID:
				return String.valueOf(get.orderID);
			case FIELD_ORDER_TYPE:
				return get.askOrBid ? "ASK" : "BID";
			default:
				return null;
			}
		}else{
			SettlePositionOrder settle = (SettlePositionOrder) order;
			switch (field) {
			case FIELD_DATE:
				return String.valueOf(settle.contractDate);
			case FIELD_PAIR:
				return String.valueOf(settle.position.currencyPair);
			case FIELD_LOT:
				return String.valueOf(settle.position.lot);
			case FIELD_DEPOSIT:
				return String.valueOf(settle.position.depositLot);
			case FIELD_ASK_BID:
				return settle.position.askOrBid ? "ASK" : "BID";
			case FIELD_RATE:
				return String.valueOf(settle.position.contractRate);
			case FIELD_ID:
				return String.valueOf(settle.position.positionID);
			case FIELD_LIMITS:
				return String.valueOf(settle.limits);
			case FIELD_ORDER_TYPE:
				return "SETTLE";
			default:
				return null;
			}
		}
	}

}
