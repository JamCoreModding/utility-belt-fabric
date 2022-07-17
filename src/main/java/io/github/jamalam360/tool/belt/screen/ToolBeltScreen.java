package io.github.jamalam360.tool.belt.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * @author Jamalam
 */
public class ToolBeltScreen extends HandledScreen<ToolBeltScreenHandler> {
    private static final Identifier TEXTURE = ToolBeltInit.idOf("textures/gui/tool_belt.png");
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

    public ToolBeltScreen(ToolBeltScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        MinecraftClient.getInstance().mouse.lockCursor();
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.backgroundWidth) / 2 - 1;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2 + (backgroundWidth / 4);
        int y = height / 2 - (49 / 2);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        this.setZOffset(-90);

        Slot slot = this.handler.getSlot(this.handler.selectedSlot);
        this.drawTexture(matrices, (this.width / 2) - 15 * 2 + (this.handler.selectedSlot * 18), this.height / 2 - 11, 0, 22, 24, 22);

        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount == -1.0) {
            if (this.handler.selectedSlot < 2) {
                this.handler.selectedSlot = this.handler.selectedSlot + 1;
                this.syncSelectedSlot();
            }
        } else if (amount == 1.0) {
            if (this.handler.selectedSlot > 0) {
                this.handler.selectedSlot = this.handler.selectedSlot - 1;
                this.syncSelectedSlot();
            }
        } else {
            return false;
        }

        return true;
    }

    private void syncSelectedSlot() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.handler.selectedSlot);
        ClientPlayNetworking.send(ToolBeltInit.TOOL_BELT_SELECTED_SLOT, buf);
    }
}
