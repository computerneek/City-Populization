package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.world.aircraft.Template;
import CityPopulization.world.aircraft.schedule.ScheduleElement;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.ListComponentButton;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
import simplelibrary.opengl.gui.components.MenuComponentSlider;
public class MenuAirportSchedule_NewAircraft extends Menu{
    private float screenHeight;
    private final MenuComponentMulticolumnList list;
    private final MenuComponentButton cancel;
    private double screenWidth;
    private static final int TEMPLATE = 0;
    private static final int RESOURCES = 1;
    private static final int RESOURCE = 2;
    private static final int WORKERS = 3;
    private static final int TIME = 4;
    private static final int CHECK = 5;
    private final int type;
    private final Template template;
    private final ResourceList resources;
    private final Resource resource;
    private final MenuComponentSlider slider;
    private final MenuComponentButton next;
    private int workers;
    private int time;
    private int cost;
    public MenuAirportSchedule_NewAircraft(GUI gui, MenuAirportSchedule parent){
        super(gui, parent);
        type = TEMPLATE;
        screenHeight = gui.helper.guiScale;
        list = add(new MenuComponentMulticolumnList(-1.0, -screenHeight, 2, screenHeight*2-0.1, 2, 0.06));
        cancel = add(new MenuComponentButton(-1, screenHeight-0.08, 1.98, 0.06, "Cancel", true));
        for(Template template : Template.values()){
            list.add(new ListComponentButton(template.toString(), true, 2, 0.06));
        }
        template = null;
        resources = null;
        resource = null;
        slider = null;
        next = null;
    }
    private MenuAirportSchedule_NewAircraft(GUI gui, Menu parent, Template template, ResourceList resources){
        super(gui, parent);
        type = RESOURCES;
        this.template = template;
        this.resources = resources;
        screenHeight = gui.helper.guiScale;
        list = add(new MenuComponentMulticolumnList(-1.0, -screenHeight, 2, screenHeight*2-0.2, 2, 0.06));
        next = add(new MenuComponentButton(-1, screenHeight-0.18, 1.98, 0.06, "Next", true));
        cancel = add(new MenuComponentButton(-1, screenHeight-0.08, 1.98, 0.06, "Cancel", true));
        for(Resource resource : Resource.values()){
            list.add(new ListComponentButton(resource.name()+" = "+resources.get(resource), true, 2, 0.06));
        }
        resource = null;
        slider = null;
    }
    private MenuAirportSchedule_NewAircraft(GUI gui, Menu parent, Template template, ResourceList resources, final Resource resource){
        super(gui, parent);
        type = RESOURCE;
        this.template = template;
        this.resources = resources;
        this.resource = resource;
        screenHeight = gui.helper.guiScale;
        slider = add(new MenuComponentSlider(-1.0, -0.11, 2.0, 0.12, 0, template.cargo-resources.count()+resources.get(resource), resources.get(resource), true){
            @Override
            public String getValueS(){
                return super.getValueS()+" "+resource.name();
            }
        });
        next = add(new MenuComponentButton(-1.0, 0.05, 0.98, 0.06, "Next", true));
        cancel = add(new MenuComponentButton(0.02, 0.05, 0.98, 0.06, "Cancel", true));
        list = null;
    }
    private MenuAirportSchedule_NewAircraft(GUI gui, Menu parent, Template template, ResourceList resources, String ignored){
        super(gui, parent);
        type = WORKERS;
        this.template = template;
        this.resources = resources;
        screenHeight = gui.helper.guiScale;
        slider = add(new MenuComponentSlider(-1.0, -0.11, 2.0, 0.12, 0, template.passengers, 0, true){
            @Override
            public String getValueS(){
                return super.getValueS()+" Workers";
            }
        });
        next = add(new MenuComponentButton(-1.0, 0.05, 0.98, 0.06, "Next", true));
        cancel = add(new MenuComponentButton(0.02, 0.05, 0.98, 0.06, "Cancel", true));
        resource = null;
        list = null;
    }
    private MenuAirportSchedule_NewAircraft(GUI gui, Menu parent, final Template template, ResourceList resources, int workers){
        super(gui, parent);
        type = TIME;
        this.template = template;
        this.resources = resources;
        this.workers = workers;
        screenHeight = gui.helper.guiScale;
        slider = add(new MenuComponentSlider(-1.0, -0.11, 2.0, 0.12, 0, template.totalTimes-1, 0, true){
            @Override
            public String getValueS(){
                return (template.shortestTime+template.timeInterval*(int)getValue())/20+" seconds";
            }
        });
        next = add(new MenuComponentButton(-1.0, 0.05, 0.98, 0.06, "Next", true));
        cancel = add(new MenuComponentButton(0.02, 0.05, 0.98, 0.06, "Cancel", true));
        resource = null;
        list = null;
    }
    private MenuAirportSchedule_NewAircraft(GUI gui, Menu parent, final Template template, ResourceList resources, int workers, int time){
        super(gui, parent);
        type = CHECK;
        this.template = template;
        this.resources = resources;
        this.workers = workers;
        this.time = time;
        screenHeight = gui.helper.guiScale;
        list = add(new MenuComponentMulticolumnList(-1.0, -screenHeight, 2, screenHeight*2-0.1, 2, 0.06));
        next = add(new MenuComponentButton(-1.0, screenHeight-0.08, 0.98, 0.06, "OK", true));
        cancel = add(new MenuComponentButton(0.02, screenHeight-0.08, 0.98, 0.06, "Cancel", true));
        slider = null;
        resource = null;
        list.add(new ListComponentString(template.toString(), 2, 0.06));
        list.add(new ListComponentString("Cargo:  "+resources.toString(), 2, 0.06));
        list.add(new ListComponentString("Workers:  "+workers, 2, 0.06));
        list.add(new ListComponentString("Time:  "+time/20+" seconds", 2, 0.06));
        cost = template.cost;
        while(time<template.shortestTime+(template.totalTimes-1)*template.timeInterval){
            cost+=template.costPerTimeInterval;
            time+=template.timeInterval;
        }
        cost += 5*template.passengers;//Space for passengers
        cost += 5*workers;//Workers aboard
        if(workers<template.passengers){
            cost-=10;//The first civilian on the plane
        }
        cost+=resources.count();//Resources that are being purchased on this trip
        int minCost = cost-(template.passengers-1-workers)*10;
        if(minCost>=cost){
            list.add(new ListComponentString("Cost:  "+cost, 2, 0.06));
        }else{
            list.add(new ListComponentString("Cost:  $"+minCost+" to $"+cost, 2, 0.06));
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        if(type==TEMPLATE||type==RESOURCES||type==WORKERS||type==TIME||type==CHECK){
            parent.parent.render(millisSinceLastTick);
        }else if(type==RESOURCE){
            parent.parent.parent.render(millisSinceLastTick);
        }
        GL11.glColor4d(0, 0, 0, 0.2);
        screenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
        screenHeight = gui.helper.guiScale;
        drawRect(-screenWidth, -screenHeight, screenWidth, screenHeight, 0);
        GL11.glColor4d(1, 1, 1, 1);
        super.render(millisSinceLastTick);
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            if(type==TEMPLATE||type==RESOURCES||type==WORKERS||type==TIME||type==CHECK){
                gui.open(parent.parent);
            }else{
                gui.open(parent.parent.parent);
            }
        }
    }
    @Override
    public void listButtonClicked(ListComponentButton button){
        if(type==TEMPLATE){
            gui.open(new MenuAirportSchedule_NewAircraft(gui, parent, Template.values()[list.components.indexOf(button)], new ResourceList()));
        }else if(type==RESOURCES){
            gui.open(new MenuAirportSchedule_NewAircraft(gui, this, template, resources, Resource.values()[list.components.indexOf(button)]));
        }
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==cancel){
            if(type==TEMPLATE||type==RESOURCES||type==WORKERS||type==TIME||type==CHECK){
                gui.open(parent.parent);
            }else{
                gui.open(parent.parent.parent);
            }
        }else if(button==next){
            if(type==RESOURCES){
                gui.open(new MenuAirportSchedule_NewAircraft(gui, parent, template, resources, "ignored"));
            }else if(type==RESOURCE){
                resources.set(resource, (int)Math.round(slider.getValue()));
                gui.open(new MenuAirportSchedule_NewAircraft(gui, parent.parent, template, resources));
            }else if(type==WORKERS){
                gui.open(new MenuAirportSchedule_NewAircraft(gui, parent, template, resources, (int)Math.round(slider.getValue())));
            }else if(type==TIME){
                gui.open(new MenuAirportSchedule_NewAircraft(gui, parent, template, resources, workers, template.shortestTime+template.timeInterval*(int)slider.getValue()));
            }else if(type==CHECK){
                cost = template.cost;
                int time = this.time;
                while(time<template.shortestTime+(template.totalTimes-1)*template.timeInterval){
                    cost+=template.costPerTimeInterval;
                    time+=template.timeInterval;
                }
                ((MenuAirportSchedule)parent).schedule.elements.add(new ScheduleElement(template, 1, workers, resources, this.time, cost));
                gui.open(((MenuAirportSchedule)parent).refresh());
            }
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}
