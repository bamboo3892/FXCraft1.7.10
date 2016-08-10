package com.okina.fxcraft.rate;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.Lists;

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

	public boolean hasUpdate(long lastUpdate) {
		return lastUpdate < lastUpdateMills;
	}

	public double getEarliestRate(String pair) {
		if("USDJPY".equals(pair)){
			if(!rateUSDJPY.isEmpty()) return rateUSDJPY.get(0).open;
		}else if("EURJPY".equals(pair)){
			if(!rateEURJPY.isEmpty()) return rateEURJPY.get(0).open;
		}else if("EURUSD".equals(pair)){
			if(!rateEURUSD.isEmpty()) return rateEURUSD.get(0).open;
		}
		return 0;
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
