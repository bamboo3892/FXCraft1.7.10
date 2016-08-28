package com.okina.fxcraft.rate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okina.fxcraft.account.AccountHandler;

/**Client only*/
public class FXRateUpdateThread extends TimerTask {

	public static String[] VALID_PAIRS = { "USDJPY", "EURJPY", "AUDJPY" };

	private Timer updateTimer;
	private int updateInterval = 4000;
	private long lastUpdateMills = 100L;
	/**Nower rate data is set at smaller index*/
	private List<RateData>[] realTime = new List[VALID_PAIRS.length];
	private List<RateData>[][] rateList = new List[VALID_PAIRS.length][6];

	public FXRateUpdateThread() {
		for (int i = 0; i < realTime.length; i++){
			new ArrayList();
			realTime[i] = Collections.synchronizedList(Lists.<RateData> newArrayList());
		}
		for (List<RateData>[] list : rateList){
			for (int i = 0; i < list.length; i++){
				list[i] = Collections.synchronizedList(Lists.<RateData> newArrayList());
			}
		}
		updateTimer = new Timer();
	}

	public void init() {
		updateTimer.schedule(this, updateInterval, updateInterval);
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateRealTimeRate();
				for (int i = 0; i < 6; i++){
					updateRate(i);
				}
				Map<String, List<RateData>> dataMap = Maps.newHashMap();
				for (int i = 0; i < rateList.length; i++){
					List<RateData> list = Lists.newArrayList();
					for (int term = 0; term < rateList[i].length; term++){
						list.addAll(rateList[i][term]);
					}
					list.sort(FXRateGetHelper.DATA_COMPARATOR);
					dataMap.put(VALID_PAIRS[i], list);
				}
				AccountHandler.instance.checkLosscutAndOrderFromPast(dataMap);
			}
		}, "FX History Get Thread Client").start();
	}

	public boolean hasUpdate(long lastUpdate) {
		return lastUpdate < lastUpdateMills;
	}

	public List<RateData> getRateForChart(String ratePair, int term) {
		if(term == FXRateGetHelper.TERM_REALTIME){
			for (int pair = 0; pair < VALID_PAIRS.length; pair++){
				if(VALID_PAIRS[pair].equals(ratePair)){
					return Lists.newArrayList(realTime[pair]);
				}
			}
			return null;
		}else{
			for (int pair = 0; pair < VALID_PAIRS.length; pair++){
				if(VALID_PAIRS[pair].equals(ratePair)){
					List<RateData> list = Lists.newArrayList();
					list.addAll(rateList[pair][term]);
					return list;
				}
			}
			return Lists.newArrayList();
		}
	}

	public RateData getEarliestRate(String pair) throws NoValidRateException {
		for (int i = 0; i < VALID_PAIRS.length; i++){
			if(VALID_PAIRS[i].equals(pair)){
				RateData data = realTime[i].isEmpty() ? null : realTime[i].get(0);
				if(FXRateGetHelper.isValidRateData(data)){
					return data;
				}else{
					throw new NoValidRateException();
				}
			}
		}
		throw new NoValidRateException();
	}

	public Map<String, RateData> getEarliestRate() {
		Map<String, RateData> map = Maps.newHashMap();
		for (int i = 0; i < VALID_PAIRS.length; i++){
			RateData data = realTime[i].isEmpty() ? null : realTime[i].get(0);
			if(FXRateGetHelper.isValidRateData(data)){
				map.put(VALID_PAIRS[i], data);
			}
		}
		return map;
	}

	public List<RateData> getAllRates(String ratePair) {
		for (int i = 0; i < VALID_PAIRS.length; i++){
			if(VALID_PAIRS[i].equals(ratePair)){
				List<RateData> list = Lists.newArrayList();
				list.addAll(realTime[i]);
				for (int term = 0; term < 6; term++){
					list.addAll(rateList[i][term]);
				}
				list.sort(FXRateGetHelper.DATA_COMPARATOR);
				return list;
			}
		}
		return Lists.newArrayList();
	}

	public RateData getTodaysOpen(String pair) {
		for (int i = 0; i < VALID_PAIRS.length; i++){
			if(VALID_PAIRS[i].equals(pair)){
				if(!rateList[i][FXRateGetHelper.TERM_1d].isEmpty()){
					return rateList[i][FXRateGetHelper.TERM_1d].get(0);
				}else{
					return RateData.NO_DATA;
				}
			}
		}
		return RateData.NO_DATA;
	}

	private void updateRealTimeRate() {
		Map<String, RateData> dataMap = FXRateGetHelper.getRealtimeData();
		for (int i = 0; i < VALID_PAIRS.length; i++){
			if(dataMap.containsKey(VALID_PAIRS[i])){
				realTime[i].add(0, dataMap.get(VALID_PAIRS[i]));
				if(realTime[i].size() > 200){
					for (int j = 200; j < realTime[i].size();){
						realTime[i].remove(j);
					}
				}
			}
		}
	}

	private void updateRate(int term) {
		for (int i = 0; i < VALID_PAIRS.length; i++){
			try{
				List<RateData> list = FXRateGetHelper.getHistory(VALID_PAIRS[i], FXRateGetHelper.REQUEST_TERMS[term]);
				//				if(list.size() < ((term == FXRateGetHelper.TERM_60m || term == FXRateGetHelper.TERM_1M) ? 100 : 190)){
				//					throw new IOException("Not Enough Data");
				//				}
				rateList[i][term].clear();
				rateList[i][term].addAll(list);
				rateList[i][term].sort(FXRateGetHelper.DATA_COMPARATOR);
			}catch (Exception e){
				//e.printStackTrace();
			}
		}
	}

	private long updateCount1m = 0;
	private long updateCount15m = 0;
	private long updateCount60m = 0;
	private long updateCount1d = 0;
	private long updateCount1w = 0;
	private long updateCount1M = 0;

	/**Update rate<br>
	 * Once per 4 seconds*/
	@Override
	public void run() {
		updateRealTimeRate();
		if(updateCount1m++ >= (int) (FXRateGetHelper.getTermMills(FXRateGetHelper.TERM_1m) / (float) updateInterval)){
			updateRate(FXRateGetHelper.TERM_1m);
			updateCount1m = 0;
		}
		if(updateCount15m++ >= (int) (FXRateGetHelper.getTermMills(FXRateGetHelper.TERM_15m) / (float) updateInterval)){
			updateRate(FXRateGetHelper.TERM_15m);
			updateCount15m = 0;
		}
		if(updateCount60m++ >= (int) (FXRateGetHelper.getTermMills(FXRateGetHelper.TERM_60m) / (float) updateInterval)){
			updateRate(FXRateGetHelper.TERM_60m);
			updateCount60m = 0;
		}
		if(updateCount1d++ >= (int) (FXRateGetHelper.getTermMills(FXRateGetHelper.TERM_1d) / (float) updateInterval)){
			updateRate(FXRateGetHelper.TERM_1d);
			updateCount1d = 0;
		}
		if(updateCount1w++ >= (int) (FXRateGetHelper.getTermMills(FXRateGetHelper.TERM_1w) / (float) updateInterval)){
			updateRate(FXRateGetHelper.TERM_1w);
			updateCount1w = 0;
		}
		if(updateCount1M++ >= (int) (FXRateGetHelper.getTermMills(FXRateGetHelper.TERM_1M) / (float) updateInterval)){
			updateRate(FXRateGetHelper.TERM_1M);
			updateCount1M = 0;
		}
		lastUpdateMills = System.currentTimeMillis();

		Map<String, RateData> map = Maps.newHashMap();
		for (int i = 0; i < VALID_PAIRS.length; i++){
			if(!realTime[i].isEmpty()){
				map.put(VALID_PAIRS[i], realTime[i].get(0));
			}
		}
		AccountHandler.instance.checkLosscutAndOrder(map);
	}

}