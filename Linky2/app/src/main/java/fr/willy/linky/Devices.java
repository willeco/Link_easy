package fr.willy.linky;


import android.util.Log;
import android.widget.ImageView;

/**
 * Création d'un appareil électrique
 * ---------------------------------
 */
public class Devices {

    private int      id;
    private String   name;
    private int      power;
    private float    standbypower;
    private float    meanpower;
    private float    userate;
    private int icon;
    private int delete;


    /**
     *  Constructeur
     *  ------------
     */
    public Devices(int id, int icon, String name, int power,float standbypower, float meanpower, float userate, int delete) {
        super();
        this.id           = id;
        this.icon         = icon;
        this.name         = name;
        this.power        = power;
        this.standbypower = standbypower;
        this.meanpower    = meanpower;
        this.userate      = userate;
        this.delete       = delete;
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
    public int getIcon() {
        return  icon;
    }
    public String getName() {
        return this.name;
    }

    public int getPower() {
        return power;
    }
    public float getMeanPower(){return meanpower;}
    public float getStandbyPower() {
        return standbypower;
    }
    public float getUseRate() {
        return userate;
    }
    public int getDelete(){return delete;}


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
    public void setPower(int power) {
        this.power = power;
    }
    public void setStandbyPower(float StandbyPower) {
        this.standbypower = StandbyPower;
    }
    public void setUseRate(float UseRate) {
        this.userate = UseRate;
    }
    public void setMeanPower() {
        Float meanPower = (getPower()*getUseRate()+getStandbyPower()*(24-getUseRate()))/24;
        this.meanpower = Math.round(meanPower);
    }

    public void setDelete(){ this.delete=1; }
    public void setIcon(int index){
        this.icon = index;
    }
    public void displayIcon(DeviceActivity deviceActivity, ImageView image){
        image.setImageResource(this.icon);
    }

    @Override
    public String toString() {
        return "Appareil [id=" + id + ", nom=" + name + ", puissance=" + power + ", supression =" + delete + "]";
    }


}