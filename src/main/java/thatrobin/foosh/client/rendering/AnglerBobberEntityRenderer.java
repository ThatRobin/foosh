package thatrobin.foosh.client.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import thatrobin.foosh.entity.AnglerBobberEntity;
import thatrobin.foosh.entity.WanderingAnglerEntity;

public class AnglerBobberEntityRenderer extends EntityRenderer<AnglerBobberEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/fishing_hook.png");
    private static final RenderLayer LAYER;
    private static final double field_33632 = 960.0D;

    public AnglerBobberEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public void render(AnglerBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Entity entity = fishingBobberEntity.getOwner();
        if (entity != null) {
            if(entity instanceof WanderingAnglerEntity anglerEntity) {
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

                ItemStack itemStack = anglerEntity.getMainHandStack();

                float h = anglerEntity.getHandSwingProgress(g);
                float k = MathHelper.sin(MathHelper.sqrt(h) * 3.1415927F);
                float l = MathHelper.lerp(g, anglerEntity.prevBodyYaw, anglerEntity.bodyYaw) * 0.017453292F;
                double d = (double) MathHelper.sin(l);
                double e = (double) MathHelper.cos(l);
                double m = (double) 0 * 0.35D;
                double n = 0.8D;
                double o;
                double p;
                double q;
                float r;
                double s;

                o = MathHelper.lerp((double) g, anglerEntity.prevX, anglerEntity.getX()) - e * m - d * 0.8D;
                p = anglerEntity.prevY + (double) anglerEntity.getStandingEyeHeight() + (anglerEntity.getY() - anglerEntity.prevY) * (double) g - 0.45D;
                q = MathHelper.lerp((double) g, anglerEntity.prevZ, anglerEntity.getZ()) - d * m + e * 0.8D;
                r = anglerEntity.isInSneakingPose() ? -0.1875F : 0.0F;

                s = MathHelper.lerp((double) g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
                double t = MathHelper.lerp((double) g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25D;
                double u = MathHelper.lerp((double) g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
                float v = (float) (o - s);
                float w = (float) (p - t) + r;
                float x = (float) (q - u);
                VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
                MatrixStack.Entry entry2 = matrixStack.peek();

                for (int z = 0; z <= 16; ++z) {
                    renderFishingLine(v, w, x, vertexConsumer2, entry2, percentage(z, 16), percentage(z + 1, 16));
                }

                matrixStack.pop();
                super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
            }
        }
    }

    private static float percentage(int value, int max) {
        return (float)value / (float)max;
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5F, (float)y - 0.5F, 0.0F).color(255, 255, 255, 255).texture((float)u, (float)v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }

    private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.6f;
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

    public Identifier getTexture(AnglerBobberEntity fishingBobberEntity) {
        return TEXTURE;
    }

    static {
        LAYER = RenderLayer.getEntityCutout(TEXTURE);
    }
}
