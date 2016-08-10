package com.okina.fxcraft.account;

import java.util.Calendar;

import com.okina.fxcraft.account.IFXDealer.Result;

/**Use only on server*/
public class Account extends AccountInfo {

	protected String password;

	protected Account(String name, String password) {
		super(name);
		this.password = password;
	}

	public Result tryGetPosition(Calendar date, String pair, int lot, int deposit, double rate, boolean askOrBid) {
		if(deposit <= 0 || lot < deposit){
			return Result.FAIL_ILLEGAL_PARAM;
		}
		if(lot > balance){
			return Result.FAIL_BALANCE_LACK;
		}
		if(lot > DEAL_LIMIT[dealLotLimit]){
			return Result.NO_PERMISSION_DEAL_LOT;
		}
		double leverage = lot / (double) deposit;
		if(leverage > LEVERAGE_LIMIT[leverageLimit]){
			return Result.NO_PERMISSION_LEVERAGE;
		}
		if(positionList.size() >= POSITION_LIMIT[positionLimit]){
			return Result.NO_PERMISSION_POSITION;
		}
		balance -= lot;
		FXPosition position = new FXPosition(date, pair, lot, deposit, rate, askOrBid);
		positionList.add(position);
		AccountUpdateHandler.instance.notifyAccountUpdate(getInfo());
		return Result.SUCCESS;
	}

	public Result tryGetPositionOrder(Calendar date, String pair, int lot, int deposit, boolean askOrBid, double limits) {
		if(deposit <= 0 || lot < deposit){
			return Result.FAIL_ILLEGAL_PARAM;
		}
		if(lot > balance){
			return Result.FAIL_BALANCE_LACK;
		}
		if(lot > DEAL_LIMIT[dealLotLimit]){
			return Result.NO_PERMISSION_DEAL_LOT;
		}
		double leverage = lot / (double) deposit;
		if(leverage > LEVERAGE_LIMIT[leverageLimit]){
			return Result.NO_PERMISSION_LEVERAGE;
		}
		if(positionList.size() >= POSITION_LIMIT[positionLimit]){
			return Result.NO_PERMISSION_POSITION;
		}
		balance -= lot;
		GetPositionOrder order = new GetPositionOrder(date, pair, lot, deposit, askOrBid, limits);
		getPositionOrder.add(order);
		AccountUpdateHandler.instance.notifyAccountUpdate(getInfo());
		return Result.SUCCESS;
	}

	public Result trySettlePosition(FXPosition position, int dealLot, double rate) {
		if(!positionList.contains(position) || position.lot < dealLot){
			return Result.FAIL_ILLEGAL_PARAM;
		}
		FXPosition split = position.split(dealLot);
		if(position.lot <= 0){
			positionList.remove(position);
		}
		balance += split.getValue(rate);
		AccountUpdateHandler.instance.notifyAccountUpdate(getInfo());
		return Result.SUCCESS;
	}

	public Result trySettlePositionOrder(FXPosition position, int dealLot, double limits) {
		if(!positionList.contains(position) || position.lot < dealLot){
			return Result.FAIL_ILLEGAL_PARAM;
		}
		FXPosition split = position.split(dealLot);
		if(position.lot <= 0){
			positionList.remove(position);
		}
		settlePositionOrder.add(new SettlePositionOrder(split, limits));
		AccountUpdateHandler.instance.notifyAccountUpdate(getInfo());
		return Result.SUCCESS;
	}

	public AccountInfo getInfo() {
		AccountInfo info = new AccountInfo(name);
		info.balance = balance;
		info.totalDeal = totalDeal;
		info.totalLimitsDeal = totalLimitsDeal;
		info.totalLimitsDeal = totalLimitsDeal;
		info.totalGain = totalGain;
		info.totalLoss = totalLoss;
		info.leverageLimit = leverageLimit;
		info.positionLimit = positionLimit;
		info.dealLotLimit = dealLotLimit;
		for (FXPosition pos : positionList){
			info.positionList.add(pos.clone());
		}
		for (GetPositionOrder order : getPositionOrder){
			info.getPositionOrder.add(order.clone());
		}
		for (SettlePositionOrder order : settlePositionOrder){
			info.settlePositionOrder.add(order.clone());
		}
		return info;
	}

}
