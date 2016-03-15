package city.populization.menu.ingame;
import city.populization.core.ClientSide;
import city.populization.render.ResourceLocation;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
class MenuButtonUpgrade extends MenuComponentButtonIngame {
    public MenuButtonUpgrade(ClientSide c, PlotPos pos, Plot p, int level) {
        super(getResource(p, level), "Upgrade", p.name);
    }
    private static ResourceLocation getResource(Plot p, int level){
        if(p==Plot.Road){
            return upgradeRoad;
        }else if(p==Plot.Warehouse){
            return upgradeWarehouse;
        }else if(p==Plot.Workshop){
            return upgradeWorkshop;
        }else if(p==Plot.Market){
            return upgradeMarket;
        }else if(p==Plot.TownHall){
            return upgradeTownHall;
        }else if(p==Plot.Farm){
            return upgradeFarm;
        }else if(p==Plot.Room){
            return upgradeRoom;
        }else if(p==Plot.Apartment){
            return upgradeApartment;
        }else if(p==Plot.Hall){
            return upgradeHall;
        }else if(p==Plot.Lobby){
            return upgradeLobby;
        }else if(p==Plot.Staircase){
            return upgradeStaircase;
        }else if(p==Plot.StaircaseTop){
            return upgradeStaircaseTop;
        }else if(p==Plot.Elevator){
            return upgradeElevator;
        }else if(p==Plot.ElevatorTop){
            return upgradeElevatorTop;
        }else if(p==Plot.Lattice){
            return upgradeLattice;
        }else if(p==Plot.Pillar){
            return upgradePillar;
        }else{
            return upgradeStructure;
        }
    }
    @Override
    public void action() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Request plot upgrade from server
    }
}
