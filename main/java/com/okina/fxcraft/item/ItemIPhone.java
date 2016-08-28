package com.okina.fxcraft.item;

import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.tileentity.FXDealerTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemIPhone extends Item {

	public ItemIPhone() {
		setTextureName(FXCraft.MODID + ":iphone");
		setUnlocalizedName("fxcraft_iphone");
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof FXDealerTileEntity){
			if(!world.isRemote){
				NBTTagCompound tag = stack.getTagCompound();
				if(tag == null) tag = new NBTTagCompound();
				tag.setInteger("x", x);
				tag.setInteger("y", y);
				tag.setInteger("z", z);
				stack.setTagCompound(tag);
				player.addChatComponentMessage(new ChatComponentText("Connection Established!"));
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!world.isRemote){
			NBTTagCompound tag = stack.getTagCompound();
			if(tag == null || !tag.hasKey("x") || !tag.hasKey("y") || !tag.hasKey("z")){
				player.addChatComponentMessage(new ChatComponentText("No Connection"));
			}else{
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				TileEntity tile = world.getTileEntity(x, y, z);
				if(tile instanceof FXDealerTileEntity){
					player.openGui(FXCraft.instance, FXCraft.BLOCK_GUI_ID_0, world, x, y, z);
				}else{
					player.addChatComponentMessage(new ChatComponentText("Cannot Find FXDealer at Registered Position"));
				}
			}
		}
		return stack;
	}

}
