package com.dank.storage;

import com.dank.storage.tank.block.TankBlock;
import net.minecraft.block.Block;

public class ModBlocks {


	//Tanks
	public static Block TANK_BASIC;
	public static Block TANK_HARDENED;
	public static Block TANK_REINFORCED;
	public static Block TANK_RESONANT;
	public static Block TANK_CREATIVE;

	public static void preInit() {
		TANK_BASIC = new TankBlock("basic",8);
		TANK_HARDENED = new TankBlock("hardened",32);
		TANK_REINFORCED = new TankBlock("reinforced",128);
		TANK_RESONANT = new TankBlock("resonant",512);
		TANK_CREATIVE = new TankBlock("creative",2048);
	}
}
