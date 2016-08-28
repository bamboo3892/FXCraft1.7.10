package com.okina.fxcraft.rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okina.fxcraft.utils.UtilMethods;

public class FXRateGetHelper {

	public static final int TERM_REALTIME = 10;
	public static final int TERM_1m = 0;
	public static final int TERM_15m = 1;
	public static final int TERM_60m = 2;
	public static final int TERM_1d = 3;
	public static final int TERM_1w = 4;
	public static final int TERM_1M = 5;

	//	private static String address = "https://www.google.com/finance/converter?a=1&from=AED&to=HKD";
	//	private static final String[] GOOGLE_ADDRESS = { "https://www.google.com/finance/converter?a=1&from=", "&to=" };
	//	private static final String GAITAME_ADDRESS = "http://www.gaitameonline.com/rateaj/getrate";
	/**
	 * code : usdjpy, eurusd, eurjpy<br>
	 * term : 1m, 15m, 60m, 1d, 1w, 1m<br><br>
	 * Sample<blockquote>http://info.finance.yahoo.co.jp/fx/async/getHistory/?code=usdjpy&candle_period=1m&results=200&start=1</blockquote><br>
	 */
	private static final String[] YAHOO_ADDRESS = { "http://info.finance.yahoo.co.jp/fx/async/getHistory/?code=", "&candle_period=", "&results=200&start=1" };
	private static final String YAHOO_REALTIME = "http://info.finance.yahoo.co.jp/fx/async/getRate/";

	public static final String[] REQUEST_TERMS = { "1m", "15m", "60m", "1d", "1w", "1M" };
	public static final Comparator<RateData> DATA_COMPARATOR = new Comparator<RateData>() {
		@Override
		public int compare(RateData o1, RateData o2) {
			return -o1.calendar.compareTo(o2.calendar);
		}
	};

	public static List<RateData> getHistory(String pair, String term) {
		/*Data Format : { { Date, High, Low, Open, End } , { Date, High, Low, Open, End } , { Date, High, Low, Open, End } }*/
		try{
			URL url = new URL(YAHOO_ADDRESS[0] + pair + YAHOO_ADDRESS[1] + term + YAHOO_ADDRESS[2]);
			HttpURLConnection connection = null;
			try{
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
					InputStreamReader isr = null;
					BufferedReader reader = null;
					try{
						isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
						reader = new BufferedReader(isr);
						String line;
						List<RateData> dataList = Lists.newArrayList();
						while ((line = reader.readLine()) != null){
							line = line.replaceAll("\\[", "");
							line = line.replaceAll("\\]", "");
							line = line.replaceAll("\"", "");
							String[] datas = line.split(",");
							//2016-07-09 02:33:00
							//2015-11-16
							Calendar calendar = null;//2016-07-09 02:33:00
							double high = 0;
							double low = 0;
							double open = 0;
							for (int i = 0; i < datas.length; i++){
								int i2 = i % 5;
								if(i2 == 0){//calendar
									try{
										String[] str1 = datas[i].split("-");//2016, 07, 09 02:33:00
										String[] str2 = str1[2].split(" ");//09, 02:33:00
										String[] str3 = str2.length <= 1 ? null : str2[1].split(":");//02, 33, 00 or 02
										int year = Integer.parseInt(str1[0]);
										int month = Integer.parseInt(str1[1]) - 1;
										int date = Integer.parseInt(str2[0]);
										int hour = str3 == null ? 0 : Integer.parseInt(str3[0]);
										int minute = str3 == null ? 0 : Integer.parseInt(str3[1]);
										int second = str3 == null ? 0 : Integer.parseInt(str3[2]);
										calendar = new GregorianCalendar(year, month, date, hour, minute, second);
									}catch (Exception e){
										e.printStackTrace();
										System.out.println(datas[i]);
									}
								}else if(i2 == 1){//high
									high = Double.parseDouble(datas[i]);
								}else if(i2 == 2){//low
									low = Double.parseDouble(datas[i]);
								}else if(i2 == 3){//open
									open = Double.parseDouble(datas[i]);
								}else if(i2 == 4){//end
									double end = Double.parseDouble(datas[i]);
									dataList.add(new RateData(calendar, high, low, open));
									if(i + 5 >= datas.length) break;
								}
							}
						}
						return dataList;
					}finally{
						if(isr != null){
							isr.close();
						}
						if(reader != null){
							reader.close();
						}
					}
				}
			}finally{
				if(connection != null){
					connection.disconnect();
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, RateData> getRealtimeData() {
		Map<String, RateData> map = Maps.newHashMap();
		Calendar date = Calendar.getInstance();
		try{
			URL url = new URL(YAHOO_REALTIME);
			HttpURLConnection connection = null;
			try{
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
					InputStreamReader isr = null;
					BufferedReader reader = null;
					try{
						isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
						reader = new BufferedReader(isr);
						String line;
						StringBuilder builder = new StringBuilder();
						List<RateData> dataList = Lists.newArrayList();
						while ((line = reader.readLine()) != null){
							builder.append(line);
						}
						try{
							JSONObject json = new JSONObject(builder.toString());
							for (String pair : JSONObject.getNames(json)){
								JSONObject o = json.getJSONObject(pair);
								if(o.has("Ask")){
									map.put(pair, new RateData(date, o.getDouble("High"), o.getDouble("Low"), o.getDouble("Ask")));
								}
							}
						}catch (JSONException e){
							e.printStackTrace();
						}
					}finally{
						if(isr != null){
							isr.close();
						}
						if(reader != null){
							reader.close();
						}
					}
				}
			}finally{
				if(connection != null){
					connection.disconnect();
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		return map;
	}

	public static long getTermMills(int term) {
		switch (term) {
		case TERM_REALTIME:
			return 4000L;
		case TERM_1m:
			return 60000L;
		case TERM_15m:
			return 900000L;
		case TERM_60m:
			return 3600000L;
		case TERM_1d:
			return 86400000L;
		case TERM_1w:
			return 604800000L;
		case TERM_1M:
			return 2419200000L;
		default:
			throw new IllegalArgumentException(term + " is not available number.");
		}
	}

	public static String getCalendarString(Calendar calendar, int term) {
		switch (term) {
		case TERM_REALTIME:
			return calendar.get(Calendar.HOUR_OF_DAY) + ":" + UtilMethods.zeroFill(calendar.get(Calendar.MINUTE), 2) + ":" + UtilMethods.zeroFill(calendar.get(Calendar.SECOND), 2);
		case TERM_1m:
			return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + UtilMethods.zeroFill(calendar.get(Calendar.MINUTE), 2);
		case TERM_15m:
			return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + UtilMethods.zeroFill(calendar.get(Calendar.MINUTE), 2);
		case TERM_60m:
			return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + UtilMethods.zeroFill(calendar.get(Calendar.MINUTE), 2);
		case TERM_1d:
			return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
		case TERM_1w:
			return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
		case TERM_1M:
			return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
		default:
			return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + UtilMethods.zeroFill(calendar.get(Calendar.MINUTE), 2);
		}
	}

	public static boolean isValidRateData(RateData rate) {
		//TODO
		return rate == null ? false : Calendar.getInstance().getTimeInMillis() - rate.calendar.getTimeInMillis() <= 10000;
		//		return rate != null;
	}

	private class RawRateDatas {
		List<RawRateData> quotes;
	}

	private class RawRateData {
		private String currencyPairCode;
		private double high;
		private double open;
		private double bid;
		private double ask;
		private double low;

		private RateData getData(Calendar calendar) {
			RateData data = new RateData(calendar, high, low, open);
			return data;
		}
	}

}
