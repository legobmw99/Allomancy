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

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.network.PowerRequests;
import com.legobmw99.allomancy.modules.powers.client.util.Inputs;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class MetalSelectScreen extends Screen {

    private static final String[] METAL_NAMES =
            Arrays.stream(Metal.values()).map(Metal::getName).toArray(String[]::new);
    private static final String GUI_METAL = "textures/gui/metals/%s_symbol.png";
    private static final String[] METAL_LOCAL =
            Arrays.stream(METAL_NAMES).map(s -> "metals." + s).toArray(String[]::new);
    private static final ResourceLocation[] METAL_ICONS = Arrays
            .stream(METAL_NAMES)
            .map(s -> Allomancy.rl(String.format(GUI_METAL, s)))
            .toArray(ResourceLocation[]::new);
    private final Minecraft mc;
    private int timeIn = PowersConfig.animate_selection.get() ? 0 : 16;
    // Config setting for whether the wheel animates open or instantly appears
    private int slotSelected = -1;


    private static final RenderSystem.AutoStorageIndexBuffer indices =
            RenderSystem.getSequentialBuffer(VertexFormat.Mode.TRIANGLE_FAN);
    private static final RenderPipeline SELECTION_BACKGROUND = RenderPipeline
            .builder(RenderPipelines.GUI_SNIPPET)
            .withLocation("pipeline/allomancy_selection")
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withCull(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN)
            .build();

    public MetalSelectScreen() {
        super(Component.translatable("allomancy.gui"));
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

        BufferBuilder buf =
                tess.begin(SELECTION_BACKGROUND.getVertexFormatMode(), SELECTION_BACKGROUND.getVertexFormat());


        buf.addVertex(x, y, 0).setColor(0x19, 0x19, 0x19, 0x05);

        for (int seg = 0; seg < segments; seg++) {
            Metal mt = Metal.getMetal(toMetalIndex(seg));
            boolean mouseInSector = data.hasPower(mt) && (degPer * seg < angle && angle < degPer * (seg + 1));
            float radius =
                    Math.max(0.0F, Math.min((this.timeIn + partialTicks - seg * 6.0F / segments) * 40.0F, maxRadius));
            if (mouseInSector) {
                this.slotSelected = seg;
                radius *= 1.025f;
            }

            int gs = 0x55;
            if (seg % 2 == 0) {
                gs += 0x19;
            }

            gs = (!data.hasPower(mt) || data.getStored(mt) == 0) ? 0 : gs;

            int r = data.isBurning(mt) ? 0xFF : gs;
            int g = gs;
            int b = gs;
            int a = 0x99;

            for (float v = 0; v < degPer + step / 2; v += step) {
                float rad = v + seg * degPer;
                float xp = x + Mth.cos(rad) * radius;
                float yp = y + Mth.sin(rad) * radius;

                if (v == 0) {
                    buf.addVertex(xp, yp, 0).setColor(r, g, b, a);
                }
                buf.addVertex(xp, yp, 0).setColor(r, g, b, a);
            }
        }

        try (MeshData meshData = buf.buildOrThrow()) {
            GpuBuffer vertexBuffer = RenderSystem
                    .getDevice()
                    .createBuffer(() -> "Allomancy selection buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE,
                                  meshData.vertexBuffer());

            RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
            if (renderTarget.getColorTexture() == null) {
                return;
            }
            int indexCount = meshData.drawState().indexCount();
            GpuBuffer gpuBuffer = indices.getBuffer(indexCount);
            try (RenderPass renderPass = RenderSystem
                    .getDevice()
                    .createCommandEncoder()
                    .createRenderPass(renderTarget.getColorTexture(), OptionalInt.empty(),
                                      renderTarget.getDepthTexture(), OptionalDouble.empty())) {

                renderPass.setPipeline(SELECTION_BACKGROUND);
                renderPass.setIndexBuffer(gpuBuffer, indices.type());
                renderPass.setVertexBuffer(0, vertexBuffer);
                renderPass.drawIndexed(0, indexCount);

            }
        }
        tess.clear();


        for (int seg = 0; seg < segments; seg++) {
            Metal mt = Metal.getMetal(toMetalIndex(seg));
            boolean mouseInSector = data.hasPower(mt) && (degPer * seg < angle && angle < degPer * (seg + 1));
            float radius =
                    Math.max(0.0F, Math.min((this.timeIn + partialTicks - seg * 6.0F / segments) * 40.0F, maxRadius));
            if (mouseInSector) {
                radius *= 1.025f;
            }


            float rad = (seg + 0.5f) * degPer;
            float xp = x + Mth.cos(rad) * radius;
            float yp = y + Mth.sin(rad) * radius;

            float xsp = xp - 4;
            float ysp = yp;
            String name = (mouseInSector ? ChatFormatting.UNDERLINE : ChatFormatting.RESET) +
                          Component.translatable(METAL_LOCAL[toMetalIndex(seg)]).getString();
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

            guiGraphics.blit(RenderType::guiTexturedOverlay, METAL_ICONS[toMetalIndex(seg)], xdp - 8, ydp - 8, 0, 0,
                             16, 16, 16, 16);

        }


        // second circle

        tess.clear();
        buf = tess.begin(SELECTION_BACKGROUND.getVertexFormatMode(), SELECTION_BACKGROUND.getVertexFormat());

        x = x / 4;
        y = y / 2 * 3;

        segments = 2;
        step = (float) Math.PI / 180;
        degPer = (float) Math.PI * 2 / segments;

        for (int seg = 0; seg < segments; seg++) {
            float radius = 20;

            int gs = 0x55;

            int r = gs;
            int g = gs;
            int b = gs;
            int a = 0x99;

            if (seg == 0) {
                buf.addVertex(x, y, 0).setColor(r, g, b, a);
            }

            for (float v = 0; v < degPer + step / 2; v += step) {
                float rad = v + seg * degPer;
                float xp = x + Mth.cos(rad) * radius;
                float yp = y + Mth.sin(rad) * radius;

                if (v == 0) {
                    buf.addVertex(xp, yp, 0).setColor(r, g, b, a);
                }
                buf.addVertex(xp, yp, 0).setColor(r, g, b, a);
            }
        }

        try (MeshData meshData = buf.buildOrThrow()) {
            GpuBuffer vertexBuffer = RenderSystem
                    .getDevice()
                    .createBuffer(() -> "Allomancy selection buffer 2", BufferType.VERTICES, BufferUsage.STATIC_WRITE,
                                  meshData.vertexBuffer());

            RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
            if (renderTarget.getColorTexture() == null) {
                return;
            }
            int indexCount = meshData.drawState().indexCount();
            GpuBuffer gpuBuffer = indices.getBuffer(indexCount);
            try (RenderPass renderPass = RenderSystem
                    .getDevice()
                    .createCommandEncoder()
                    .createRenderPass(renderTarget.getColorTexture(), OptionalInt.empty(),
                                      renderTarget.getDepthTexture(), OptionalDouble.empty())) {

                renderPass.setPipeline(SELECTION_BACKGROUND);
                renderPass.setIndexBuffer(gpuBuffer, indices.type());
                renderPass.setVertexBuffer(0, vertexBuffer);
                renderPass.drawIndexed(0, indexCount);

            }
        }

        guiGraphics.blit(RenderType::guiTexturedOverlay, Allomancy.rl(String.format(GUI_METAL, "unused_atium")),
                         x - 8, y - 8, 0, 0, 16, 16, 16, 16);

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
        if (Inputs.burn.matches(keysym, scancode)) {
            this.mc.setScreen(null);
            this.mc.mouseHandler.grabMouse();
            return true;
        }
        return super.keyReleased(keysym, scancode, modifiers);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (Inputs.burn.matchesMouse(button)) {
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
            PowerRequests.toggleBurn(mt, data);
            this.mc.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.1F, 2.0F);
        }
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        // unused
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(SELECTION_BACKGROUND);
    }
}