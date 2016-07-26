package com.dank.storage;

import com.dank.storage.tank.client.TankRenderer;
import com.dank.storage.tank.tile.TankTile;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRender() {
		ClientRegistry.bindTileEntitySpecialRenderer(TankTile.class, new TankRenderer());
	}
}
