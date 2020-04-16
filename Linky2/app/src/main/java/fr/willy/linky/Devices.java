package fr.willy.linky;


/**
 * Création d'un appareil électrique
 * ---------------------------------
 */
public class Devices {

    private int      id;
    private String   name;
    private String   nickname;
    private String   power;
    private float    standbypower;
    private float    meanpower;
    private float    userate;

    /**
     *  Constructeur
     *  ------------
     */
    public Devices() {

    }

    /**
     *  Constructeur
     *  ------------
     */
    public Devices(int id, String name, String power) {
        super();
        this.id           = id;
        this.name         = name;
        this.power        = power;
        this.standbypower = 0;
        this.meanpower    = 0;
        this.nickname     = "installé quelque part";
        this.userate      = 0.5f;
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

    public String getPower() {
        return power;
    }
    public float getStandbyPower() {
        return standbypower;
    }
    public float getUseRate() {
        return userate;
    }

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

    public void setPower(String power) {
        this.power = power;
    }

    public void setStandbyPower(float StandbyPower) {
        this.standbypower = StandbyPower;
    }
    public void setUseRate(float UseRate) {
        this.userate = UseRate;
    }

    /**
     * Ne sert pas
     * -------------------
     */
    @Override
    public String toString() {
        return "Appareil [id=" + id + ", nom=" + name + ", puissance=" + power + "]";
    }


}