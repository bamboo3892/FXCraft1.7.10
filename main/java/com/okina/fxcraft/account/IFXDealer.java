package com.okina.fxcraft.account;

public interface IFXDealer extends IAccountInfoContainer {

	public void receiveResult(FXDeal deal, boolean success, String message, Object... obj);

	//	public void receiveResult(FXDeal deal, Result result, Object... obj);

	//	public enum Result {
	//
	//		SUCCESS(),
	//
	//		FAIL_ILLEGAL_PARAM(),
	//
	//		FAIL_NO_ACCOUNT(),
	//
	//		FAIL_NOT_ENOUGH_EMERALD(),
	//
	//		FAIL_NO_VALID_ITEM(),
	//
	//		FAIL_BALANCE_LACK(),
	//
	//		NO_PERMISSION_DEAL_LOT(),
	//
	//		NO_PERMISSION_LEVERAGE(),
	//
	//		NO_PERMISSION_POSITION(),
	//
	//		NO_PERMISSION_LIMITS_TRADE(),
	//
	//		NOT_CONSTRUCTED(),
	//
	//		FAIL_NO_VALID_RATE();
	//
	//	}

}
