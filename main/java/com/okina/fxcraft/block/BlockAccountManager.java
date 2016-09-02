package com.okina.fxcraft.block;

import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.tileentity.AccountManegerTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAccountManager extends BlockContainer {

	//side icon = block icon
	protected IIcon front;
	protected IIcon back;

	public BlockAccountManager() {
		super(Material.iron);
		setBlockName("fxcraft_account_manager");
		setBlockTextureName("stone");
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack itemStack) {
		int l = MathHelper.floor_double(livingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if(l == 0){
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}else if(l == 1){
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);
		}else if(l == 2){
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}else if(l == 3){
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) player.openGui(FXCraft.instance, FXCraft.BLOCK_GUI_ID_0, world, x, y, z);
		return true;
	}

	//	@Override
	//	public int getRenderType() {
	//		return FXCraft.ACCOUNT_MANAGER_RENDER_ID;
	//	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		if(meta == 0 || meta == 1){
			meta = 4;
		}
		return side == ForgeDirection.OPPOSITES[meta] ? back : (side == meta ? front : blockIcon);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(FXCraft.MODID + ":fx_block");
		front = register.registerIcon(FXCraft.MODID + ":account_manager");
		back = register.registerIcon(FXCraft.MODID + ":fx_block_back");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new AccountManegerTileEntity();
	}

}
