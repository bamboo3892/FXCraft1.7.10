package com.okina.fxcraft.main;

import static com.okina.fxcraft.main.FXCraft.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.block.BlockAccountManager;
import com.okina.fxcraft.block.BlockFXDealer;
import com.okina.fxcraft.item.ItemIPhone;
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
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

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
	}

	protected void registerTileEntity() {
		GameRegistry.registerTileEntity(AccountManegerTileEntity.class, "AccountManegerTileEntity");
		GameRegistry.registerTileEntity(FXDealerTileEntity.class, "FXDealerTileEntity");
	}

	protected void registerRecipe() {}

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

	protected void spawnParticle(World world, int id, Object... objects) {}

	protected List<PopUpMessage> messageList = Collections.<PopUpMessage> synchronizedList(Lists.<PopUpMessage> newLinkedList());

	public void appendPopUp(String message) {}

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
