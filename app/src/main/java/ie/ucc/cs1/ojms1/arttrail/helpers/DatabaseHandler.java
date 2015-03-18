package ie.ucc.cs1.ojms1.arttrail.helpers;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

import ie.ucc.cs1.ojms1.arttrail.R;

public class DatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ArtTrail.db";

    public static final String TABLE_ART = "Art";
    public static final String ART_ID = "_id";
    public static final String ART_NAME = "name";
    public static final String ART_ARTIST = "artist";
    public static final String ART_INFO = "art_info";
    public static final String ART_PIC = "picture";
    public static final String ART_LOCATION = "location";
    public static final String ART_LOC_INFO = "location_info";
    public static final String ART_VISITED = "visited";

    public static final String TABLE_BEACON = "Beacon";
    public static final String BEACON_ID = "_id";
    public static final String BEACON_MAJOR = "major";
    public static final String BEACON_MINOR = "minor";
    public static final String BEACON_AD = "advertisement";
    public static final String BEACON_ART_ID = "art_id";

    public static final String TABLE_GEOFENCE = "Geofence";
    public static final String GEOFENCE_ID = "_id";
    public static final String GEOFENCE_LAT = "latitude";
    public static final String GEOFENCE_LONG = "longitude";
    public static final String GEOFENCE_RADIUS = "radius";
    public static final String GEOFENCE_ART_ID = "art_id";

    private SQLiteDatabase db;
    private Cursor cursor;

    public DatabaseHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createArtTable = "CREATE TABLE "+ TABLE_ART + "(" +
                ART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ART_NAME + " TEXT NOT NULL, " +
                ART_ARTIST + " TEXT NOT NULL, " +
                ART_INFO + " TEXT, " +
                ART_PIC + " INTEGER, " +
                ART_LOCATION + " TEXT NOT NULL, " +
                ART_LOC_INFO + " TEXT, " +
                ART_VISITED + " INTEGER NOT NULL " +
                ");";
        db.execSQL(createArtTable);

        String createBeaconTable = "CREATE TABLE " + TABLE_BEACON + "(" +
                BEACON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BEACON_MAJOR + " INTEGER NOT NULL, " +
                BEACON_MINOR + " INTEGER NOT NULL, "+
                BEACON_AD + " TEXT, " +
                BEACON_ART_ID + " INTEGER, "+
                "FOREIGN KEY("+BEACON_ART_ID+") REFERENCES "+ TABLE_ART + "("+ART_ID+")" +
                ");";
        db.execSQL(createBeaconTable);

        String createGeofenceTable = "CREATE TABLE " + TABLE_GEOFENCE + "(" +
                GEOFENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GEOFENCE_LAT + " REAL NOT NULL, " +
                GEOFENCE_LONG + " REAL NOT NULL, " +
                GEOFENCE_RADIUS + " REAL NOT NULL, " +
                GEOFENCE_ART_ID + " INTEGER, " +
                "FOREIGN KEY("+GEOFENCE_ART_ID+") REFERENCES "+TABLE_ART + "("+ART_ID+")" +
                ");";
        db.execSQL(createGeofenceTable);

        insertSampleDataIntoArtTable(db);
        insertSampleDataIntoGeofenceTable(db);
        insertSampleDataIntoBeaconTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ART);
        onCreate(db);
    }

    private void insertSampleDataIntoArtTable(SQLiteDatabase db) {
        ContentValues values1 = new ContentValues();
        values1.put(ART_NAME, "Iron Giant");
        values1.put(ART_ARTIST, "Unknown person");
        values1.put(ART_INFO, "Created using GIMP 2.8. This piece is an example of Pixel Art.");
        values1.put(ART_PIC, R.drawable.iron_giant);
        values1.put(ART_LOCATION, "Brookfield UCC, Co. Cork");
        values1.put(ART_LOC_INFO, "UCC building.");
        values1.put(ART_VISITED, 0);

        ContentValues values2 = new ContentValues();
        values2.put(ART_NAME, "Mona Lisa");
        values2.put(ART_ARTIST, "Leonardo da Vinci");
        values2.put(ART_INFO, "Portrait of the famous Mona Lisa. Note the distinct lack of a smile.");
        values2.put(ART_PIC, R.drawable.mona_lisa);
        values2.put(ART_LOCATION, "WGB UCC, Co. Cork");
        values2.put(ART_LOC_INFO, "Western Gateway Building: computer science building.");
        values2.put(ART_VISITED, 0);

        ContentValues values3 = new ContentValues();
        values3.put(ART_NAME, "Girl With The Pearl Earring");
        values3.put(ART_ARTIST, "Johannes Vermeer");
        values3.put(ART_INFO, "Portrait of a girl with a pearl earring. Note the pearl earring.");
        values3.put(ART_PIC, R.drawable.pearl_earring);
        values3.put(ART_LOCATION, "UCC Main Gates, Co. Cork");
        values3.put(ART_LOC_INFO, "Main entrance to UCC.");
        values3.put(ART_VISITED, 0);

        ContentValues values4 = new ContentValues();
        values4.put(ART_NAME, "Starry Night");
        values4.put(ART_ARTIST, "Vincent van Gogh");
        values4.put(ART_INFO, "Van Gogh painted this after seeing a starry night.");
        values4.put(ART_PIC, R.drawable.starry_night);
        values4.put(ART_LOCATION, "St Patricks Church, Co. Cork");
        values4.put(ART_LOC_INFO, "A church where people can go to mass");
        values4.put(ART_VISITED, 0);

        ContentValues values5 = new ContentValues();
        values5.put(ART_NAME, "The Scream");
        values5.put(ART_ARTIST, "Edvard Munch");
        values5.put(ART_INFO, "We may never know what the man is screaming about. Spooky.");
        values5.put(ART_PIC, R.drawable.the_scream);
        values5.put(ART_LOCATION, "City Hall, Co. Cork");
        values5.put(ART_LOC_INFO, "City Hall is, well, City Hall.");
        values5.put(ART_VISITED, 0);

        db.insert(TABLE_ART, null, values1);
        db.insert(TABLE_ART, null, values2);
        db.insert(TABLE_ART, null, values3);
        db.insert(TABLE_ART, null, values4);
        db.insert(TABLE_ART, null, values5);

    }

    private void insertSampleDataIntoGeofenceTable(SQLiteDatabase db) {
        ContentValues values1 = new ContentValues();
        values1.put(GEOFENCE_LAT, 51.891242);
        values1.put(GEOFENCE_LONG, -8.500687);
        values1.put(GEOFENCE_RADIUS, 50);
        values1.put(GEOFENCE_ART_ID, 1); //Brookfield UCC

        ContentValues values2 = new ContentValues();
        values2.put(GEOFENCE_LAT, 51.893040);
        values2.put(GEOFENCE_LONG, -8.500363);
        values2.put(GEOFENCE_RADIUS, 100);
        values2.put(GEOFENCE_ART_ID, 2); //WGB UCC

        ContentValues values3 = new ContentValues();
        values3.put(GEOFENCE_LAT, 51.8955202);
        values3.put(GEOFENCE_LONG, -8.48896);
        values3.put(GEOFENCE_RADIUS, 30);
        values3.put(GEOFENCE_ART_ID, 3); //Main Gates UCC

        ContentValues values4 = new ContentValues();
        values4.put(GEOFENCE_LAT, 51.901468);
        values4.put(GEOFENCE_LONG, -8.463639);
        values4.put(GEOFENCE_RADIUS, 100);
        values4.put(GEOFENCE_ART_ID, 4); //St. Patrick's Church

        ContentValues values5 = new ContentValues();
        values5.put(GEOFENCE_LAT, 51.897379);
        values5.put(GEOFENCE_LONG, -8.465723);
        values5.put(GEOFENCE_RADIUS, 30);
        values5.put(GEOFENCE_ART_ID, 5); //City Hall

        db.insert(TABLE_GEOFENCE, null, values1);
        db.insert(TABLE_GEOFENCE, null, values2);
        db.insert(TABLE_GEOFENCE, null, values3);
        db.insert(TABLE_GEOFENCE, null, values4);
        db.insert(TABLE_GEOFENCE, null, values5);
    }

    private void insertSampleDataIntoBeaconTable(SQLiteDatabase db) {
        ContentValues values1 = new ContentValues();
        values1.put(BEACON_MAJOR, 11492); //blueberry pie
        values1.put(BEACON_MINOR, 17761); //blueberry pie
        values1.put(BEACON_ART_ID, 2);

        ContentValues values2 = new ContentValues();
        values2.put(BEACON_MAJOR, 24770); //mint cocktail
        values2.put(BEACON_MINOR, 63730); //mint cocktail
        values2.put(BEACON_ART_ID, 2);
        values2.put(BEACON_AD, "Your next purchase can be reduced by 20% if you show this.");

        db.insert(TABLE_BEACON, null, values1);
        db.insert(TABLE_BEACON, null, values2);
    }

    public Cursor getArtTableContents() {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_ART, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getArtDetails(int artId) {
        db = getReadableDatabase();
        String[] selectionArgs = {""+artId};
        cursor = db.rawQuery("SELECT * FROM " + TABLE_ART + " WHERE _id = ?", selectionArgs);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getLatLong(int artId) {
        db = getReadableDatabase();
        String[] selectionArgs = {""+artId};
        cursor = db.rawQuery("SELECT " + GEOFENCE_LAT +", " + GEOFENCE_LONG +
                             " FROM " + TABLE_GEOFENCE +
                             " WHERE _id = ?", selectionArgs);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getGeofences() {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_GEOFENCE + " JOIN " + TABLE_ART +
                             " ON " + TABLE_GEOFENCE+"."+GEOFENCE_ART_ID +"="+ TABLE_ART+"."+ART_ID, null);
        return cursor;
    }

    public Cursor getArtIdFromName(String artName) {
        db = getReadableDatabase();
        String[] selectionArgs = {artName};
        cursor = db.rawQuery("SELECT " + ART_ID + " FROM " + TABLE_ART + " WHERE " + ART_NAME + " = ?", selectionArgs );
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getBeaconArtId(String[] args) {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + BEACON_ART_ID + " FROM " + TABLE_BEACON +
                             " WHERE " + BEACON_MAJOR + "= ? AND " + BEACON_MINOR + "=?", args);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getBeaconAd(String[] args) {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT " + BEACON_AD + " FROM " + TABLE_BEACON +
                             " WHERE " + BEACON_MAJOR + "= ? AND " + BEACON_MINOR + "=?", args);
        cursor.moveToFirst();
        return cursor;
    }
}