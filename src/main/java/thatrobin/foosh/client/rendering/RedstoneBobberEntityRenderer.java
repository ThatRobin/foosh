package thatrobin.foosh.client.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import thatrobin.foosh.entity.bobbers.RedstoneBobberEntity;
import thatrobin.foosh.registry.FooshItems;

public class RedstoneBobberEntityRenderer extends EntityRenderer<RedstoneBobberEntity> {
    private static final Identifier TEXTURE = new Identifier("foosh", "textures/entity/redstone_hook.png");
    private static final RenderLayer LAYER;

    public RedstoneBobberEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public void render(RedstoneBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        PlayerEntity playerEntity = fishingBobberEntity.getPlayerOwner();
        if (playerEntity != null) {
            matrixStack.push();
            matrixStack.push();
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.multiply(this.dispatcher.getRotation());
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            MatrixStack.Entry entry = matrixStack.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
            vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 0, 0, 1);
            vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 0, 1, 1);
            vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 1, 1, 0);
            vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 1, 0, 0);
            matrixStack.pop();
            int j = playerEntity.getMainArm() == Arm.RIGHT ? 1 : -1;
            ItemStack itemStack = playerEntity.getMainHandStack();
            if (!itemStack.isOf(FooshItems.REDSTONE_ROD)) {
                j = -j;
            }

            float h = playerEntity.getHandSwingProgress(g);
            float k = MathHelper.sin(MathHelper.sqrt(h) * 3.1415927F);
            float l = MathHelper.lerp(g, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * 0.017453292F;
            double d = MathHelper.sin(l);
            double e = MathHelper.cos(l);
            double m = (double)j * 0.35D;
            double o;
            double p;
            double q;
            float r;
            double s;
            if ((this.dispatcher.gameOptions == null || this.dispatcher.gameOptions.getPerspective().isFirstPerson()) && playerEntity == MinecraftClient.getInstance().player) {
                assert this.dispatcher.gameOptions != null;
                s = 960.0D / (double)this.dispatcher.gameOptions.getFov().getValue();
                Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition((float)j * 0.525F, -0.1F);
                vec3d = vec3d.multiply(s);
                vec3d = vec3d.rotateY(k * 0.5F);
                vec3d = vec3d.rotateX(-k * 0.7F);
                o = MathHelper.lerp(g, playerEntity.prevX, playerEntity.getX()) + vec3d.x;
                p = MathHelper.lerp(g, playerEntity.prevY, playerEntity.getY()) + vec3d.y;
                q = MathHelper.lerp(g, playerEntity.prevZ, playerEntity.getZ()) + vec3d.z;
                r = playerEntity.getStandingEyeHeight();
            } else {
                o = MathHelper.lerp(g, playerEntity.prevX, playerEntity.getX()) - e * m - d * 0.8D;
                p = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.getY() - playerEntity.prevY) * (double)g - 0.45D;
                q = MathHelper.lerp(g, playerEntity.prevZ, playerEntity.getZ()) - d * m + e * 0.8D;
                r = playerEntity.isInSneakingPose() ? -0.1875F : 0.0F;
            }

            s = MathHelper.lerp(g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
            double t = MathHelper.lerp(g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25D;
            double u = MathHelper.lerp(g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
            float v = (float)(o - s);
            float w = (float)(p - t) + r;
            float x = (float)(q - u);
            VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
            MatrixStack.Entry entry2 = matrixStack.peek();

            for(int z = 0; z <= 16; ++z) {
                renderFishingLine(v, w, x, vertexConsumer2, entry2, percentage(z), percentage(z + 1));
            }

            matrixStack.pop();
            super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    private static float percentage(int value) {
        return (float)value / (float) 16;
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5F, (float)y - 0.5F, 0.0F).color(255, 255, 255, 255).texture((float)u, (float)v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }

    private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.25F;
        float h = z * segmentStart;
        float i = x * segmentEnd - f;
        float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5F + 0.25F - g;
        float k = z * segmentEnd - h;
        float l = MathHelper.sqrt(i * i + j * j + k * k);
        i /= l;
        j /= l;
        k /= l;
        buffer.vertex(matrices.getPositionMatrix(), f, g, h).color(0, 0, 0, 255).normal(matrices.getNormalMatrix(), i, j, k).next();
    }

    public Identifier getTexture(RedstoneBobberEntity fishingBobberEntity) {
        return TEXTURE;
    }

    static {
        LAYER = RenderLayer.getEntityCutout(TEXTURE);
    }
}
