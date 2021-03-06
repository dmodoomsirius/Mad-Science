package madscience.content.items.warningsign;

import madscience.MadEntities;
import madscience.MadScience;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WarningSignEntityRender extends Render
{
    // Texture Atlas (Sprite Sheet) that contains all the warning signs that are possible.
    private static final ResourceLocation TEXTURE = new ResourceLocation(MadScience.ID, "models/" + MadEntities.WARNING_SIGN_INTERNALNAME + "/" + MadEntities.WARNING_SIGN_INTERNALNAME + ".png");

    private void adjustDirection(WarningSignEntity par1EntityPainting, float par2, float par3)
    {
        int fX = MathHelper.floor_double(par1EntityPainting.posX);
        int fY = MathHelper.floor_double(par1EntityPainting.posY + par3 / 16.0F);
        int fZ = MathHelper.floor_double(par1EntityPainting.posZ);

        if (par1EntityPainting.hangingDirection == 2)
        {
            fX = MathHelper.floor_double(par1EntityPainting.posX + par2 / 16.0F);
        }

        if (par1EntityPainting.hangingDirection == 1)
        {
            fZ = MathHelper.floor_double(par1EntityPainting.posZ - par2 / 16.0F);
        }

        if (par1EntityPainting.hangingDirection == 0)
        {
            fX = MathHelper.floor_double(par1EntityPainting.posX - par2 / 16.0F);
        }

        if (par1EntityPainting.hangingDirection == 3)
        {
            fZ = MathHelper.floor_double(par1EntityPainting.posZ + par2 / 16.0F);
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    private void adjustToImageSize(WarningSignEntity par1EntityPainting, int par2, int par3, int par4, int par5)
    {
        float f = (-par2) / 2.0F;
        float f1 = (-par3) / 2.0F;
        float f2 = 0.5F;
        float f3 = 0.75F;
        float f4 = 0.8125F;
        float f5 = 0.0F;
        float f6 = 0.0625F;
        float f7 = 0.75F;
        float f8 = 0.8125F;
        float f9 = 0.001953125F;
        float f10 = 0.001953125F;
        float f11 = 0.7519531F;
        float f12 = 0.7519531F;
        float f13 = 0.0F;
        float f14 = 0.0625F;

        for (int i1 = 0; i1 < par2 / 16; ++i1)
        {
            for (int j1 = 0; j1 < par3 / 16; ++j1)
            {
                float f15 = f + (i1 + 1) * 16;
                float f16 = f + i1 * 16;
                float f17 = f1 + (j1 + 1) * 16;
                float f18 = f1 + j1 * 16;
                
                this.adjustDirection(par1EntityPainting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
                
                float f19 = (par4 + par2 - i1 * 16) / 256.0F;
                float f20 = (par4 + par2 - (i1 + 1) * 16) / 256.0F;
                float f21 = (par5 + par3 - j1 * 16) / 256.0F;
                float f22 = (par5 + par3 - (j1 + 1) * 16) / 256.0F;
                
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawing(GL11.GL_QUADS);
                
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addVertexWithUV(f15, f18, (-f2), f20, f21);
                tessellator.addVertexWithUV(f16, f18, (-f2), f19, f21);
                tessellator.addVertexWithUV(f16, f17, (-f2), f19, f22);
                tessellator.addVertexWithUV(f15, f17, (-f2), f20, f22);
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addVertexWithUV(f15, f17, f2, f3, f5);
                tessellator.addVertexWithUV(f16, f17, f2, f4, f5);
                tessellator.addVertexWithUV(f16, f18, f2, f4, f6);
                tessellator.addVertexWithUV(f15, f18, f2, f3, f6);
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV(f15, f17, (-f2), f7, f9);
                tessellator.addVertexWithUV(f16, f17, (-f2), f8, f9);
                tessellator.addVertexWithUV(f16, f17, f2, f8, f10);
                tessellator.addVertexWithUV(f15, f17, f2, f7, f10);
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                tessellator.addVertexWithUV(f15, f18, f2, f7, f9);
                tessellator.addVertexWithUV(f16, f18, f2, f8, f9);
                tessellator.addVertexWithUV(f16, f18, (-f2), f8, f10);
                tessellator.addVertexWithUV(f15, f18, (-f2), f7, f10);
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV(f15, f17, f2, f12, f13);
                tessellator.addVertexWithUV(f15, f18, f2, f12, f14);
                tessellator.addVertexWithUV(f15, f18, (-f2), f11, f14);
                tessellator.addVertexWithUV(f15, f17, (-f2), f11, f13);
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV(f16, f17, (-f2), f12, f13);
                tessellator.addVertexWithUV(f16, f18, (-f2), f12, f14);
                tessellator.addVertexWithUV(f16, f18, f2, f11, f14);
                tessellator.addVertexWithUV(f16, f17, f2, f11, f13);
                tessellator.draw();
            }
        }
    }

    /** Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic (Render<T extends
     * Entity) and this method has signature public void doRender(T entity, double d, double d1, double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that. */
    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderThePainting((WarningSignEntity) par1Entity, par2, par4, par6, par8, par9);
    }

    /** Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture. */
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getTexture((WarningSignEntity) par1Entity);
    }

    protected ResourceLocation getTexture(WarningSignEntity par1EntityPainting)
    {
        return TEXTURE;
    }

    public void renderThePainting(WarningSignEntity entity, double x, double y, double z, float rotation, float k)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        // Set the texture to current one we should be using (which contains them all).
        this.bindEntityTexture(entity);

        // Set the array to proper one we will be using.
        WarningSignEnum enumart = null;
        if (entity.clientCurrentSignType != null)
        {
            enumart = entity.clientCurrentSignType;
        }
        else
        {
            // Tell client it should request from server correct sign type.
            entity.clientShouldRequestSignType = true;
        }

        // Default entry if none is provided.
        if (enumart == null)
        {
            enumart = WarningSignEnum.GenericWarning;
        }

        // Scale the image to correct size.
        float halfOfBlock = 0.03125F;
        GL11.glScalef(halfOfBlock, halfOfBlock, halfOfBlock);
        this.adjustToImageSize(entity, 32, 32, enumart.offsetX, enumart.offsetY);

        // Re-enable rescaling.
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}
