package com.okina.fxcraft.rate;

import java.util.List;

public interface IFXRateGetter {

	void init();

	boolean hasUpdate(long lastUpdate);

	double getEarliestRate(String pair);

	/**Client only*/
	List<RateData> getRateForChart(String ratePair, int term);

	/**Client only*/
	RateData getTodaysOpen(String pair);

}
