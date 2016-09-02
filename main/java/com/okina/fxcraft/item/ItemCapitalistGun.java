package com.okina.fxcraft.item;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.fxcraft.account.AccountHandler;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.IAccountInfoContainer;
import com.okina.fxcraft.client.IHUDItem;
import com.okina.fxcraft.client.IToolTipUser;
import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.network.CommandPacket;
import com.okina.fxcraft.utils.ColoredString;
import com.okina.fxcraft.utils.RenderingHelper;
import com.okina.fxcraft.utils.UtilMethods;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemCapitalistGun extends Item implements IToolTipUser, IHUDItem {

	public static final int LotToShot = 10;

	public ItemCapitalistGun() {
		setTextureName(FXCraft.MODID + ":capitalist_gun");
		setUnlocalizedName("fxcraft_capitalist_gun");
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean equipped) {
		if(equipped && !world.isRemote && entity instanceof EntityPlayerMP){
			NBTTagCompound tag = itemStack.getTagCompound();
			if(tag != null){
				AccountInfo account = AccountHandler.instance.getAccountInfo(tag.getString("account"));
				if(account != null){
					tag.setDouble("balance", account.balance);
				}else{
					tag.setDouble("balance", 0);
				}
			}
		}
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof IAccountInfoContainer){
			AccountInfo account = ((IAccountInfoContainer) tile).getAccountInfo();
			if(account != null){
				NBTTagCompound tag = stack.getTagCompound();
				if(tag == null) tag = new NBTTagCompound();
				tag.setString("account", account.name);
				stack.setTagCompound(tag);
				if(world.isRemote) player.addChatComponentMessage(new ChatComponentText("Account Registered!"));
			}
			return !world.isRemote;
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null || !tag.hasKey("account")){
			if(world.isRemote) player.addChatComponentMessage(new ChatComponentText("Not Account Registered"));
		}else{
			String name = tag.getString("account");
			if(!world.isRemote && player instanceof EntityPlayerMP){
				AccountInfo account = AccountHandler.instance.getAccountInfo(name);
				if(account != null){
					if(AccountHandler.instance.decBalance(name, LotToShot)){
						world.playSoundAtEntity(player, FXCraft.MODID + ":gun", 1f, 1f);
						Entity entity = UtilMethods.getCollidedEntityFromEntity(world, player, 20);
						if(entity instanceof EntityLivingBase){
							EntityLivingBase living = (EntityLivingBase) entity;
							living.attackEntityFrom(DamageSource.outOfWorld, 0.5f);
							FXCraft.proxy.sendCommandPacket(new CommandPacket("gun", living.posX + "," + (living.posY + living.height) + "," + living.posZ), (EntityPlayerMP) player);
						}
					}else{
						player.addChatComponentMessage(new ChatComponentText("Not Enough Balance"));
					}
				}else{
					player.addChatComponentMessage(new ChatComponentText("Invalid Account"));
				}
			}else if(world.isRemote){
				if(name != null && !"".equals(name)){
					double balance = tag.getDouble("balance");
					if(balance >= LotToShot){
						float f = (float) (Math.random() * 3);
						player.cameraPitch -= f;
						player.rotationPitch -= f * 0.5f;
						Vec3 vec = player.getLookVec();
						Vec3 v = vec.crossProduct(Vec3.createVectorHelper(0, 1, 0));
						double x = player.posX + v.xCoord * 0.1;
						double y = player.posY + player.getDefaultEyeHeight() - 0.15;
						double z = player.posZ + v.zCoord * 0.1;
						FXCraft.proxy.spawnParticle(world, FXCraft.PARTICLE_GUN, x, y, z, vec.xCoord, vec.yCoord, vec.zCoord);
					}
				}
			}
		}
		return stack;
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		toolTip.add("1 Shot = " + LotToShot + " Lot");
		toolTip.add("Account: " + (itemStack.hasTagCompound() ? itemStack.getTagCompound().getString("account") : ""));
	}

	@Override
	public int getNeutralLines() {
		return 0;
	}

	@Override
	public int getShiftLines() {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderHUD(Minecraft mc, double renderTicks, ItemStack itemStack) {
		EntityPlayer player = mc.thePlayer;
		if(player != null){
			if(itemStack != null && itemStack.getItem() == FXCraft.capitalist_gun){
				NBTTagCompound tag = itemStack.getTagCompound();
				if(tag != null && tag.hasKey("account")){
					String account = tag.getString("account");
					double balance = tag.getDouble("balance");
					RenderingHelper.renderHUDRight(mc, Lists.<ColoredString> newArrayList(new ColoredString("Account: " + account, 0xFFFFFF), new ColoredString(String.format("%.1f", balance), 0x7fff00)));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean comparePastRenderObj(ItemStack object) {
		return this == object.getItem();
	}

}
