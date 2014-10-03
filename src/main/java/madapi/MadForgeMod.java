package madapi;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;

import madapi.data.MadFluidFactoryProductData;
import madapi.data.MadItemFactoryProductData;
import madapi.data.MadTileEntityFactoryProductData;
import madapi.item.MadMetaItemData;
import madapi.mod.MadGUIHandler;
import madapi.mod.MadModLoader;
import madapi.network.MadPacketHandler;
import madapi.network.MadUpdateChecker;
import madapi.product.MadItemFactoryProduct;
import madapi.product.MadTileEntityFactoryProduct;
import madapi.proxy.CommonProxy;
import madapi.recipe.MadRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = MadModMetadata.ID, name = MadModMetadata.NAME, version = MadModMetadata.VERSION_FULL, useMetadata = false, acceptedMinecraftVersions = MadModMetadata.MINECRAFT_VERSION, dependencies = MadModMetadata.DEPENDENCIES)
@NetworkMod(channels = { MadModMetadata.CHANNEL_NAME }, packetHandler = MadPacketHandler.class, clientSideRequired = true, serverSideRequired = false)
public class MadForgeMod
{
    // Proxy that runs commands based on where they are from so we can separate server and client logic easily.
    @SidedProxy(clientSide = MadModMetadata.CLIENT_PROXY, serverSide = MadModMetadata.SERVER_PROXY)
    public static CommonProxy proxy;

    // Public instance of our mod that Forge needs to hook us, based on our internal modid.
    @Instance(value = MadModMetadata.CHANNEL_NAME)
    public static MadForgeMod instance;

    // Public extra data about our mod that Forge uses in the mods listing page for more information.
    @Mod.Metadata(MadModMetadata.ID)
    private static ModMetadata metadata;

    // Hooks Forge's replacement openGUI function so we can route our own ID's to proper interfaces.
    private static MadGUIHandler guiHandler = new MadGUIHandler();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) // NO_UCD (unused code)
    {
        // --------------
        // PRE-INIT START
        // --------------
        
        // Register instance.
        instance = this;

        // Populate generic logger with instance provided by Minecraft/Forge. 
        MadModLoader.setLog(event.getModLog(), FMLLog.getLogger());
        
        // Generate and read our standardized Forge configuration file.
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        
        // Load existing configuration data.
        config.load();
        
        // Configure block and item ID's for all unregistered products.
        this.configureTileEntities(config);
        this.configureItems(config);
        this.configureFluids(config);
        
        // Save any changed or added values to config.
        config.save();

        // Setup Mod Metadata for players to see in mod list with other mods.
        metadata.modId = MadModMetadata.ID;
        metadata.name = MadModMetadata.NAME;
        metadata.description = MadModMetadata.DESCRIPTION;
        metadata.url = MadModMetadata.HOME_URL;
        metadata.logoFile = MadModMetadata.LOGO_PATH;
        metadata.version = MadModMetadata.VMAJOR + "." + MadModMetadata.VMINOR + MadModMetadata.VREVISION;
        metadata.authorList = Arrays.asList(MadModMetadata.AUTHORS);
        metadata.credits = MadModMetadata.CREDITS;
        metadata.autogenerated = false;

        // Register all unregistered products with their respective factories.
        this.loadItems();
        this.loadFluids();
        this.loadTileEntities();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) // NO_UCD (unused code)
    {
        // -------------
        // INIT - MIDDLE
        // -------------
        
        // Registers sound handler which will be called to load sounds added to event bus for sounds.
        proxy.registerSoundHandler();
        
        // Registers GUI handler which allows server and client to map ID's to GUI's.
        NetworkRegistry.instance().registerGuiHandler(MadForgeMod.instance, MadForgeMod.guiHandler);
        
        // Check Mad Science Jenkins build server for latest build numbers to compare with running one.
        MadUpdateChecker.checkJenkinsBuildNumbers();
    }
    
    @EventHandler
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void postInit(FMLPostInitializationEvent event) // NO_UCD (unused code)
    {
        // --------------
        // POST-INIT LAST
        // --------------
        
        // Loads all shaped and shapeless (and vanilla furnace) recipes required by factory products.
        this.recipeItems();
        this.recipeTileEntity();
        
        // Interface with NEI and attempt to call functions from it if it exists.
        // Note: This method was given by Alex_hawks, buy him a beer if you see him!
        if (Loader.isModLoaded("NotEnoughItems"))
        {
            try
            {
                Class clazz = Class.forName("codechicken.nei.api.API");
                Method m = clazz.getMethod("hideItem", Integer.TYPE);

                // Magazine Loader Ghost Block.
                //m.invoke(null, MadFurnaces.MAGLOADERGHOST.blockID);
            }
            catch (Throwable err)
            {
                MadModLoader.log().log(Level.WARNING, "NEI Integration has failed...");
                MadModLoader.log().log(Level.WARNING, "Please email devs@madsciencemod.com the following stacktrace.");
                err.printStackTrace();
                MadModLoader.log().log(Level.WARNING, "Spamming console to make more obvious...");
                for (int i = 0; i < 15; i++)
                {
                    MadModLoader.log().log(Level.WARNING, "Something Broke. See above.");
                }
            }

        }
        
        // ---------
        // DEBUGGING
        // ---------
        
        // Prints out all internal names.
        proxy.dumpUnlocalizedNames();
        
        // Dumps all registered machines JSON to disk.
        proxy.dumpAllMachineJSON();
    }

    private void recipeTileEntity()
    {
        Iterable<MadTileEntityFactoryProduct> registeredMachines = MadTileEntityFactory.instance().getMachineInfoList();
        for (Iterator iterator = registeredMachines.iterator(); iterator.hasNext();)
        {
            MadTileEntityFactoryProduct registeredMachine = (MadTileEntityFactoryProduct) iterator.next();
            if (registeredMachine != null)
            {
                // Recipes that pertain to machine itself, association slots with items they should have in them.
                registeredMachine.loadMachineInternalRecipes();
                
                // Recipes for crafting the machine itself, registered with Minecraft/Forge GameRegistry.
                MadRecipe.loadCraftingRecipes(
                        registeredMachine.getCraftingRecipe(),
                        registeredMachine.getMachineName(),
                        new ItemStack(registeredMachine.getBlockContainer()));
            }
        }
    }

    private void recipeItems()
    {
        Iterable<MadItemFactoryProduct> registeredItems = MadItemFactory.instance().getItemInfoList();
        for (Iterator iterator = registeredItems.iterator(); iterator.hasNext();)
        {
            MadItemFactoryProduct registeredItem = (MadItemFactoryProduct) iterator.next();
            if (registeredItem != null)
            {                
                // Recipes for crafting the item (if one exists, since most are made by machines).
                // Note: ItemStack that is sent will have output amount changed according to recipe.
                for (MadMetaItemData subItem : registeredItem.getSubItems())
                {
                    MadRecipe.loadCraftingRecipes(
                            subItem.getCraftingRecipes(),
                            subItem.getItemName(),
                            new ItemStack(registeredItem.getItem(), 1, subItem.getMetaID()));
                    
                    // Recipes for cooking one item into another in vanilla Minecraft furnace.
                    MadRecipe.loadVanillaFurnaceRecipes(subItem.getFurnaceRecipes());
                }
            }
        }
    }

    @EventHandler
    public void invalidFingerprint(FMLFingerprintViolationEvent event) // NO_UCD (unused code)
    {
        // Check to see if fingerprint matches what we expect.
        if (MadModMetadata.FINGERPRINT.equals(MadModMetadata.FINGERPRINT))
        {
            LogWrapper.warning("The copy of " + MadModMetadata.NAME + " that you are running passesd all verification and fingerprint checks. It has not been modified from original.");
        }
        else
        {
            LogWrapper.severe("The copy of " + MadModMetadata.NAME + " that you are running has been modified from the original, and unpredictable things may happen. Please consider re-downloading the original version of the mod.");
        }
    }
    
    private void loadTileEntities()
    {
        MadModLoader.log().info("Creating Tile Entities");
        
        // Take the machines from loaded mod instance and register them with tile entity factory.
        MadTileEntityFactoryProductData[] machineData = MadModLoader.getUnregisteredMachines();
        for (int i = 0; i < machineData.length; i++) 
        {
            MadTileEntityFactoryProductData unregisteredMachine = machineData[i];
            MadTileEntityFactory.instance().registerMachine(unregisteredMachine);
        }
    }

    private void loadItems()
    {
        MadModLoader.log().info("Creating Items");
        
        // Grab all unregistered items from mod manager and pass them through the item factory.
        MadItemFactoryProductData[] itemData = MadModLoader.getUnregisteredItems();
        for (int i = 0; i < itemData.length; i++) 
        {
            MadItemFactoryProductData unregisteredItem = itemData[i];
            MadItemFactory.instance().registerItem(unregisteredItem);
        }
    }
    
    private void loadFluids()
    {
        MadModLoader.log().info("Creating Fluids");

        // Take the fluids from loaded mod instance and register them with fluid factory.
        MadFluidFactoryProductData[] fluidData = MadModLoader.getUnregisteredFluids();
        for (int i = 0; i < fluidData.length; i++) 
        {
            MadFluidFactoryProductData unregisteredFluid = fluidData[i];
            MadFluidFactory.instance().registerFluid(unregisteredFluid);
        }
    }

    private void configureTileEntities(Configuration config)
    {
        MadTileEntityFactoryProductData[] machineData = MadModLoader.getUnregisteredMachines();
        for (int x = 0; x < machineData.length; x++) 
        {
            MadTileEntityFactoryProductData unregisteredMachine = machineData[x];
            
            // Get a new block ID from our ID manager, and set the block ID for our unregistered machine to whatever ID Manager decides.
            int defaultBlockID = MadModLoader.getNextBlockID();
            unregisteredMachine.setBlockID(defaultBlockID);
            
            // Get configuration file information, if there is any...
            int configBlockID = config.getBlock(Configuration.CATEGORY_BLOCK, unregisteredMachine.getMachineName(), unregisteredMachine.getBlockID()).getInt();
            
            // Check if unregistered machine default ID is different from read value.
            if (unregisteredMachine.getBlockID() == configBlockID)
            {
                MadModLoader.log().info("[" + unregisteredMachine.getMachineName() + "]Using default block ID of " + String.valueOf(configBlockID));
            }
            else
            {
                MadModLoader.log().info("[" + unregisteredMachine.getMachineName() + "]Using user configured block ID of " + String.valueOf(configBlockID));
            }
        }
    }

    private void configureItems(Configuration config)
    {
        MadItemFactoryProductData[] itemData = MadModLoader.getUnregisteredItems();
        for (int i = 0; i < itemData.length; i++)
        {
            MadItemFactoryProductData unregisteredItem = itemData[i];
            
            // Get new item ID from ID manager, use this as default.
            int defaultItemID = MadModLoader.getNextItemID();
            unregisteredItem.setItemID(defaultItemID);
            
            // Grab existing item item ID configuration if it exists.
            int configItemID = config.getItem(Configuration.CATEGORY_ITEM, unregisteredItem.getItemBaseName(), unregisteredItem.getItemID()).getInt();
            
            // Check if we used the configuration value or the ID manager one.
            if (unregisteredItem.getItemID() == configItemID)
            {
                MadModLoader.log().info("[" + unregisteredItem.getItemBaseName() + "]Using default item ID of " + String.valueOf(configItemID));
            }
            else
            {
                MadModLoader.log().info("[" + unregisteredItem.getItemBaseName() + "]Using user configured item ID of " + String.valueOf(configItemID));
            }
        }
    }
    
    private void configureFluids(Configuration config)
    {
        MadFluidFactoryProductData[] fluidData = MadModLoader.getUnregisteredFluids();
        for (int x = 0; x < fluidData.length; x++) 
        {
            MadFluidFactoryProductData unregisteredFluid = fluidData[x];
            
            // Get a new block ID from our ID manager, and set the block ID for our unregistered machine to whatever ID Manager decides.
            int defaultFluidID = MadModLoader.getNextBlockID();
            unregisteredFluid.setFluidID(defaultFluidID);
            
            // Get a new item ID from out ID manager for the fluid container (but only if this fluid has one).
            if (unregisteredFluid.hasFluidContainerItem())
            {
                int defaultFluidContainerID = MadModLoader.getNextItemID();
                unregisteredFluid.setFluidContainerID(defaultFluidContainerID);
            }
            
            // Get configuration file information, if there is any...
            int configFluidID = config.getBlock(Configuration.CATEGORY_BLOCK, unregisteredFluid.getFluidName(), unregisteredFluid.getFluidID()).getInt();
            int configFluidContainerID = config.getItem(Configuration.CATEGORY_ITEM, unregisteredFluid.getFluidContainerName(), unregisteredFluid.getFluidContainerID()).getInt();            
            
            // Check if unregistered fluid default ID is different from read value.
            if (unregisteredFluid.getFluidID() == configFluidID)
            {
                MadModLoader.log().info("[" + unregisteredFluid.getFluidName() + "]Using default block ID of " + String.valueOf(configFluidID));
            }
            else
            {
                MadModLoader.log().info("[" + unregisteredFluid.getFluidName() + "]Using user configured block ID of " + String.valueOf(configFluidID));
            }
            
            // Check if unregistered fluid container item ID is different from default ID manager value.
            if (unregisteredFluid.getFluidContainerID() == configFluidContainerID)
            {
                MadModLoader.log().info("[" + unregisteredFluid.getFluidContainerName() + "]Using default item ID of " + String.valueOf(configFluidContainerID));
            }
            else
            {
                MadModLoader.log().info("[" + unregisteredFluid.getFluidContainerName() + "]Using user configured item ID of " + String.valueOf(configFluidContainerID));
            }
        }
    }
}