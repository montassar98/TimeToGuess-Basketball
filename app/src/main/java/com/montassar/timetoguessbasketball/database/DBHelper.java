package com.montassar.timetoguessbasketball.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBHelper extends SQLiteAssetHelper {

    private static final String DB_NAME="basketball.db";
    private static final int DB_VERSION=1;
    // ===== players table =======
    private static final String TAB_PLAYERS="players";
    private static final String TAB_COINS = "coins";
    private static final String TAB_HELPS = "helps";

    // columns  of players table
    private static final String COL_ID="id";
    private static final String COL_ANSWER="answer";
    private static final String COL_IMG="img";
    private static final String COL_TRIES="tries";
    private static final String COL_COMPLETED="completed";

    private static final String COL_ID_COIN="id_coin";
    private static final String COL_TOTAL_COINS="total_coins";
    private static final String COL_USED_COINS="used_coins";

    public static final String HELPS_ID="he_id";
    public static final String HELPS_PLAYER="he_player";
    public static final String HELPS_HIDE="he_hide";
    public static final String HELPS_SOLUTION="he_solution";

    private Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }
    public String getAnswer(int id)
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT "+COL_ANSWER+" FROM "+TAB_PLAYERS+" WHERE "+COL_ID+"='"+id+"'";
            Cursor cursor = db.rawQuery(query,null);
            if (cursor.getCount()!=0)
            {
                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex(COL_ANSWER));
            }else{
               // Toast.makeText(context, "Check Query in getAnswer", Toast.LENGTH_SHORT).show();
                return null;
            }

        }catch (SQLException e){
           // Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    public String getImageName(int id)
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query="SELECT "+COL_IMG+" FROM "+TAB_PLAYERS+" WHERE "+COL_ID+"='"+id+"'";
            Cursor cursor = db.rawQuery(query,null);
            if (cursor.getCount()!=0)
            {
                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex(COL_IMG));

            }else {
                //Toast.makeText(context, "Check query in getImage", Toast.LENGTH_SHORT).show();
                return null;
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
    public void setPlayerCompleted(int playerID)
    {
        try {
            SQLiteDatabase db =getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_COMPLETED,1);
            db.update(TAB_PLAYERS,values,COL_ID+"=?",new String[]{String.valueOf(playerID)});

        }catch (SQLException e){
            e.printStackTrace();
            // Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public int getNextPlayerID()
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT "+COL_ID+" FROM "+TAB_PLAYERS+" WHERE "+COL_COMPLETED+"=0 ORDER BY "+COL_ID + " ASC LIMIT 1";
            Cursor cursor = db.rawQuery(query,null);
            if (cursor.getCount() != 0 )
            {
                cursor.moveToFirst();
                return cursor.getInt(cursor.getColumnIndex(COL_ID));
            }else {
               // Toast.makeText(context, "error in query on getNextPlayerID", Toast.LENGTH_LONG).show();
                return 0;
            }

        }catch (SQLException e){
           e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            return 0;
        }
    }
    public void setTries(int id, int playerTries)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_TRIES,playerTries);
            db.update(TAB_PLAYERS,values,COL_ID+"=?",new String[]{String.valueOf(id)});

        }catch (SQLException e){
           e.printStackTrace();
            // Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void addTotalCoins(int coins)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String query = "UPDATE " + TAB_COINS + " SET " + COL_TOTAL_COINS + " = " + COL_TOTAL_COINS + " + " + coins + " WHERE " + COL_ID_COIN + " = 1";
            db.execSQL(query);
        }catch (SQLException e)
        {
            e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public Cursor getCoinsCount() {

        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT * " + " FROM " + TAB_COINS + " WHERE " + COL_ID_COIN+ " = 1";
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            return cursor;
        }catch (SQLException e)
        {
            e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }

    }
    public int getCoinsNum() {

        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT * " + " FROM " + TAB_COINS + " WHERE " + COL_ID_COIN+ " = 1";
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(COL_TOTAL_COINS));
        }catch (SQLException e)
        {
            e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            return 0;
        }

    }

    public Cursor getHelpState(int playerID)
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT * FROM "+TAB_HELPS+" WHERE "+HELPS_PLAYER+" = "+playerID;
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            return cursor;

        }catch (SQLException e)
        {
            e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void updateHelpState(String playerID,String helpField)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(helpField,1);
            int result = db.update(TAB_HELPS,values,HELPS_PLAYER+"=?",new String[]{playerID});
            if (result == 0)
            {
                values.put(HELPS_PLAYER,playerID);
                db.insert(TAB_HELPS,null,values);
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void addUsedCoins(String coins)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String query = "UPDATE "+TAB_COINS+" SET "+COL_USED_COINS+"="+COL_USED_COINS+"+"+coins+" WHERE "+COL_ID_COIN+"=1";
            db.execSQL(query);

        }catch (SQLException e)
        {
            e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void addRewardedCoins(String coins)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String query = "UPDATE "+TAB_COINS+" SET "+COL_TOTAL_COINS+"="+COL_TOTAL_COINS+"+"+coins+" WHERE "+COL_ID_COIN+"=1";
            db.execSQL(query);

        }catch (SQLException e)
        {
            e.printStackTrace();
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void resetGame()
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_TRIES,0);
            values.put(COL_COMPLETED,0);
            db.update(TAB_PLAYERS,values,null,null);

            ContentValues coinsValues = new ContentValues();
            coinsValues.put(COL_TOTAL_COINS,25);
            coinsValues.put(COL_USED_COINS,0);
            db.update(TAB_COINS,coinsValues,null,null);

            String query="DELETE FROM "+TAB_HELPS;
            db.execSQL(query);

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
