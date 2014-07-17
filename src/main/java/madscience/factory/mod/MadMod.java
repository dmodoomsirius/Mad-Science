package madscience.factory.mod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import madscience.factory.MadTileEntityFactory;
import madscience.factory.MadTileEntityFactoryProductData;
import madscience.util.IDManager;

import com.google.common.base.Throwables;
import com.google.gson.Gson;

public class MadMod
{    
    // Identification.
    public static final String ID = "madscience";
    public static final String NAME = "Mad Science";
    public static final String CHANNEL_NAME = "madscience";
    
    // Metadata.
    public static final String DESCRIPTION = "Adds machines, items and mobs to create your own laboratory! Remember kids, science has no limits.. no bounds..";
    public static final String HOME_URL = "http://madsciencemod.com/";
    
    public static final String LOGO_PATH = "assets/madscience/logo.png";
    public static final String CREDITS = "Thanks to Prowler for the awesome assets!";
    public static final String[] AUTHORS = {"Maxwolf Goodliffe", "Fox Diller"};
    public static final String FINGERPRINT = "@FINGERPRINT@";
    public static final String MINECRAFT_VERSION = "[1.6.4,)";
    public static final String DEPENDENCIES = "required-after:Forge@[9.11.1.953,);after:BuildCraft|Energy;after:factorization;after:IC2;after:Railcraft;after:ThermalExpansion";
    
    // Proxy Classes Namespace.
    public static final String CLIENT_PROXY = "madscience.client.ClientProxy";
    public static final String SERVER_PROXY = "madscience.server.CommonProxy";
    
    // Full version string for internal reference by mod.
    public static final String VMAJOR = "@MAJOR@";
    public static final String VMINOR = "@MINOR@";
    public static final String VREVISION = "@REVIS@";
    public static final String VBUILD = "@BUILD@";
    public static final String VERSION_FULL = VMAJOR + "." + VMINOR + "." + VREVISION + "." + VBUILD;

    // Update checker.
    public static final String UPDATE_URL = "http://madsciencemod.com:8080/job/Mad%20Science/Release%20Version/api/xml?xpath=/freeStyleBuild/number";
    
    // Directories definition for assets and localization files.
    public static final String RESOURCE_DIRECTORY = "/assets/" + MadMod.ID + "/";
    public static final String BASE_DIRECTORY_NO_SLASH = MadMod.ID + "/";
    public static final String ASSET_DIRECTORY = "/assets/" + MadMod.ID + "/";
    public static final String TEXTURE_DIRECTORY = "textures/";
    public static final String MODEL_DIRECTORY = "models/";
    
    // Quick links to popular places.
    public static final String MODEL_PATH = ASSET_DIRECTORY + MODEL_DIRECTORY;
    
    // Hook standardized logging class so we can report data on the console without standard out.
    public static Logger LOGGER = null;
    
    // Data container which gets serialized with all our mod information.
    private static MadModData unregisteredMachines;
    
    /** Auto-incrementing configuration IDs. Use this to make sure no config ID is the same. */
    private static IDManager idManager;
    private static int idManagerBlockIndex;
    private static int idManagerItemIndex;

    static
    {
        // Name of the JSON file we are looking for along the classpath.
        String expectedFilename = "mod.json";
        
        // Input we expect to be filled with JSON for every machine we want to load.
        String jsonMachineInput = null;
        
        try
        {
            // Locate all the of JSON files stored in the machines asset folder.
            InputStream machinesJSON = MadMod.class.getClassLoader().getResourceAsStream(expectedFilename);
            
            if (machinesJSON != null)
            {
                // Read the entire contents of the input stream.
                BufferedReader reader = new BufferedReader(new InputStreamReader(machinesJSON));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    out.append(line);
                }
                
                // Copy over the data we just read from the resource stream.
                jsonMachineInput = out.toString();
                
                // Cleanup!
                reader.close();
            }
            else
            {
                LOGGER.info("Unable to locate machine master list '" + expectedFilename + "'");
            }
        }
        catch (Exception err)
        {
            throw Throwables.propagate(err);
        }
        
        // Parse the JSON string we got from resources into an array of product data.
        Gson gson = new Gson();
        MadModData loadedModData = null;
        loadedModData = gson.fromJson(jsonMachineInput, MadModData.class);
        
        // Populate this class with the data we just got.
        unregisteredMachines = loadedModData;
        
        // Create ID manager with ranges it should operate in.
        idManagerBlockIndex = loadedModData.getIDManagerBlockIndex();
        idManagerItemIndex = loadedModData.getIDManagerItemIndex();
        idManager = new IDManager(idManagerBlockIndex, idManagerItemIndex);
    }
    
    public static int getNextBlockID()
    {
        return idManager.getNextBlockID();
    }

    public static int getNextItemID()
    {
        return idManager.getNextItemID();
    }
    
    public static MadTileEntityFactoryProductData[] getUnregisteredMachines()
    {
        return unregisteredMachines.getUnregisteredMachines();
    }
    
    public static MadModData getMadModData()
    {
        return new MadModData(
                ID,
                NAME,
                CHANNEL_NAME,
                DESCRIPTION,
                HOME_URL,
                LOGO_PATH,
                CREDITS,
                AUTHORS,
                FINGERPRINT,
                MINECRAFT_VERSION,
                DEPENDENCIES,
                CLIENT_PROXY,
                SERVER_PROXY,
                VMAJOR,
                VMINOR,
                VREVISION,
                VBUILD,
                UPDATE_URL,
                idManagerBlockIndex,
                idManagerItemIndex,
                MadTileEntityFactory.getMachineDataList());
    }
}