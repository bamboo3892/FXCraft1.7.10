package com.okina.fxcraft.rate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**Server only*/
public class FXRateUpdateThreadServer extends TimerTask implements IFXRateGetter {

	private Timer updateTimer = new Timer();
	private int updateInterval = 4000;
	private long lastUpdateMills = 100L;
	/**Nower rate data is set at smaller index*/
	private List<RateData> rateUSDJPY = Lists.<RateData> newArrayList();
	private List<RateData> rateEURJPY = Lists.<RateData> newArrayList();
	private List<RateData> rateEURUSD = Lists.<RateData> newArrayList();

	public FXRateUpdateThreadServer() {}

	@Override
	public void init() {
		updateTimer.schedule(this, updateInterval, updateInterval);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 6; i++){
					updateRate(i);
				}
			}
		}, "FX History Get Thread Server").start();
	}

	@Override
	public boolean hasUpdate(long lastUpdate) {
		return lastUpdate < lastUpdateMills;
	}

	@Override
	public double getEarliestRate(String pair) throws NoValidRateException {
		RateData data = null;
		if("USDJPY".equals(pair)){
			if(!rateUSDJPY.isEmpty()){
				data = rateUSDJPY.get(0);
			}
		}else if("EURJPY".equals(pair)){
			if(!rateEURJPY.isEmpty()){
				data = rateEURJPY.get(0);
			}
		}else if("EURUSD".equals(pair)){
			if(!rateEURUSD.isEmpty()){
				data = rateEURUSD.get(0);
			}
		}else{
			throw new IllegalArgumentException("Available rate pair: USDJPY, EURJPY, EURUSD");
		}
		if(FXRateGetHelper.isValidRateData(data)){
			return data.open;
		}
		throw new NoValidRateException();
	}

	@Override
	public Map<String, Double> getEarliestRate() {
		Map<String, Double> map = Maps.newHashMap();
		try{
			map.put("USDJPY", getEarliestRate("USDJPY"));
		}catch (NoValidRateException e){

		}
		try{
			map.put("EURJPY", getEarliestRate("EURJPY"));
		}catch (NoValidRateException e){

		}
		try{
			map.put("EURUSD", getEarliestRate("EURUSD"));
		}catch (NoValidRateException e){

		}
		return map;
	}

	private void updateRate(int term) {
		try{
			List<RateData> list = FXRateGetHelper.getHistory("USD", "JPY", FXRateGetHelper.REQUEST_TERMS[term]);
			if(list.size() < ((term == FXRateGetHelper.TERM_60m || term == FXRateGetHelper.TERM_1M) ? 100 : 190)){
				throw new IOException("Not Enough Data");
			}
			rateUSDJPY.clear();
			rateUSDJPY.addAll(list);
			rateUSDJPY.sort(FXRateGetHelper.DATA_COMPARATOR);
		}catch (Exception e){
			//			e.printStackTrace();
		}
	}

	/**Update rate<br>
	 * Once per 4 seconds*/
	@Override
	public void run() {
		//		updateRate(TERM_1m);
		//		lastUpdateMills = System.currentTimeMillis();
	}

	@Override
	public List<RateData> getRateForChart(String ratePair, int term) {
		return null;
	}

	@Override
	public RateData getTodaysOpen(String pair) {
		return null;
	}

}
