package com.okina.fxcraft.rate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.Lists;

/**Client only*/
public class FXRateUpdateThreadClient extends TimerTask implements IFXRateGetter {

	private Timer updateTimer;
	private int updateInterval = 4000;
	private long lastUpdateMills = 100L;
	/**Nower rate data is set at smaller index*/
	private List<RateData>[] rateUSDJPY = new List[6];
	private List<RateData>[] rateEURJPY = new List[6];
	private List<RateData>[] rateEURUSD = new List[6];

	public FXRateUpdateThreadClient() {
		for (int i = 0; i < 6; i++){
			rateUSDJPY[i] = Collections.synchronizedList(Lists.<RateData> newArrayList());
			rateEURJPY[i] = Collections.synchronizedList(Lists.<RateData> newArrayList());
			rateEURUSD[i] = Collections.synchronizedList(Lists.<RateData> newArrayList());
		}
		updateTimer = new Timer();
	}

	public void init() {
		updateTimer.schedule(this, updateInterval, updateInterval);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 6; i++){
					updateRate(i);
				}
			}
		}, "FX History Get Thread Client").start();
	}

	public boolean hasUpdate(long lastUpdate) {
		return lastUpdate < lastUpdateMills;
	}

	public List<RateData> getRateForChart(String ratePair, int term) {
		List<RateData> list = Lists.newArrayList();
		List<RateData> term1mList;
		if("USDJPY".equals(ratePair)){
			list.addAll(rateUSDJPY[term]);
			term1mList = rateUSDJPY[0];
		}else if("EURJPY".equals(ratePair)){
			list.addAll(rateEURJPY[term]);
			term1mList = rateEURJPY[0];
		}else if("EURUSD".equals(ratePair)){
			list.addAll(rateEURUSD[term]);
			term1mList = rateEURUSD[0];
		}else{
			throw new IllegalArgumentException("Available rate pair: USDJPY, EURJPY, EURUSD");
		}
		//		if(!originalList.isEmpty()){
		//			int count = 0;
		//			Calendar last = originalList.get(0).calendar;
		//			for (RateData data : originalList){
		//				if(last.getTimeInMillis() - data.calendar.getTimeInMillis() >= getTermMills(term)){
		//					list.add(data);
		//					last = data.calendar;
		//					count++;
		//					if(count > 100){
		//						break;
		//					}
		//				}
		//			}
		//		}
		if(term != FXRateGetHelper.TERM_1m && !term1mList.isEmpty()){
			list.add(0, term1mList.get(0));
		}
		return list;
	}

	public double getEarliestRate(String pair) {
		if("USDJPY".equals(pair)){
			if(!rateUSDJPY[0].isEmpty()) return rateUSDJPY[0].get(0).open;
		}else if("EURJPY".equals(pair)){
			if(!rateEURJPY[0].isEmpty()) return rateEURJPY[0].get(0).open;
		}else if("EURUSD".equals(pair)){
			if(!rateEURUSD[0].isEmpty()) return rateEURUSD[0].get(0).open;
		}
		return 0;
	}

	public RateData getTodaysOpen(String pair) {
		if("USDJPY".equals(pair)){
			if(!rateUSDJPY[FXRateGetHelper.TERM_1d].isEmpty()) return rateUSDJPY[FXRateGetHelper.TERM_1d].get(0);
		}else if("EURJPY".equals(pair)){
			if(!rateEURJPY[FXRateGetHelper.TERM_1d].isEmpty()) return rateEURJPY[FXRateGetHelper.TERM_1d].get(0);
		}else if("EURUSD".equals(pair)){
			if(!rateEURUSD[FXRateGetHelper.TERM_1d].isEmpty()) return rateEURUSD[FXRateGetHelper.TERM_1d].get(0);
		}
		return RateData.NO_DATA;
	}

	private void updateRate(int term) {
		try{
			List<RateData> list = FXRateGetHelper.getHistory("USD", "JPY", FXRateGetHelper.REQUEST_TERMS[term]);
			if(list.size() < ((term == FXRateGetHelper.TERM_60m || term == FXRateGetHelper.TERM_1M) ? 100 : 190)){
				throw new IOException("Not Enough Data");
			}
			rateUSDJPY[term].clear();
			rateUSDJPY[term].addAll(list);
			rateUSDJPY[term].sort(FXRateGetHelper.DATA_COMPARATOR);
		}catch (Exception e){
			//			e.printStackTrace();
		}
		//		try{
		//			List<RateData> list = getHistory("EUR", "JPY", REQUEST_TERMS[term]);
		//			if(list.size() < ((term == FXRateUpdateThread.TERM_60m || term == FXRateUpdateThread.TERM_1M) ? 100 : 190)){
		//				throw new IOException("Not Enough Data");
		//			}
		//			rateEURJPY[term].clear();
		//			rateEURJPY[term].addAll(list);
		//			rateEURJPY[term].sort(DATA_COMPARATOR);
		//		}catch (Exception e){
		//			//			e.printStackTrace();
		//		}
		//		try{
		//			List<RateData> list = getHistory("EUR", "USD", REQUEST_TERMS[term]);
		//			if(list.size() < ((term == FXRateUpdateThread.TERM_60m || term == FXRateUpdateThread.TERM_1M) ? 100 : 190)){
		//				throw new IOException("Not Enough Data");
		//			}
		//			rateEURUSD[term].clear();
		//			rateEURUSD[term].addAll(list);
		//			rateEURUSD[term].sort(DATA_COMPARATOR);
		//		}catch (Exception e){
		//			//			e.printStackTrace();
		//		}
	}

	private long updateCount15m = 0;
	private long updateCount60m = 0;
	private long updateCount1d = 0;
	private long updateCount1w = 0;
	private long updateCount1M = 0;

	/**Update rate<br>
	 * Once per 4 seconds*/
	@Override
	public void run() {
		//		updateRate(FXRateGetHelper.TERM_1m);
		//		if(updateCount15m++ >= (int) (this.getTermMills(TERM_15m) / (float) updateInterval)){
		//		updateRate(FXRateGetHelper.TERM_15m);
		//		}
		//		if(updateCount60m++ >= (int) (this.getTermMills(TERM_60m) / (float) updateInterval)){
		//			updateRate(FXRateGetHelper.TERM_60m);
		//		}
		//		if(updateCount1d++ >= (int) (this.getTermMills(TERM_1d) / (float) updateInterval)){
		//			updateRate(FXRateGetHelper.TERM_1d);
		//		}
		//		if(updateCount1w++ >= (int) (this.getTermMills(TERM_1w) / (float) updateInterval)){
		//			updateRate(FXRateGetHelper.TERM_1w);
		//		}
		//		if(updateCount1M++ >= (int) (this.getTermMills(TERM_1M) / (float) updateInterval)){
		//			updateRate(FXRateGetHelper.TERM_1M);
		//		}
		//		lastUpdateMills = System.currentTimeMillis();
	}

}