package com.okina.fxcraft.main;

import static com.okina.fxcraft.main.FXCraft.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.Reward.Rewards;
import com.okina.fxcraft.account.RewardRegister;
import com.okina.fxcraft.block.BlockAccountManager;
import com.okina.fxcraft.block.BlockFXDealer;
import com.okina.fxcraft.item.ItemCapitalistGuard;
import com.okina.fxcraft.item.ItemCapitalistGun;
import com.okina.fxcraft.item.ItemIPhone;
import com.okina.fxcraft.item.ItemJentlemensCap;
import com.okina.fxcraft.item.ItemJentlemensPanz;
import com.okina.fxcraft.item.ItemToolTip;
import com.okina.fxcraft.network.CommandPacket;
import com.okina.fxcraft.network.CommandPacket.CommandPacketHandler;
import com.okina.fxcraft.network.SimpleTilePacket;
import com.okina.fxcraft.network.SimpleTilePacket.SimpleTilePacketHandler;
import com.okina.fxcraft.network.SimpleTilePacket.SimpleTileReplyPacketHandler;
import com.okina.fxcraft.tileentity.AccountManegerTileEntity;
import com.okina.fxcraft.tileentity.FXDealerTileEntity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;

public class CommonProxy {

	protected void loadConfiguration(File pfile) {
		Configuration config = new Configuration(pfile);
		try{
			config.load();
			config.getInt("Particle Level", "EFFECT", 3, 0, 3, "Now this configulation replaced to proterty file.");
		}catch (Exception e){
			FMLLog.severe("config load errer");
		}finally{
			config.save();
		}
		AccountHandler.instance.readFromFile();
	}

	protected void registerBlock() {
		accountManager = new BlockAccountManager();
		GameRegistry.registerBlock(accountManager, accountManager.getUnlocalizedName());
		fxDealer = new BlockFXDealer();
		GameRegistry.registerBlock(fxDealer, fxDealer.getUnlocalizedName());
	}

	protected void registerItem() {
		iPhone = new ItemIPhone();
		GameRegistry.registerItem(iPhone, iPhone.getUnlocalizedName());
		ArmorMaterial panz = EnumHelper.addArmorMaterial("panz", 0, new int[] { 0, 0, 0, 0 }, 0);
		ArmorMaterial emeral = EnumHelper.addArmorMaterial("emerald", 0, new int[] { 0, 0, 0, 0 }, 0);
		jentlemens_cap = new ItemJentlemensCap(panz, 1);
		GameRegistry.registerItem(jentlemens_cap, jentlemens_cap.getUnlocalizedName());
		jentlemens_panz = new ItemJentlemensPanz(panz, 1);
		GameRegistry.registerItem(jentlemens_panz, jentlemens_panz.getUnlocalizedName());
		capitalist_gun = new ItemCapitalistGun();
		GameRegistry.registerItem(capitalist_gun, capitalist_gun.getUnlocalizedName());
		capitalist_guard = new ItemCapitalistGuard(emeral, 1);
		GameRegistry.registerItem(capitalist_guard, capitalist_guard.getUnlocalizedName());

		for (int i = 0; i < 5; i++){
			limit_dealLot[i] = new ItemToolTip(Lists.newArrayList("Permit to deal " + AccountInfo.DEAL_LIMIT[i + 1] + " lot or less")).setUnlocalizedName("fxcraft_limit_dealLot_" + (i + 1)).setTextureName(MODID + ":limit_g_" + (i + 1)).setCreativeTab(FXCraftCreativeTab);
			GameRegistry.registerItem(limit_dealLot[i], limit_dealLot[i].getUnlocalizedName());
		}
		for (int i = 0; i < 5; i++){
			limit_leverage[i] = new ItemToolTip(Lists.newArrayList("Permit to deal by leverage " + AccountInfo.LEVERAGE_LIMIT[i + 1] + ".0 or less")).setUnlocalizedName("fxcraft_limit_leverage_" + (i + 1)).setTextureName(MODID + ":limit_b_" + (i + 1)).setCreativeTab(FXCraftCreativeTab);
			GameRegistry.registerItem(limit_leverage[i], limit_leverage[i].getUnlocalizedName());
		}
		for (int i = 0; i < 5; i++){
			limit_position[i] = new ItemToolTip(Lists.newArrayList("Permit to get " + AccountInfo.POSITION_LIMIT[i + 1] + " positions or less")).setUnlocalizedName("fxcraft_limit_position_" + (i + 1)).setTextureName(MODID + ":limit_r_" + (i + 1)).setCreativeTab(FXCraftCreativeTab);
			GameRegistry.registerItem(limit_position[i], limit_position[i].getUnlocalizedName());
		}
		limit_limits_trade = new ItemToolTip(Lists.newArrayList("Permit to trade with limits")).setUnlocalizedName("fxcraft_limit_limits_trade").setTextureName(MODID + ":limit_black").setCreativeTab(FXCraftCreativeTab);
		GameRegistry.registerItem(limit_limits_trade, limit_limits_trade.getUnlocalizedName());

		for (int i = 0; i < Rewards.TOTAL_DEAL.length; i++){
			RewardRegister.instance.registerReward(Rewards.TOTAL_DEAL[i]);
		}
		for (int i = 0; i < Rewards.TOTAL_GAIN.length; i++){
			RewardRegister.instance.registerReward(Rewards.TOTAL_GAIN[i]);
		}
		for (int i = 0; i < Rewards.TOTAL_LOSS.length; i++){
			RewardRegister.instance.registerReward(Rewards.TOTAL_LOSS[i]);
		}
		RewardRegister.instance.registerReward(Rewards.MAX_LOT);
		RewardRegister.instance.registerReward(Rewards.MAX_LEVERAGE);
		RewardRegister.instance.registerReward(Rewards.MAX_LOT_LEVERAGE);
		RewardRegister.instance.registerReward(Rewards.FIRST_DEAL);
		RewardRegister.instance.registerReward(Rewards.FIRST_LIMITS_DEAL);
		RewardRegister.instance.registerReward(Rewards.FIRST_LOSSCUT);
		RewardRegister.instance.registerFirstAimableReward(Rewards.FIRST_DEAL);
		RewardRegister.instance.registerFirstAimableReward(Rewards.TOTAL_DEAL[0]);
		RewardRegister.instance.registerFirstAimableReward(Rewards.TOTAL_GAIN[0]);
		RewardRegister.instance.registerFirstAimableReward(Rewards.TOTAL_LOSS[0]);
		RewardRegister.instance.registerFirstAimableReward(Rewards.FIRST_LIMITS_DEAL);
	}

	protected void registerTileEntity() {
		GameRegistry.registerTileEntity(AccountManegerTileEntity.class, "AccountManegerTileEntity");
		GameRegistry.registerTileEntity(FXDealerTileEntity.class, "FXDealerTileEntity");
	}

	protected void registerRecipe() {
		GameRegistry.addRecipe(new ItemStack(accountManager), "SGS", "OSO", 'G', Blocks.glass_pane, 'S', Blocks.stone, 'O', Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(fxDealer), "SES", "OSO", 'E', Items.emerald, 'S', Blocks.stone, 'O', Blocks.obsidian);
	}

	protected void registerRenderer() {}

	protected void registerPacket() {
		packetDispatcher.registerMessage(SimpleTilePacketHandler.class, SimpleTilePacket.class, SIMPLETILE_PACKET_ID, Side.SERVER);
		packetDispatcher.registerMessage(SimpleTileReplyPacketHandler.class, SimpleTilePacket.class, SIMPLETILE_REPLY_PACKET_ID, Side.CLIENT);
		//		packetDispatcher.registerMessage(MultiBlockPacketHandler.class, MultiBlockPacket.class, MULTIBLOCK_PACKET_ID, Side.CLIENT);
		//		packetDispatcher.registerMessage(WorldUpdatePacketHandler.class, WorldUpdatePacket.class, WORLD_UPDATE_PACKET_ID, Side.CLIENT);
		packetDispatcher.registerMessage(CommandPacketHandler.class, CommandPacket.class, COMMAND_PACKET_ID, Side.CLIENT);
	}

	//file io//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected void updatePropertyFile() {}

	protected void initFXThread() {
		rateGetter.init();
	}

	//packet//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//	private Map<PacketType, List<Position>> positionListMap = new HashMap<PacketType, List<Position>>();
	//
	//	/**return true if newly marked*/
	//	public boolean markForTileUpdate(Position position, PacketType type) {
	//		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
	//			if(positionListMap.get(type) != null){
	//				List<Position> positionList = positionListMap.get(type);
	//				for (Position tmp : positionList){
	//					if(tmp != null && tmp.equals(position)){
	//						//System.out.println("already marked update");
	//						return false;
	//					}
	//				}
	//				positionList.add(position);
	//			}else{
	//				List<Position> positionList = new ArrayList<Position>();
	//				positionList.add(position);
	//				positionListMap.put(type, positionList);
	//			}
	//			return true;
	//		}else{
	//			return false;
	//		}
	//	}
	//
	//	void sendAllUpdatePacket() {
	//		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
	//			List<SimpleTilePacket> packets = Lists.newArrayList();
	//			for (PacketType type : PacketType.values()){
	//				List<Position> positionList = positionListMap.get(type);
	//				if(positionList != null){
	//					for (Position position : positionList){
	//						TileEntity tile = MinecraftServer.getServer().getEntityWorld().getTileEntity(position.x, position.y, position.z);
	//						if(tile instanceof ISimpleTilePacketUser){
	//							SimpleTilePacket packet = ((ISimpleTilePacketUser) tile).getPacket(type);
	//							if(packet != null){
	//								//								packetDispatcher.sendToAll(packet);
	//								packets.add(packet);
	//							}
	//						}
	//					}
	//					positionList.clear();
	//				}
	//			}
	//			if(!packets.isEmpty()){
	//				WorldUpdatePacket packet = new WorldUpdatePacket(packets);
	//				packetDispatcher.sendToAll(packet);
	//			}
	//		}
	//	}

	protected List<SimpleTilePacket> serverPacketList = Collections.<SimpleTilePacket> synchronizedList(Lists.<SimpleTilePacket> newArrayList());

	public void sendPacketToClient(SimpleTilePacket packet) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			packetDispatcher.sendToAll(packet);
		}else{
			serverPacketList.add(packet);
		}
	}

	public void sendPacketToServer(SimpleTilePacket packet) {}

	public void sendCommandPacket(CommandPacket packet, EntityPlayerMP player) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			packetDispatcher.sendTo(packet, player);
		}
	}

	public void spawnParticle(World world, int id, double x, double y, double z, double vecX, double vecY, double vecZ) {}

	protected List<PopUpMessage> messageList = Collections.<PopUpMessage> synchronizedList(Lists.<PopUpMessage> newLinkedList());

	public void appendPopUp(String message) {
		packetDispatcher.sendToAll(new CommandPacket("message", message));
	}

	protected class PopUpMessage {
		protected String message;
		protected int liveTime;
		protected int index;

		protected PopUpMessage(String message, int liveTime, int index) {
			this.message = message;
			this.liveTime = liveTime;
			this.index = index;
		}
	}

}
