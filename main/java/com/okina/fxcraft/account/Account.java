package com.okina.fxcraft.account;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.okina.fxcraft.rate.RateData;

/**Use only on server*/
public class Account extends AccountInfo {

	protected String password;

	protected Account(String name, String password) {
		super(name);
		this.password = password;
	}

	/**Result.obj {positionID}*/
	public synchronized Result tryGetPosition(Calendar date, String pair, double lot, double deposit, double rate, boolean askOrBid) {
		if(deposit <= 0 || lot < deposit){
			return new Result(false, "Invalid Deposit");
		}
		if(deposit > balance){
			return new Result(false, "Not Enough Balance");
		}
		if(lot > DEAL_LIMIT[dealLotLimit]){
			return new Result(false, "Not Permitted Lot");
		}
		double leverage = lot / deposit;
		if(leverage > LEVERAGE_LIMIT[leverageLimit]){
			return new Result(false, "Not Permitted Leverage");
		}
		if(positionList.size() >= POSITION_LIMIT[positionLimit]){
			return new Result(false, "Not Permitted Position Count");
		}
		balance -= deposit;
		FXPosition position = new FXPosition(date, pair, lot, deposit, rate, askOrBid);
		positionList.add(position);
		addHistory(date, askOrBid ? "Ask" : "Bid", false, pair, lot, deposit, rate, -1);
		AccountUpdateHandler.instance.notifyAccountUpdate(this);
		return new Result(true, "Success", position.positionID);
	}

	/**Result.obj {orderID}*/
	public synchronized Result tryGetPositionOrder(Calendar date, String pair, double dealLot, double deposit, boolean askOrBid, double limits) {
		if(deposit <= 0 || dealLot < deposit){
			return new Result(false, "Invalid Deposit");
		}
		if(deposit > balance){
			return new Result(false, "Not Enough Balance");
		}
		if(!limitsTradePermission){
			return new Result(false, "Not Permitted To Trade with Limits");
		}
		if(dealLot > DEAL_LIMIT[dealLotLimit]){
			return new Result(false, "Not Permitted Lot");
		}
		double leverage = dealLot / deposit;
		if(leverage > LEVERAGE_LIMIT[leverageLimit]){
			return new Result(false, "Not Permitted Leverage");
		}
		if(positionList.size() >= POSITION_LIMIT[positionLimit]){
			return new Result(false, "Not Permitted Position Count");
		}
		balance -= deposit;
		GetPositionOrder order = new GetPositionOrder(date, pair, dealLot, deposit, askOrBid, limits);
		getPositionOrder.add(order);
		AccountUpdateHandler.instance.notifyAccountUpdate(this);
		return new Result(true, "Success", order.orderID);
	}

	/**Result.obj null*/
	public synchronized Result trySettlePosition(Calendar date, String positionID, double dealLot, double rate) {
		FXPosition position = null;
		for (FXPosition position1 : positionList){
			if(position1.positionID.equals(positionID)){
				position = position1;
				break;
			}
		}
		if(position == null){
			return new Result(false, "No Such Position");
		}
		if(position.lot < dealLot){
			return new Result(false, "Invalid Lot");
		}
		FXPosition split = position.split(dealLot);
		if(position.lot <= 0){
			positionList.remove(position);
		}
		balance += split.getValue(rate);
		addHistory(date, "Settle", false, split.currencyPair, split.lot, split.depositLot, rate, split.getGain(rate));
		AccountUpdateHandler.instance.notifyAccountUpdate(this);
		return new Result(true, "Success");
	}

	/**Result.obj {orderID}*/
	public synchronized Result trySettlePositionOrder(Calendar date, String positionID, double dealLot, double limits) {
		FXPosition position = null;
		for (FXPosition position1 : positionList){
			if(position1.positionID.equals(positionID)){
				position = position1;
				break;
			}
		}
		if(position == null){
			return new Result(false, "No Such Position");
		}
		if(position.lot < dealLot){
			return new Result(false, "Invalid Lot");
		}
		if(!limitsTradePermission){
			return new Result(false, "Not Permitted To Trade with Limits");
		}
		FXPosition split = position.split(dealLot);
		if(position.lot <= 0){
			positionList.remove(position);
		}
		SettlePositionOrder order = new SettlePositionOrder(date, split, limits);
		settlePositionOrder.add(order);
		AccountUpdateHandler.instance.notifyAccountUpdate(this);
		return new Result(true, "Success", order.position.positionID);
	}

	/**Result.obj null*/
	public synchronized Result tryDeleteGetOrder(String orderID) {
		GetPositionOrder order = null;
		for (GetPositionOrder order1 : getPositionOrder){
			if(order1.orderID.equals(orderID)){
				order = order1;
				break;
			}
		}
		if(order == null){
			return new Result(false, "Invalid Order");
		}
		getPositionOrder.remove(order);
		balance += order.depositLot;
		AccountUpdateHandler.instance.notifyAccountUpdate(this);
		return new Result(true, "Success");
	}

	/**Result.obj null*/
	public synchronized Result tryDeleteSettleOrder(String orderID) {
		SettlePositionOrder order = null;
		for (SettlePositionOrder order1 : settlePositionOrder){
			if(order1.position.positionID.equals(orderID)){
				order = order1;
				break;
			}
		}
		if(order == null){
			return new Result(false, "Invalid Order");
		}
		settlePositionOrder.remove(order);
		positionList.add(order.position);
		AccountUpdateHandler.instance.notifyAccountUpdate(this);
		return new Result(true, "Success");
	}

	public synchronized boolean checkLosscutFromPast(Map<String, List<RateData>> dataMap) {
		boolean change = false;
		flag1: for (int i = 0; i < positionList.size(); i++){
			FXPosition position = positionList.get(i);
			if(dataMap.containsKey(position.currencyPair)){
				List<RateData> dataList = dataMap.get(position.currencyPair);
				for (int j = dataList.size() - 1; j >= 0; j--){
					RateData data = dataList.get(j);
					if(position.contractDate.compareTo(data.calendar) <= 0 && position.getValue(data.open) <= 0){
						positionList.remove(i);
						addHistory(data.calendar, position.askOrBid ? "Ask" : "Bid", false, position.currencyPair, position.lot, position.depositLot, data.open, -position.depositLot);
						i--;
						change = true;
						continue flag1;
					}
				}
			}
		}
		if(change){
			AccountUpdateHandler.instance.notifyAccountUpdate(this);
		}
		return change;
	}

	public synchronized boolean checkOrderFromPast(Map<String, List<RateData>> dataMap) {
		boolean change = false;
		boolean delete = false;
		flag1: for (int i = 0; i < getPositionOrder.size(); i++){
			GetPositionOrder order = getPositionOrder.get(i);
			double leverage = order.lot / order.depositLot;
			if(order.depositLot <= 0 || order.lot < order.depositLot || order.lot > DEAL_LIMIT[dealLotLimit] || leverage > LEVERAGE_LIMIT[leverageLimit]){
				getPositionOrder.remove(order);
				balance += order.depositLot;
				delete = true;
				continue flag1;
			}
			if(positionList.size() >= POSITION_LIMIT[positionLimit]){
				continue flag1;
			}
			if(dataMap.containsKey(order.currencyPair)){
				List<RateData> dataList = dataMap.get(order.currencyPair);
				for (int j = dataList.size() - 1; j >= 0; j--){
					RateData data = dataList.get(j);
					if(order.checkConstruct(data)){
						FXPosition position = new FXPosition(data.calendar, data.open, order);
						positionList.add(position);
						getPositionOrder.remove(i);
						addHistory(data.calendar, order.askOrBid ? "Ask" : "Bid", true, order.currencyPair, order.lot, order.depositLot, data.open, -1);
						i--;
						change = true;
						continue flag1;
					}
				}
			}
		}
		flag2: for (int i = 0; i < settlePositionOrder.size(); i++){
			SettlePositionOrder order = settlePositionOrder.get(i);
			FXPosition position = order.position;
			if(dataMap.containsKey(position.currencyPair)){
				List<RateData> dataList = dataMap.get(position.currencyPair);
				for (int j = dataList.size() - 1; j >= 0; j--){
					RateData data = dataList.get(j);
					if(order.checkConstruct(data)){
						balance += position.getValue(data.open);
						settlePositionOrder.remove(i);
						addHistory(data.calendar, "Settle", true, position.currencyPair, position.lot, position.depositLot, data.open, position.getGain(data.open));
						i--;
						change = true;
						continue flag2;
					}
				}
			}
		}
		if(change || delete){
			AccountUpdateHandler.instance.notifyAccountUpdate(this);
		}
		return change;
	}

	public synchronized boolean checkLosscut(Map<String, RateData> dataMap) {
		boolean change = false;
		for (int i = 0; i < positionList.size(); i++){
			FXPosition position = positionList.get(i);
			if(dataMap.containsKey(position.currencyPair)){
				RateData data = dataMap.get(position.currencyPair);
				if(position.contractDate.compareTo(data.calendar) <= 0 && position.getValue(data.open) <= 0){
					positionList.remove(i);
					addHistory(data.calendar, position.askOrBid ? "Ask" : "Bid", false, position.currencyPair, position.lot, position.depositLot, data.open, -position.depositLot);
					i--;
					change = true;
					continue;
				}
			}
		}
		if(change){
			AccountUpdateHandler.instance.notifyAccountUpdate(this);
		}
		return change;
	}

	public synchronized boolean checkOrder(Map<String, RateData> dataMap) {
		boolean change = false;
		boolean delete = false;
		flag1: for (int i = 0; i < getPositionOrder.size(); i++){
			GetPositionOrder order = getPositionOrder.get(i);
			double leverage = order.lot / order.depositLot;
			if(order.depositLot <= 0 || order.lot < order.depositLot || order.lot > DEAL_LIMIT[dealLotLimit] || leverage > LEVERAGE_LIMIT[leverageLimit]){
				getPositionOrder.remove(order);
				balance += order.depositLot;
				delete = true;
				continue flag1;
			}
			if(positionList.size() >= POSITION_LIMIT[positionLimit]){
				continue flag1;
			}
			if(dataMap.containsKey(order.currencyPair)){
				RateData data = dataMap.get(order.currencyPair);
				if(order.checkConstruct(data)){
					FXPosition position = new FXPosition(data.calendar, data.open, order);
					positionList.add(position);
					getPositionOrder.remove(i);
					addHistory(data.calendar, order.askOrBid ? "Ask" : "Bid", true, order.currencyPair, order.lot, order.depositLot, data.open, -1);
					i--;
					change = true;
					continue flag1;
				}
			}
		}
		flag2: for (int i = 0; i < settlePositionOrder.size(); i++){
			SettlePositionOrder order = settlePositionOrder.get(i);
			FXPosition position = order.position;
			if(dataMap.containsKey(position.currencyPair)){
				RateData data = dataMap.get(position.currencyPair);
				if(order.checkConstruct(data)){
					balance += position.getValue(data.open);
					settlePositionOrder.remove(i);
					addHistory(data.calendar, "Settle", true, position.currencyPair, position.lot, position.depositLot, data.open, position.getGain(data.open));
					i--;
					change = true;
					continue flag2;
				}
			}
		}
		if(change || delete){
			AccountUpdateHandler.instance.notifyAccountUpdate(this);
		}
		return change;
	}

	//dealType : Ask, Bid, Settle
	private void addHistory(Calendar date, String dealType, boolean isLimits, String pair, double lot, double deposit, double rate, double gain) {
		if("Ask".equals(dealType)){

		}else if("Bid".equals(dealType)){

		}else if("Settle".equals(dealType)){
			totalDeal += lot;
			totalMovedDeposit += deposit;
			if(isLimits){
				totalLimitsDeal += lot;
			}
			if(gain >= 0){
				totalGain += gain;
			}else{
				totalLoss += -gain;
			}
		}else{
			throw new IllegalArgumentException();
		}

		history.add(new FXDealHistory(date, dealType, isLimits, pair, lot, deposit, rate, gain));
		for (int i = 30; i < history.size();){
			history.remove(i);
		}
	}

	public AccountInfo getInfo() {
		AccountInfo info = new AccountInfo(name);
		info.balance = balance;
		info.totalDeal = totalDeal;
		info.totalLimitsDeal = totalLimitsDeal;
		info.totalGain = totalGain;
		info.totalLoss = totalLoss;
		info.dealLotLimit = dealLotLimit;
		info.leverageLimit = leverageLimit;
		info.positionLimit = positionLimit;
		info.limitsTradePermission = limitsTradePermission;

		for (String reward : receivableReward){
			info.receivableReward.add(reward);
		}
		for (String reward : receivedReward){
			info.receivedReward.add(reward);
		}
		for (FXPosition pos : positionList){
			info.positionList.add(pos.clone());
		}
		for (GetPositionOrder order : getPositionOrder){
			info.getPositionOrder.add(order.clone());
		}
		for (SettlePositionOrder order : settlePositionOrder){
			info.settlePositionOrder.add(order.clone());
		}
		for (FXDealHistory history : this.history){
			info.history.add(history.clone());
		}
		return info;
	}

	public class Result {

		public boolean success = false;
		public String message;
		public Object[] obj;

		public Result(boolean success, String message, Object... obj) {
			this.success = success;
			this.message = message;
			this.obj = obj;
		}

	}

}
