package me.desht.pneumaticcraft.client.gui;

import me.desht.pneumaticcraft.api.tileentity.IAirHandler;
import me.desht.pneumaticcraft.client.gui.widget.GuiAnimatedStat;
import me.desht.pneumaticcraft.common.block.Blockss;
import me.desht.pneumaticcraft.common.inventory.ContainerAirCannon;
import me.desht.pneumaticcraft.common.tileentity.TileEntityAirCannon;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiAirCannon extends GuiPneumaticContainerBase<TileEntityAirCannon> {
    private GuiAnimatedStat statusStat;
    private int gpsX;
    private int gpsY;
    private int gpsZ;

    public GuiAirCannon(InventoryPlayer player, TileEntityAirCannon te) {

        super(new ContainerAirCannon(player, te), te, Textures.GUI_AIR_CANNON_LOCATION);
        gpsX = te.gpsX;
        gpsY = te.gpsY;
        gpsZ = te.gpsZ;

    }

    @Override
    public void initGui() {
        super.initGui();
        statusStat = this.addAnimatedStat("Cannon Status", new ItemStack(Blockss.AIR_CANNON), 0xFFFFAA00, false);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {

        super.drawGuiContainerForegroundLayer(x, y);
        fontRenderer.drawString("GPS", 50, 20, 4210752);
        fontRenderer.drawString("Upgr.", 13, 19, 4210752);

    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        statusStat.setText(getStatusText());

        if (gpsX != te.gpsX || gpsY != te.gpsY || gpsZ != te.gpsZ) {
            gpsX = te.gpsX;
            gpsY = te.gpsY;
            gpsZ = te.gpsZ;
            statusStat.openWindow();
        }
    }

    private List<String> getStatusText() {
        List<String> text = new ArrayList<>();
        text.add("\u00a77Current Aimed Coordinate:");
        if (te.gpsX != 0 || te.gpsY != 0 || te.gpsZ != 0) {
            text.add("\u00a70X: " + te.gpsX + ", Y: " + te.gpsY + ", Z: " + te.gpsZ);
        } else {
            text.add("\u00a70- No coordinate selected -");
        }
        text.add("\u00a77Current Heading Angle:");
        text.add("\u00a70" + Math.round(te.rotationAngle) + " degrees.");
        text.add("\u00a77Current Height Angle:");
        text.add("\u00a70" + (90 - Math.round(te.heightAngle)) + " degrees.");
        text.add(TextFormatting.GRAY + "Range");
        text.add(TextFormatting.BLACK + "About " + PneumaticCraftUtils.roundNumberTo(te.getForce() * 25F, 0) + "m");
        return text;
    }

    @Override
    protected void addProblems(List<String> textList) {
        List<Pair<EnumFacing, IAirHandler>> teSurrounding = te.getAirHandler(null).getConnectedPneumatics();
        super.addProblems(textList);

        if (teSurrounding.isEmpty()) {
            textList.add("\u00a77No air input connected.");
            textList.add("\u00a70Add pipes / machines");
            textList.add("\u00a70to the input.");
        }
        if (te.getPrimaryInventory().getStackInSlot(0).isEmpty()) {
            textList.add("\u00a77No items to fire");
            textList.add("\u00a70Add items in the");
            textList.add("\u00a70cannon slot.");
        }
        if (!te.hasCoordinate()) {
            textList.add("\u00a77No destination coordinate set");
            textList.add("\u00a70Put a GPS Tool with a");
            textList.add("\u00a70coordinate set in the GPS slot.");
        } else if (!te.coordWithinReach) {
            textList.add("\u00a77Selected coordinate");
            textList.add("\u00a77can't be reached");
            textList.add("\u00a70Select a coordinate");
            textList.add("\u00a70closer to the cannon.");
        } else if (te.getRedstoneMode() == 0 && !te.doneTurning) {
            textList.add("\u00a77Cannon still turning");
            textList.add("\u00a70Wait for the cannon");
        } else if (te.getRedstoneMode() == 2 && !te.insertingInventoryHasSpace) {
            textList.add("\u00a77The last shot inventory does not have space for the items in the Cannon.");
        }

        if (textList.size() == 0) {
            textList.add("\u00a77No problems");
            textList.add("\u00a70Apply a redstone");
            textList.add("\u00a70signal to fire.");
        }
    }
}
