/**
 * This class was modified from one created by <Vazkii>. The original is
 * distributed as part of the Psi Mod.
 * This code is used under the
 * Psi License: http://psi.vazkii.us/license.php
 * <p>
 * The code was used as a template for the circular GUI,
 * and was heavily modified
 */
package com.legobmw99.allomancy.modules.powers.client.gui;

import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.ClientUtils;
import com.legobmw99.allomancy.modules.powers.client.PowerClientSetup;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MetalSelectScreen extends Screen {

    private static final String[] METAL_NAMES = {"Iron", "Steel", "Tin", "Pewter", "Zinc", "Brass", "Copper", "Bronze"};
    private static final String GUI_METAL = "allomancy:textures/gui/metals/sign%d.png";

    private static final ResourceLocation[] METAL_ICONS = new ResourceLocation[]{
            new ResourceLocation(String.format(GUI_METAL, 0)), new ResourceLocation(String.format(GUI_METAL, 1)),
            new ResourceLocation(String.format(GUI_METAL, 2)), new ResourceLocation(String.format(GUI_METAL, 3)),
            new ResourceLocation(String.format(GUI_METAL, 4)), new ResourceLocation(String.format(GUI_METAL, 5)),
            new ResourceLocation(String.format(GUI_METAL, 6)), new ResourceLocation(String.format(GUI_METAL, 7)),};

    int timeIn = PowersConfig.animate_selection.get() ? 0 : 10; // Config setting for whether the wheel animates open or instantly appears
    int slotSelected = -1;
    AllomancyCapability cap;
    List<Integer> slots;
    Minecraft mc;

    public MetalSelectScreen() {
        super(new StringTextComponent("allomancy_gui"));
        ClientPlayerEntity player;
        player = Minecraft.getInstance().player;
        cap = AllomancyCapability.forPlayer(player);
        mc = Minecraft.getInstance();

        slots = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            slots.add(i);
        }
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();

        int x = width / 2;
        int y = height / 2;
        int maxRadius = 80;

        boolean mouseIn = true;
        float angle = (float) mouseAngle(x, y, mx, my);

        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        int segments = slots.size();
        float totalDeg = 0;
        float degPer = 360F / segments;

        List<int[]> stringPositions = new ArrayList();

        slotSelected = -1;

        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = mouseIn && angle > totalDeg && angle < totalDeg + degPer;
            float radius = Math.max(0F, Math.min((timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius));

            GL11.glBegin(GL11.GL_TRIANGLE_FAN);

            float gs = 0.3F;

            if (seg % 2 == 1)
                gs += 0.25F;

            gs = cap.getMetalAmounts((seg + 4) % 8) == 0 ? 0 : gs;

            float r = cap.getMetalBurning((seg + 4) % 8) ? 1.0F : gs;
            float g = gs;
            float b = gs;
            float a = 0.6F;
            if (mouseInSector) {
                slotSelected = seg;
            }

            RenderSystem.color4f(r, g, b, a);
            GL11.glVertex2i(x, y);

            for (float i = degPer; i >= 0; i--) {
                float rad = (float) ((i + totalDeg) / 180F * Math.PI);
                double xp = x + Math.cos(rad) * radius;
                double yp = y + Math.sin(rad) * radius;
                if (i == (int) (degPer / 2)) {
                    stringPositions.add(new int[]{seg, (int) xp, (int) yp, mouseInSector ? 'n' : 'r'});
                    stringPositions.add(
                            new int[]{seg, (int) xp, (int) yp, cap.getMetalAmounts((seg + 4) % 8) == 0 ? '7' : 'f'}); // Mark unused ones as disabled
                }
                GL11.glVertex2d(xp, yp);
            }
            totalDeg += degPer;

            GL11.glVertex2i(x, y);
            GL11.glEnd();

        }
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableTexture();

        for (int[] pos : stringPositions) {
            int slot = slots.get(pos[0]);
            int xp = pos[1];
            int yp = pos[2];
            char c = (char) pos[3];

            int xsp = xp - 4;
            int ysp = yp;
            String name = "\u00a7" + c + METAL_NAMES[(slot + 4) % 8];
            // add four and mod by eight to get #1 where I want it to be
            int width = mc.getRenderManager().getFontRenderer().getStringWidth(name);


            if (xsp < x)
                xsp -= width - 8;
            if (ysp < y)
                ysp -= 9;

            mc.getRenderManager().getFontRenderer().drawStringWithShadow(name, xsp, ysp, 0xFFFFFF);

            double mod = 0.8;
            int xdp = (int) ((xp - x) * mod + x);
            int ydp = (int) ((yp - y) * mod + y);

            mc.getRenderManager().textureManager.bindTexture(METAL_ICONS[(slot + 4) % 8]);
            RenderSystem.color4f(1, 1, 1, 1);
            blit(xdp - 8, ydp - 8, 0, 0, 16, 16, 16, 16);

        }
        float stime = 5F;
        float fract = Math.min(stime, timeIn + partialTicks) / stime;
        float s = 3F * fract;
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderHelper.func_227780_a_();  //enableGUIStandardItemLighting();

        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableBlend();
        RenderSystem.disableRescaleNormal();

        RenderSystem.popMatrix();
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        toggleSelected();
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public void tick() {
        timeIn++;
        super.tick();
    }

    @Override
    public boolean keyReleased(int keysym, int scancode, int p_keyReleased_3_) {
        if (PowerClientSetup.burn.matchesKey(keysym, scancode)) {
            mc.displayGuiScreen(null);
            mc.mouseHelper.grabMouse();
            return true;
        }
        return super.keyReleased(keysym, scancode, p_keyReleased_3_);
    }

    /**
     * Toggles the metal the mouse is currently over
     */
    private void toggleSelected() {
        if (slotSelected != -1) {
            int slot = slots.get(slotSelected);
            slot = (slot + 4) % 8; // Make the slot the one I actually want
            ClientUtils.toggleMetalBurn((byte) slot, cap);
            mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.1F,
                    2.0F);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    private static double mouseAngle(int x, int y, int mx, int my) {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }
}