package city.populization.menu.ingame;
import city.populization.core.ClientSide;
import city.populization.render.ResourceLocation;
import city.populization.world.plot.PlotPos;
import city.populization.world.resource.Resource;
public class MenuButtonHarvest extends MenuComponentButtonIngame {
    public MenuButtonHarvest(ClientSide c, PlotPos pos, Resource resourceProduced) {
        super(getResource(resourceProduced), "Harvest", resourceProduced.name().replaceAll("_", " "));
    }
    @Override
    public void action() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Ask server to harvest resource
    }
    private static ResourceLocation getResource(Resource r){
        switch(r){
            case Dirt:
                return digDirt;
            case Sand:
                return digSand;
            case Stone:
                return mineStone;
            case Coal:
                return mineCoal;
            case Clay:
                return digClay;
            case Oil:
                return pumpOil;
            case Iron:
                return mineIron;
            case Gold:
                return mineGold;
            case Diamond:
                return mineDiamond;
            default:
                return harvestResource;
        }
    }
}
