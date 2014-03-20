package madscience.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.common.network.PacketDispatcher;
import madscience.MadConfig;
import madscience.network.MadParticlePacket;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class MadExplosion extends Explosion
{
    /** whether or not this explosion spawns smoke particles */
    private int field_77289_h = 16;
    private Random explosionRNG = new Random();
    private World worldObj;

    /** A list of ChunkPositions of blocks affected by this explosion */
    private Map field_77288_k = new HashMap();

    public MadExplosion(World world, Entity entity, double posX, double posY, double posZ, float strength, boolean spawnFire, boolean breakBlocks)
    {
        super(world, entity, posX, posY, posZ, strength);
        worldObj = world;
    }

    /** Does the first part of the explosion (destroy blocks) */
    @Override
    public void doExplosionA()
    {
        float f = this.explosionSize;
        HashSet hashset = new HashSet();
        int i;
        int j;
        int k;
        double d0;
        double d1;
        double d2;

        for (i = 0; i < this.field_77289_h; ++i)
        {
            for (j = 0; j < this.field_77289_h; ++j)
            {
                for (k = 0; k < this.field_77289_h; ++k)
                {
                    if (i == 0 || i == this.field_77289_h - 1 || j == 0 || j == this.field_77289_h - 1 || k == 0 || k == this.field_77289_h - 1)
                    {
                        double d3 = i / (this.field_77289_h - 1.0F) * 2.0F - 1.0F;
                        double d4 = j / (this.field_77289_h - 1.0F) * 2.0F - 1.0F;
                        double d5 = k / (this.field_77289_h - 1.0F) * 2.0F - 1.0F;
                        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                        d3 /= d6;
                        d4 /= d6;
                        d5 /= d6;
                        float f1 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                        d0 = this.explosionX;
                        d1 = this.explosionY;
                        d2 = this.explosionZ;

                        for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F)
                        {
                            int l = MathHelper.floor_double(d0);
                            int i1 = MathHelper.floor_double(d1);
                            int j1 = MathHelper.floor_double(d2);
                            int k1 = this.worldObj.getBlockId(l, i1, j1);

                            if (k1 > 0)
                            {
                                Block block = Block.blocksList[k1];
                                float f3 = this.exploder != null ? this.exploder.getBlockExplosionResistance(this, this.worldObj, l, i1, j1, block) : block.getExplosionResistance(this.exploder, worldObj, l, i1, j1, explosionX, explosionY, explosionZ);
                                f1 -= (f3 + 0.3F) * f2;
                            }

                            if (f1 > 0.0F && (this.exploder == null || this.exploder.shouldExplodeBlock(this, this.worldObj, l, i1, j1, k1, f1)))
                            {
                                hashset.add(new ChunkPosition(l, i1, j1));
                            }

                            d0 += d3 * f2;
                            d1 += d4 * f2;
                            d2 += d5 * f2;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(hashset);
        this.explosionSize *= 2.0F;
        i = MathHelper.floor_double(this.explosionX - this.explosionSize - 1.0D);
        j = MathHelper.floor_double(this.explosionX + this.explosionSize + 1.0D);
        k = MathHelper.floor_double(this.explosionY - this.explosionSize - 1.0D);
        int l1 = MathHelper.floor_double(this.explosionY + this.explosionSize + 1.0D);
        int i2 = MathHelper.floor_double(this.explosionZ - this.explosionSize - 1.0D);
        int j2 = MathHelper.floor_double(this.explosionZ + this.explosionSize + 1.0D);
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getAABBPool().getAABB(i, k, i2, j, l1, j2));
        Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.explosionX, this.explosionY, this.explosionZ);

        for (int k2 = 0; k2 < list.size(); ++k2)
        {
            Entity entity = (Entity) list.get(k2);
            double d7 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize;

            if (d7 <= 1.0D)
            {
                d0 = entity.posX - this.explosionX;
                d1 = entity.posY + entity.getEyeHeight() - this.explosionY;
                d2 = entity.posZ - this.explosionZ;
                double d8 = MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);

                if (d8 != 0.0D)
                {
                    d0 /= d8;
                    d1 /= d8;
                    d2 /= d8;
                    double d9 = this.worldObj.getBlockDensity(vec3, entity.boundingBox);
                    double d10 = (1.0D - d7) * d9;
                    entity.attackEntityFrom(DamageSource.setExplosionSource(this), ((int) ((d10 * d10 + d10) / 2.0D * 8.0D * this.explosionSize + 1.0D)));
                    double d11 = EnchantmentProtection.func_92092_a(entity, d10);
                    entity.motionX += d0 * d11;
                    entity.motionY += d1 * d11;
                    entity.motionZ += d2 * d11;

                    if (entity instanceof EntityPlayer)
                    {
                        this.field_77288_k.put(entity, this.worldObj.getWorldVec3Pool().getVecFromPool(d0 * d10, d1 * d10, d2 * d10));
                    }
                }
            }
        }

        this.explosionSize = f;
    }

    /** Does the second part of the explosion (sound, particles, drop spawn) */
    @Override
    public void doExplosionB(boolean smoking)
    {
        Iterator iterator;
        ChunkPosition chunkposition;
        int i;
        int j;
        int k;
        int l;

        if (this.isSmoking)
        {
            iterator = this.affectedBlockPositions.iterator();

            while (iterator.hasNext())
            {
                chunkposition = (ChunkPosition) iterator.next();
                i = chunkposition.x;
                j = chunkposition.y;
                k = chunkposition.z;
                l = this.worldObj.getBlockId(i, j, k);

                if (smoking)
                {
                    double d0 = i + this.worldObj.rand.nextFloat();
                    double d1 = j + this.worldObj.rand.nextFloat();
                    double d2 = k + this.worldObj.rand.nextFloat();
                    double d3 = d0 - this.explosionX;
                    double d4 = d1 - this.explosionY;
                    double d5 = d2 - this.explosionZ;
                    double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5D / (d6 / this.explosionSize + 0.1D);
                    d7 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
                    d3 *= d7;
                    d4 *= d7;
                    d5 *= d7;
                    
                    
                    //this.worldObj.spawnParticle("explode", (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
                    PacketDispatcher.sendPacketToAllAround(this.explosionX, this.explosionY, this.explosionZ, MadConfig.PACKETSEND_RADIUS, worldObj.provider.dimensionId, new MadParticlePacket("explode", (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5).makePacket());
                    
                    //this.worldObj.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
                    PacketDispatcher.sendPacketToAllAround(this.explosionX, this.explosionY, this.explosionZ, MadConfig.PACKETSEND_RADIUS, worldObj.provider.dimensionId, new MadParticlePacket("smoke", d0, d1, d2, d3, d4, d5).makePacket());
                }

                if (l > 0)
                {
                    Block block = Block.blocksList[l];

                    if (block.canDropFromExplosion(this))
                    {
                        block.dropBlockAsItemWithChance(this.worldObj, i, j, k, this.worldObj.getBlockMetadata(i, j, k), 1.0F / this.explosionSize, 0);
                    }

                    block.onBlockExploded(this.worldObj, i, j, k, this);
                }
            }
        }

        if (this.isFlaming)
        {
            iterator = this.affectedBlockPositions.iterator();

            while (iterator.hasNext())
            {
                chunkposition = (ChunkPosition) iterator.next();
                i = chunkposition.x;
                j = chunkposition.y;
                k = chunkposition.z;
                l = this.worldObj.getBlockId(i, j, k);
                int i1 = this.worldObj.getBlockId(i, j - 1, k);

                if (l == 0 && Block.opaqueCubeLookup[i1] && this.explosionRNG.nextInt(3) == 0)
                {
                    this.worldObj.setBlock(i, j, k, Block.fire.blockID);
                }
            }
        }
    }

    @Override
    public Map func_77277_b()
    {
        return this.field_77288_k;
    }

    /** Returns either the entity that placed the explosive block, the entity that caused the explosion or null. */
    @Override
    public EntityLivingBase getExplosivePlacedBy()
    {
        return this.exploder == null ? null : (this.exploder instanceof EntityTNTPrimed ? ((EntityTNTPrimed) this.exploder).getTntPlacedBy() : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase) this.exploder : null));
    }
}