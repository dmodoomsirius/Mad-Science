package madscience;

import madscience.tileentities.dnaextractor.DNAExtractorRecipes;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class MadDNA
{


    // -----------------------
    // DNA SAMPLE REGISTRY ADD
    // -----------------------

    public static void createBatDNA(int itemID)
    {
        // Adds pure sample of Bat DNA.
        MadScience.logger.info("-DNA Bat");
        DNA_BAT = (DNABat) new DNABat(itemID, 4996656, 986895).setUnlocalizedName(DNA_BAT_INTERNALNAME);
        GameRegistry.registerItem(DNA_BAT, DNA_BAT_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_BAT.itemID, new ItemStack(DNA_BAT), 0.50F);
    }

    public static void createCaveSpiderDNA(int itemID)
    {
        MadScience.logger.info("-DNA Cave Spider");
        DNA_CAVESPIDER = (DNACaveSpider) new DNACaveSpider(itemID, 803406, 11013646).setUnlocalizedName(DNA_CAVESPIDER_INTERNALNAME);
        GameRegistry.registerItem(DNA_CAVESPIDER, DNA_CAVESPIDER_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_CAVESPIDER.itemID, new ItemStack(DNA_CAVESPIDER), 0.35F);
        DNAExtractorRecipes.addSmelting(Item.fermentedSpiderEye.itemID, new ItemStack(DNA_CAVESPIDER), 0.35F);
    }

    public static void createChickenDNA(int itemID)
    {
        // Adds pure sample of Chicken DNA.
        MadScience.logger.info("-DNA Chicken");
        DNA_CHICKEN = (DNAChicken) new DNAChicken(itemID, 10592673, 16711680).setUnlocalizedName(DNA_CHICKEN_INTERNALNAME);
        GameRegistry.registerItem(DNA_CHICKEN, DNA_CHICKEN_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_CHICKEN.itemID, new ItemStack(DNA_CHICKEN), 0.15F);
        DNAExtractorRecipes.addSmelting(Item.feather.itemID, new ItemStack(DNA_CHICKEN), 0.15F);
        DNAExtractorRecipes.addSmelting(Item.egg.itemID, new ItemStack(DNA_CHICKEN), 0.15F);
        DNAExtractorRecipes.addSmelting(Item.chickenCooked.itemID, new ItemStack(DNA_CHICKEN), 0.15F);
        DNAExtractorRecipes.addSmelting(Item.chickenRaw.itemID, new ItemStack(DNA_CHICKEN), 0.15F);
    }

    public static void createCowDNA(int itemID)
    {
        // Adds pure sample of Cow DNA.
        MadScience.logger.info("-DNA Cow");
        DNA_COW = (DNACow) new DNACow(itemID, 4470310, 10592673).setUnlocalizedName(DNA_COW_INTERNALNAME);
        GameRegistry.registerItem(DNA_COW, DNA_COW_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_COW.itemID, new ItemStack(DNA_COW), 0.30F);
        DNAExtractorRecipes.addSmelting(Item.leather.itemID, new ItemStack(DNA_COW), 0.30F);
        DNAExtractorRecipes.addSmelting(Item.beefCooked.itemID, new ItemStack(DNA_COW), 0.30F);
        DNAExtractorRecipes.addSmelting(Item.beefRaw.itemID, new ItemStack(DNA_COW), 0.30F);
        DNAExtractorRecipes.addSmelting(Item.bootsLeather.itemID, new ItemStack(DNA_COW), 0.30F);
        DNAExtractorRecipes.addSmelting(Item.helmetLeather.itemID, new ItemStack(DNA_COW), 0.30F);
        DNAExtractorRecipes.addSmelting(Item.legsLeather.itemID, new ItemStack(DNA_COW), 0.30F);
        DNAExtractorRecipes.addSmelting(Item.plateLeather.itemID, new ItemStack(DNA_COW), 0.30F);
        DNAExtractorRecipes.addSmelting(Item.bucketMilk.itemID, new ItemStack(DNA_COW), 0.30F);
    }

    public static void createCreeperDNA(int itemID)
    {
        // Adds pure sample of Creeper DNA.
        MadScience.logger.info("-DNA Creeper");
        DNA_CREEPER = (DNACreeper) new DNACreeper(itemID, 894731, 0).setUnlocalizedName(DNA_CREEPER_INTERNALNAME);
        GameRegistry.registerItem(DNA_CREEPER, DNA_CREEPER_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_CREEPER.itemID, new ItemStack(DNA_CREEPER), 0.22F);
        DNAExtractorRecipes.addSmelting(Item.gunpowder.itemID, new ItemStack(DNA_CREEPER), 0.22F);
        DNAExtractorRecipes.addSmelting(Item.skull.itemID, 4, new ItemStack(DNA_CREEPER), 0.22F);
    }

    public static void createEndermanDNA(int itemID)
    {
        MadScience.logger.info("-DNA Enderman");
        DNA_ENDERMAN = (DNAEnderman) new DNAEnderman(itemID, 1447446, 0).setUnlocalizedName(DNA_ENDERMAN_INTERNALNAME);
        GameRegistry.registerItem(DNA_ENDERMAN, DNA_ENDERMAN_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_ENDERMAN.itemID, new ItemStack(DNA_ENDERMAN), 0.42F);
        DNAExtractorRecipes.addSmelting(Item.enderPearl.itemID, new ItemStack(DNA_ENDERMAN), 0.42F);
        DNAExtractorRecipes.addSmelting(Item.eyeOfEnder.itemID, new ItemStack(DNA_ENDERMAN), 0.42F);
    }

    public static void createGhastDNA(int itemID)
    {
        MadScience.logger.info("-DNA Ghast");
        DNA_GHAST = (DNAGhast) new DNAGhast(itemID, 16382457, 12369084).setUnlocalizedName(DNA_GHAST_INTERNALNAME);
        GameRegistry.registerItem(DNA_GHAST, DNA_GHAST_INTERNALNAME);

        // DNA Extractor (ghast tears give ghast DNA).
        DNAExtractorRecipes.addSmelting(Item.ghastTear.itemID, new ItemStack(DNA_GHAST), 0.42F);
    }

    public static void createHorseDNA(int itemID)
    {
        MadScience.logger.info("-DNA Horse");
        DNA_HORSE = (DNAHorse) new DNAHorse(itemID, 12623485, 15656192).setUnlocalizedName(DNA_HORSE_INTERNALNAME);
        GameRegistry.registerItem(DNA_HORSE, DNA_HORSE_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_HORSE.itemID, new ItemStack(DNA_HORSE), 0.42F);
    }

    public static void createMushroomCowDNA(int itemID)
    {
        MadScience.logger.info("-DNA Mushroom Cow");
        DNA_MUSHROOMCOW = (DNAMushroomCow) new DNAMushroomCow(itemID, 10489616, 12040119).setUnlocalizedName(DNA_MUSHROOMCOW_INTERNALNAME);
        GameRegistry.registerItem(DNA_MUSHROOMCOW, DNA_MUSHROOMCOW_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_MUSHROOMCOW.itemID, new ItemStack(DNA_MUSHROOMCOW), 0.42F);
    }

    public static void createOcelotDNA(int itemID)
    {
        MadScience.logger.info("-DNA Ocelot");
        DNA_OCELOT = (DNAOcelot) new DNAOcelot(itemID, 15720061, 5653556).setUnlocalizedName(DNA_OCELOT_INTERNALNAME);
        GameRegistry.registerItem(DNA_OCELOT, DNA_OCELOT_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_OCELOT.itemID, new ItemStack(DNA_OCELOT), 0.42F);
    }

    public static void createPigDNA(int itemID)
    {
        // Adds pure sample of Pig DNA.
        MadScience.logger.info("-DNA Pig");
        DNA_PIG = (DNAPig) new DNAPig(itemID, 15771042, 14377823).setUnlocalizedName(DNA_PIG_INTERNALNAME);
        GameRegistry.registerItem(DNA_PIG, DNA_PIG_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_PIG.itemID, new ItemStack(DNA_PIG), 0.35F);
        DNAExtractorRecipes.addSmelting(Item.porkRaw.itemID, new ItemStack(DNA_PIG), 0.35F);
        DNAExtractorRecipes.addSmelting(Item.porkCooked.itemID, new ItemStack(DNA_PIG), 0.35F);
    }

    public static void createSheepDNA(int itemID)
    {
        MadScience.logger.info("-DNA Sheep");
        DNA_SHEEP = (DNASheep) new DNASheep(itemID, 15198183, 16758197).setUnlocalizedName(DNA_SHEEP_INTERNALNAME);
        GameRegistry.registerItem(DNA_SHEEP, DNA_SHEEP_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_SHEEP.itemID, new ItemStack(DNA_SHEEP), 0.42F);
        DNAExtractorRecipes.addSmelting(Block.cloth.blockID, new ItemStack(DNA_SHEEP), 0.42F);
    }

    public static void createSkeletonDNA(int itemID)
    {
        MadScience.logger.info("-DNA Skeleton");
        DNA_SKELETON = (DNASkeleton) new DNASkeleton(itemID, 12698049, 4802889).setUnlocalizedName(DNA_SKELETON_INTERNALNAME);
        GameRegistry.registerItem(DNA_SKELETON, DNA_SKELETON_INTERNALNAME);

        // DNA Extractor (a full bone is required to make skeleton DNA).
        DNAExtractorRecipes.addSmelting(Item.bone.itemID, new ItemStack(DNA_SKELETON), 0.42F);
        DNAExtractorRecipes.addSmelting(Item.dyePowder.itemID, 15, new ItemStack(DNA_SKELETON), 0.42F);
        DNAExtractorRecipes.addSmelting(Item.skull.itemID, 0, new ItemStack(DNA_SKELETON), 0.42F);
    }

    public static void createSlimeDNA(int itemID)
    {
        // Adds pure sample of Slime DNA.
        MadScience.logger.info("-DNA Slime");
        DNA_SLIME = new DNASlime(itemID, 5349438, 8306542).setUnlocalizedName(DNA_SLIME_INTERNALNAME);
        GameRegistry.registerItem(DNA_SLIME, DNA_SLIME_INTERNALNAME);

        // DNA Extractor (slime ball turns into slime DNA)
        DNAExtractorRecipes.addSmelting(Item.slimeBall.itemID, new ItemStack(DNA_SLIME), 0.50F);
        DNAExtractorRecipes.addSmelting(Block.pistonStickyBase.blockID, new ItemStack(DNA_SLIME), 0.50F);
    }

    public static void createSpiderDNA(int itemID)
    {
        // Adds pure sample of Spider DNA.
        MadScience.logger.info("-DNA Spider");
        DNA_SPIDER = (DNASpider) new DNASpider(itemID, 3419431, 11013646).setUnlocalizedName(DNA_SPIDER_INTERNALNAME);
        GameRegistry.registerItem(DNA_SPIDER, DNA_SPIDER_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_SPIDER.itemID, new ItemStack(DNA_SPIDER), 0.66F);
        DNAExtractorRecipes.addSmelting(Item.spiderEye.itemID, new ItemStack(DNA_SPIDER), 0.66F);
        DNAExtractorRecipes.addSmelting(Item.silk.itemID, new ItemStack(DNA_SPIDER), 0.66F);
    }

    public static void createSquidDNA(int itemID)
    {
        MadScience.logger.info("-DNA Squid");
        DNA_SQUID = (DNASquid) new DNASquid(itemID, 2243405, 7375001).setUnlocalizedName(DNA_SQUID_INTERNALNAME);
        GameRegistry.registerItem(DNA_SQUID, DNA_SQUID_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_SQUID.itemID, new ItemStack(DNA_SQUID), 0.42F);
        DNAExtractorRecipes.addSmelting(Item.dyePowder.itemID, 0, new ItemStack(DNA_SQUID), 0.42F);
    }

    public static void createVillagerDNA(int itemID)
    {
        // Adds pure sample of Villager DNA.
        MadScience.logger.info("-DNA Villager");
        DNA_VILLAGER = (DNAVillager) new DNAVillager(itemID, 5651507, 12422002).setUnlocalizedName(DNA_VILLAGER_INTERNALNAME);
        GameRegistry.registerItem(DNA_VILLAGER, DNA_VILLAGER_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_VILLAGER.itemID, new ItemStack(DNA_VILLAGER), 1.0F);
    }

    public static void createWitchDNA(int itemID)
    {
        MadScience.logger.info("-DNA Witch");
        DNA_WITCH = (DNAWitch) new DNAWitch(itemID, 3407872, 5349438).setUnlocalizedName(DNA_WITCH_INTERNALNAME);
        GameRegistry.registerItem(DNA_WITCH, DNA_WITCH_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_WITCH.itemID, new ItemStack(DNA_WITCH), 0.42F);
    }

    public static void createWolfDNA(int itemID)
    {
        MadScience.logger.info("-DNA Wolf");
        DNA_WOLF = (DNAWolf) new DNAWolf(itemID, 14144467, 13545366).setUnlocalizedName(DNA_WOLF_INTERNALNAME);
        GameRegistry.registerItem(DNA_WOLF, DNA_WOLF_INTERNALNAME);

        // DNA Extractor
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_WOLF.itemID, new ItemStack(DNA_WOLF), 0.42F);
    }

    public static void createZombieDNA(int itemID)
    {
        // DNA Extractor
        // Adds pure sample of Zombie DNA.
        MadScience.logger.info("-DNA Zombie");
        DNA_ZOMBIE = (DNAZombie) new DNAZombie(itemID, 44975, 7969893).setUnlocalizedName(DNA_ZOMBIE_INTERNALNAME);
        GameRegistry.registerItem(DNA_ZOMBIE, DNA_ZOMBIE_INTERNALNAME);

        // Can create single DNA zombie strand from 1 needle or zombie skull.
        DNAExtractorRecipes.addSmelting(MadNeedles.NEEDLE_ZOMBIE.itemID, new ItemStack(DNA_ZOMBIE), 0.50F);
        DNAExtractorRecipes.addSmelting(Item.skull.itemID, 2, new ItemStack(DNA_ZOMBIE), 0.50F);
        DNAExtractorRecipes.addSmelting(Item.rottenFlesh.itemID, new ItemStack(DNA_ZOMBIE), 0.50F);
    }
}
