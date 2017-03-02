package test.com.test.util;

import java.io.ByteArrayOutputStream;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DB";

    // Table Names
    private static final String DB_TABLE = "images";

    // column names
    private static final String KEY_NAME = "image_name";
    private static final String KEY_IMAGE = "image_data";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+ 
                       KEY_NAME + " TEXT," + 
                       KEY_IMAGE + " BLOB);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating table
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        // create new table
        onCreate(db);
    }
    public void addEntry( String name, Bitmap image) throws SQLiteException{
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_NAME,    name);
        cv.put(KEY_IMAGE,   getBytes(image));
        database.insertWithOnConflict(DB_TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }
    
    public Bitmap getBitmap(String url){
    	
    	Bitmap b=null;
    	try{
    		SQLiteDatabase db = this.getWritableDatabase();
    	    String sql              =   "SELECT * FROM "+DB_TABLE+" WHERE "+KEY_NAME +" = ?";
    	    Cursor cursor           =   db.rawQuery(sql, new String[] {url});

    	    
	    	 if(cursor.moveToFirst()){
	    	    	 byte[] image = cursor.getBlob(1);
	    			 b=getImage(image);
	    	 }
    	    
    	    
    	    try{
	    	    if (cursor != null && !cursor.isClosed()) {
	    	        cursor.close();
	    	    }
	    	    db.close();
    	    }catch(Exception e){}
		   
    	}catch(Exception e){
    		return null;
    	}
    	return b;
    }
    
 // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}