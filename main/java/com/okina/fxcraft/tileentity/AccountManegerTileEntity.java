package com.okina.fxcraft.tileentity;

import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.AccountUpdateHandler;
import com.okina.fxcraft.account.FXDeal;
import com.okina.fxcraft.account.FXDealLimit;
import com.okina.fxcraft.account.IFXDealer;
import com.okina.fxcraft.account.Reward;
import com.okina.fxcraft.client.gui.account_manager.AccountManagerContainer;
import com.okina.fxcraft.client.gui.account_manager.AccountManagerGui;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.main.GuiHandler.IGuiTile;
import com.okina.fxcraft.network.ISimpleTilePacketUser;
import com.okina.fxcraft.network.PacketType;
import com.okina.fxcraft.network.SimpleTilePacket;
import com.okina.fxcraft.utils.Position;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class AccountManegerTileEntity extends TileEntity implements IGuiTile, ISimpleTilePacketUser, IFXDealer {

	private long lastAccountUpdate = 100;
	private AccountInfo loginAccount;

	//client only
	public int lastOpenedTab = 0;
	public String checkingAccountName = "";
	public String checkedAccountName;
	public boolean accountCheck = true;

	/**Client only*/
	public void checkAccount(String checkingAccountName) {
		if(!AccountHandler.instance.checkIsValidAccountName(checkingAccountName)){
			this.checkingAccountName = checkingAccountName;
			checkedAccountName = checkingAccountName;
			accountCheck = false;
			return;
		}
		FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_CHECK, checkingAccountName));
		this.checkingAccountName = checkingAccountName;
		accountCheck = true;
	}

	public void tryMakeAccount(String name, String password) {
		if(AccountHandler.instance.checkIsValidAccountName(name)){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("name", name);
			tag.setString("password", password);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_MAKE, tag));
			return;
		}
		return;
	}

	public void tryLogIn(String name, String password) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		tag.setString("password", password);
		FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGIN, tag));
	}

	public boolean hasAccountUpdate(long lastAccountUpdate) {
		return this.lastAccountUpdate > lastAccountUpdate;
	}

	public AccountInfo getAccountInfo() {
		return loginAccount;
	}

	public void logOut() {
		FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGOUT, 0));
		loginAccount = null;
		lastAccountUpdate = System.currentTimeMillis();
	}

	public void tryDispose(EntityPlayer player, int emerald) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setInteger("emerald", emerald);
			tag.setString("player", player.getCommandSenderName());
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_DISPOSE, tag));
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	public void tryRealize(int emerald) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setInteger("emerald", emerald);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_REALIZE, tag));
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	public void tryLimitRelease(EntityPlayer player, FXDealLimit limit) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setString("type", limit.name());
			tag.setString("player", player.getCommandSenderName());
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_LIMIT_RELEASE, tag));
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	public void tryGetReward(EntityPlayer player, String reward) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setString("reward", reward);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_REWARD, tag));
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void validate() {
		super.validate();
		AccountUpdateHandler.instance.registerUpdateObject(this);
	}

	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	/**Server only*/
	@Override
	public void updateAccountInfo(AccountInfo account) {
		if(account.equals(loginAccount)){
			loginAccount = account;
			lastAccountUpdate = System.currentTimeMillis();
			NBTTagCompound tag = new NBTTagCompound();
			account.writeToNBT(tag);
			FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_UPDATE, tag));
		}
	}

	@Override
	public void receiveResult(FXDeal deal, boolean success, String message, Object... obj) {
		ForgeDirection dir = ForgeDirection.getOrientation(getBlockMetadata());
		if(deal == FXDeal.REALIZE){
			if(success){
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5 + dir.offsetX, yCoord + 0.5 + dir.offsetY, zCoord + 0.5 + dir.offsetZ, new ItemStack(Items.emerald, (Integer) obj[1])));
			}
		}else if(deal == FXDeal.REWARD){
			if(success){
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5 + dir.offsetX, yCoord + 0.5 + dir.offsetY, zCoord + 0.5 + dir.offsetZ, ((Reward) obj[1]).getItem()));
			}
		}
		FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, deal + ": " + message));
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new AccountManagerContainer(player) : new AccountManagerGui(player, this);
	}

	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		return null;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){//server
			if(type == PacketType.ACCOUNT_CHECK && value instanceof String){
				String name = (String) value;
				boolean check = AccountHandler.instance.checkIsValidAccountName(name);
				FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_CHECK, check ? "1" : "0" + name));
			}else if(type == PacketType.ACCOUNT_MAKE && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String name = tag.getString("name");
				String password = tag.getString("password");
				AccountInfo info = AccountHandler.instance.addAccount(name, password);
				if(info != null){
					tag.setBoolean("result", true);
					tag.removeTag("password");
					NBTTagCompound infoTag = new NBTTagCompound();
					info.writeToNBT(infoTag);
					tag.setTag("info", infoTag);
					loginAccount = info;
					lastAccountUpdate = System.currentTimeMillis();
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_MAKE, tag));
				}else{
					tag.setBoolean("result", false);
					tag.removeTag("password");
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_MAKE, tag));
				}
			}else if(type == PacketType.ACCOUNT_LOGIN && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String name = tag.getString("name");
				String password = tag.getString("password");
				if(AccountHandler.instance.canLogIn(name, password)){
					AccountInfo info = AccountHandler.instance.getAccountInfo(name);
					NBTTagCompound infoTag = new NBTTagCompound();
					info.writeToNBT(infoTag);
					tag.setTag("info", infoTag);
					tag.setBoolean("result", true);
					tag.removeTag("password");
					loginAccount = info;
					lastAccountUpdate = System.currentTimeMillis();
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGIN, tag));
				}else{
					tag.setBoolean("result", false);
					tag.removeTag("password");
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGIN, tag));
				}
			}else if(type == PacketType.ACCOUNT_LOGOUT && value instanceof Integer){
				FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGOUT, 0));
				loginAccount = null;
				lastAccountUpdate = System.currentTimeMillis();
			}else if(type == PacketType.ACCOUNT_DISPOSE && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					int emerald = tag.getInteger("emerald");
					String playerName = tag.getString("player");
					EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(playerName);
					if(player == null){
						FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Invalid Player"));
					}else{
						AccountHandler.instance.tryDispose(this, player.inventory, accountName, emerald);
					}
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.ACCOUNT_REALIZE && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					int emerald = tag.getInteger("emerald");
					AccountHandler.instance.tryRealize(this, accountName, emerald);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.ACCOUNT_LIMIT_RELEASE && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					FXDealLimit limit = FXDealLimit.valueOf(tag.getString("type"));
					String playerName = tag.getString("player");
					EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(playerName);
					if(player == null){
						FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Invalid Player"));
					}else{
						AccountHandler.instance.tryLimitRelease(this, player.inventory, accountName, limit);
					}
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.ACCOUNT_REWARD && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String reward = tag.getString("reward");
					AccountHandler.instance.tryGetReward(this, accountName, reward);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}
		}else{//client
			if(type == PacketType.ACCOUNT_CHECK && value instanceof String){
				String str = (String) value;
				if(str.length() >= 1){
					accountCheck = str.charAt(0) == '1';
					checkedAccountName = str.substring(1);
				}
			}else if(type == PacketType.ACCOUNT_MAKE && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String name = tag.getString("name");
				Boolean result = tag.getBoolean("result");
				if(result){
					AccountInfo info = new AccountInfo(name);
					info.readFromNBT(tag.getCompoundTag("info"));
					loginAccount = info;
					lastAccountUpdate = System.currentTimeMillis();
					FXCraft.proxy.appendPopUp("Make : " + name);
					FXCraft.proxy.appendPopUp("LogIn : " + name);
					accountCheck = true;
					checkedAccountName = name;
				}else{
					FXCraft.proxy.appendPopUp("Make failed: " + name);
					accountCheck = false;
					checkedAccountName = name;
				}
			}else if(type == PacketType.ACCOUNT_LOGIN && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String name = tag.getString("name");
				Boolean result = tag.getBoolean("result");
				if(result){
					AccountInfo info = new AccountInfo(name);
					info.readFromNBT(tag.getCompoundTag("info"));
					loginAccount = info;
					lastAccountUpdate = System.currentTimeMillis();
					accountCheck = true;
					checkedAccountName = name;
					FXCraft.proxy.appendPopUp("LogIn: " + name);
				}else{
					accountCheck = false;
					checkedAccountName = name;
					FXCraft.proxy.appendPopUp("LogIn failed: " + name);
				}
			}else if(type == PacketType.ACCOUNT_LOGOUT && value instanceof Integer){
				if(loginAccount != null){
					loginAccount = null;
					lastAccountUpdate = System.currentTimeMillis();
				}
				FXCraft.proxy.appendPopUp("LogOut");
			}else if(type == PacketType.ACCOUNT_UPDATE && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				if(tag.hasKey("name")){
					String name = tag.getString("name");
					AccountInfo info = new AccountInfo(name);
					info.readFromNBT(tag);
					loginAccount = info;
				}else{
					loginAccount = null;
				}
				lastAccountUpdate = System.currentTimeMillis();
			}else if(type == PacketType.MESSAGE && value instanceof String){
				FXCraft.proxy.appendPopUp((String) value);
			}
		}
	}

	@Override
	public Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

}
