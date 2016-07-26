package com.dank.storage.tank;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

public class ItemTank extends ItemBlock {
	public ItemTank(Block block) {
		super(block);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		if(stack.hasTagCompound()) {
			return new Handler(stack);
		}
		else return super.initCapabilities(stack, nbt);
	}

	private static class Handler extends FluidHandlerItemStackSimple.SwapEmpty {

		ItemStack stack;

		private Handler(ItemStack stack) {
			super(stack, new ItemStack(stack.getItem()), stack.getTagCompound().getInteger("cap"));
			this.stack=stack;
			setFluid(FluidStack.loadFluidStackFromNBT(stack.getTagCompound()));
		}

		@Override
		public boolean canFillFluidType(FluidStack fluid) {
			if(FluidStack.areFluidStackTagsEqual(fluid,FluidStack.loadFluidStackFromNBT(stack.getTagCompound())))
				return true;
			return false;
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluid) {
			if(FluidStack.areFluidStackTagsEqual(fluid,FluidStack.loadFluidStackFromNBT(stack.getTagCompound())))
				return true;
			return false;
		}
	}
}
