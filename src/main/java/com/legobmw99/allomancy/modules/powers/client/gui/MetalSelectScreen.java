/*
 * This class was modified from one created by <Vazkii>. The original is
 * distributed as part of the Psi Mod.
 * This code is used under the
 * Psi License: https://psi.vazkii.net/license.php
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
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

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
    protected Metal selectedMetal = null;

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

        var data = AllomancerAttachment.get(this.mc.player);

        int x = this.width / 2;
        int y = this.height / 2;
        int maxRadius = 80;

        double angle = mouseAngle(x, y, mx, my);

        int segments = METAL_NAMES.length;
        float degPer = (float) Math.PI * 2 / segments;

        this.selectedMetal = null;

        guiGraphics.submitPictureInPictureRenderState(
                new SelectionWheelState(data, angle, this.timeIn + partialTicks, this.width, this.height,
                                        guiGraphics.peekScissorStack()));

        for (int seg = 0; seg < segments; seg++) {
            Metal mt = Metal.getMetal(toMetalIndex(seg));
            boolean mouseInSector = data.hasPower(mt) && (degPer * seg < angle && angle < degPer * (seg + 1));
            float radius =
                    Math.max(0.0F, Math.min((this.timeIn + partialTicks - seg * 6.0F / segments) * 40.0F, maxRadius));
            if (mouseInSector) {
                this.selectedMetal = Metal.getMetal(toMetalIndex(seg));
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

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, METAL_ICONS[toMetalIndex(seg)],
                             xdp - 8, ydp - 8, 0, 0, 16, 16, 16, 16);

        }
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent evt, boolean isDoubleClick) {
        toggleSelected();
        return super.mouseClicked(evt, isDoubleClick);
    }

    @Override
    public void tick() { // tick
        this.timeIn++;
    }

    @Override
    public boolean keyReleased(KeyEvent evt) {
        if (Inputs.BURN.matches(evt)) {
            this.mc.setScreen(null);
            this.mc.mouseHandler.grabMouse();
            return true;
        }
        return super.keyReleased(evt);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent evt) {
        if (Inputs.BURN.matchesMouse(evt)) {
            this.mc.setScreen(null);
            this.mc.mouseHandler.grabMouse();
            return true;
        }
        return super.mouseReleased(evt);
    }

    /**
     * Toggles the metal the mouse is currently over
     */
    private void toggleSelected() {
        if (this.selectedMetal != null) {
            var data = AllomancerAttachment.get(this.mc.player);
            PowerRequests.toggleBurn(this.selectedMetal, data);
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


    public record SelectionWheelState(IAllomancerData data, double mouseAngle, float timeInPartial, int x0, int y0,
                                      int x1, int y1, @Nullable ScreenRectangle scissorArea,
                                      @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {

        private SelectionWheelState(IAllomancerData data,
                                    double mouseAngle,
                                    float timeInPartial,
                                    int width,
                                    int height,
                                    @Nullable ScreenRectangle scissorArea) {
            this(data, mouseAngle, timeInPartial, 0, 0, width, height, scissorArea,
                 PictureInPictureRenderState.getBounds(0, 0, width, height, scissorArea));
        }

        @Override
        public float scale() {
            return 1.0F;
        }

    }

    public static class SelectionWheelRenderer extends PictureInPictureRenderer<SelectionWheelState> {
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


        public SelectionWheelRenderer(MultiBufferSource.BufferSource s) {
            super(s);
        }

        @Override
        public Class<SelectionWheelState> getRenderStateClass() {
            return SelectionWheelState.class;
        }

        @Override
        protected void renderToTexture(SelectionWheelState state, PoseStack stack) {
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
        protected float getTranslateY(int height, int guiScale) {
            return height / 2.0F;
        }

        @Override
        protected String getTextureLabel() {
            return "metal selection";
        }

        public static void registerPipeline(RegisterRenderPipelinesEvent event) {
            event.registerPipeline(SELECTION_BACKGROUND);
        }

        public static void registerPiP(RegisterPictureInPictureRenderersEvent event) {
            event.register(SelectionWheelState.class, SelectionWheelRenderer::new);
        }
    }
}