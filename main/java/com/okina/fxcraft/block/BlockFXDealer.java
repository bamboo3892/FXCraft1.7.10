package com.okina.fxcraft.block;

import com.okina.fxcraft.main.FXCraft;
import com.okina.fxcraft.tileentity.FXDealerTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFXDealer extends BlockContainer {

	public BlockFXDealer() {
		super(Material.iron);
		setBlockName("fxcraft_fx_dealer");
		setBlockTextureName("stone");
		setCreativeTab(FXCraft.FXCraftCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) player.openGui(FXCraft.instance, FXCraft.BLOCK_GUI_ID_0, world, x, y, z);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new FXDealerTileEntity();
	}

}
