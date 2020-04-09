package fr.willy.linky;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DeviceOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DEVICE_TABLE_NAME = "device";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String POWER = "power";

    private static final String DEVICE_TABLE_CREATE =
            "CREATE TABLE " + DEVICE_TABLE_NAME + " (" + ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL, "
                    + POWER + " TEXT NOT NULL;";

    public DeviceOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DEVICE_TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DEVICE_TABLE_NAME + ";");
        onCreate(db);
    }
}