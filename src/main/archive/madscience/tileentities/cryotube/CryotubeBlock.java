package madscience.tileentities.cryotube;

import java.util.List;
import java.util.Random;

import madscience.MadEntities;
import madscience.MadFurnaces;
import madscience.MadScience;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.UniversalElectricity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CryotubeBlock extends BlockContainer
{
    // This flag is used to prevent the furnace inventory to be dropped upon
    // block removal, is used internally when the furnace block changes from
    // idle to active and vice-versa.
    private static boolean keepFurnaceInventory;

    // Is the random generator used by furnace to drop the inventory contents in
    // random directions.
    private final Random cryoTubeTileEntity = new Random();

    // Tile entity class that gets instanced in the world.
    private CryotubeEntity lastPlacedTileEntity;

    public CryotubeBlock(int id)
    {
        // Solid like a rock baby.
        super(id, UniversalElectricity.machine);

        // Set what tab we show up in creative tab.
        this.setCreativeTab(MadEntities.tabMadScience);

        // Determines how many hits it takes to break the block.
        this.setHardness(3.5F);

        // Determines how resistant this block is to explosions.
        this.setResistance(2000.0F);

        // Define how big this item is we make it same size as a default block.
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F);
    }

    /** Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the mask.) Parameters: World, X, Y, Z, mask, list, colliding entity */
    @Override
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }

    /** Called on server worlds only when the block has been replaced by a different block ID, or the same block with a different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old metadata */
    @Override
    public void breakBlock(World world, int x, int y, int z, int blockID, int metaData)
    {
        if (!keepFurnaceInventory)
        {
            CryotubeEntity tileentityfurnace = (CryotubeEntity) world.getBlockTileEntity(x, y, z);

            if (tileentityfurnace != null)
            {
                for (int j1 = 0; j1 < tileentityfurnace.getSizeInputInventory() + tileentityfurnace.getSizeOutputInventory(); ++j1)
                {
                    ItemStack itemstack = tileentityfurnace.getStackInSlot(j1);

                    if (itemstack != null)
                    {
                        float f = this.cryoTubeTileEntity.nextFloat() * 0.8F + 0.1F;
                        float f1 = this.cryoTubeTileEntity.nextFloat() * 0.8F + 0.1F;
                        float f2 = this.cryoTubeTileEntity.nextFloat() * 0.8F + 0.1F;

                        while (itemstack.stackSize > 0)
                        {
                            int k1 = this.cryoTubeTileEntity.nextInt(21) + 10;

                            if (k1 > itemstack.stackSize)
                            {
                                k1 = itemstack.stackSize;
                            }

                            itemstack.stackSize -= k1;
                            EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));

                            if (itemstack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                            }

                            float f3 = 0.05F;
                            entityitem.motionX = (float) this.cryoTubeTileEntity.nextGaussian() * f3;
                            entityitem.motionY = (float) this.cryoTubeTileEntity.nextGaussian() * f3 + 0.2F;
                            entityitem.motionZ = (float) this.cryoTubeTileEntity.nextGaussian() * f3;

                            world.spawnEntityInWorld(entityitem);
                        }
                    }
                }

                world.func_96440_m(x, y, z, blockID);
            }
        }

        super.breakBlock(world, x, y, z, blockID, metaData);

        // Break all the 'ghost blocks'
        if (world.getBlockId(x, y + 1, z) == MadFurnaces.CRYOTUBEGHOST.blockID)
        {
            world.setBlockToAir(x, y + 1, z);
        }

        if (world.getBlockId(x, y + 2, z) == MadFurnaces.CRYOTUBEGHOST.blockID)
        {
            world.setBlockToAir(x, y + 2, z);
        }
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        if (world.getMaterial(x, y + 1, z).isSolid())
        {
            return false;
        }

        if (world.getBlockMaterial(x, y + 2, z).isSolid())
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return !super.canPlaceBlockAt(world, x, y, z) ? false : this.canBlockStay(world, x, y, z);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        // Make sure you set this as your TileEntity class!
        return new CryotubeEntity();
    }

    @Override
    public int damageDropped(int par1)
    {
        // Makes blocks drop the correct metadata in the world.
        return par1;
    }

    public TileEntity getBlockEntity()
    {
        // Returns the TileEntity used by this block.
        return new CryotubeEntity();
    }

    /** If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal strength when this block inputs to a comparator. */
    @Override
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
        return Container.calcRedstoneFromInventory((IInventory) par1World.getBlockTileEntity(par2, par3, par4));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
        return this.blockIcon;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    /** If this returns true, then comparators facing away from this block will use the value from getComparatorInputOverride instead of the actual redstone signal strength. */
    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public boolean hasTileEntity(int meta)
    {
        return true;
    }

    // Returns the ID of the items to drop on destruction.
    @Override
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return MadFurnaces.CRYOTUBE_TILEENTITY.blockID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return MadFurnaces.CRYOTUBE_TILEENTITY.blockID;
    }

    @Override
    public boolean isBlockNormalCube(World world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
    {
        return true;
    }

    // It's not an opaque cube, so you need this.
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        // Called upon block activation (right click on the block.)
        if (par1World.isRemote)
        {
            return true;
        }
        else if (!player.isSneaking())
        {
            // Open GUI on the client...
            CryotubeEntity tileentityfurnace = (CryotubeEntity) par1World.getBlockTileEntity(par2, par3, par4);

            if (tileentityfurnace != null)
            {
                player.openGui(MadScience.instance, this.blockID, par1World, par2, par3, par4);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        this.setDefaultDirection(world, x, y, z);

        if (!world.isRemote)
        {
            // Add 'ghost' blocks that makeup upper section of cryotube.
            world.setBlock(x, y + 1, z, MadFurnaces.CRYOTUBEGHOST.blockID, 1, 3);
            world.setBlock(x, y + 2, z, MadFurnaces.CRYOTUBEGHOST.blockID, 2, 3);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, living, stack);
        lastPlacedTileEntity = (CryotubeEntity) world.getBlockTileEntity(x, y, z);
        int dir = MathHelper.floor_double((living.rotationYaw * 4F) / 360F + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, dir, 0);
    }

    // This is the icon to use for showing the block in your hand.
    @Override
    public void registerIcons(IconRegister icon)
    {
        this.blockIcon = icon.registerIcon(MadScience.ID + ":" + MadFurnaces.CRYOTUBE_INTERNALNAME);
    }

    // It's not a normal block, so you need this too.
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    // Set a blocks direction
    private void setDefaultDirection(World par1World, int par2, int par3, int par4)
    {
        if (!par1World.isRemote)
        {
            int l = par1World.getBlockId(par2, par3, par4 - 1);
            int i1 = par1World.getBlockId(par2, par3, par4 + 1);
            int j1 = par1World.getBlockId(par2 - 1, par3, par4);
            int k1 = par1World.getBlockId(par2 + 1, par3, par4);
            byte b0 = 3;

            if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1])
            {
                b0 = 3;
            }

            if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l])
            {
                b0 = 2;
            }

            if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1])
            {
                b0 = 5;
            }

            if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1])
            {
                b0 = 4;
            }

            par1World.setBlockMetadataWithNotify(par2, par3, par4, b0, 2);
        }
    }

    // This will tell minecraft not to render any side of our cube.
    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        return false;
    }
}
