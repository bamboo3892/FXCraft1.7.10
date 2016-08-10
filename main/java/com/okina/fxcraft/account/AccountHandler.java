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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.okina.fxcraft.account.IFXDealer.Result;
import com.okina.fxcraft.main.FXCraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

/**Basically server only*/
public class AccountHandler {

	public static final AccountHandler instance = new AccountHandler();

	private List<Account> accountList = Collections.<Account> synchronizedList(Lists.<Account> newArrayList());
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
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			new Thread(new Runnable() {
				@Override
				public void run() {
					PrintWriter writer = null;
					try{
						Gson gson = new Gson();
						File file = new File(FXCraft.ConfigFile.getAbsolutePath() + File.separator + FXCraft.MODID + "_account.properties");
						writer = new PrintWriter(new FileWriter(file));
						String json = gson.toJson(accountList);
						json = json.replaceAll("~", "");
						json = json.replaceAll("\\{", "~\n");
						json = json.replaceAll("~", "{");
						json = json.replaceAll("\\}", "\n~");
						json = json.replaceAll("~", "}");
						json = json.replaceAll("\\,", "~\n");
						json = json.replaceAll("~", ",");
						writer.print(json);
					}catch (Exception e){
						e.printStackTrace();
					}finally{
						if(writer != null) writer.close();
					}
				}
			}, "Write Account Property Thread").start();
		}
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

	public void tryGetPosition(final IFXDealer dealer, final String accountName, final String pair, final int dealLot, final int deposit, final boolean askOrBid) {
		exec.submit(new Runnable() {
			@Override
			public void run() {
				Account account = getAccount(accountName);
				if(account == null){
					dealer.receiveResult(FXDeal.GET_POSITION, Result.FAIL_NO_ACCOUNT, (Object[]) null);
				}else{
					double rate = FXCraft.rateGetter.getEarliestRate(pair);
					Result result = account.tryGetPosition(Calendar.getInstance(), pair, dealLot, deposit, rate, askOrBid);
					dealer.receiveResult(FXDeal.GET_POSITION, result, rate);
				}
			}
		});
	}

	public void tryGetPositionOrder(final IFXDealer dealer, final String accountName, final String pair, final int dealLot, final int deposit, final boolean askOrBid, final double limits) {
		exec.submit(new Runnable() {
			@Override
			public void run() {

			}
		});
	}

	public void trySettlePosition(final IFXDealer dealer, final String accountName, final FXPosition position, final int dealLot) {
		exec.submit(new Runnable() {
			@Override
			public void run() {

			}
		});
	}

	public void trySettlePositionOrder(final IFXDealer dealer, final String accountName, final FXPosition position, final int dealLot, final double limits) {
		exec.submit(new Runnable() {
			@Override
			public void run() {

			}
		});
	}

}
