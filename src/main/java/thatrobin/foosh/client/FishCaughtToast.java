package thatrobin.foosh.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FishCaughtToast implements Toast {

    public String name;
    public String length;
    public int rarity;
    public ItemStack icon;

    public FishCaughtToast(String name, String length, int rarity, ItemStack icon) {
        this.name = name;
        this.length = length;
        this.rarity = rarity;
        this.icon = icon;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
            List<OrderedText> list = manager.getClient().textRenderer.wrapLines(StringVisitable.plain("Length: " + this.length + "cm"), 125);
            if (list.size() == 1) {
                manager.getClient().textRenderer.draw(matrices, Text.of(this.name), 30.0F, 7.0F, this.rarity);
                manager.getClient().textRenderer.draw(matrices, (OrderedText)list.get(0), 30.0F, 18.0F, -1);
            } else {
                float f = 300.0F;
                int k;
                if (startTime < 1500L) {
                    k = MathHelper.floor(MathHelper.clamp((float)(1500L - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                    manager.getClient().textRenderer.draw(matrices, Text.of(this.name), 30.0F, 11.0F, this.rarity);
                } else {
                    k = MathHelper.floor(MathHelper.clamp((float)(startTime - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                    int var10000 = this.getHeight() / 2;
                    int var10001 = list.size();
                    Objects.requireNonNull(manager.getClient().textRenderer);
                    int l = var10000 - var10001 * 9 / 2;

                    for(Iterator var12 = list.iterator(); var12.hasNext(); l += 9) {
                        OrderedText orderedText = (OrderedText)var12.next();
                        manager.getClient().textRenderer.draw(matrices, orderedText, 30.0F, (float)l, 16777215 | k);
                        Objects.requireNonNull(manager.getClient().textRenderer);
                    }
                }
            }

            manager.getClient().getItemRenderer().renderInGui(this.icon, 8, 8);
            return startTime >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
