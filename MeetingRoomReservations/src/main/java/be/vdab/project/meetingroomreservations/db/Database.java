package be.vdab.project.meetingroomreservations.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	private static final String TAG = "Database";

	private static final int DB_VERSION = 2;
	private static final String DB_NAME = "meetingroom_reservation_database";

	private static final String CREATE_TABLE_RESERVATIONS =
			"create table " + DB.RESERVATIONS.TABLE
			+ " ("
            + DB.RESERVATIONS.ID + " integer primary key autoincrement, "
		    + DB.RESERVATIONS.reservationId + " string, "
		    + DB.RESERVATIONS.meetingRoomId + " string, "
			+ DB.RESERVATIONS.beginDate + " string, "
			+ DB.RESERVATIONS.endDate + " string, "
		    + DB.RESERVATIONS.personName + " string, "
			+ DB.RESERVATIONS.description + " string, "
		    + DB.RESERVATIONS.active+ " string );";
	
	private static final String CREATE_TABLE_MEETINGROOMS =
			"create table " + DB.MEETINGROOMS.TABLE
			+ " ("
            + DB.RESERVATIONS.ID + " integer primary key autoincrement, "
		    + DB.MEETINGROOMS.meetingRoomId + " string, "
			+ DB.MEETINGROOMS.name + " string); ";

	public Database(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_RESERVATIONS);
		db.execSQL(CREATE_TABLE_MEETINGROOMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database. Existing contents will be lost. ["
				+ oldVersion + "]->[" + newVersion + "]");
		db.execSQL("DROP TABLE IF EXISTS " + DB.RESERVATIONS.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB.MEETINGROOMS.TABLE);
		onCreate(db);
	}

}
