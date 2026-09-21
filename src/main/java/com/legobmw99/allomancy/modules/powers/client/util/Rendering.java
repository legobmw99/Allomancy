package com.legobmw99.allomancy.modules.powers.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.*;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public final class Rendering {
    private Rendering() {}

    // TODO(valhalla): value
    public record Line(Vec3 target, int color, float width) {
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
     */
    public static void drawMetalLines(RenderPass renderPass, PoseStack stack, Vec3 source, List<Line> lines) {
        if (lines.isEmpty()) {
            return;
        }

        VertexFormat format = METAL_LINES.getVertexFormatBinding(0);
        try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(
                lines.size() * 4 * format.getVertexSize())) {

            BufferBuilder builder = new BufferBuilder(byteBufferBuilder, METAL_LINES.getPrimitiveTopology(), format);

            PoseStack.Pose pose = stack.last();
            Vector3f src = source.toVector3f();
            Vector3f normal = new Vector3f();

            for (var line : lines) {
                Vector3f dest = line.target.toVector3f();
                dest.normalize(normal);

                builder.addVertex(pose, src).setColor(line.color).setNormal(pose, normal).setLineWidth(line.width);
                builder.addVertex(pose, dest).setColor(line.color).setNormal(pose, normal).setLineWidth(line.width);

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


                int indexCount = meshData.drawState().indexCount();
                GpuBuffer gpuBuffer = indices.getBuffer(indexCount);

                renderPass.setPipeline(RenderSystem.getCompiledPipeline(METAL_LINES));
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setVertexBuffer(0, vertexBuffer.slice());
                renderPass.setIndexBuffer(gpuBuffer, indices.type());
                renderPass.setUniform("DynamicTransforms", dynamic);
                renderPass.drawIndexed(indexCount, 1, 0, 0, 0);
            }

            matrix4fStack.popMatrix();
        }
    }

    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(METAL_LINES);
    }
}