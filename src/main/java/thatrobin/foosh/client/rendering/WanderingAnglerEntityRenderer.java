package thatrobin.foosh.client.rendering;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;
import thatrobin.foosh.entity.WanderingAnglerEntity;

public class WanderingAnglerEntityRenderer extends MobEntityRenderer<WanderingAnglerEntity, VillagerResemblingModel<WanderingAnglerEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/wandering_trader.png");

    public WanderingAnglerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VillagerResemblingModel(context.getPart(EntityModelLayers.WANDERING_TRADER)), 0.5F);
        this.addFeature(new HeadFeatureRenderer(this, context.getModelLoader(), context.getHeldItemRenderer()));
        this.addFeature(new AnglerHeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    public Identifier getTexture(WanderingAnglerEntity wanderingAnglerEntity) {
        return TEXTURE;
    }

    protected void scale(WanderingAnglerEntity wanderingAnglerEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
