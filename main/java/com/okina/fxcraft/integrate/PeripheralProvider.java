package com.okina.fxcraft.integrate;

import com.okina.fxcraft.tileentity.FXDealerTileEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PeripheralProvider implements IPeripheralProvider {

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null && tile instanceof FXDealerTileEntity){
			return (IPeripheral) tile;
		}
		return null;
	}

}
