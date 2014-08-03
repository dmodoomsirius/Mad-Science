package madscience.items;

import madscience.factory.item.MadItemFactoryProduct;
import madscience.factory.item.prefab.MadItemPrefab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemGMOMobPlacer extends MadItemPrefab
{
    public ItemGMOMobPlacer(MadItemFactoryProduct itemData)
    {
        super(itemData);
    }

    private static Entity spawnCreature(World world, ItemStack stack, double x, double y, double z)
    {
        MadSpawnEggInfo info = MadGMORegistry.getEggInfo((short) stack.getItemDamage());

        if (info == null)
            return null;

        String mobID = info.mobID;
        NBTTagCompound spawnData = info.getSpawnData();

        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("mobID"))
                mobID = compound.getString("mobID");
            if (compound.hasKey("spawnData"))
                spawnData = compound.getCompoundTag("spawnData");
        }

        Entity entity = null;

        entity = EntityList.createEntityByName(mobID, world);

        if (entity != null)
        {
            if (entity instanceof EntityLiving)
            {
                EntityLiving entityliving = (EntityLiving) entity;
                entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                entityliving.rotationYawHead = entityliving.rotationYaw;
                entityliving.renderYawOffset = entityliving.rotationYaw;
                entityliving.onSpawnWithEgg(null);
                if (!spawnData.hasNoTags())
                    addNBTData(entity, spawnData);
                world.spawnEntityInWorld(entity);
                entityliving.playLivingSound();
                spawnRiddenCreatures(entity, world, spawnData);
            }
        }

        return entity;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
            return stack;

        MovingObjectPosition trace = getMovingObjectPositionFromPlayer(world, player, true);

        if (trace == null)
            return stack;

        if (trace.typeOfHit == EnumMovingObjectType.TILE)
        {
            int x = trace.blockX;
            int y = trace.blockY;
            int z = trace.blockZ;

            if (!world.canMineBlock(player, x, y, z) || !player.canPlayerEdit(x, y, z, trace.sideHit, stack))
                return stack;

            if (world.getBlockMaterial(x, y, z) == Material.water)
            {

                Entity entity = spawnCreature(world, stack, x, y, z);
                if (entity != null)
                {
                    if (entity instanceof EntityLiving && stack.hasDisplayName())
                        ((EntityLiving) entity).setCustomNameTag(stack.getDisplayName());
                    if (!player.capabilities.isCreativeMode)
                        --stack.stackSize;
                }
            }
        }

        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        if (world.isRemote)
            return true;

        int i1 = world.getBlockId(x, y, z);
        x += Facing.offsetsXForSide[par7];
        y += Facing.offsetsYForSide[par7];
        z += Facing.offsetsZForSide[par7];
        double d0 = 0.0D;

        if (par7 == 1 && Block.blocksList[i1] != null && Block.blocksList[i1].getRenderType() == 11)
            d0 = 0.5D;

        Entity entity = spawnCreature(world, stack, x + 0.5D, y + d0, z + 0.5D);

        if (entity != null)
        {
            if (entity instanceof EntityLiving && stack.hasDisplayName())
                ((EntityLiving) entity).setCustomNameTag(stack.getDisplayName());
            if (!player.capabilities.isCreativeMode)
                --stack.stackSize;
        }
        return true;

    }
}