package madscience.tileentities.dataduplicator;

import java.awt.Desktop;
import java.net.URI;

import madscience.MadConfig;
import madscience.MadFurnaces;
import madscience.MadScience;
import madscience.gui.GUIButtonInvisible;
import madscience.util.GUIContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DataDuplicatorGUI extends GUIContainerBase
{
    // Placed tile entity in the game world.
    private DataDuplicatorEntity ENTITY;

    public DataDuplicatorGUI(InventoryPlayer par1InventoryPlayer, DataDuplicatorEntity par2TileEntityFurnace)
    {
        super(new DataDuplicatorContainer(par1InventoryPlayer, par2TileEntityFurnace));
        this.ENTITY = par2TileEntityFurnace;
        TEXTURE = new ResourceLocation(MadScience.ID, "textures/gui/" + MadFurnaces.DATADUPLICATOR_INTERNALNAME + ".png");
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);

        // -----------
        // POWER LEVEL
        // -----------
        int powerRemianingPercentage = this.ENTITY.getPowerRemainingScaled(14);
        // Screen Coords: 55x54
        // Filler Coords: 176x0
        // Image Size WH: 14x14
        this.drawTexturedModalRect(screenX + 55, screenY + 54 + 14 - powerRemianingPercentage, 176, 14 - powerRemianingPercentage, 14, powerRemianingPercentage);

        // ---------------------
        // ITEM COOKING PROGRESS
        // ---------------------
        int powerCookPercentage = this.ENTITY.getItemCookTimeScaled(43);
        // Screen Coords: 78x35
        // Filler Coords: 176x14
        // Image Size WH: 43x17
        this.drawTexturedModalRect(screenX + 78, screenY + 35, 176, 14, powerCookPercentage + 1, 17);
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        
        // Name displayed above the GUI, typically name of the furnace.
        String s = MadFurnaces.DATADUPLICATOR_TILEENTITY.getLocalizedName();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);

        // Text that labels player inventory area as "Inventory".
        String x = I18n.getString("container.inventory");
        this.fontRenderer.drawString(x, 8, this.ySize - 96 + 2, 4210752);
        
        // Power level
        if (this.isPointInRegion(55, 54, 14, 14, mouseX, mouseY))
        {
            String powerLevelLiteral = String.valueOf(this.ENTITY.getEnergy(ForgeDirection.UNKNOWN)) + "/" + String.valueOf(this.ENTITY.getEnergyCapacity(ForgeDirection.UNKNOWN));
            this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Energy " + String.valueOf(this.ENTITY.getPowerRemainingScaled(100)) + " %", powerLevelLiteral);
        }
        
        // Cooking progress
        if (this.isPointInRegion(78, 35, 43, 17, mouseX, mouseY))
        {
            String powerLevelLiteral = String.valueOf(this.ENTITY.currentItemCookingValue) + "/" + String.valueOf(this.ENTITY.currentItemCookingMaximum);
            this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Progress " + String.valueOf(this.ENTITY.getItemCookTimeScaled(100)) + " %",
                    powerLevelLiteral);
        }

        // Data reel that is to be copied.
        if (this.isPointInRegion(22, 36, 18, 18, mouseX, mouseY))
        {
            if (this.ENTITY.getStackInSlot(0) == null)
                this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Input data reel to copy");
        }
        
        // Empty data reel to encode information onto.
        if (this.isPointInRegion(54, 36, 18, 18, mouseX, mouseY))
        {
            if (this.ENTITY.getStackInSlot(1) == null)
                this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Input empty data reel");
        }
        
        // Help link
        if (this.isPointInRegion(166, 4, 6, 5, mouseX, mouseY))
        {
            if (this.isCtrlKeyDown())
            {
                // The Net Reference - Easter Egg 1
                this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Sandra Bullock Mode");
            }
            else
            {
                this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Help");
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int posX = (this.width - 6) / 2;
        int posY = (this.height - 5) / 2;
        
        // make buttons
        buttonList.clear();
        buttonList.add(new GUIButtonInvisible(1, posX + 81, posY - 76, 6, 5));
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        
        if (button.id == 1 && Desktop.isDesktopSupported())
        {
            if (this.isCtrlKeyDown() && this.isShiftKeyDown())
            {
                try
                {
                    this.passClick(new URI(this.SANDRA_YOUTUBE));
                }
                catch (Exception err)
                {
                    MadScience.logger.info("Unable to open sandra youtube easter egg link in default browser.");
                }
            }
            else
            {
                try
                {
                    this.passClick(new URI(MadConfig.DATADUPLICATOR_HELP));
                }
                catch (Exception err)
                {
                    MadScience.logger.info("Unable to open wiki link in default browser.");
                }
            }
        }
    }
}
