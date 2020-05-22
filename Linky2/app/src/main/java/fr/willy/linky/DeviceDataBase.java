package fr.willy.linky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

/**
 * Création d'une nouvelle classe. Elle va nous permettre de gérer
 * l'insertion, la suppression, la modification des équipements électriques
 */
public class DeviceDataBase {

    private static final int DATABASE_VERSION       = 1;
    private static final    String DBNAME              = "device.db";
    private static final    String DEVICE_TABLE_NAME   = "device";

    private static final    String ID                  = "id";
    private static final    String NAME                = "name";
    private static final    String POWER               = "power";
    private static final    String STANDBY_POWER       = "standby_power";
    private static final    String MEAN_POWER          = "mean_power";
    private static final    String USERATE             = "userate";


    private                 SQLiteDatabase      bdd;
    private                 DeviceOpenHelper    deviceBaseSQLite;

    public DeviceDataBase(Context context)
    {
        deviceBaseSQLite = new DeviceOpenHelper(context, DBNAME, null, DATABASE_VERSION);
    }

    /**
     * Constructeur : on ouvre la Base de données en écriture
     * ---------------------------------------
     */
    public void open()
    {
        bdd = deviceBaseSQLite.getWritableDatabase();
    }

    /**
     * on ferme l'accès à la Base de données
     * --------------------------------------
     */
    public void close()
    {
        bdd.close();
    }



    /**
     * on récupère la base de données
     * ------------------------------
     */
    public SQLiteDatabase getBDD()
    {
        return bdd;
    }

    /**
     * on insere un appareil électrique dans la base de données
     * --------------------------------------------------------
     */
    public long insert(Devices device)
    {
        // Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();

        //on lui ajoute une valeur associée à une clé
        // (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(ID,    device.getId());
        values.put(NAME,  device.getName());
        values.put(POWER, device.getPower());
        values.put(STANDBY_POWER, device.getStandbyPower());
        values.put(MEAN_POWER, device.getMeanPower());
        values.put(USERATE, device.getUseRate());

        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(DEVICE_TABLE_NAME, null, values);
    }

    /**
     * on met à jour un appareil de la base de données
     * -----------------------------------------------
     */
    public int update(int id, Devices device)
    {
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put( ID,    device.getId());
        values.put( NAME,  device.getName());
        values.put( POWER, device.getPower());
        values.put(MEAN_POWER, device.getMeanPower());
        values.put(STANDBY_POWER, device.getStandbyPower());
        values.put(USERATE, device.getUseRate());

        return bdd.update(DEVICE_TABLE_NAME, values, ID + " = " +id, null);
    }

    /**
     * on supprime un appareil de la base de donnees à partir de son ID
     * ----------------------------------------------------------------
     */
    public int remove(int id)
    {
        return bdd.delete(DEVICE_TABLE_NAME, ID + " = " +id, null);
    }

    /**
     * on supprime tous les éléments
     * -----------------------------
     */
    public void removeAll()
    {
        bdd.execSQL("DELETE from "+ DEVICE_TABLE_NAME);
    }

    /**
     * on récupère la taille de la base de données
     * -------------------------------------------
     */
    public int getSize()
    {
        int i = 0;

        Cursor cursor = bdd.rawQuery("SELECT * from " + DEVICE_TABLE_NAME, null);
        if(cursor.getCount() != 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                i++;
            }
        }
        return i;

    }

    /**
     * on récupère un appareil en fonction de son id
     * ---------------------------------------------
     */
    public Devices selectWithID(String[] args)
    {
        Cursor cursor = bdd.rawQuery("SELECT * from " + DEVICE_TABLE_NAME + " WHERE id = ? ", args);
        Devices device = cursorToDevice(cursor, false);
        cursor.close();
        return device;
    }


    public Devices selectWithRowID(String[] args)
    {
        Cursor cursor = bdd.rawQuery("SELECT * from " + DEVICE_TABLE_NAME + " WHERE _id = ? ", args);
        Devices device = cursorToDevice(cursor, false);
        cursor.close();
        return device;
    }


    /**
     * Permet de Retourner un cursor sur tous les élements
     * ---------------------------------------------------------------------------------------------
     * SimpleCursorAdapter requires that the Cursor's result set must include a column named
     * exactly "_id". Don't haste to change schema if you didn't define the "_id" column in your
     * table. SQLite automatically added an hidden column called "rowid" for every table.
     * All you need to do is that just select rowid explicitly and alias it as '_id'
     * ---------------------------------------------------------------------------------------------
     */
    public Cursor return_cursor_bd()
    {
        Cursor cursor = bdd.rawQuery("SELECT rowid _id, * from " + DEVICE_TABLE_NAME, null);
        return(cursor) ;
    }

    /**
     * Permet de sélectionner dnas la base  de données tous les appareils
     * ------------------------------------------------------------------
     */
    public LinkedList<Devices> selectAll()
    {
        LinkedList<Devices> devices = new LinkedList<Devices>();
        Cursor cursor = bdd.rawQuery("SELECT * from " + DEVICE_TABLE_NAME, null);
        if (cursor.getCount() != 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Devices device = cursorToDevice(cursor, true);
                devices.add(device);
            }
        }
        cursor.close();
        return devices;
    }

    /**
     * Permet d'afficher tous les appareils sur la sortie stdout
     * ---------------------------------------------------------
     */
    public void displayDevices()
    {
        LinkedList<Devices> devices = selectAll();
        for (int i = 0; i < devices.size(); i++) {
            System.out.println(devices.get(i));
        }
    }

    /**
     * Permet de convertir un cursor en un appareil
     * --------------------------------------------
     */
    private Devices cursorToDevice(Cursor c, boolean multipleResult)
    {
        if(!multipleResult) {
            c.moveToFirst();
        }

        Devices device = new Devices();
        device.setId(c.getInt(0));
        device.setName(c.getString(1));
        device.setPower(c.getInt(2));
        device.setStandbyPower(c.getInt(3));
        device.setUseRate(c.getFloat(4));
        device.setMeanPower(c.getFloat(5));


        return device;
    }

}
