package fr.willy.linky;

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
