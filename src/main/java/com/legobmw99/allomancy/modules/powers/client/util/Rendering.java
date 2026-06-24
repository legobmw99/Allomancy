package com.legobmw99.allomancy.modules.powers.client.util;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public final class Rendering {
    private Rendering() {}


    // TODO(soon): include line width
    public record Line(Vec3 dest, int color) {
    }


    private static final RenderSystem.AutoStorageIndexBuffer indices =
            RenderSystem.getSequentialBuffer(PrimitiveTopology.LINES);

    private static final RenderPipeline METAL_LINES = RenderPipeline
            .builder(RenderPipelines.LINES_SNIPPET)
            .withLocation("pipeline/allomancy_lines")
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader("core/rendertype_lines")
            .withCull(false)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false, 0.0f, 0.0f))
            .withPrimitiveTopology(PrimitiveTopology.LINES)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH)
            .build();


    /**
     * Draws lines from the player to each destination
     *
     * @param source location of the player
     * @param lines  locations to draw toward
     * @param width  the width of the line
     */
    public static void drawMetalLines(PoseStack stack, Vec3 source, List<Line> lines, float width) {
        if (lines.isEmpty()) {
            return;
        }
        try (ByteBufferBuilder byteb = new ByteBufferBuilder(
                lines.size() * 2 * DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH.getVertexSize())) {

            BufferBuilder builder = new BufferBuilder(byteb, METAL_LINES.getPrimitiveTopology(),
                                                      METAL_LINES.getVertexFormatBinding(0));

            PoseStack.Pose pose = stack.last();
            Vector3f src = source.toVector3f();
            Vector3f normal = new Vector3f();

            for (var line : lines) {
                Vector3f dest = line.dest.toVector3f();
                dest.normalize(normal);

                builder.addVertex(pose, src).setColor(line.color).setNormal(pose, normal).setLineWidth(width);
                builder.addVertex(pose, dest).setColor(line.color).setNormal(pose, normal).setLineWidth(width);

            }
            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            matrix4fStack.pushMatrix();
            var dynamic = RenderSystem
                    .getDynamicUniforms()
                    .writeTransform(matrix4fStack, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(),
                                    new Matrix4f());

            try (MeshData meshData = builder.buildOrThrow()) {

                GpuBuffer vertexBuffer = RenderSystem
                        .getDevice()
                        .createBuffer(() -> "Allomancy lines", GpuBuffer.USAGE_VERTEX, meshData.vertexBuffer());

                RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();

                int indexCount = meshData.drawState().indexCount();
                GpuBuffer gpuBuffer = indices.getBuffer(indexCount);
                try (RenderPass renderPass = RenderSystem
                        .getDevice()
                        .createCommandEncoder()
                        .createRenderPass(() -> "allomancy lines", renderTarget.getColorTextureView(),
                                          Optional.empty(), renderTarget.getDepthTextureView(),
                                          OptionalDouble.empty())) {

                    renderPass.setPipeline(METAL_LINES);
                    RenderSystem.bindDefaultUniforms(renderPass);
                    renderPass.setVertexBuffer(0, vertexBuffer.slice());
                    renderPass.setIndexBuffer(gpuBuffer, indices.type());
                    renderPass.setUniform("DynamicTransforms", dynamic);
                    renderPass.drawIndexed(indexCount, 1, 0, 0, 0);

                }
            }

            matrix4fStack.popMatrix();
        }
    }

    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(METAL_LINES);
    }
}