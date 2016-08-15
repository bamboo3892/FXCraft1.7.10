package com.okina.fxcraft.tileentity;

import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.AccountUpdateHandler;
import com.okina.fxcraft.account.FXDeal;
import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.account.IFXDealer;
import com.okina.fxcraft.client.gui.DummyContainer;
import com.okina.fxcraft.client.gui.fxdealer.FXDealerGui;
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

public class FXDealerTileEntity extends TileEntity implements IGuiTile, ISimpleTilePacketUser, IFXDealer {

	private long lastAccountUpdate = 100;
	private AccountInfo loginAccount;

	//client only
	public int lastOpenedTab = 0;

	public FXDealerTileEntity() {}

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
		loginAccount = null;
		lastAccountUpdate = System.currentTimeMillis();
		FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.ACCOUNT_LOGOUT, 0));
	}

	public void tryGetPosition(String pair, int dealLot, int deposit, boolean askOrBid) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setString("pair", pair);
			tag.setInteger("deal", dealLot);
			tag.setInteger("deposit", deposit);
			tag.setBoolean("askOrBid", askOrBid);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_GET_POSITION, tag));
			FXCraft.proxy.appendPopUp("Send " + (askOrBid ? "ask" : "bid") + " packet");
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	public void tryGetPositionOrder(String pair, int dealLot, int deposit, boolean askOrBid, double limits) {
		if(loginAccount != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("account", loginAccount.name);
			tag.setString("pair", pair);
			tag.setInteger("deal", dealLot);
			tag.setInteger("deposit", deposit);
			tag.setBoolean("askOrBid", askOrBid);
			tag.setDouble("limits", limits);
			FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_ORDER_GET_POSITION, tag));
		}else{
			FXCraft.proxy.appendPopUp("Please Login");
		}
	}

	public void trySettlePosition(FXPosition position, int dealLot) {
		if(position != FXPosition.NO_INFO){
			if(loginAccount != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("account", loginAccount.name);
				tag.setString("id", position.positionID);
				tag.setInteger("deal", dealLot);
				FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_SETTLE_POSITION, tag));
			}else{
				FXCraft.proxy.appendPopUp("Please Login");
			}
		}
	}

	public void trySettlePositionOrder(FXPosition position, int dealLot, double limits) {
		if(position != FXPosition.NO_INFO){
			if(loginAccount != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("account", loginAccount.name);
				tag.setString("id", position.positionID);
				tag.setInteger("deal", dealLot);
				tag.setDouble("limits", limits);
				FXCraft.proxy.sendPacketToServer(new SimpleTilePacket(this, PacketType.FX_ORDER_SETTLE_POSITION, tag));
			}else{
				FXCraft.proxy.appendPopUp("Please Login");
			}
		}
	}

	public void tryDeleteOrder(String orderID) {

	}

	/////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void validate() {
		super.validate();
		AccountUpdateHandler.instance.registerUpdateObject(this);
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new DummyContainer() : new FXDealerGui(player, this);
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
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public void receiveResult(FXDeal deal, Result result, Object... obj) {
		//TODO
		FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, deal + ": " + result));
		//		switch (deal) {
		//		case GET_POSITION:
		//			break;
		//		case GET_POSITION_ORDER:
		//			break;
		//		case SETTLE_POSITION:
		//			break;
		//		case SETTLE_POSITION_ORDER:
		//			break;
		//		default:
		//			return;
		//		}
	}

	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		return null;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){//server
			if(type == PacketType.ACCOUNT_LOGIN && value instanceof NBTTagCompound){
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
			}else if(type == PacketType.FX_GET_POSITION && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String pair = tag.getString("pair");
					int dealLot = tag.getInteger("deal");
					int deposit = tag.getInteger("deposit");
					boolean askOrBid = tag.getBoolean("askOrBid");
					AccountHandler.instance.tryGetPosition(this, accountName, pair, dealLot, deposit, askOrBid);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.FX_ORDER_GET_POSITION && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String pair = tag.getString("pair");
					int dealLot = tag.getInteger("deal");
					int deposit = tag.getInteger("deposit");
					boolean askOrBid = tag.getBoolean("askOrBid");
					double limits = tag.getDouble("limits");
					AccountHandler.instance.tryGetPositionOrder(this, accountName, pair, dealLot, deposit, askOrBid, limits);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.FX_SETTLE_POSITION && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String id = tag.getString("id");
					int dealLot = tag.getInteger("deal");
					AccountHandler.instance.trySettlePosition(this, accountName, id, dealLot);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}else if(type == PacketType.FX_ORDER_SETTLE_POSITION && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String accountName = tag.getString("account");
				if(loginAccount != null && !"".equals(accountName) && accountName.equals(loginAccount.name)){
					String id = tag.getString("id");
					int dealLot = tag.getInteger("deal");
					double limits = tag.getDouble("limits");
					AccountHandler.instance.trySettlePositionOrder(this, accountName, id, dealLot, limits);
				}else{
					FXCraft.proxy.sendPacketToClient(new SimpleTilePacket(this, PacketType.MESSAGE, "Illegal Account Name"));
				}
			}
		}else{//client
			if(type == PacketType.ACCOUNT_LOGIN && value instanceof NBTTagCompound){
				NBTTagCompound tag = (NBTTagCompound) value;
				String name = tag.getString("name");
				Boolean result = tag.getBoolean("result");
				if(result){
					AccountInfo info = new AccountInfo(name);
					info.readFromNBT(tag.getCompoundTag("info"));
					loginAccount = info;
					lastAccountUpdate = System.currentTimeMillis();
					FXCraft.proxy.appendPopUp("LogIn: " + name);
				}else{
					FXCraft.proxy.appendPopUp("LogIn failed : " + name);
				}
			}else if(type == PacketType.ACCOUNT_LOGOUT && value instanceof Integer){
				if(loginAccount != null){
					FXCraft.proxy.appendPopUp("LogOut : " + loginAccount.name);
					loginAccount = null;
					lastAccountUpdate = System.currentTimeMillis();
				}
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
		System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " " + type + " " + value);
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
