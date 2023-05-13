package thatrobin.foosh.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.registry.FooshItems;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ToggleMeasureToast implements Toast {

    public boolean toggled;
    public String mainText;

    public ToggleMeasureToast(String mainText, boolean toggled) {
        this.toggled = toggled;
        this.mainText = mainText;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
            String tog = "";
            int colour = -1;
            if(this.toggled) {
                tog = "toggle ON";
                colour = 65280;
            } else {
                tog = "toggle OFF";
                colour = 16711680;
            }

        manager.getClient().textRenderer.draw(matrices, Text.of(this.mainText), 30.0F, 7.0F, -1);
            List<OrderedText> list = manager.getClient().textRenderer.wrapLines(StringVisitable.plain(tog), 125);
            if (list.size() == 1) {
                manager.getClient().textRenderer.draw(matrices, list.get(0), 30.0F, 18.0F, colour);
            }
            ItemStack stack = new ItemStack(FooshItems.REDSTONE_ROD);
            NbtCompound compound = stack.getOrCreateNbt();
            compound.putBoolean("lit", this.toggled);
            stack.setNbt(compound);
            manager.getClient().getItemRenderer().renderInGui(stack, 8, 8);
            return startTime >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
