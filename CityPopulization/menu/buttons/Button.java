package CityPopulization.menu.buttons;
public class Button {
    public String image;
    public String[] text;
    public ButtonEvent event;
    public Button setImage(String image){
        this.image = image;
        return this;
    }
    public Button setText(String... text){
        this.text = text;
        return this;
    }
    public Button setEvent(ButtonEvent event){
        this.event = event;
        return this;
    }
    public void onClicked(){
        event.onClicked();
    }
}
