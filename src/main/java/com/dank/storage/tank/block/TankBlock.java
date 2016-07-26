package com.dank.storage.tank.block;

import com.dank.framework.ModItems;
import com.dank.framework.blocks.BlockStates;
import com.dank.framework.util.Platform;
import com.dank.framework.util.TileHelper;
import com.dank.storage.Storage;
import com.dank.storage.tank.ItemTank;
import com.dank.storage.tank.tile.TankTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TankBlock extends Block implements ITileEntityProvider {

	private int cap;
	private String resourceLocation;

	public TankBlock(String name, int cap) {
		super(Material.GLASS);
		this.resourceLocation = "tank"+name;
		setUnlocalizedName("tank"+name);
		this.cap=cap;
		setRegistryName("tank"+name);
		setDefaultState(blockState.getBaseState().withProperty(BlockStates.ACTIVE,false));
		GameRegistry.register(this);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		ItemBlock itemBlock = new ItemTank(this);
		itemBlock.setCreativeTab(CreativeTabs.DECORATIONS);
		itemBlock.setRegistryName("tank"+name);
		GameRegistry.register(itemBlock);
		GameRegistry.registerTileEntity(TankTile.class,"tank"+name);
		if(Platform.isClient())
			registerRenderers();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TankTile te = (TankTile) world.getTileEntity(pos);
		if (te!=null) {
			ItemStack input = player.getHeldItem(hand);
			if(input!=null&&input.isItemEqual(new ItemStack(ModItems.WRENCH))) {
				if(!world.isRemote) {
					if (player.isSneaking()) {
						te.setOutput(!te.isOutput());
						te.markForUpdate();
						te.markForLightUpdate();
						player.swingArm(hand);
						return true;
					} else {
						NBTTagCompound tag = new NBTTagCompound();
						ItemStack stack = new ItemStack(world.getBlockState(pos).getBlock());
						te.writeToNBT(tag);
						tag.removeTag("x");
						tag.removeTag("y");
						tag.removeTag("z");
						stack.setTagCompound(tag);
						world.destroyBlock(pos, false);
						EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
						world.spawnEntityInWorld(item);
						player.swingArm(hand);
						return true;
					}
				}
			}
			if (FluidUtil.interactWithFluidHandler(input, te.tank, player)) {
				te.markForLightUpdate();
				te.markForUpdate();
				return true;
			}
		}
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		tooltip.add("Max: "+ Fluid.BUCKET_VOLUME*cap);
		if(stack.hasTagCompound()) {
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)||Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				if(stack.getTagCompound()!=null) {
					tooltip.add("Contains");
					tooltip.add(String.format("<%s>", StringUtils.capitalize(stack.getTagCompound().getString("FluidName").toLowerCase())));
					tooltip.add(String.format("%d/%d", stack.getTagCompound().getInteger("Amount"), this.cap*Fluid.BUCKET_VOLUME));
				}
			} else {
				tooltip.add("Press <SHIFT> for more info");
			}
		}

		super.addInformation(stack, player, tooltip, advanced);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity thisTile = TileHelper.getTileEntity(worldIn,pos, TileEntity.class);
		TileEntity belowTile = TileHelper.getTileEntity(worldIn,pos.offset(EnumFacing.DOWN), TileEntity.class);
		if(thisTile!=null && thisTile instanceof TankTile) {
			if(stack.getTagCompound()!=null) {
				NBTTagCompound tag = stack.getTagCompound();
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				thisTile.readFromNBT(tag);
				((TankTile) thisTile).markForLightUpdate();
				((TankTile) thisTile).markForUpdate();
				thisTile.markDirty();
			}
			if(belowTile!=null&&belowTile instanceof TankTile) {
				if(((TankTile) belowTile).tank.getFluid() == ((TankTile) thisTile).tank.getFluid())
					((TankTile) thisTile).setOutput(true);
				else ((TankTile) thisTile).setOutput(false);
			}
		}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(BlockStates.ACTIVE,false);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,BlockStates.ACTIVE);
	}

	@Override
	public String getUnlocalizedName() {
		String blockName = getUnwrappedUnlocalizedName(super.getUnlocalizedName());

		return String.format("tile.%s.%s", Storage.MOD_ID, blockName);
	}

	private String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TankTile(cap);
	}

	public void registerRenderers() {
		final String resourcePath = String.format("%s:%s", Storage.MOD_ID, this.resourceLocation);
		Item item = Item.getItemFromBlock(this);
		IBlockState blockState = this.getStateFromMeta(0);
		if(item!=null)
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(resourcePath, Platform.getPropertyString(blockState.getProperties())));
	}
	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof TankTile)) {
			return 0;
		}
		TankTile tank = (TankTile) te;
		return tank.getBrightness();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.12D, 0.01D, 0.12D, 0.88D, 1.0D, 0.88D);
	}

}

