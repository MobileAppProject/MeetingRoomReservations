package be.vdab.project.meetingroomreservations.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ReservationsContentProvider extends ContentProvider {

	private static final String AUTHORITY = "be.vdab.project.meetingroomreservations.ReservationsContentProvider";

	public static final int RESERVATIONS = 100;
	public static final int RESERVATION_ID = 110;

	public static final int MEETINGROOMS = 200;
	public static final int MEETINGROOM_ID = 210;


	private static final String RESERVATION_BASE_PATH = "reservation";
	private static final String MEETINGROOM_BASE_PATH = "meetingRoom";

	public static final Uri CONTENT_URI_RESERVATION = Uri.parse("content://"
			+ AUTHORITY + "/" + RESERVATION_BASE_PATH);

	public static final Uri CONTENT_URI_MEETINGROOM = Uri.parse("content://"
			+ AUTHORITY + "/" + MEETINGROOM_BASE_PATH);

	private static final UriMatcher uRIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		uRIMatcher.addURI(AUTHORITY, RESERVATION_BASE_PATH, RESERVATIONS);
		uRIMatcher.addURI(AUTHORITY, RESERVATION_BASE_PATH + "/#", RESERVATION_ID);
		uRIMatcher.addURI(AUTHORITY, MEETINGROOM_BASE_PATH, MEETINGROOMS);
		uRIMatcher.addURI(AUTHORITY, MEETINGROOM_BASE_PATH + "/#", MEETINGROOM_ID);
	}

	private Database db;

	@Override
	public boolean onCreate() {
		db = new Database(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = uRIMatcher.match(uri);
		if (uriType != RESERVATIONS && uriType != RESERVATION_ID && uriType != MEETINGROOMS && uriType != MEETINGROOM_ID) {
			throw new IllegalArgumentException("Invalid URI for delete");
		}
	    SQLiteDatabase sqlDB = db.getWritableDatabase();
	    int rowsAffected = 0;
	    String id;
	    switch (uriType) {
	    case RESERVATIONS:
	        rowsAffected = sqlDB.delete(DB.RESERVATIONS.TABLE,
	                selection, selectionArgs);
	        break;
	    case RESERVATION_ID:
	        id = uri.getLastPathSegment();
	        if (TextUtils.isEmpty(selection)) {
	            rowsAffected = sqlDB.delete(DB.RESERVATIONS.TABLE,
	            		DB.RESERVATIONS.ID + "=" + id, null);
	        } else {
	            rowsAffected = sqlDB.delete(DB.RESERVATIONS.TABLE,
	                    selection + " and " + DB.RESERVATIONS.ID + "=" + id,
	                    selectionArgs);
	        }
	        break;
	    case MEETINGROOMS:
	        rowsAffected = sqlDB.delete(DB.MEETINGROOMS.TABLE,
	                selection, selectionArgs);
	        break;
	    case MEETINGROOM_ID:
	        id = uri.getLastPathSegment();
	        if (TextUtils.isEmpty(selection)) {
	            rowsAffected = sqlDB.delete(DB.MEETINGROOMS.TABLE,
	            		DB.MEETINGROOMS.ID + "=" + id, null);
	        } else {
	            rowsAffected = sqlDB.delete(DB.MEETINGROOMS.TABLE,
	                    selection + " and " + DB.MEETINGROOMS.ID + "=" + id,
	                    selectionArgs);
	        }
	        break;
	    default:
	        throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsAffected;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Uri insert(Uri uri, ContentValues values) {
		int uriType = uRIMatcher.match(uri);
		if (uriType != RESERVATIONS && uriType != MEETINGROOMS) {
			throw new IllegalArgumentException("Invalid URI for insert");
		}
		SQLiteDatabase sqlDB = db.getWritableDatabase();
		long newID = 0;
		if (uriType == RESERVATIONS) {
			newID = sqlDB.insertWithOnConflict(DB.RESERVATIONS.TABLE, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);
		}
		if (uriType == MEETINGROOMS) {
			newID = sqlDB.insertWithOnConflict(DB.MEETINGROOMS.TABLE, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);
		}
		if (newID > 0) {
			Uri newUri = ContentUris.withAppendedId(uri, newID);
			getContext().getContentResolver().notifyChange(uri, null);
			return newUri;
		} else {
			throw new SQLException("Failed to insert row into " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		int uriType = uRIMatcher.match(uri);
		switch (uriType) {
		case RESERVATION_ID:
			queryBuilder.setTables(DB.RESERVATIONS.TABLE);
			queryBuilder.appendWhere(DB.RESERVATIONS.ID + "="
					+ uri.getLastPathSegment());
			break;
		case RESERVATIONS:
			queryBuilder.setTables(DB.RESERVATIONS.TABLE);
			// no filter
			break;
		case MEETINGROOM_ID:
			queryBuilder.setTables(DB.MEETINGROOMS.TABLE);
			queryBuilder.appendWhere(DB.MEETINGROOMS.ID + "="
					+ uri.getLastPathSegment());
			break;
		case MEETINGROOMS:
			queryBuilder.setTables(DB.MEETINGROOMS.TABLE);
			// no filter
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		Cursor cursor = queryBuilder.query(db.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		SQLiteDatabase sqlDB = db.getWritableDatabase();
		switch (uRIMatcher.match(uri)) {
		case RESERVATION_ID:
			count = sqlDB.update(DB.RESERVATIONS.TABLE, values,
					DB.RESERVATIONS.ID
							+ " = "
							+ uri.getPathSegments().get(1)
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		case RESERVATIONS:
			count = sqlDB.update(DB.RESERVATIONS.TABLE, values, selection,
					selectionArgs);
			break;
		case MEETINGROOM_ID:
			count = sqlDB.update(DB.MEETINGROOMS.TABLE, values,
					DB.MEETINGROOMS.ID
							+ " = "
							+ uri.getPathSegments().get(1)
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		case MEETINGROOMS:
			count = sqlDB.update(DB.MEETINGROOMS.TABLE, values, selection,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
}
