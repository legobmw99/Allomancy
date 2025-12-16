package com.legobmw99.allomancy.modules.powers.client.util;

import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class Sounds {
    private Sounds() {}

    public static void soundForBurnChange(boolean burning) {
        if (burning) {
            Minecraft.getInstance().player.playSound(
                    SoundEvent.createFixedRangeEvent(Identifier.withDefaultNamespace("item.flintandsteel.use"), 1.0f),
                    1, 5);
        } else {
            Minecraft.getInstance().player.playSound(
                    SoundEvent.createFixedRangeEvent(Identifier.withDefaultNamespace("block.fire.extinguish"), 1.0f),
                    1, 4);
        }
    }

    public static void spawnParticleForSound(Player player, SoundInstance sound) {
        double magnitude = Math.sqrt(player.position().distanceToSqr(sound.getX(), sound.getY(), sound.getZ()));

        if (((magnitude) > 25) || ((magnitude) < 3)) {
            return;
        }
        Vec3 vec = player.position();
        double posX = vec.x(), posY = vec.y(), posZ = vec.z();
        // Spawn sound particles
        String soundName = sound.getIdentifier().toString();
        if (soundName.contains("entity") || soundName.contains("step")) {
            double motionX = ((posX - (sound.getX() + 0.5)) * -0.7) / magnitude;
            double motionY = ((posY - (sound.getY() + 0.2)) * -0.7) / magnitude;
            double motionZ = ((posZ - (sound.getZ() + 0.5)) * -0.7) / magnitude;
            Minecraft.getInstance()

                    .particleEngine.createParticle(new SoundParticleData(sound.getSource()),
                                                   posX + (Math.sin(Math.toRadians(player.getYHeadRot())) * -0.7d),
                                                   posY + 0.2,
                                                   posZ + (Math.cos(Math.toRadians(player.getYHeadRot())) * 0.7d),
                                                   motionX, motionY, motionZ);
        }
    }
}
