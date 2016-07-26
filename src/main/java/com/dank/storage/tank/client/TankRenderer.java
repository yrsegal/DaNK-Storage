package com.dank.storage.tank.client;

import com.dank.framework.util.FluidHelper;
import com.dank.storage.tank.client.model.ModelFluid;
import com.dank.storage.tank.tile.TankTile;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class TankRenderer extends TileEntitySpecialRenderer<TankTile> {

	private static ModelFluid fluidModel = new ModelFluid();

	@Override
	public void renderTileEntityAt(TankTile te, double x, double y, double z, float partialTicks, int destroyStage) {
		BlockPos pos = te.getPos();
		renderFluid(te, x, y, z);
	}

	private void renderFluid(TankTile te, double x, double y, double z) {
		if (te.tank.getFluid() != null && te.tank.getFluidAmount() > 0) {
			ResourceLocation fluidTexture = FluidHelper.getTexture(te.tank.getFluid().getFluid());

			if (fluidTexture != null) {
				GL11.glPushMatrix();
				GL11.glTranslatef((float) x + .5f, (float) y + 0, (float) z + .5f);
				GL11.glRotatef(180, 1, 0, 0);
				double fluidPercent = (int) (((double) te.tank.getFluidAmount() / (double) te.tank.getCapacity()) * 100);
				GL11.glTranslated(0,(-(fluidPercent / 100)) / 2, 0);
				GL11.glScaled(1.125, (fluidPercent / 100), 1.125);
				bindTexture(fluidTexture);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 242, 242);
				fluidModel.renderAll();
				GL11.glPopMatrix();
			}
		}
	}
}
