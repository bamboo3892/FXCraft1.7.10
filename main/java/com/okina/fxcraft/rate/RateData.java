package com.okina.fxcraft.rate;

import java.util.Calendar;

public class RateData {

	public static final RateData NO_DATA = new RateData(Calendar.getInstance(), 0, 0, 0);
	static{
		NO_DATA.calendar.setTimeInMillis(0);
	}

	public Calendar calendar;
	public double high;
	public double low;
	public double open;

	public RateData(Calendar calendar, double high, double low, double open) {
		this.calendar = calendar;
		this.high = high;
		this.low = low;
		this.open = open;
	}

}