package fr.willy.linky;

public class Devices {
    private int id;
    private String name;
    private String power;

    public Devices() {

    }

    public Devices(int id, String name, String power) {
        super();
        this.id = id;
        this.name = name;
        this.power = power;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }


    @Override
    public String toString() {
        return "Article [id=" + id + ", name=" + name + ", power=" + power + "]";
    }
}