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
import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.network.PowerRequests;
import com.legobmw99.allomancy.modules.powers.client.util.Inputs;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.Arrays;

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

    // Config setting for whether the wheel animates open or instantly appears
    private int timeIn = PowersConfig.animate_selection.get() ? 0 : 16;
    private int slotSelected = -1;


    private static final RenderSystem.AutoStorageIndexBuffer indices =
            RenderSystem.getSequentialBuffer(VertexFormat.Mode.TRIANGLE_FAN);

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
        float degPer = (float) Math.PI * 2 / segments;

        this.slotSelected = -1;

        guiGraphics.submitPictureInPictureRenderState(
                new MetalSelectPiPState(data, angle, this.timeIn + partialTicks, 0, 0, this.width, this.height,
                                        guiGraphics.peekScissorStack()));

        for (int seg = 0; seg < segments; seg++) {
            Metal mt = Metal.getMetal(toMetalIndex(seg));
            boolean mouseInSector = data.hasPower(mt) && (degPer * seg < angle && angle < degPer * (seg + 1));
            float radius =
                    Math.max(0.0F, Math.min((this.timeIn + partialTicks - seg * 6.0F / segments) * 40.0F, maxRadius));
            if (mouseInSector) {
                this.slotSelected = seg;
                radius *= 1.025f;
            }

            float rad = (seg + 0.5f) * degPer;
            float xp = x + Mth.cos(rad) * radius;
            float yp = y + Mth.sin(rad) * radius;

            float xsp = xp - 1;
            float ysp = yp;
            String name = (mouseInSector ? ChatFormatting.UNDERLINE : ChatFormatting.RESET) +
                          Component.translatable(METAL_LOCAL[toMetalIndex(seg)]).getString();
            int textwidth = this.font.width(name);

            if (xsp < x) {
                xsp -= textwidth - 2;
            }
            if (ysp < y) {
                ysp -= 9;
            }
            guiGraphics.drawString(this.font, name, Math.round(xsp), Math.round(ysp), 0xffffffff, true);

            double mod = 0.8;
            int xdp = (int) ((xp - x) * mod + x);
            int ydp = (int) ((yp - y) * mod + y);

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, METAL_ICONS[toMetalIndex(seg)], xdp - 8, ydp - 8, 0, 0, 16,
                             16, 16, 16);

        }
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


    private record MetalSelectPiPState(IAllomancerData data, double mouseAngle, float timeInPartial, int x0, int y0,
                                       int x1, int y1,
                                       @Nullable ScreenRectangle scissorArea) implements PictureInPictureRenderState {

        @Override
        public float scale() {
            return 1.0F;
        }

        @Override
        public ScreenRectangle bounds() {
            return PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea);
        }
    }

    public static class MetalSelectPiPRenderer extends PictureInPictureRenderer<MetalSelectPiPState> {
        private static final RenderPipeline SELECTION_BACKGROUND = RenderPipeline
                .builder(RenderPipelines.GUI_SNIPPET)
                .withLocation("pipeline/allomancy_selection")
                .withVertexShader("core/position_color")
                .withFragmentShader("core/position_color")
                .withCull(false)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN)
                .build();
        private static final RenderType SELECTION_BACKGROUND_TYPE =
                RenderType.create("allomancy_selection", 1536, false, true, SELECTION_BACKGROUND,
                                  RenderType.CompositeState.builder().createCompositeState(false));


        public MetalSelectPiPRenderer(MultiBufferSource.BufferSource s) {
            super(s);
        }

        @Override
        public Class<MetalSelectPiPState> getRenderStateClass() {
            return MetalSelectPiPState.class;
        }

        @Override
        protected void renderToTexture(MetalSelectPiPState state, PoseStack stack) {
            VertexConsumer vertexconsumer = this.bufferSource.getBuffer(SELECTION_BACKGROUND_TYPE);

            var data = state.data;

            int x = (state.x0 + state.x1) / 2;
            int y = (state.y0 + state.y1) / 2;
            int maxRadius = 80;

            stack.translate(-x, -y, 0.0F);
            Matrix4f matrix4f = stack.last().pose();

            int segments = METAL_NAMES.length;
            float step = (float) Math.PI / 180;
            float degPer = (float) Math.PI * 2 / segments;


            vertexconsumer.addVertex(matrix4f, x, y, 0).setColor(0x19, 0x19, 0x19, 0x05);

            for (int seg = 0; seg < segments; seg++) {
                Metal mt = Metal.getMetal(toMetalIndex(seg));
                boolean mouseInSector = data.hasPower(mt) &&
                                        (degPer * seg < state.mouseAngle && state.mouseAngle < degPer * (seg + 1));
                float radius =
                        Math.max(0.0F, Math.min((state.timeInPartial - seg * 6.0F / segments) * 40.0F, maxRadius));
                if (mouseInSector) {
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
                        vertexconsumer.addVertex(matrix4f, xp, yp, 0).setColor(r, g, b, a);
                    }
                    vertexconsumer.addVertex(matrix4f, xp, yp, 0).setColor(r, g, b, a);
                }
            }
        }

        @Override
        protected float getTranslateY(int i, int j) {
            // don't understand this, but the F3 profiler chart does it!
            return i / 2.0F;
        }

        @Override
        protected String getTextureLabel() {
            return "metal selection";
        }

        public static void registerPipeline(RegisterRenderPipelinesEvent event) {
            event.registerPipeline(SELECTION_BACKGROUND);
        }

        public static void registerPiP(RegisterPictureInPictureRenderersEvent event) {
            event.register(MetalSelectPiPRenderer::new);
        }
    }
}