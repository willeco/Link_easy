package fr.willy.linky;


import android.text.Editable;
import android.widget.ImageView;

/**
 * Création d'un appareil électrique
 * ---------------------------------
 */
public class Devices {

    private int      id;
    private String   name;
    private int      icon;
    private int      instant_power = 0;
    private float    stand_by_power;
    private float    mean_power;
    private float    use_rate;
    private String  delete = "false";


    /**
     *  Constructeur
     *  ------------
     */
    public Devices(int id, String name, int power, int icon) {
        super();
        this.id             = id;
        this.name           = name;
        this.instant_power  = power;
        this.icon           = icon;
        this.stand_by_power = 0;
        this.mean_power     = 0;
        this.use_rate       = 0.5f;
        this.delete         = "false";
    }

    public Devices() {

    }

    /**
     *  Création des Getter
     *  -------------------
     */
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getIcon(){return this.icon;}

    public int getInstantPower() {
        return instant_power;
    }
    public float getMeanPower(){return mean_power;}
    public float getStandbyPower() {
        return stand_by_power;
    }
    public float getUseRate() {
        return use_rate;
    }
    public String getDelete(){return delete;}


    /**
     * Création des Setter
     * -------------------
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstantPower(int power) {
        this.instant_power = power;
    }
    public void setStandbyPower(float StandbyPower) {
        this.stand_by_power = StandbyPower;
    }
    public void setMeanPower(float MeanPower) {
        this.mean_power = MeanPower;
    }
    public void setUseRate(float UseRate) {
        this.use_rate = UseRate;
    }

    public void setDelete(){ delete="true"; }

    public void setIcon(DeviceActivity deviceActivity, ImageView image, int index){
        //int index = deviceActivity.return_index_icon(this.getName());
        image.setImageResource(index);
        this.icon = index;
    }



    /**
     * Ne sert pas
     * -------------------
     */
    @Override
    public String toString() {
        return "Appareil [id=" + id + ", nom=" + name + ", puissance=" + instant_power + "]";
    }


}