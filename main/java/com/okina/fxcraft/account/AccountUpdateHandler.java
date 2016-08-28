package com.okina.fxcraft.account;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

public class AccountUpdateHandler {

	public static AccountUpdateHandler instance = new AccountUpdateHandler();

	private List<IAccountInfoContainer> containerList = Lists.newArrayList();

	private AccountUpdateHandler() {}

	/**Make sure to register valid object*/
	public void registerUpdateObject(IAccountInfoContainer container) {
		if(!containerList.contains(container)){
			containerList.add(Objects.requireNonNull(container));
		}
	}

	public void notifyAccountUpdate(Account account) {
		RewardRegister.instance.updateAccountReward(account);
		AccountInfo info = account.getInfo();
		for (int i = 0; i < containerList.size(); i++){
			IAccountInfoContainer container = containerList.get(i);
			if(container.isValid()){
				container.updateAccountInfo(info);
			}else{
				containerList.remove(i);
				i--;
			}
		}
		AccountHandler.instance.updatePropertyFile();
	}

}
