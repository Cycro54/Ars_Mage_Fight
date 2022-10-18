package invoker54.magefight.capability.player;

import invoker54.magefight.ArsMageFight;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MagicDataProvider implements ICapabilitySerializable<INBT> {
    private static final Logger LOGGER = LogManager.getLogger();

    //The location of capability
    public static final ResourceLocation CAP_MAGIC_DATA_LOC = new ResourceLocation(ArsMageFight.MOD_ID, "cap_magic_data");
    public static final byte COMPOUND_NBT_ID = new CompoundNBT().getId();

    public MagicDataProvider(){
        magicDataCap = new MagicDataCap();
    }

    //region Capability setup
    @CapabilityInject(MagicDataCap.class)
    public static Capability<MagicDataCap> CAP_MAGIC_DATA = null;

    private final static String CAP_MAGIC_DATA_NBT = "CAP_MAGIC_DATA_NBT";

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (CAP_MAGIC_DATA == null){
            LOGGER.debug("RETURNING EMPTY");
            return LazyOptional.empty();
        }
        return CAP_MAGIC_DATA.orEmpty(capability, LazyOptional.of(() -> magicDataCap).cast());

//        if (CAP_MAGIC_DATA == capability) {
//            return LazyOptional.of(() -> magicDataCap).cast();
//            // why are we using a lambda?  Because LazyOptional.of() expects a NonNullSupplier interface.  The lambda automatically
//            //   conforms itself to that interface.  This save me having to define an inner class implementing NonNullSupplier.
//            // The explicit cast to LazyOptional<T> is required because our CAPABILITY_ELEMENTAL_FIRE can't be typed.  Our code has
//            //   checked that the requested capability matches, so the explict cast is safe (unless you have mixed them up)
//        }
//
////        LOGGER.debug("I AM RETURNING AN EMPTY LAZY OPTIONAL");
//        return LazyOptional.empty();

        //return LazyOptional.empty();
        // Note that if you are implementing getCapability in a derived class which implements ICapabilityProvider
        // eg you have added a new MyEntity which has the method MyEntity::getCapability instead of using AttachCapabilitiesEvent to attach a
        // separate class, then you should call
        // return super.getCapability(capability, facing);
        //   instead of
        // return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        CompoundNBT nbtData = new CompoundNBT();
        INBT MagicDataNBT = CAP_MAGIC_DATA.writeNBT(magicDataCap, null);
        nbtData.put(CAP_MAGIC_DATA_NBT, MagicDataNBT);
        return  nbtData;
    }

    @Override
    public void deserializeNBT(INBT nbt) {
//        LOGGER.info("DESERIALIZING MAGIC CAP DATA");

        if (nbt.getId() != COMPOUND_NBT_ID) {
//            LOGGER.debug("THE NBT ID DIDN'T MATCH");
            //System.out.println("Unexpected NBT type:"+nbt);
            return;  // leave as default in case of error
        }
        //System.out.println("I ran for deserializing");
        CompoundNBT nbtData = (CompoundNBT) nbt;
        CAP_MAGIC_DATA.readNBT(magicDataCap, null, nbtData.getCompound(CAP_MAGIC_DATA_NBT));
    }

    //This is where the current capability is stored to read and write
    private final MagicDataCap magicDataCap;
}
