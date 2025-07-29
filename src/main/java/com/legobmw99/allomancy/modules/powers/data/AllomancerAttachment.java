package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.client.util.Sounds;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;

public final class AllomancerAttachment {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Allomancy.MODID);

    private static final Supplier<AttachmentType<AllomancerData>> ALLOMANCY_DATA =
            ATTACHMENT_TYPES.register("allomancy_data", () -> AttachmentType
                    .builder(AllomancerData::new)
                    .serialize(AllomancerData.CODEC)
                    .sync(new AttachmentSyncHandler<>() {
                        @Override
                        public void write(RegistryFriendlyByteBuf buf,
                                          AllomancerData attachment,
                                          boolean initialSync) {
                            AllomancerData.STREAM_CODEC.encode(buf, attachment);
                        }

                        @Override
                        public AllomancerData read(IAttachmentHolder holder,
                                                   RegistryFriendlyByteBuf buf,
                                                   @Nullable AllomancerData previousData) {
                            AllomancerData data = AllomancerData.STREAM_CODEC.decode(buf);
                            if (previousData != null && holder instanceof LocalPlayer) {
                                var burningBefore =
                                        Arrays.stream(Metal.values()).filter(previousData::isBurning).count();
                                var burningAfter = Arrays.stream(Metal.values()).filter(data::isBurning).count();
                                if (burningBefore > burningAfter) {
                                    Sounds.soundForBurnChange(false);
                                }
                            }

                            return data;
                        }
                    })
                    .copyOnDeath()
                    .build());

    private AllomancerAttachment() {}

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    public static AllomancerData get(Player player) {
        return player.getData(ALLOMANCY_DATA);
    }

    public static boolean needsData(Player player) {
        return !player.hasData(ALLOMANCY_DATA);
    }

    public static void sync(Player player) {
        player.syncData(ALLOMANCY_DATA);
    }

    public static void set(Player player, AllomancerData data) {
        player.setData(ALLOMANCY_DATA, data);
    }
}