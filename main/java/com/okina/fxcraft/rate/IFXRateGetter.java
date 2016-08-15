package com.okina.fxcraft.rate;

import java.util.List;
import java.util.Map;

public interface IFXRateGetter {

	void init();

	boolean hasUpdate(long lastUpdate);

	double getEarliestRate(String pair) throws NoValidRateException;

	Map<String, Double> getEarliestRate();

	/**Client only*/
	List<RateData> getRateForChart(String ratePair, int term);

	/**Client only*/
	RateData getTodaysOpen(String pair);

}
