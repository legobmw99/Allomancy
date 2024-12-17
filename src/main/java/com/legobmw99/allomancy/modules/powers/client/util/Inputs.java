package com.legobmw99.allomancy.modules.powers.client.util;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalSelectScreen;
import com.legobmw99.allomancy.modules.powers.client.network.PowerRequests;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public final class Inputs {

    private static KeyMapping hud;
    public static KeyMapping burn;
    private static KeyMapping[] powers;

    private Inputs() {}

    /**
     * Adapted from vanilla, allows getting mouseover at given distances
     *
     * @param dist the distance requested
     * @return a RayTraceResult for the requested raytrace
     */
    @Nullable
    public static HitResult getMouseOverExtended(float dist) {
        var mc = Minecraft.getInstance();
        float partialTicks = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        HitResult objectMouseOver = null;
        Entity entity = mc.getCameraEntity();
        if (entity != null) {
            if (mc.level != null) {
                objectMouseOver = entity.pick(dist, partialTicks, false);
                Vec3 vec3d = entity.getEyePosition(partialTicks);
                double d1;

                d1 = objectMouseOver.getLocation().distanceToSqr(vec3d);

                Vec3 vec3d1 = entity.getViewVector(1.0F);
                Vec3 vec3d2 = vec3d.add(vec3d1.x * dist, vec3d1.y * dist, vec3d1.z * dist);
                AABB axisalignedbb =
                        entity.getBoundingBox().expandTowards(vec3d1.scale(dist)).inflate(1.0D, 1.0D, 1.0D);
                EntityHitResult entityraytraceresult =
                        ProjectileUtil.getEntityHitResult(entity, vec3d, vec3d2, axisalignedbb, (e) -> true, d1);
                if (entityraytraceresult != null) {
                    Vec3 vec3d3 = entityraytraceresult.getLocation();
                    double d2 = vec3d.distanceToSqr(vec3d3);
                    if (d2 < d1) {
                        objectMouseOver = entityraytraceresult;
                    }
                }

            }
        }
        return objectMouseOver;

    }

    private static float calculateImpulse(boolean input, boolean otherInput) {
        if (input == otherInput) {
            return 0.0F;
        } else {
            return input ? 1.0F : -1.0F;
        }
    }

    public static void fakeMovement(ClientInput input) {
        // TODO not working
        // See similar code in https://github.com/gigaherz/ToolBelt/blob/master/src/main/java/dev/gigaherz/toolbelt/client/ToolBeltClient.java#L186
        Options options = Minecraft.getInstance().options;
        LocalPlayer player = Minecraft.getInstance().player;
        var window = Minecraft.getInstance().getWindow().getWindow();

        input.tick();
        // from KeyboardInput#tick
        //        input.keyPresses = new Input(options.keyUp.isDown(), options.keyDown.isDown(), options.keyLeft
        //        .isDown(),
        //                                     options.keyRight.isDown(), options.keyJump.isDown(), options
        //                                     .keyShift.isDown(),
        //                                     options.keySprint.isDown());
        //        input.forwardImpulse = calculateImpulse(input.keyPresses.forward(), input.keyPresses.backward());
        //        input.leftImpulse = calculateImpulse(input.keyPresses.left(), input.keyPresses.right());

        // from LocalPlayer#aiStep
        if (!player.isSprinting() && (!(player.isInWater() || player.isInFluidType(
                (fluidType, height) -> player.canSwimInFluidType(fluidType))) ||
                                      (player.isUnderWater() || player.canStartSwimming())) &&
            input.forwardImpulse >= 0.8 && !player.isUsingItem() &&
            (player.getFoodData().getFoodLevel() > 6.0F || player.mayFly()) &&
            !player.hasEffect(MobEffects.BLINDNESS) &&
            InputConstants.isKeyDown(window, options.keySprint.getKey().getValue())) {
            player.setSprinting(true);
        }
    }

    public static void registerKeyBinding(final RegisterKeyMappingsEvent evt) {
        burn = new KeyMapping("key.burn", GLFW.GLFW_KEY_V, "key.categories.allomancy");
        hud = new KeyMapping("key.hud", GLFW.GLFW_KEY_UNKNOWN, "key.categories.allomancy");
        evt.register(burn);
        evt.register(hud);

        powers = new KeyMapping[Metal.values().length];
        for (int i = 0; i < powers.length; i++) {
            powers[i] = new KeyMapping("key.metals." + Metal.getMetal(i).name().toLowerCase(), GLFW.GLFW_KEY_UNKNOWN,
                                       "key.categories.allomancy");
            evt.register(powers[i]);
        }

    }


    public static void acceptAllomancyKeybinds() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) {
            return;
        }
        Player player = mc.player;
        if (player == null || !mc.isWindowActive()) {
            return;
        }

        if (hud.isDown()) {
            PowersConfig.enable_overlay.set(!PowersConfig.enable_overlay.get());
            return;
        }
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        for (int i = 0; i < powers.length; i++) {
            if (powers[i].isDown()) {
                PowerRequests.toggleBurn(Metal.getMetal(i), data);
            }
        }
        if (burn.isDown()) {
            switch (data.getPowerCount()) {
                case 0:
                    break;
                case 1:
                    PowerRequests.toggleBurn(data.getPowers()[0], data);
                    break;
                default:
                    mc.setScreen(new MetalSelectScreen());
                    break;
            }
        }
    }
}
