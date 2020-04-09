package fr.willy.linky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

public class DeviceDataBase {

    private static final int DATABASE_VERSION = 1;
    private static final String DBNAME = "device.db";
    private static final String DEVICE_TABLE_NAME = "device";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String POWER = "power";

    private SQLiteDatabase bdd;
    private DeviceOpenHelper deviceBaseSQLite;

    public DeviceDataBase(Context context)
    {
        deviceBaseSQLite = new DeviceOpenHelper(context, DBNAME, null, DATABASE_VERSION);
    }

    public void open()
    {
        bdd = deviceBaseSQLite.getWritableDatabase();
    }

    public void close()
    {
        bdd.close();
    }

    public SQLiteDatabase getBDD()
    {
        return bdd;
    }

    public long insert(Devices device)
    {
        ContentValues values = new ContentValues();
        //values.put(ID, device.getId());
        values.put(NAME, device.getName());
        values.put(POWER, device.getPower());
        return bdd.insert(DEVICE_TABLE_NAME, null, values);
    }

    public int update(int id, Devices device)
    {
        ContentValues values = new ContentValues();
        values.put(ID, device.getId());
        values.put(NAME, device.getName());
        values.put(POWER, device.getPower());
        return bdd.update(DEVICE_TABLE_NAME, values, ID + " = " +id, null);
    }

    public int remove(int id)
    {
        return bdd.delete(DEVICE_TABLE_NAME, ID + " = " +id, null);
    }

    public void removeAll()
    {
        bdd.execSQL("DELETE from "+ DEVICE_TABLE_NAME);
    }

    public Devices selectWithID(String[] args)
    {
        Cursor cursor = bdd.rawQuery("SELECT * from " + DEVICE_TABLE_NAME + " WHERE id = ? ", args);
        Devices device = cursorToDevice(cursor, false);
        cursor.close();
        return device;
    }

    public LinkedList<Devices> selectAll()
    {
        LinkedList<Devices> devices = new LinkedList<Devices>();
        Cursor cursor = bdd.rawQuery("SELECT * from " + DEVICE_TABLE_NAME, null);
        if(cursor.getCount() != 0){
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Devices device = cursorToDevice(cursor, true);
                devices.add(device);
            }
        }
        cursor.close();
        return devices;
    }

    public void displayDevices()
    {
        LinkedList<Devices> devices = selectAll();
        for (int i = 0; i < devices.size(); i++) {
            System.out.println(devices.get(i));
        }
    }

    private Devices cursorToDevice(Cursor c, boolean multipleResult)
    {
        if(!multipleResult) {
            c.moveToFirst();
        }
        Devices device = new Devices();
        device.setId(c.getInt(0));
        device.setName(c.getString(1));
        device.setPower(c.getString(2));
        return device;
    }

}
