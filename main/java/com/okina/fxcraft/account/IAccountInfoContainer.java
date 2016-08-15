package com.okina.fxcraft.account;

public interface IAccountInfoContainer {

	public AccountInfo getAccountInfo();

	public void updateAccountInfo(AccountInfo account);

	public boolean hasAccountUpdate(long lastAccountUpdate);

	public boolean isValid();

}
