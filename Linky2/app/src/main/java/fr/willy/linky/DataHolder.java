package fr.willy.linky;

//Cette classe a permis la simplification du passage de variable entre les differentes classes
//Elle n'est plus d'actualité.

public class DataHolder {
    private String data;
    private static final DataHolder holder = new DataHolder();

    public static DataHolder getInstance() {
        return holder;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void deleteData(){
        this.data = null;
    }
}
