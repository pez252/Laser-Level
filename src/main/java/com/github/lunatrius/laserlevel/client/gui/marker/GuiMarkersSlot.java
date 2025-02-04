package com.github.lunatrius.laserlevel.client.gui.marker;

import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.laserlevel.client.renderer.RenderMarkers;
import com.github.lunatrius.laserlevel.marker.Marker;
import com.github.lunatrius.laserlevel.proxy.ClientProxy;
import com.github.lunatrius.laserlevel.reference.Names;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiMarkersSlot extends GuiSlot {
    private final GuiMarkers guiMarkers;

    protected int selectedIndex = -1;

    private final String strX = I18n.format(Names.Gui.GuiMarkerEdit.X);
    private final String strY = I18n.format(Names.Gui.GuiMarkerEdit.Y);
    private final String strZ = I18n.format(Names.Gui.GuiMarkerEdit.Z);

    public GuiMarkersSlot(final GuiMarkers guiMarkers) {
        super(Minecraft.getMinecraft(), guiMarkers.width, guiMarkers.height, 16, guiMarkers.height - 50, 26);
        this.guiMarkers = guiMarkers;
    }

    @Override
    protected int getSize() {
        return ClientProxy.MARKERS.size();
    }

    @Override
    protected void elementClicked(final int index, final boolean isDoubleClick, final int mouseX, final int mouseY) {
        this.selectedIndex = index;

        this.guiMarkers.btnDelete.enabled = true;
        this.guiMarkers.btnEdit.enabled = true;

        if (isDoubleClick) {
            final Marker marker = ClientProxy.MARKERS.get(index);
            marker.enabled = !marker.enabled;
            RenderMarkers.INSTANCE.markDirty();
        }
    }

    @Override
    protected boolean isSelected(final int index) {
        return index == this.selectedIndex;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawContainerBackground(final Tessellator tessellator) {
    }

    @Override
    public int getListWidth() {
        return 270;
    }

    @Override
    protected void drawSlot(final int index, final int x, final int y, final int par4, final int mouseX, final int mouseY) {
        if (index < 0 || index >= ClientProxy.MARKERS.size()) {
            return;
        }

        final Marker marker = ClientProxy.MARKERS.get(index);
        final MBlockPos pos = marker.pos;
        final int color = marker.enabled ? 0x00FFFFFF : 0x00FF7F7F;
        final int spacing = 80;

        this.guiMarkers.drawString(this.mc.fontRendererObj, this.strX + " " + pos.x, x + 2 + spacing * 0, y + 2, color);
        this.guiMarkers.drawString(this.mc.fontRendererObj, this.strY + " " + pos.y, x + 2 + spacing * 1, y + 2, color);
        this.guiMarkers.drawString(this.mc.fontRendererObj, this.strZ + " " + pos.z, x + 2 + spacing * 2, y + 2, color);

        final StringBuilder builder = new StringBuilder();
        builder.append(I18n.format(marker.enabled ? Names.Gui.GuiMarkerEdit.ON : Names.Gui.GuiMarkerEdit.OFF));

        final List<String> sides = new ArrayList<String>();
        for (final EnumFacing side : EnumFacing.VALUES) {
            if (marker.isEnabled(side)) {
                sides.add(I18n.format(Names.Gui.GuiMarkerEdit.SIDE_BASE + side.getName()));
            }
        }

        if (sides.size() > 0) {
            builder.append(" ").append(sides);
        }

        this.guiMarkers.drawString(this.mc.fontRendererObj, builder.toString(), x + 2, y + 12, color);

        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        final int w = 20;
        final int h = 20;
        final int xx = x + getListWidth() - w - 6;
        final int yy = y + 1;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableTexture2D();
        worldRenderer.startDrawingQuads();

        worldRenderer.setColorRGBA_I(0x000000, 0xFF);
        worldRenderer.addVertex(xx + 0, yy + 0, 0);
        worldRenderer.addVertex(xx + 0, yy + h, 0);
        worldRenderer.addVertex(xx + w, yy + h, 0);
        worldRenderer.addVertex(xx + w, yy + 0, 0);

        worldRenderer.setColorRGBA_I(marker.rgb, 0xFF);
        worldRenderer.addVertex(xx + 1 + 0, yy + 1 + 0, 0);
        worldRenderer.addVertex(xx + 1 + 0, yy - 1 + h, 0);
        worldRenderer.addVertex(xx - 1 + w, yy - 1 + h, 0);
        worldRenderer.addVertex(xx - 1 + w, yy + 1 + 0, 0);

        worldRenderer.setColorOpaque_I(0x00404040);
        worldRenderer.setColorRGBA_I(0x404040, 0x7f);
        worldRenderer.addVertex(x - 2, y - 2, -1);
        worldRenderer.addVertex(x - 2, y - 2 + getSlotHeight(), -1);
        worldRenderer.addVertex(x - 2 + getListWidth(), y - 2 + getSlotHeight(), -1);
        worldRenderer.addVertex(x - 2 + getListWidth(), y - 2, -1);

        worldRenderer.setColorOpaque_I(0x00808080);
        worldRenderer.setColorRGBA_I(0xC0C0C0, 0x3f);
        worldRenderer.addVertex(x - 1, y - 1, -1);
        worldRenderer.addVertex(x - 1, y - 1 + getSlotHeight() - 2, -1);
        worldRenderer.addVertex(x - 1 + getListWidth() - 2, y - 1 + getSlotHeight() - 2, -1);
        worldRenderer.addVertex(x - 1 + getListWidth() - 2, y - 1, -1);

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }
}
