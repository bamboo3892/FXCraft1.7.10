package com.okina.fxcraft.account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.okina.fxcraft.account.Account.Result;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.rate.NoValidRateException;
import com.okina.fxcraft.rate.RateData;
import com.okina.fxcraft.utils.InventoryHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**Basically server only*/
public class AccountHandler {

	public static final AccountHandler instance = new AccountHandler();

	private List<Account> accountList = Collections.<Account> synchronizedList(Lists.<Account> newArrayList());
	private ExecutorService accountExec = Executors.newSingleThreadExecutor();
	private ExecutorService exec = Executors.newSingleThreadExecutor();

	public void readFromFile() {
		BufferedReader reader = null;
		try{
			Gson gson = new Gson();
			File file = new File(FXCraft.ConfigFile.getAbsolutePath() + File.separator + FXCraft.MODID + "_account.properties");
			reader = new BufferedReader(new FileReader(file));
			accountList = gson.<List<Account>> fromJson(reader, new TypeToken<Collection<Account>>() {
			}.getType());
		}catch (FileNotFoundException e){
			System.out.println("Make file config/FXCraft_account.properties");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}finally{
			if(reader != null){
				try{
					reader.close();
				}catch (IOException e){
					e.printStackTrace(System.out);
				}
			}
		}
		if(accountList == null) accountList = Lists.newArrayList();

		System.out.println("FXCraft Accounts////////////////////////////////////////////////////////////////////");
		for (Account account : accountList){
			System.out.println(account.name);
		}
		System.out.println("////////////////////////////////////////////////////////////////////");

		updatePropertyFile();
	}

	public void updatePropertyFile() {
		accountExec.submit(new Runnable() {
			@Override
			public void run() {
				PrintWriter writer = null;
				try{
					JsonObject o;
					Gson gson = new Gson();
					File file = new File(FXCraft.ConfigFile.getAbsolutePath() + File.separator + FXCraft.MODID + "_account.properties");
					writer = new PrintWriter(new FileWriter(file));
					String json = gson.toJson(accountList);
					JSONArray obj = new JSONArray(json);
					writer.print(obj.toString(2));
					//					json = json.replaceAll("~", "");
					//					json = json.replaceAll("\\{", "~\n");
					//					json = json.replaceAll("~", "{");
					//					json = json.replaceAll("\\}", "\n~");
					//					json = json.replaceAll("~", "}");
					//					json = json.replaceAll("\\,", "~\n");
					//					json = json.replaceAll("~", ",");
					//					writer.print(json);
				}catch (Exception e){
					e.printStackTrace();
				}finally{
					if(writer != null) writer.close();
				}
			}

			private String format(String json, String indent) {
				return null;
			}
		});
	}

	/**This method can use on client*/
	public boolean checkIsValidAccountName(String name) {
		if(name == null || name.equals("") || name.matches("¥W")) return false;
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			for (Account account : accountList){
				if(account.name.equals(name)) return false;
			}
		}
		return true;
	}

	public AccountInfo addAccount(String name, String password) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			return null;
		}
		if(!checkIsValidAccountName(name) || password == null || password.matches("¥W")) return null;
		Account account = new Account(name, password);
		accountList.add(account);
		updatePropertyFile();
		return account.getInfo();
	}

	public boolean hasAccount(String name) {
		for (Account account : accountList){
			if(account.name.equals(name)){
				return true;
			}
		}
		return false;
	}

	private Account getAccount(String name) {
		for (Account account : accountList){
			if(account.name.equals(name)){
				return account;
			}
		}
		return null;
	}

	public AccountInfo getAccountInfo(String name) {
		for (Account account : accountList){
			if(account.name.equals(name)){
				return account.getInfo();
			}
		}
		return null;
	}

	public boolean canLogIn(String name, String password) {
		for (Account account : accountList){
			if(account.name.equals(name)){
				if(account.password.equals(password)){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

	/**Return result {accountName, emerald}*/
	public void tryDispose(final IFXDealer dealer, final IInventory inv, final String accountName, final int emerald) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				Account account = getAccount(accountName);
				if(account == null){
					dealer.receiveResult(FXDeal.DISPOSE, false, "No Such Account " + accountName, accountName, emerald);
					return;
				}
				if(!InventoryHelper.tryConsumeItem(inv, new ItemStack(Items.emerald, emerald))){
					dealer.receiveResult(FXDeal.DISPOSE, false, "No Enough Emerald", accountName, emerald);
					return;
				}
				account.balance += emerald * 1000;
				AccountUpdateHandler.instance.notifyAccountUpdate(account);
				dealer.receiveResult(FXDeal.DISPOSE, true, "Success", accountName, emerald);
			}
		});
	}

	/**Return result {accountName, emerald}*/
	public void tryRealize(final IFXDealer dealer, final String accountName, final int emerald) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				Account account = getAccount(accountName);
				if(account == null){
					dealer.receiveResult(FXDeal.REALIZE, false, "No Such Account " + accountName, accountName, emerald);
					return;
				}
				if(account.balance < emerald * 1000){
					dealer.receiveResult(FXDeal.REALIZE, false, "Not Enough Balance", accountName, emerald);
					return;
				}
				account.balance -= emerald * 1000;
				AccountUpdateHandler.instance.notifyAccountUpdate(account);
				dealer.receiveResult(FXDeal.REALIZE, true, "Success", accountName, emerald);
			}
		});
	}

	/**Return result {accountName, LimitType}*/
	public void tryLimitRelease(final IFXDealer dealer, final IInventory inv, final String accountName, final FXDealLimit limit) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				Account account = getAccount(accountName);
				if(account == null){
					dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "No Such Account " + accountName, accountName, limit);
					return;
				}
				if(limit == FXDealLimit.LOT){
					int level = account.dealLotLimit + 1;
					if(level < 1){
						dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "Invalid Level", accountName, limit);
						return;
					}else if(level > 5){
						dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "Max Level", accountName, limit);
						return;
					}else{
						if(!InventoryHelper.tryConsumeItem(inv, new ItemStack(FXCraft.limit_dealLot[level - 1], 1))){
							dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "No Valid License", accountName, limit);
							return;
						}
						account.dealLotLimit = level;
					}
				}else if(limit == FXDealLimit.LEVERAGE){
					int level = account.leverageLimit + 1;
					if(level < 1){
						dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "Invalid Level", accountName, limit);
						return;
					}else if(level > 5){
						dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "Max Level", accountName, limit);
						return;
					}else{
						if(!InventoryHelper.tryConsumeItem(inv, new ItemStack(FXCraft.limit_leverage[level - 1], 1))){
							dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "No Valid License", accountName, limit);
							return;
						}
						account.leverageLimit = level;
					}
				}else if(limit == FXDealLimit.POSITION){
					int level = account.positionLimit + 1;
					if(level < 1){
						dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "Invalid Level", accountName, limit);
						return;
					}else if(level > 5){
						dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "Max Level", accountName, limit);
						return;
					}else{
						if(!InventoryHelper.tryConsumeItem(inv, new ItemStack(FXCraft.limit_position[level - 1], 1))){
							dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "No Valid License", accountName, limit);
							return;
						}
						account.positionLimit = level;
					}
				}else if(limit == FXDealLimit.LIMITS_TRADE){
					if(account.limitsTradePermission){
						dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "Already Permitted", accountName, limit);
						return;
					}else{
						if(!InventoryHelper.tryConsumeItem(inv, new ItemStack(FXCraft.limit_limits_trade, 1))){
							dealer.receiveResult(FXDeal.LIMIT_RELEASE, false, "No Valid License", accountName, limit);
							return;
						}
						account.limitsTradePermission = true;
					}
				}
				AccountUpdateHandler.instance.notifyAccountUpdate(account);
				dealer.receiveResult(FXDeal.LIMIT_RELEASE, true, "Success", accountName, limit);
			}
		});
	}

	/**Return result {accountName, reward}*/
	public void tryGetReward(final IFXDealer dealer, final String accountName, final String reward) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try{
					Account account = getAccount(accountName);
					if(account == null){
						dealer.receiveResult(FXDeal.REWARD, false, "No Such Account " + accountName, accountName);
					}else{
						Reward re = RewardRegister.instance.getReward(reward);
						if(re != null){
							if(account.receivableReward.contains(reward)){
								if(!account.receivedReward.contains(reward)){
									account.receivedReward.add(reward);
									AccountUpdateHandler.instance.notifyAccountUpdate(account);
									dealer.receiveResult(FXDeal.REWARD, true, "Success", accountName, re);
								}else{
									dealer.receiveResult(FXDeal.REWARD, false, "Already Received Reward", accountName);
								}
							}else{
								dealer.receiveResult(FXDeal.REWARD, false, "Cannot Receive This Reward", accountName);
							}
						}else{
							dealer.receiveResult(FXDeal.REWARD, false, "No Such Reward " + reward, accountName);
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**Return result<br>
	 * Fail : {accountName}<br>
	 * Success : {accountName, positionID}*/
	public void tryGetPosition(final IFXDealer dealer, final String accountName, final String pair, final double dealLot, final double deposit, final boolean askOrBid) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try{
					Account account = getAccount(accountName);
					if(account == null){
						dealer.receiveResult(FXDeal.GET_POSITION, false, "No Such Account " + accountName, accountName);
					}else{
						RateData rate = FXCraft.rateGetter.getEarliestRate(pair);
						Result result = account.tryGetPosition(Calendar.getInstance(), pair, dealLot, deposit, rate.open, askOrBid);
						if(result.obj == null || result.obj.length == 0){
							dealer.receiveResult(FXDeal.GET_POSITION, result.success, result.message, accountName);
						}else{
							dealer.receiveResult(FXDeal.GET_POSITION, result.success, result.message, accountName, result.obj[0]);
						}
					}
				}catch (NoValidRateException e){
					dealer.receiveResult(FXDeal.GET_POSITION, false, "No Valid Rate", accountName);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**Return result<br>
	 * Fail : {accountName}<br>
	 * Success : {accountName, orderID}*/
	public void tryGetPositionOrder(final IFXDealer dealer, final String accountName, final String pair, final double dealLot, final double deposit, final boolean askOrBid, final double limits) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try{
					Account account = getAccount(accountName);
					if(account == null){
						dealer.receiveResult(FXDeal.GET_POSITION_ORDER, false, "No Such Account " + accountName, accountName);
					}else{
						Result result = account.tryGetPositionOrder(Calendar.getInstance(), pair, dealLot, deposit, askOrBid, limits);
						if(result.obj == null || result.obj.length == 0){
							dealer.receiveResult(FXDeal.GET_POSITION_ORDER, result.success, result.message, accountName);
						}else{
							dealer.receiveResult(FXDeal.GET_POSITION_ORDER, result.success, result.message, accountName, result.obj[0]);
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**Return result<br>
	 * Fail : {accountName}<br>
	 * Success : {accountName, constructRate}*/
	public void trySettlePosition(final IFXDealer dealer, final String accountName, final String positionID, final double dealLot) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try{
					Account account = getAccount(accountName);
					if(account == null){
						dealer.receiveResult(FXDeal.SETTLE_POSITION, false, "No Such Account " + accountName, accountName);
					}else{
						FXPosition position = account.getPosition(positionID);
						if(position == null){
							dealer.receiveResult(FXDeal.SETTLE_POSITION, false, "No Such Position", accountName);
						}else{
							RateData rate = FXCraft.rateGetter.getEarliestRate(position.currencyPair);
							Result result = account.trySettlePosition(Calendar.getInstance(), positionID, dealLot, rate.open);
							if(result.success){
								dealer.receiveResult(FXDeal.SETTLE_POSITION, result.success, result.message, accountName, rate.open);
							}else{
								dealer.receiveResult(FXDeal.SETTLE_POSITION, result.success, result.message, accountName);
							}
						}
					}
				}catch (NoValidRateException e){
					dealer.receiveResult(FXDeal.SETTLE_POSITION, false, "No Valid Rate", accountName);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**Return result<br>
	 * Fail : {accountName}<br>
	 * Success : {accountName, orderID}*/
	public void trySettlePositionOrder(final IFXDealer dealer, final String accountName, final String positionID, final double dealLot, final double limits) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try{
					Account account = getAccount(accountName);
					if(account == null){
						dealer.receiveResult(FXDeal.SETTLE_POSITION_ORDER, false, "No Such Account " + accountName, accountName);
					}else{
						Result result = account.trySettlePositionOrder(Calendar.getInstance(), positionID, dealLot, limits);
						if(result.success){
							dealer.receiveResult(FXDeal.SETTLE_POSITION_ORDER, result.success, result.message, result.obj[0]);
						}else{
							dealer.receiveResult(FXDeal.SETTLE_POSITION_ORDER, result.success, result.message);
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	/**Return result {accountName, orderID}*/
	public void tryDeleteOrder(final IFXDealer dealer, final String accountName, final String orderID, final boolean isGetOrder) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try{
					Account account = getAccount(accountName);
					if(account == null){
						dealer.receiveResult(isGetOrder ? FXDeal.DELETE_GET_ORDER : FXDeal.DELETE_SETTLE_ORDER, false, "No Such Account " + accountName, accountName, orderID);
					}else{
						if(isGetOrder){
							Result result = account.tryDeleteGetOrder(orderID);
							dealer.receiveResult(FXDeal.DELETE_GET_ORDER, result.success, result.message, accountName, orderID);
						}else{
							Result result = account.tryDeleteSettleOrder(orderID);
							dealer.receiveResult(FXDeal.DELETE_SETTLE_ORDER, result.success, result.message, accountName, orderID);
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public void checkLosscutAndOrderFromPast(final Map<String, List<RateData>> dataMap) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try{
					for (Account account : accountList){
						if(account.checkOrderFromPast(dataMap)){
							FXCraft.proxy.appendPopUp(account.name + ": Limit Trade Success");
						}
						if(account.checkLosscutFromPast(dataMap)){
							FXCraft.proxy.appendPopUp(account.name + ": Loss Cut!!! (Position Value Goes Below 0)");
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public void checkLosscutAndOrder(final Map<String, RateData> dataMap) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try{
					for (Account account : accountList){
						if(account.checkOrder(dataMap)){
							FXCraft.proxy.appendPopUp(account.name + ": Limit Trade Success");
						}
						if(account.checkLosscut(dataMap)){
							FXCraft.proxy.appendPopUp(account.name + ": Loss Cut!!! (Position Value Goes Below 0)");
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

}
