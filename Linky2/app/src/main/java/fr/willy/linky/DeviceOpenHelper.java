package fr.willy.linky;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper qui est une classe d'assistance pour gérer la création de bases de données et la gestion des versions
 * -------------------------------------------------------------------------------------------------
 */
public class DeviceOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 7;

    private static final String DEVICE_TABLE_NAME   = "device";

    private static final    String ID                  = "id";
    private static final    String ICON               = "icon";
    private static final    String NAME                = "name";
    private static final    String POWER               = "power";
    private static final    String STANDBY_POWER       = "standbypower";
    private static final    String MEAN_POWER          = "meanpower";
    private static final    String USERATE             = "userate";
    private static final    String DELETE              = "deleterequest";

    /**
     * Préparation Instruction pour créer la table
     * -------------------------------------------
     */
    private static final String DEVICE_TABLE_CREATE =
            "CREATE TABLE " + DEVICE_TABLE_NAME + " (" + ID  + " INTEGER PRIMARY KEY, " + ICON + " TEXT NOT NULL, " + NAME + " TEXT NOT NULL, "
                    + POWER + " TEXT NOT NULL, " + MEAN_POWER + " TEXT NOT NULL, " + STANDBY_POWER+ " TEXT NOT NULL, " + USERATE+ " TEXT NOT NULL, " + DELETE+ " TEXT NOT NULL);";

    /**
     * Constructeur
     * ------------
     */
    public DeviceOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, DATABASE_VERSION);
    }

    /**
     * On crée la table à partir de la requête écrite dans la variable CREATE_BDD
     * ---------------------------------------------------------------------------
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DEVICE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DEVICE_TABLE_NAME + ";");
        onCreate(db);
    }
}