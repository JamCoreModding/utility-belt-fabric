package io.github.jamalam360.tool.belt.tutorial;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Jamalam
 */
public class ToolBeltTutorialToast implements Toast {
    private final Text title;
    @Nullable
    private final Text description;
    private final List<OrderedText> descriptionLines;

    public ToolBeltTutorialToast(Text title, @Nullable Text description) {
        this.title = title;
        this.description = description;
        this.descriptionLines = description == null ? null : MinecraftClient.getInstance().textRenderer.wrapLines(description, 140);
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        manager.drawTexture(matrices, 0, 0, 0, 96, this.getWidth(), this.getHeight());

        if (this.description == null) {
            manager.getGame().textRenderer.draw(matrices, this.title, 4.0F, 12.0F, -11534256);
        } else {
            manager.getGame().textRenderer.draw(matrices, this.title, 4.0F, 7.0F, -11534256);
            for (int i = 0; i < this.descriptionLines.size(); ++i) {
                manager.getGame().textRenderer.draw(matrices, this.descriptionLines.get(i), 4.0F, 28.0F + i * 10.0F, -16777216);
            }
        }

        return startTime >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
