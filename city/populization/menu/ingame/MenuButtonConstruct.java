package city.populization.menu.ingame;
import city.populization.render.ResourceLocation;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
/**
 *
 * @author Bryan
 */
public class MenuButtonConstruct extends MenuComponentButtonIngame {
    public MenuButtonConstruct(World world, PlotPos pos, Plot plot, Player localPlayer) {
        super(getTexture(plot), getLabel(plot));
    }
    @Override
    public void action() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private static ResourceLocation getTexture(Plot p){
        if(p==Plot.Road) return build_road;
        if(p==Plot.Warehouse) return build_warehouse;
        if(p==Plot.Workshop) return build_workshop;
        if(p==Plot.Market) return build_market;
        if(p==Plot.TownHall) return build_townHall;
        if(p==Plot.Farm) return build_farm;
        if(p==Plot.Hut) return build_hut;
        if(p==Plot.Room) return build_room;
        if(p==Plot.Apartment) return build_apartment;
        if(p==Plot.Hall) return build_hall;
        if(p==Plot.Lobby) return build_lobby;
        if(p==Plot.Staircase) return build_staircase;
        if(p==Plot.StaircaseTop) return build_staircaseTop;
        if(p==Plot.Elevator) return build_elevator;
        if(p==Plot.ElevatorTop) return build_elevatorTop;
        if(p==Plot.Lattice) return build_lattice;
        if(p==Plot.Pillar) return build_pillar;
        System.err.println("WARNING:  Plot type "+p.name+" has no build texture!  This may be a bug!");
        return build;
    }
    private static String[] getLabel(Plot p){
        return new String[]{"Build", p.name};
    }
}
