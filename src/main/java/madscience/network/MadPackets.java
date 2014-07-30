package madscience.network;

import madscience.factory.mod.MadMod;
import madscience.factory.tileentity.templates.MadTileEntityPacketTemplate;
import madscience.items.warningsign.WarningSignPacketClientRequestSignType;
import madscience.items.warningsign.WarningSignPacketServerRequestReplySignType;
import madscience.items.warningsign.WarningSignPacketServerUpdateSignType;
import madscience.items.weapons.pulserifle.PulseRiflePackets;
import madscience.tile.magloader.MagLoaderPackets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

public abstract class MadPackets
{
    public static class ProtocolException extends Exception
    {
        public ProtocolException()
        {
        }

        public ProtocolException(String message)
        {
            super(message);
        }
    }

    private static final BiMap<Integer, Class<? extends MadPackets>> idMap;

    static
    {
        ImmutableBiMap.Builder<Integer, Class<? extends MadPackets>> builder = ImmutableBiMap.builder();

        // Mad Tile Entity Packet
        builder.put(Integer.valueOf(3), MadTileEntityPacketTemplate.class);

        // Mad Particle Packet (Can be any particle!)
        builder.put(Integer.valueOf(11), MadParticlePacket.class);
        
        // Pulse Rifle
        builder.put(Integer.valueOf(13), PulseRiflePackets.class);
        
        // Magazine Loader
        builder.put(Integer.valueOf(15), MagLoaderPackets.class);
        
        // Warning Sign Server (Sent from server to all clients to change Sign Type)
        builder.put(Integer.valueOf(17), WarningSignPacketServerUpdateSignType.class);
        
        // Warning Sign Server (Sent from server to specific client)
        builder.put(Integer.valueOf(18), WarningSignPacketServerRequestReplySignType.class);
        
        // Warning Sign Client (Sent by clients to server to ask for painting)
        builder.put(Integer.valueOf(19), WarningSignPacketClientRequestSignType.class);

        idMap = builder.build();
    }

    static MadPackets constructPacket(int packetId) throws ProtocolException, ReflectiveOperationException
    {
        Class<? extends MadPackets> clazz = idMap.get(Integer.valueOf(packetId));
        if (clazz == null)
        {
            throw new ProtocolException("Unknown Packet Id!");
        }
        else
        {
            return clazz.newInstance();
        }
    }

    public abstract void execute(EntityPlayer player, Side side) throws ProtocolException;

    public final int getPacketId()
    {
        if (idMap.inverse().containsKey(getClass()))
        {
            return idMap.inverse().get(getClass()).intValue();
        }
        else
        {
            throw new RuntimeException("Packet " + getClass().getSimpleName() + " is missing a mapping!");
        }
    }

    public final Packet makePacket()
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(getPacketId());
        write(out);
        return PacketDispatcher.getPacket(MadMod.CHANNEL_NAME, out.toByteArray());
    }

    public abstract void read(ByteArrayDataInput in) throws ProtocolException;

    public abstract void write(ByteArrayDataOutput out);
}