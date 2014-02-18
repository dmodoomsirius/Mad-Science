package madscience.items;

import java.util.Collection;
import java.util.List;

import madscience.GMORegistry;
import madscience.MadEntities;
import madscience.MadScience;
import madscience.metaitems.MainframeComponentsMetadata;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class GeneticallyModifiedMonsterPlacer extends Item
{

    private Icon icon;

    public GeneticallyModifiedMonsterPlacer(int id)
    {
        super(id);
        setHasSubtypes(true);
        setCreativeTab(MadEntities.tabMadScience);
        this.setUnlocalizedName("gmoMonsterPlacer");
        
        // We may stack the same amount as normal spawn eggs.
        this.maxStackSize = 64;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        String name = ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".name")).trim();
        MadSpawnEggInfo info = GMORegistry.getEggInfo((short) stack.getItemDamage());

        if (info == null)
            return name;

        String mobID = info.mobID;
        String displayName = I18n.getString("item.monsterPlacer.name") + " " + I18n.getString(info.mobID);

        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("mobID"))
                mobID = compound.getString("mobID");
            if (compound.hasKey("displayName"))
                displayName = compound.getString("displayName");
        }
       
        name = "entity." + mobID;

        return name;
    }

    @Override
	public int getColorFromItemStack(ItemStack stack, int par2)
    {
        MadSpawnEggInfo info = GMORegistry.getEggInfo((short) stack.getItemDamage());

        if (info == null)
            return 16777215;

        int color = (par2 == 0) ? info.primaryColor : info.secondaryColor;

        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (par2 == 0 && compound.hasKey("primaryColor"))
                color = compound.getInteger("primaryColor");
            if (par2 != 0 && compound.hasKey("secondaryColor"))
                color = compound.getInteger("secondaryColor");
        }

        return color;
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

    public static Entity spawnCreature(World world, ItemStack stack, double x, double y, double z)
    {
        MadSpawnEggInfo info = GMORegistry.getEggInfo((short) stack.getItemDamage());

        if (info == null)
            return null;

        String mobID = info.mobID;
        NBTTagCompound spawnData = info.spawnData;

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

    private static void spawnRiddenCreatures(Entity entity, World world, NBTTagCompound cur)
    {
        while (cur.hasKey("Riding"))
        {
            cur = cur.getCompoundTag("Riding");
            Entity newEntity = EntityList.createEntityByName(cur.getString("id"), world);
            if (newEntity != null)
            {
                addNBTData(newEntity, cur);
                newEntity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                world.spawnEntityInWorld(newEntity);
                entity.mountEntity(newEntity);
            }
            entity = newEntity;
        }
    }

    private static void addNBTData(Entity entity, NBTTagCompound spawnData)
    {
        NBTTagCompound newTag = new NBTTagCompound();
        entity.writeToNBTOptional(newTag);

        for (NBTBase nbt : (Collection<NBTBase>) spawnData.getTags())
            newTag.setTag(nbt.getName(), nbt.copy());

        entity.readFromNBT(newTag);
    }

    @Override
	public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
	public Icon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 > 0 ? icon : super.getIconFromDamageForRenderPass(par1, par2);
    }

    @Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List list)
    {
        for (MadSpawnEggInfo info : GMORegistry.getEggInfoList())
            list.add(new ItemStack(par1, 1, info.eggID));
    }

    @Override
	public void registerIcons(IconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon(MadScience.ID + ":gmoMonsterPlacer");
        icon = iconRegister.registerIcon(MadScience.ID + ":gmoMonsterPlacer_overlay");
    }

    public static String attemptToTranslate(String key, String _default)
    {
        String result = StatCollector.translateToLocal(key);
        return (result.equals(key)) ? _default : result;
    }
}