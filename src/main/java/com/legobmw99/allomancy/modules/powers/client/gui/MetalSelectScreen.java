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
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.setup.Metal;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class MetalSelectScreen extends Screen {

    private static final String[] METAL_NAMES = Arrays.stream(Metal.values()).map(Metal::getName).toArray(String[]::new);
    private static final String GUI_METAL = "allomancy:textures/gui/metals/%s_symbol.png";
    private static final String[] METAL_LOCAL = Arrays.stream(METAL_NAMES).map(s -> "metals." + s).toArray(String[]::new);
    private static final ResourceLocation[] METAL_ICONS = Arrays.stream(METAL_NAMES).map(s -> new ResourceLocation(String.format(GUI_METAL, s))).toArray(ResourceLocation[]::new);

    int timeIn = PowersConfig.animate_selection.get() ? 0 : 16; // Config setting for whether the wheel animates open or instantly appears
    int slotSelected = -1;
    AllomancyCapability cap;
    Minecraft mc;

    public MetalSelectScreen() {
        super(new StringTextComponent("allomancy_gui"));
        mc = Minecraft.getInstance();
        cap = AllomancyCapability.forPlayer(mc.player);

    }

    private static double mouseAngle(int x, int y, int mx, int my) {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    private static int toMetalIndex(int segment) {
        return (segment + 8) % Metal.values().length;
    }

    @Override
    public void render(MatrixStack matrixStack, int mx, int my, float partialTicks) { // render
        super.render(matrixStack, mx, my, partialTicks);

        int x = this.width / 2; //width
        int y = this.height / 2; //height
        int maxRadius = 80;

        double angle = mouseAngle(x, y, mx, my);

        int segments = METAL_NAMES.length;
        float step = (float) Math.PI / 180;
        float degPer = (float) Math.PI * 2 / segments;

        slotSelected = -1;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();


        RenderSystem.disableCull();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

        for (int seg = 0; seg < segments; seg++) {
            Metal mt = Metal.getMetal(toMetalIndex(seg));
            boolean mouseInSector = cap.hasPower(mt) && (degPer * seg < angle && angle < degPer * (seg + 1));
            float radius = Math.max(0F, Math.min((timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius));
            if (mouseInSector) {
                slotSelected = seg;
                radius *= 1.025f;
            }

            int gs = 0x40;
            if (seg % 2 == 0)
                gs += 0x19;

            gs = (!cap.hasPower(mt) || cap.getAmount(mt) == 0) ? 0 : gs;

            int r = cap.isBurning(mt) ? 0xFF : gs;
            int g = gs;
            int b = gs;
            int a = 0x99;


            if (seg == 0)
                buf.pos(x, y, 0).color(r, g, b, a).endVertex(); //pos, color


            for (float i = 0; i < degPer + step / 2; i += step) {
                float rad = i + seg * degPer;
                float xp = x + MathHelper.cos(rad) * radius;
                float yp = y + MathHelper.sin(rad) * radius;

                if (i == 0)
                    buf.pos(xp, yp, 0).color(r, g, b, a).endVertex();
                buf.pos(xp, yp, 0).color(r, g, b, a).endVertex();
            }
        }
        tess.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableTexture();

        for (int seg = 0; seg < segments; seg++) {
            Metal mt = Metal.getMetal(toMetalIndex(seg));
            boolean mouseInSector = cap.hasPower(mt) && (degPer * seg < angle && angle < degPer * (seg + 1));
            float radius = Math.max(0F, Math.min((timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius));
            if (mouseInSector)
                radius *= 1.025f;


            float rad = (seg + 0.5f) * degPer;
            float xp = x + MathHelper.cos(rad) * radius;
            float yp = y + MathHelper.sin(rad) * radius;

            float xsp = xp - 4;
            float ysp = yp;
            String name = (mouseInSector ? TextFormatting.UNDERLINE : TextFormatting.RESET) + new TranslationTextComponent(METAL_LOCAL[toMetalIndex(seg)]).getString();
            int width = mc.getRenderManager().getFontRenderer().getStringWidth(name);

            if (xsp < x)
                xsp -= width - 8;
            if (ysp < y)
                ysp -= 9;

            mc.getRenderManager().getFontRenderer().drawStringWithShadow(matrixStack, name, xsp, ysp, 0xFFFFFF);

            double mod = 0.8;
            int xdp = (int) ((xp - x) * mod + x);
            int ydp = (int) ((yp - y) * mod + y);

            mc.getRenderManager().textureManager.bindTexture(METAL_ICONS[toMetalIndex(seg)]);
            RenderSystem.color4f(1, 1, 1, 1);
            blit(matrixStack, xdp - 8, ydp - 8, 0, 0, 16, 16, 16, 16);

        }

        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        RenderHelper.enableStandardItemLighting();

        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableBlend();
        RenderSystem.disableRescaleNormal();

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) { //mouseClicked
        toggleSelected();
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void tick() { // tick
        timeIn++;
    }

    @Override
    public boolean keyReleased(int keysym, int scancode, int p_keyReleased_3_) {
        if (PowersClientSetup.burn.matchesKey(keysym, scancode)) {
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
            Metal mt = Metal.getMetal(toMetalIndex(slotSelected));
            ClientUtils.toggleBurn(mt, cap);
            mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.1F,
                    2.0F);

        }
    }

    @Override // isPauseScreen?
    public boolean isPauseScreen() {
        return false;
    }
}