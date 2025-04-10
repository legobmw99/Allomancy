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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.*;
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

        if (isKeyDown(hud)) {
            PowersConfig.enable_overlay.set(!PowersConfig.enable_overlay.get());
            return;
        }
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        for (int i = 0; i < powers.length; i++) {
            if (isKeyDown(powers[i])) {
                PowerRequests.toggleBurn(Metal.getMetal(i), data);
            }
        }
        if (isKeyDown(burn)) {
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


    private static float calculateImpulse(boolean input, boolean otherInput) {
        if (input == otherInput) {
            return 0.0F;
        } else {
            return input ? 1.0F : -1.0F;
        }
    }

    // Ignores Neo's conflict management to allow it to work in GUIs
    // See similar code in https://github.com/gigaherz/ToolBelt/blob/master/src/main/java/dev/gigaherz/toolbelt/client/ToolBeltClient.java#L186
    private static boolean isKeyDown0(KeyMapping keybind) {
        if (keybind.isUnbound()) {
            return false;
        }

        return switch (keybind.getKey().getType()) {
            case KEYSYM -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),
                                                    keybind.getKey().getValue());
            case MOUSE -> GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(),
                                                  keybind.getKey().getValue()) == GLFW.GLFW_PRESS;
            default -> false;
        };
    }


    private static boolean isKeyDown(KeyMapping keybind) {
        return isKeyDown0(keybind) && keybind.getKeyConflictContext().isActive() &&
               keybind.getKeyModifier().isActive(keybind.getKeyConflictContext());
    }


    public static void fakeMovement(ClientInput input) {
        // basically KeyboardInput.tick() and LocalPlayer.aiStep()

        Options settings = Minecraft.getInstance().options;
        input.keyPresses =
                new Input(isKeyDown0(settings.keyUp), isKeyDown0(settings.keyDown), isKeyDown0(settings.keyLeft),
                          isKeyDown0(settings.keyRight), isKeyDown0(settings.keyJump), isKeyDown0(settings.keyShift),
                          isKeyDown0(settings.keySprint));

        float forward = calculateImpulse(input.keyPresses.forward(), input.keyPresses.backward());
        float left = calculateImpulse(input.keyPresses.left(), input.keyPresses.right());

        var player = Minecraft.getInstance().player;
        if (player.isMovingSlowly()) {
            forward = (float) (forward * 0.3D);
            left = (float) (left * 0.3D);
        }
        input.moveVector = new Vec2(left, forward).normalized();

        if (!player.isSprinting() && (!(player.isInWater() || player.isInFluidType(
                (fluidType, height) -> player.canSwimInFluidType(fluidType))) ||
                                      (player.isUnderWater() || player.canStartSwimming())) && forward >= 0.8 &&
            !player.isUsingItem() && (player.getFoodData().getFoodLevel() > 6.0F || player.mayFly()) &&
            !player.hasEffect(MobEffects.BLINDNESS) && isKeyDown0(settings.keySprint)) {
            player.setSprinting(true);
        }
    }
}
