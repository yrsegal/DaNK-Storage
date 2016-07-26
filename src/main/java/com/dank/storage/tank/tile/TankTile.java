package com.dank.storage.tank.tile;

import com.dank.framework.TileEntityBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class TankTile extends TileEntityBase implements IFluidHandler {

	public FluidTank tank;

	public boolean output = false;

	public TankTile() {
		this.tank=new FluidTank(Fluid.BUCKET_VOLUME*8);
	}

	public int getBrightness() {
		if(containsFluid()) {
			return tank.getFluid().getFluid().getLuminosity();
		}
		return 0;
	}

	public boolean containsFluid() {
		return tank.getFluid() != null;
	}

	public boolean isOutput() {
		return output;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
		tank.setCapacity(tag.getInteger("cap"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag = super.writeToNBT(tag);
		tank.writeToNBT(tag);
		tag.setInteger("cap",tank.getCapacity());
		return tag;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) tank;
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	public TankTile(int cap) {
		this.tank=new FluidTank(Fluid.BUCKET_VOLUME*cap);
	}

	public void setOutput(boolean bool) {
		this.output=bool;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tank.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return tank.drain(resource, doDrain);
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}
}
