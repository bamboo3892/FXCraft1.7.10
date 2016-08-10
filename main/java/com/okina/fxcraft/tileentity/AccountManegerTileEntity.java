package com.okina.fxcraft.tileentity;

import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class AccountManegerTileEntity extends TileEntity implements IGuiTile, ISimpleTilePacketUser {

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

	public AccountInfo getLogInAccount() {
		return loginAccount;
	}

	public void logOut() {
		FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGOUT, 0));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////

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
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGIN, tag));
				}else{
					tag.setBoolean("result", false);
					tag.removeTag("password");
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGIN, tag));
				}
			}else if(type == PacketType.ACCOUNT_LOGOUT && value instanceof Integer){
				FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGOUT, 0));
				loginAccount = null;
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
					accountCheck = true;
					checkedAccountName = name;
					FXCraft.proxy.appendPopUp("LogIn: " + name);
				}else{
					accountCheck = false;
					checkedAccountName = name;
					FXCraft.proxy.appendPopUp("LogIn failed : " + name);
				}
			}else if(type == PacketType.ACCOUNT_LOGOUT && value instanceof Integer){
				if(loginAccount != null){
					FXCraft.proxy.appendPopUp("LogOut : " + loginAccount.name);
					loginAccount = null;
				}
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
