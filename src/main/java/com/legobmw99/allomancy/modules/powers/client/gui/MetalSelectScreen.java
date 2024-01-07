/*
 * This class was modified from one created by <Vazkii>. The original is
 * distributed as part of the Psi Mod.
 * This code is used under the
 * Psi License: http://psi.vazkii.us/license.php
 * <p>
 * The code was used as a template for the circular GUI,
 * and was heavily modified
 */
package com.legobmw99.allomancy.modules.powers.client.gui;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.modules.powers.client.util.ClientUtils;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class MetalSelectScreen extends Screen {

    private static final String[] METAL_NAMES = Arrays.stream(Metal.values()).map(Metal::getName).toArray(String[]::new);
    private static final String GUI_METAL = "allomancy:textures/gui/metals/%s_symbol.png";
    private static final String[] METAL_LOCAL = Arrays.stream(METAL_NAMES).map(s -> "metals." + s).toArray(String[]::new);
    private static final ResourceLocation[] METAL_ICONS = Arrays.stream(METAL_NAMES).map(s -> new ResourceLocation(String.format(GUI_METAL, s))).toArray(ResourceLocation[]::new);
    final Minecraft mc;
    int timeIn = PowersConfig.animate_selection.get() ? 0 : 16; // Config setting for whether the wheel animates open or instantly appears
    int slotSelected = -1;

    public MetalSelectScreen() {
        super(Component.translatable("allomancy_gui"));
        this.mc = Minecraft.getInstance();
    }

    private static double mouseAngle(int x, int y, int mx, int my) {
        return (Mth.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    private static int toMetalIndex(int segment) {
        return (segment + 5) % Metal.values().length;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mx, int my, float partialTicks) {
        super.render(guiGraphics, mx, my, partialTicks);

        var data = this.mc.player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        int x = this.width / 2;
        int y = this.height / 2;
        int maxRadius = 80;

        double angle = mouseAngle(x, y, mx, my);

        int segments = METAL_NAMES.length;
        float step = (float) Math.PI / 180;
        float degPer = (float) Math.PI * 2 / segments;

        this.slotSelected = -1;

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        buf.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        for (int seg = 0; seg < segments; seg++) {
            Metal mt = Metal.getMetal(toMetalIndex(seg));
            boolean mouseInSector = data.hasPower(mt) && (degPer * seg < angle && angle < degPer * (seg + 1));
            float radius = Math.max(0F, Math.min((this.timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius));
            if (mouseInSector) {
                this.slotSelected = seg;
                radius *= 1.025f;
            }

            int gs = 0x55;
            if (seg % 2 == 0) {
                gs += 0x19;
            }

            gs = (!data.hasPower(mt) || data.getAmount(mt) == 0) ? 0 : gs;

            int r = data.isBurning(mt) ? 0xFF : gs;
            int g = gs;
            int b = gs;
            int a = 0x99;

            if (seg == 0) {
                buf.vertex(x, y, 0).color(0x19, 0x19, 0x19, 0x15).endVertex();
            }

            for (float v = 0; v < degPer + step / 2; v += step) {
                float rad = v + seg * degPer;
                float xp = x + Mth.cos(rad) * radius;
                float yp = y + Mth.sin(rad) * radius;

                if (v == 0) {
                    buf.vertex(xp, yp, 0).color(r, g, b, a).endVertex();
                }
                buf.vertex(xp, yp, 0).color(r, g, b, a).endVertex();
            }
        }
        tess.end();

        for (int seg = 0; seg < segments; seg++) {
            Metal mt = Metal.getMetal(toMetalIndex(seg));
            boolean mouseInSector = data.hasPower(mt) && (degPer * seg < angle && angle < degPer * (seg + 1));
            float radius = Math.max(0F, Math.min((this.timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius));
            if (mouseInSector) {
                radius *= 1.025f;
            }


            float rad = (seg + 0.5f) * degPer;
            float xp = x + Mth.cos(rad) * radius;
            float yp = y + Mth.sin(rad) * radius;

            float xsp = xp - 4;
            float ysp = yp;
            String name = (mouseInSector ? ChatFormatting.UNDERLINE : ChatFormatting.RESET) + Component.translatable(METAL_LOCAL[toMetalIndex(seg)]).getString();
            int textwidth = this.font.width(name);

            if (xsp < x) {
                xsp -= textwidth - 8;
            }
            if (ysp < y) {
                ysp -= 9;
            }
            guiGraphics.drawString(this.font, name, xsp, ysp, 0xFFFFFF, true);

            double mod = 0.8;
            int xdp = (int) ((xp - x) * mod + x);
            int ydp = (int) ((yp - y) * mod + y);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, METAL_ICONS[toMetalIndex(seg)]);
            guiGraphics.blit(METAL_ICONS[toMetalIndex(seg)], xdp - 8, ydp - 8, 0, 0, 16, 16, 16, 16);

        }

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        RenderSystem.disableBlend();

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        toggleSelected();
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void tick() { // tick
        this.timeIn++;
    }

    @Override
    public boolean keyReleased(int keysym, int scancode, int modifiers) {
        if (PowersClientSetup.burn.matches(keysym, scancode)) {
            this.mc.setScreen(null);
            this.mc.mouseHandler.grabMouse();
            return true;
        }
        return super.keyReleased(keysym, scancode, modifiers);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (PowersClientSetup.burn.matchesMouse(button)) {
            this.mc.setScreen(null);
            this.mc.mouseHandler.grabMouse();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }


    /**
     * Toggles the metal the mouse is currently over
     */
    private void toggleSelected() {
        if (this.slotSelected != -1) {
            Metal mt = Metal.getMetal(toMetalIndex(this.slotSelected));
            var data = this.mc.player.getData(AllomancerAttachment.ALLOMANCY_DATA);
            ClientUtils.toggleBurn(mt, data);
            this.mc.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.1F, 2.0F);
        }
    }

    @Override
    public void renderTransparentBackground(GuiGraphics unused) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}