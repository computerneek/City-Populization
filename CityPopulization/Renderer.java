package CityPopulization;
import java.util.ArrayList;
import java.util.Arrays;
import multilib.error.ErrorCategory;
import multilib.error.ErrorLevel;
import multilib.error.Sys;
import multilib.game.MouseStatus;
import multilib.gui.ImageStash;
import multilib.lang.TextManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
public class Renderer {
    private static int page;
    private static Plot lastSelected;
    private static String[] buttons;
    private static ArrayList<ArrayList<String[]>> pages;
    private static ArrayList<String[]> thePage;
    private static String lastMenu;
    private static String[] lastMenuImages;
    private static int lastPage;
    public static float widthScale;
    public static float heightScale;
    private static int currentKey;
    private static int sinceUpdate;
    private static int heldFor;
    private static String lastClick;
    private static int worldList = -1;
    public static void renderGUI(){
        findScale();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0, -1);
        //<editor-fold defaultstate="collapsed" desc="Background rendering">
        drawScaledRect(0, 0, 1600, 1000, ImageStash.instance.getTexture("/gui/ingame/background.png"));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Menu rendering">
        if(main.selected!=lastSelected){
            page = 0;
            lastSelected = main.selected;
            thePage = null;
            pages = null;
            lastMenu = null;
            lastMenuImages = null;
        }
        if(main.selected==null){
            if(main.currentConstruction==null){
                ArrayList<String> buttons = new ArrayList<>();
                getMenuEmptyPlot(buttons, PlotType.empty, 1, 1, false, null);
                renderMenu("Empty", buttons.toArray(new String[buttons.size()]));
            }else{
                page = 0;
                thePage = null;
                pages = null;
                lastMenu = null;
                lastMenuImages = null;
            }
        }else{
            PlotType type = main.selected.getType();
            int level = main.selected.getLevel();
            int maxLevel = main.selected.getMaximumLevel();
            boolean isUpgrading = main.selected.isUpgrading;
            boolean isDamaged = main.selected.broken>0;
            int damage = main.selected.broken;
            WorkerTask task = main.selected.task;
            while(task!=null){
                switch(task.getDescription()){
                    case "Destroy":
                        type = PlotType.empty;
                        level = 1;
                        maxLevel = 1;
                        break;
                    case "Upgrade":
                        level++;
                        break;
                    case "Downgrade":
                        level--;
                        if(level<1){
                            type = PlotType.empty;
                            level = 1;
                            maxLevel = 1;
                        }
                        break;
                    case "Repair":
                        damage--;
                        if(damage<1){
                            isDamaged = false;
                        }
                        break;
                    default:
                        for(PlotType TYPE : PlotType.values()){
                            if(task.getDescription().equals(TYPE.constructionTag)){
                                type = TYPE;
                                level = 1;
                                maxLevel = type.isTemporary?type.permanent.levels*2:type.levels;
                                break;
                            }
                        }
                }
                if(task.hasNextTask()){
                    task = task.getNextTask();
                }else{
                    break;
                }
            }
            ArrayList<String> buttonList = new ArrayList<>();
            switch(type){
                case empty:
                    getMenuEmptyPlot(buttonList, type, level, maxLevel, isUpgrading, task);
                    break;
                case highway:
                    getMenuHighway(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case house:
                    getMenuHouse(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case workshop:
                    getMenuWorkshop(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case mainBase:
                    getMenuMainBase(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case coalMine:
                case dirtMine:
                case forest:
                case ironMine:
                case oilWell:
                case quarry:
                case sandPit:
                case clayPit:
                case goldMine:
                    getMenuMine(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task, task instanceof WorkerTaskHarvest);
                    break;
                case shoppingMall:
                    getMenuMall(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case amusementPark:
                    getMenuAmusementPark(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case departmentStore:
                    getMenuDepartmentStore(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case fireDepartment:
                    getMenuFireDepartment(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case hospital:
                    getMenuHospital(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case militaryBase:
                    getMenuMilitaryBase(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case park:
                    getMenuPark(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case police:
                    getMenuPolice(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case repairStation:
                    getMenuRepairStation(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case restaurant:
                    getMenuRestaurant(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case school:
                    getMenuSchool(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case warehouse:
                    getMenuWarehouse(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                case zombieland:
                    getMenuZombieland(buttonList, type, level, maxLevel, isUpgrading, isDamaged, task);
                    break;
                default:
                    throw new UnsupportedOperationException("Could not figure out what menu to display for type "+type.name()+"!");
            }
            String[] theButtons = buttonList.toArray(new String[buttonList.size()]);
            switch(type){
                case empty:
                    renderMenu("Empty", theButtons);
                    break;
                case highway:
                    renderMenu("Highway", theButtons);
                    break;
                case house:
                    renderMenu("House", theButtons);
                    break;
                case workshop:
                    renderMenu("Workshop", theButtons);
                    break;
                case mainBase:
                    renderMenu("Main Base", theButtons);
                    break;
                case coalMine:
                    renderMenu("Coal Mine", theButtons);
                    break;
                case dirtMine:
                    renderMenu("Dirt Mine", theButtons);
                    break;
                case forest:
                    renderMenu("Forest", theButtons);
                    break;
                case ironMine:
                    renderMenu("Iron Mine", theButtons);
                    break;
                case oilWell:
                    renderMenu("Oil Well", theButtons);
                    break;
                case quarry:
                    renderMenu("Quarry", theButtons);
                    break;
                case sandPit:
                    renderMenu("Sand Pit", theButtons);
                    break;
                case clayPit:
                    renderMenu("Clay Pit", theButtons);
                    break;
                case goldMine:
                    renderMenu("Gold Mine", theButtons);
                    break;
                case shoppingMall:
                    renderMenu("Shopping Mall", theButtons);
                    break;
                case amusementPark:
                    renderMenu("Amusement Park", theButtons);
                    break;
                case departmentStore:
                    renderMenu("Department Store", theButtons);
                    break;
                case fireDepartment:
                    renderMenu("Fire Department", theButtons);
                    break;
                case hospital:
                    renderMenu("Hospital", theButtons);
                    break;
                case militaryBase:
                    renderMenu("Military Base", theButtons);
                    break;
                case park:
                    renderMenu("Park", theButtons);
                    break;
                case police:
                    renderMenu("Police", theButtons);
                    break;
                case repairStation:
                    renderMenu("Repair Station", theButtons);
                    break;
                case restaurant:
                    renderMenu("Restaurant", theButtons);
                    break;
                case school:
                    renderMenu("School", theButtons);
                    break;
                case warehouse:
                    renderMenu("Warehouse", theButtons);
                    break;
                case zombieland:
                    renderMenu("Zombieland", theButtons);
                    break;
                default:
                    throw new UnsupportedOperationException("Could not figure out what menu to display for type "+type.name()+"!");
            }
            PlotType atype = main.selected.getType();
            int alevel = main.selected.getLevel();
            int amaxLevel = main.selected.getMaximumLevel();
            boolean aisUpgrading = main.selected.isUpgrading;
            boolean aisDamaged = main.selected.broken>0;
            int adamage = main.selected.broken;
            WorkerTask atask = main.selected.task;
            drawScaledText(0, 900, 200, 920, atype.name+" lvl "+alevel);
            drawScaledText(0, 920, 200, 940, "to "+type.name+" lvl "+level);
            for(int i = 0; i<3&&atask!=null; i++){
                drawScaledText(0, 940+i*20, 200, 960+i*20, atask.getDescription());
                atask = atask.nextTask;
            }
        }
        //</editor-fold>
    }
    public static void update(){
        sinceUpdate++;
        if(sinceUpdate>1){
            heldFor = -1;
            currentKey = -1;
            lastClick = null;
        }
    }
    public static void renderWorld(Plot[][] world, float worldMovementX, float worldMovementY){
        if(world!=null){
            org.lwjgl.opengl.GL11.glLoadIdentity();
            org.lwjgl.opengl.GL11.glTranslatef(worldMovementX/widthScale, worldMovementY/heightScale, -1);
//            if(worldList!=-1){
////                GL11.glPushMatrix();
//                GL11.glCallList(worldList);
////                GL11.glPopMatrix();
//                return;
//            }
//            int list = GL11.glGenLists(1);
//            GL11.glNewList(list, GL11.GL_COMPILE);
            for(int i = 0; i<world.length; i++){
                Plot[] plots = world[i];
                if(plots!=null){
                    for(int j = 0; j<plots.length; j++){
                        Plot plot = plots[j];
                        if(plot!=null){
                            int coords0 = plot.getCoords()[0], coords1 = plot.getCoords()[1];
                            int type = (plot.getType().isFarm()?(plot.task==null?0:(plot.task instanceof WorkerTaskHarvest?(plot.task.progress>0?2:1):0)):0);
                            drawScaledRect(coords0*50, coords1*50, coords0*50+50, coords1*50+50, ImageStash.instance.getTexture("/Plots/"+plot.getType().name()+(type==0?"":(type==1?"/Pending Harvest":"/Harvesting"))+"/Level "+plot.getLevel()+"_"+plot.getType().getFrame(plot.getLevel(), type)+".png"));
                            if(plot.broken>0){
                                drawScaledRect(coords0*50+5, coords1*50+5, coords0*50+45, coords1*50+45, ImageStash.instance.getTexture("/Plots/damage"+main.damageFrame+".png"));
                            }
                            if(plot.isUpgrading){
                                drawScaledRect(coords0*50+10, coords1*50+10, coords0*50+40, coords1*50+40, ImageStash.instance.getTexture("/Plots/construction"+main.constructionFrame+".png"));
                            }
                        }
                    }
                }
            }
//            GL11.glPopMatrix();
//            GL11.glEndList();
//            worldList = list;
        }
    }
    public static void renderCivillians(ArrayList<Civillian> workers, float worldMovementX, float worldMovementY){
        if(workers!=null){
            org.lwjgl.opengl.GL11.glLoadIdentity();
            org.lwjgl.opengl.GL11.glTranslatef(worldMovementX/widthScale, worldMovementY/heightScale, -1);
            for(int i = 0; i<workers.size(); i++){
                Civillian civillian = workers.get(i);
                int dir = civillian.direction;
                if(dir!=1&&dir!=2&&dir!=3&&dir!=4){
                    dir = 3;
                }
                String direction = dir==1?"Up":(dir==2?"Right":(dir==3?"Down":"Left"));
                int[] coords = civillian.coords;
                if(civillian.zombie&&civillian.dead){
                    drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/zombie/Dead/"+main.civillianFrame[6]+".png"));
                }else if(civillian.zombie){
                    drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/zombie/"+direction+main.civillianFrame[6+dir]+".png"));
                }else if(civillian.dead){
                    drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/civillian/Dead"+main.civillianFrame[5]+".png"));
                }else if(civillian.angry){
                    drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/civillian/Angry"+main.civillianFrame[4]+".png"));
                }else{
                    drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/civillian/"+direction+main.civillianFrame[dir-1]+".png"));
                }
            }
        }
    }
    public static void renderWorkers(ArrayList<Worker> workers, float worldMovementX, float worldMovementY){
        if(workers!=null){
            org.lwjgl.opengl.GL11.glLoadIdentity();
            org.lwjgl.opengl.GL11.glTranslatef(worldMovementX/widthScale, worldMovementY/heightScale, -1);
            for(int i = 0; i<workers.size(); i++){
                Civillian civillian = workers.get(i);
                int dir = civillian.direction;
                if(dir<1||dir>4){
                    dir=3;
                }
                String direction = dir==1?"Up":(dir==2?"Right":(dir==3?"Down":(dir==4?"Left":"Down")));
                String type = "";
                if(((Worker)civillian).resource!=null){
                    type = ((Worker)civillian).resource.getTypeStringForWorkers();
                }
                int[] coords = civillian.coords;
                if(((Worker)civillian).resource!=null){
                    switch(((Worker)civillian).resource){
                        case Dirt:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+3]+".png"));
                            break;
                        case Oil:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+7]+".png"));
                            break;
                        case Coal:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+11]+".png"));
                            break;
                        case Stone:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+15]+".png"));
                            break;
                        case Iron:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+19]+".png"));
                            break;
                        case Wood:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+23]+".png"));
                            break;
                        case Sand:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+27]+".png"));
                            break;
                        case Clay:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+31]+".png"));
                            break;
                        case Gold:
                            drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir+35]+".png"));
                            break;
                    }
                }else{
                    drawScaledRect(coords[0]-10, coords[1]-10, coords[0]+10, coords[1]+10, ImageStash.instance.getTexture("/Plots/worker/"+type+direction+main.workerFrame[dir-1]+".png"));
                }
            }
        }
    }
    private static void renderMenu(String menu, String... menuImages){
        if(page!=lastPage&&pages!=null){
            if(page<0){
                page = 0;
                renderMenu(menu, menuImages);
                return;
            }else if(page>=pages.size()){
                page = pages.size()-1;
                renderMenu(menu, menuImages);
                return;
            }else{
                thePage = pages.get(page);
                buttons = new String[thePage.size()];
                for(int i = 0; i<buttons.length; i++){
                    buttons[i] = thePage.get(i)[1];
                }
                lastPage = page;
            }
        }
        if(pages!=null&&menu.equals(lastMenu)&&areArraysSame(menuImages, lastMenuImages)){
            renderMenu(thePage);
            return;
        }
        int pixels = 1600-250;
        pixels-=(pixels%100);
        int buttonsPerPage = pixels/100;
        pages = new ArrayList<>();
        thePage = new ArrayList<>();
        pages.add(thePage);
        if(buttonsPerPage>=menuImages.length){
            for(int i = 0; i<menuImages.length; i++){
                thePage.add(new String[]{menu, menuImages[i]});
            }
        }else if(buttonsPerPage<3){
            pages = null;
            return;
        }else{
            ArrayList<String> images = new ArrayList<>(Arrays.asList(menuImages));
            while(images.size()>0){
                if(thePage.size()==buttonsPerPage-1&&images.size()==1){
                    thePage.add(new String[]{menu, images.remove(0)});
                }else if(thePage.size()==buttonsPerPage-1&&images.size()>1){
                    thePage.add(new String[]{null, "Next Page"});
                    thePage = new ArrayList<>();
                    pages.add(thePage);
                    thePage.add(new String[]{null, "Previous Page"});
                    thePage.add(new String[]{menu, images.remove(0)});
                }else if(thePage.size()<buttonsPerPage-1){
                    thePage.add(new String[]{menu, images.remove(0)});
                }
            }
        }
        thePage = pages.get(0);
        lastMenu = menu;
        lastMenuImages = menuImages;
        page = 0;
        buttons = new String[thePage.size()];
        for(int i = 0; i<buttons.length; i++){
            buttons[i] = thePage.get(i)[1];
        }
        renderMenu(thePage);
    }
    private static void renderButton(String menu, String buttonName, int whichButton){
        drawScaledRect(210+whichButton*100, 910, 290+whichButton*100, 990, ImageStash.instance.getTexture("/gui/buttons/"+(menu==null?"":menu+"/")+buttonName+".png"));
    }
    private static void getMenuEmptyPlot(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        for(PlotType aType : PlotType.values()){
            if(aType.canBuild){
                buttonList.add(aType.constructionTag);
            }
        }
    }
    private static void getMenuHighway(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }
        if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuHouse(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }
        if(level>0){
            buttonList.add("downgrade");
        }
        if(task==null&&lastSelected.workersSent<level*level*main.handler.civilliansPerLevel){
            buttonList.add("add worker");
        }
        if(task==null&&lastSelected.workersSent>0){
            buttonList.add("remove worker");
        }
        buttonList.add("destroy");
    }
    private static void getMenuWorkshop(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }
        if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuMainBase(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }
        if(level>1){
            buttonList.add("downgrade");
        }
    }
    private static void getMenuMine(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task, boolean isHarvesting){
        if(isHarvesting){
            buttonList.add("stopHarvesting");
            return;
        }
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }
        if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("startHarvesting");
        buttonList.add("destroy");
    }
    private static void getMenuMall(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuZombieland(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        buttonList.add("destroy");
    }
    private static void getMenuAmusementPark(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuDepartmentStore(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuFireDepartment(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuHospital(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuMilitaryBase(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuPark(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuPolice(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuRepairStation(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuRestaurant(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuSchool(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    private static void getMenuWarehouse(ArrayList<String> buttonList, PlotType type, int level, int maxLevel, boolean upgrading, boolean isDamaged, WorkerTask task){
        if(task!=null||upgrading){
            buttonList.add("cancel");
        }
        if(isDamaged){
            buttonList.add("repair");
            buttonList.add("destroy");
            return;
        }
        if(level<maxLevel){
            buttonList.add("upgrade");
        }else if(level>0){
            buttonList.add("downgrade");
        }
        buttonList.add("destroy");
    }
    public static boolean onClick(MouseStatus status, int posX, int posY){
        if(buttons==null||(status!=null&&!status.leftButtonPressed)){
            return isClickOnGUI(posX, posY);
        }
        for(int i = 0; i<buttons.length; i++){
            if(buttons[i]==null){
                continue;
            }
            if(isScaledClickWithinBounds(posX, posY, i*100+210, 910, i*100+290, 990)){
                onButtonClicked(buttons[i]);
                lastClick = buttons[i];
                return true;
            }
        }
        return isClickOnGUI(posX, posY);
    }
    private static void onButtonClicked(String label){
        PlotType type = lastSelected==null?null:lastSelected.getType();
        int level = lastSelected==null?0:lastSelected.getLevel();
        int maxLevel = lastSelected==null?0:lastSelected.getMaximumLevel();
        boolean isUpgrading = lastSelected==null?false:lastSelected.isUpgrading;
        boolean isDamaged = lastSelected==null?false:lastSelected.broken>0;
        int damage = lastSelected==null?0:lastSelected.broken;
        WorkerTask task = lastSelected==null?null:lastSelected.task;
        while(task!=null){
            switch(task.getDescription()){
                case "Destroy":
                    type = PlotType.empty;
                    level = 1;
                    maxLevel = 1;
                    break;
                case "Upgrade":
                    level++;
                    break;
                case "Downgrade":
                    level--;
                    if(level<1){
                        type = PlotType.empty;
                        level = 1;
                        maxLevel = 1;
                    }
                    break;
                case "Repair":
                    damage--;
                    if(damage<1){
                        isDamaged = false;
                    }
                    break;
                default:
                    for(PlotType TYPE : PlotType.values()){
                        if(task.getDescription().equals(TYPE.constructionTag)){
                            type = TYPE;
                            level = 1;
                            maxLevel = type.isTemporary?type.permanent.levels*2:type.levels;
                            break;
                        }
                    }
            }
            if(task.hasNextTask()){
                task = task.getNextTask();
            }else{
                break;
            }
        }
        if(label==null){
        }else if(label.equals("Next Page")){
            page++;
        }else if(label.equals("Previous Page")){
            page--;
        }else if(lastSelected!=null&&label.equals("cancel")){
            lastSelected.cancelATask();
        }else if(lastSelected!=null&&label.equals("upgrade")){
            if(WorkerTaskList.hasWorkers(level+1)){
                int dirt = type.getCost(level+1, Material.Dirt);
                int coal = type.getCost(level+1, Material.Coal);
                int oil = type.getCost(level+1, Material.Oil);
                int wood = type.getCost(level+1, Material.Wood);
                int stone = type.getCost(level+1, Material.Stone);
                int iron = type.getCost(level+1, Material.Iron);
                int sand = type.getCost(level+1, Material.Sand);
                int clay = type.getCost(level+1, Material.Clay);
                int gold = type.getCost(level+1, Material.Gold);
                WorkerTask theTask = new WorkerTask("Upgrade", lastSelected, (int)(2F+(5F/maxLevel*(level+1))), (level+1)*100, dirt, coal, oil, wood, stone, iron, sand, clay, gold, level+1);
                lastSelected.addTask(theTask);
            }
        }else if(lastSelected!=null&&label.equals("downgrade")){
            if(WorkerTaskList.hasWorkers(level)){
                int dirt = type.getCost(level, Material.Dirt);
                int coal = type.getCost(level, Material.Coal);
                int oil = type.getCost(level, Material.Oil);
                int wood = type.getCost(level, Material.Wood);
                int stone = type.getCost(level, Material.Stone);
                int iron = type.getCost(level, Material.Iron);
                int sand = type.getCost(level, Material.Sand);
                int clay = type.getCost(level, Material.Clay);
                int gold = type.getCost(level, Material.Gold);
                lastSelected.addTask(new WorkerTask("Downgrade", lastSelected, (int)(2F+(5F/maxLevel*(level))), (level+1)*100, -dirt, -coal, -oil, -wood, -stone, -iron, -sand, -clay, -gold, level));
            }
        }else if(lastSelected!=null&&label.equals("destroy")){
            if(WorkerTaskList.hasWorkers(level)){
                int dirt = type.getCost(level, Material.Dirt);
                int coal = type.getCost(level, Material.Coal);
                int oil = type.getCost(level, Material.Oil);
                int wood = type.getCost(level, Material.Wood);
                int stone = type.getCost(level, Material.Stone);
                int iron = type.getCost(level, Material.Iron);
                int sand = type.getCost(level, Material.Sand);
                int clay = type.getCost(level, Material.Clay);
                int gold = type.getCost(level, Material.Gold);
                for(int i = level-1; i>0; i--){
                    dirt+=type.getCost(i, Material.Dirt);
                    coal+=type.getCost(i, Material.Coal);
                    oil+=type.getCost(i, Material.Oil);
                    wood+=type.getCost(i, Material.Wood);
                    stone+=type.getCost(i, Material.Stone);
                    iron+=type.getCost(i, Material.Iron);
                    sand+=type.getCost(level, Material.Sand);
                    clay+=type.getCost(level, Material.Clay);
                    gold+=type.getCost(level, Material.Gold);
                }
                lastSelected.addTask(new WorkerTask("Destroy", lastSelected, level*2, 500, -dirt/10, -coal/10, -oil/10, -wood/10, -stone/10, -iron/10, -sand/10, -clay/10, -gold/10, level));
            }
        }else if(lastSelected!=null&&label.equals("add worker")){
            if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)&&Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                while(lastSelected.workersSent<level*level*main.handler.civilliansPerLevel){
                    CivillianScheduler.addWorker(lastSelected);
                    lastSelected.workersSent++;
                }
            }else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                for(int i = 0; i<10&&lastSelected.workersSent<level*level*main.handler.civilliansPerLevel; i++){
                    CivillianScheduler.addWorker(lastSelected);
                    lastSelected.workersSent++;
                }
            }else{
                CivillianScheduler.addWorker(lastSelected);
                lastSelected.workersSent++;
            }
        }else if(lastSelected!=null&&label.equals("remove worker")){
            boolean go = false;
            if(!CivillianScheduler.cancelWorker(lastSelected)){
                go = Plot.removeWorker(lastSelected);
                if(go){
                    CivillianScheduler.addCivillian(lastSelected);
                }
            }else{
                go = true;
            }
            if(go){
                lastSelected.workersSent--;
            }else{
                Sys.error(ErrorLevel.severe, "No worker to remove- button should not appear!", null, ErrorCategory.bug);
            }
        }else if(lastSelected!=null&&label.equals("repair")){
            if(WorkerTaskList.hasWorkers(1)){
                lastSelected.addTask(new WorkerTask("repair", lastSelected, 5, level*10, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1));
            }
        }else if(lastSelected!=null&&label.equals("startHarvesting")){
            if(WorkerTaskList.hasWorkers(1)){
                lastSelected.addTask(new WorkerTaskHarvest(lastSelected));
            }
        }else if(lastSelected!=null&&label.equals("stopHarvesting")){
            lastSelected.cancelATask();
        }else{
            PlotType Type = PlotType.getConst(label);
            if(Type==null){
                throw new IllegalArgumentException("Unknown button command: "+label);
            }
            main.currentConstruction = Type;
        }
        lastSelected = null;
    }
    private static boolean areArraysSame(String[] first, String[] second){
        if(first==second){
            return true;
        }else if(first==null||second==null){
            return false;
        }else if(first.length!=second.length){
            return false;
        }else{
            for(int i = 0; i<first.length; i++){
                if(!first[i].equals(second[i])){
                    return false;
                }
            }
            return true;
        }
    }
    private static void renderMenu(ArrayList<String[]> thePage){
        for(int i = 0; i<thePage.size(); i++){
            String[] strs = thePage.get(i);
            renderButton(strs[0], strs[1], i);
        }
    }
    public static void renderText(int x, int y, String text){
        drawScaledText(x, y, 1600-x, y+25, text);
    }
    private static void findScale(){
        widthScale = 1600F/((float)Display.getWidth());
        heightScale = 1000F/((float)Display.getHeight());
    }
    public static boolean isScaledClickWithinBounds(int clickX, int clickY, int targetXMin, int targetYMin, int targetXMax, int targetYMax){
        float scaledX = clickX*widthScale, scaledY = clickY*heightScale;
        return scaledX>=targetXMin&&scaledY>=targetYMin&&scaledX<=targetXMax&&scaledY<=targetYMax;
    }
    public static void drawScaledRect(float leftEdge, float topEdge, float rightEdge, float bottomEdge, int texture){
        float left = leftEdge/widthScale, top = topEdge/heightScale, right = rightEdge/widthScale, bottom = bottomEdge/heightScale;
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(left, top);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(right, top);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(right, bottom);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(left, bottom);
        GL11.glEnd();
    }
    public static boolean drawScaledText(float leftEdge, float topEdge, float rightPossibleEdge, float bottomEdge, String text){
        boolean trimmed = false;
        float distanceForText = TextManager.getLengthForStringWithHeight(text, bottomEdge-topEdge);
        while(distanceForText>rightPossibleEdge-leftEdge&&!text.isEmpty()){
            trimmed = true;
            text = text.substring(0, text.length()-1);
            distanceForText = TextManager.getLengthForStringWithHeight(text+"...", bottomEdge-topEdge);
        }
        if(text.isEmpty()){
            return false;
        }
        if(trimmed){
            text+="...";
        }
        float scale = TextManager.getLengthForStringWithHeight("M", bottomEdge-topEdge)/((float)TextManager.getCharLength('M'));
        float skip = 0;
        char[] chars = new char[text.length()];
        text.getChars(0, chars.length, chars, 0);
        for(char character : chars){
            drawScaledRect(leftEdge+skip, topEdge, leftEdge+skip+(((float)TextManager.getCharLength(character))*scale), bottomEdge, (int)TextManager.getImageForChar(character));
            skip+=(((float)TextManager.getCharLength(character))*scale);
        }
        return true;
    }
    public static int drawCenteredScaledText(float leftPossibleEdge, float topEdge, float rightPossibleEdge, float bottomEdge, String text){
        boolean trimmed = false;
        float distanceForText = TextManager.getLengthForStringWithHeight(text, bottomEdge-topEdge);
        while(distanceForText>Math.abs(rightPossibleEdge-leftPossibleEdge)&&!text.isEmpty()){
            trimmed = true;
            text = text.substring(0, text.length()-1);
            distanceForText = TextManager.getLengthForStringWithHeight(text+"...", bottomEdge-topEdge);
        }
        if(text.isEmpty()){
            return 0;
        }
        if(trimmed){
            text+="...";
        }
        float scale = TextManager.getLengthForStringWithHeight("M", bottomEdge-topEdge)/((float)TextManager.getCharLength('M'));
        float skip = (Math.abs(rightPossibleEdge-leftPossibleEdge)-distanceForText)/2;
        char[] chars = new char[text.length()];
        text.getChars(0, chars.length, chars, 0);
        for(char character : chars){
            drawScaledRect(leftPossibleEdge+skip, topEdge, leftPossibleEdge+skip+(((float)TextManager.getCharLength(character))*scale), bottomEdge, (int)TextManager.getImageForChar(character));
            skip+=(((float)TextManager.getCharLength(character))*scale);
        }
        return 5;
    }
    public static void scaledTranslate(float existingValue, float targetValue, float Xtranslate, float Ytranslate, float Ztranslate){
        float scale = targetValue/existingValue;
        float X = Xtranslate/scale, Y = Ytranslate/scale, Z = Ztranslate/scale;
        GL11.glTranslatef(X, Y, Z);
    }
    private static boolean isClickOnGUI(int posX, int posY){
        return isScaledClickWithinBounds(posX, posY, 0, 0, 1600, 50)||isScaledClickWithinBounds(posX, posY, 0, 900, 1600, 1000);
    }
    public static void onKeyboard(int j){
        currentKey = j;
        sinceUpdate = 0;
        heldFor = 0;
        onClick(null, (int)((150+(j*100))/Renderer.widthScale), (int)(950/Renderer.heightScale));
    }
    static void keyboardHeld(int j){
        if(j!=currentKey){
            return;
        }
        sinceUpdate = 0;
        heldFor++;
        if(heldFor>20&&buttons.length>=j&&lastClick!=null&&lastClick.equals(buttons[j-1])){
            onButtonClicked(buttons[j-1]);
        }
    }
}
