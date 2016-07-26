package com.dank.storage;

import com.dank.framework.*;
import com.dank.storage.tank.block.TankBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@Mod(
		modid = Storage.MOD_ID,
		name = Storage.MOD_NAME,
		version = Storage.VERSION,
		dependencies = "required-after:framework"
)
public class Storage {

	@SidedProxy(clientSide = "com.dank.storage.ClientProxy", serverSide = "com.dank.storage.CommonProxy")
	public static CommonProxy proxy;
	public static final String MOD_ID = "storage";
	public static final String MOD_NAME = "DaNK Storage";
	public static final String VERSION = "1.0.0";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModBlocks.preInit();
		proxy.registerRender();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}
