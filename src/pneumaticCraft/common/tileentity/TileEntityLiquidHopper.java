package pneumaticCraft.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.util.IOHelper;
import pneumaticCraft.lib.PneumaticValues;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLiquidHopper extends TileEntityOmnidirectionalHopper implements IFluidHandler{

    @DescSynced
    private final FluidTank tank = new FluidTank(PneumaticValues.NORMAL_TANK_CAPACITY);

    public TileEntityLiquidHopper(){
        setUpgradeSlots(0, 1, 2, 3);
    }

    @Override
    protected int getInvSize(){
        return 4;
    }

    @Override
    public String getInventoryName(){
        return Blockss.liquidHopper.getUnlocalizedName();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1){
        return new int[]{0, 1, 2, 3};
    }

    @Override
    protected boolean exportItem(int maxItems){
        if(tank.getFluid() != null) {
            ForgeDirection dir = ForgeDirection.getOrientation(getBlockMetadata());
            TileEntity neighbor = IOHelper.getNeighbor(this, dir);
            if(neighbor instanceof IFluidHandler) {
                IFluidHandler fluidHandler = (IFluidHandler)neighbor;
                if(fluidHandler.canFill(dir.getOpposite(), tank.getFluid().getFluid())) {
                    FluidStack fluid = tank.getFluid().copy();
                    fluid.amount = maxItems * 100;
                    tank.getFluid().amount -= fluidHandler.fill(dir.getOpposite(), fluid, true);
                    if(tank.getFluidAmount() <= 0) tank.setFluid(null);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean suckInItem(int maxItems){
        TileEntity inputInv = IOHelper.getNeighbor(this, inputDir);
        if(inputInv instanceof IFluidHandler) {
            IFluidHandler fluidHandler = (IFluidHandler)inputInv;

            FluidStack fluid = fluidHandler.drain(inputDir.getOpposite(), maxItems * 100, false);
            if(fluid != null && fluidHandler.canDrain(inputDir.getOpposite(), fluid.getFluid())) {
                int filledFluid = fill(inputDir, fluid, true);
                if(filledFluid > 0) {
                    fluidHandler.drain(inputDir.getOpposite(), filledFluid, true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill){
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain){
        return tank.getFluid() != null && tank.getFluid().isFluidEqual(resource) ? tank.drain(resource.amount, doDrain) : null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain){
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid){
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid){
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from){
        return new FluidTankInfo[]{new FluidTankInfo(tank)};
    }

    @SideOnly(Side.CLIENT)
    public FluidTank getTank(){
        return tank;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);

        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("tank", tankTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        tank.readFromNBT(tag.getCompoundTag("tank"));
    }
}
