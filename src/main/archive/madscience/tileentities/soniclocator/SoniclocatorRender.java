package madscience.tileentities.soniclocator;

import madscience.MadFurnaces;
import madscience.MadScience;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoniclocatorRender extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer
{
    private enum TransformationTypes
    {
        DROPPED, INVENTORY, NONE, THIRDPERSONEQUIPPED
    }

    // Refers to location in asset folder with other textures and sounds.
    private ResourceLocation TEXTURE = new ResourceLocation(MadScience.ID, "models/" + MadFurnaces.SONICLOCATOR_INTERNALNAME + "/off.png");

    // Tile entity that does all the work for this instance of the block.
    private SoniclocatorEntity lastPlacedTileEntity;

    // The model of your block
    private MadTechneModel MODEL = (MadTechneModel) AdvancedModelLoader.loadModel(MadScience.MODEL_PATH + MadFurnaces.SONICLOCATOR_INTERNALNAME + "/" + MadFurnaces.SONICLOCATOR_INTERNALNAME + ".mad");

    // Unique ID for our model to render in the world.
    public int modelRenderID = RenderingRegistry.getNextAvailableRenderId();

    // Maximum amount that we can move on the Y axis.
    private float thumperCeiling;

    // Default Y coordinate for thumper piles.
    private float thumperYCoord = 0.0F;

    public SoniclocatorRender()
    {
        // Used as base reference for pile position.
        thumperYCoord = this.MODEL.parts.get("Thumper1").offsetY;
    }

    @Override
    public int getRenderId()
    {
        // Returns our unique rendering ID for this specific tile entity.
        return modelRenderID;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        switch (type)
        {
        case ENTITY:
        case EQUIPPED:
        case EQUIPPED_FIRST_PERSON:
            return true;
        case INVENTORY:
            return false;
        default:
            return false;
        }
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        return;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        GL11.glPushMatrix();

        // Use the same texture we do on the block normally.
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

        // adjust rendering space to match what caller expects
        TransformationTypes transformationToBeUndone = TransformationTypes.NONE;
        switch (type)
        {
        case EQUIPPED:
        {
            float scale = 1.4F;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0.1F, 0.3F, 0.3F);
            //GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(GL11.GL_CULL_FACE);
            transformationToBeUndone = TransformationTypes.THIRDPERSONEQUIPPED;
            break;
        }
        case EQUIPPED_FIRST_PERSON:
        {
            float scale = 1.0F;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0.2F, 0.9F, 0.5F);
            //GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            break;
        }
        case INVENTORY:
        {
            float scale = 1.0F;
            GL11.glScalef(scale, scale, scale);
            //GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(270.0F, 0.0F, 0.5F, 0.0F);
            transformationToBeUndone = TransformationTypes.INVENTORY;
            break;
        }
        case ENTITY:
        {
            float scale = 1.0F;
            GL11.glScalef(scale, scale, scale);
            //GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            transformationToBeUndone = TransformationTypes.DROPPED;
            break;
        }
        default:
            break; // never here
        }

        // Renders the model in the gameworld at the correct scale.
        MODEL.renderAll();
        GL11.glPopMatrix();

        switch (transformationToBeUndone)
        {
        case NONE:
        {
            break;
        }
        case DROPPED:
        {
            GL11.glTranslatef(0.0F, -0.5F, 0.0F);
            float scale = 1.0F;
            GL11.glScalef(scale, scale, scale);
            break;
        }
        case INVENTORY:
        {
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            break;
        }
        case THIRDPERSONEQUIPPED:
        {
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
        default:
            break;
        }
    }
    
    public void renderAModelAt(SoniclocatorEntity tileEntity, double x, double y, double z, float f)
    {
        // Grab the individual tile entity in the world.
        lastPlacedTileEntity = (SoniclocatorEntity) tileEntity;
        if (lastPlacedTileEntity == null)
        {
            return;
        }

        // Changes the objects rotation to match whatever the player was facing.
        int rotation = 180;
        switch (lastPlacedTileEntity.getBlockMetadata() % 4)
        {
        case 0:
            rotation = 0;
            break;
        case 3:
            rotation = 90;
            break;
        case 2:
            rotation = 180;
            break;
        case 1:
            rotation = 270;
            break;
        }

        // Begin OpenGL render push.
        GL11.glPushMatrix();

        // Left and right positives center the object and the middle one raises
        // it up to connect with bottom of connecting block.
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

        // Using this and the above select the tile entity will always face the
        // player.
        switch (rotation)
        {
        case 0:
            GL11.glRotatef(-rotation, 0.0F, 1.0F, 0.0F);
            break;
        case 90:
            GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
            break;
        case 180:
            GL11.glRotatef(-rotation, 0.0F, 1.0F, 0.0F);
            break;
        case 270:
            GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
            break;
        }

        // Apply our custom texture from asset directory.
        if (lastPlacedTileEntity != null && lastPlacedTileEntity.soniclocatorTexture != null && !lastPlacedTileEntity.soniclocatorTexture.isEmpty())
        {
            bindTexture(new ResourceLocation(MadScience.ID, lastPlacedTileEntity.soniclocatorTexture));
        }
        else
        {
            // Default texture.
            bindTexture(TEXTURE);
        }
        
        GL11.glPushMatrix();
        MODEL.renderAll();
        GL11.glPopMatrix();

        if (lastPlacedTileEntity != null)
        {
            // Calculate maximum possible ceiling for all thumper.
            thumperCeiling = lastPlacedTileEntity.currentHeatMaximum * 0.003F;

            if (lastPlacedTileEntity.currentHeatValue > 0)
            {
                // Thumper Pile 1
                if (Math.abs(MODEL.parts.get("Thumper1").offsetY) < thumperCeiling && lastPlacedTileEntity.currentHeatValue > 0)
                {
                    MODEL.parts.get("Thumper1").offsetY -= lastPlacedTileEntity.currentHeatValue * 0.00001F;
                    // MadScience.logger.info("THUMPER1: " + Math.abs(model.Thumper1.offsetY) + " / " + thumperCeiling);
                }

                // Thumper Pile 2
                if (Math.abs(MODEL.parts.get("Thumper1").offsetY) >= thumperCeiling && Math.abs(MODEL.parts.get("Thumper2").offsetY) < thumperCeiling)
                {
                    MODEL.parts.get("Thumper2").offsetY -= lastPlacedTileEntity.currentHeatValue * 0.00001F;
                    // MadScience.logger.info("THUMPER2: " + Math.abs(model.Thumper2.offsetY) + " / " + thumperCeiling);
                }

                // Thumper Pile 3
                if (Math.abs(MODEL.parts.get("Thumper1").offsetY) >= thumperCeiling && Math.abs(MODEL.parts.get("Thumper2").offsetY) >= thumperCeiling && Math.abs(MODEL.parts.get("Thumper3").offsetY) < thumperCeiling)
                {
                    MODEL.parts.get("Thumper3").offsetY -= lastPlacedTileEntity.currentHeatValue * 0.00001F;
                    // MadScience.logger.info("THUMPER3: " + Math.abs(model.Thumper3.offsetY) + " / " + thumperCeiling);
                }
            }
            else if (lastPlacedTileEntity.currentHeatValue == 0)
            {                                
                if (MODEL.parts.get("Thumper1").offsetY < thumperYCoord)
                {
                    MODEL.parts.get("Thumper1").offsetY = thumperYCoord;
                }

                // Thumper 2 Smash!
                if (MODEL.parts.get("Thumper2").offsetY < thumperYCoord)
                {
                    MODEL.parts.get("Thumper2").offsetY += 0.03F;
                }
                else if (MODEL.parts.get("Thumper2").offsetY > thumperYCoord)
                {
                    MODEL.parts.get("Thumper2").offsetY = thumperYCoord;
                }

                // Thumper 3 Smash!
                if (MODEL.parts.get("Thumper3").offsetY < thumperYCoord)
                {
                    MODEL.parts.get("Thumper3").offsetY += 0.03F;
                }
                else if (MODEL.parts.get("Thumper3").offsetY > thumperYCoord)
                {
                    MODEL.parts.get("Thumper3").offsetY = thumperYCoord;
                }
            }
        }

        MODEL.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float scale)
    {
        this.renderAModelAt((SoniclocatorEntity) tileEntity, x, y, z, scale);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        // Overridden by our tile entity.
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory()
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        switch (type)
        {
        case ENTITY:
        {
            return (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION || helper == ItemRendererHelper.BLOCK_3D);
        }
        case EQUIPPED:
        {
            return (helper == ItemRendererHelper.BLOCK_3D || helper == ItemRendererHelper.EQUIPPED_BLOCK);
        }
        case EQUIPPED_FIRST_PERSON:
        {
            return (helper == ItemRendererHelper.EQUIPPED_BLOCK);
        }
        case INVENTORY:
        {
            return (helper == ItemRendererHelper.INVENTORY_BLOCK);
        }
        default:
        {
            return false;
        }
        }
    }
}
