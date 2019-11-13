package com.zafaco.common;

/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2019                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2019-01-03
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.json.JSONObject;

/**
 * class DBHelper.java
 * has methods to create database and tables and to add or get entities from tables
 */
public class Database extends SQLiteOpenHelper
{
	/**************************** Variables ****************************/

	Context ctx;

	//Module Objects
	private Tool mTool;
	private SQLiteDatabase mDatabase;

	private boolean attachFlag = false;

	private static int DATABASE_VERSION = 1;
	private static String TABLE_NAME = "";

	/*******************************************************************/
	
	/**
	 * standard constructor to create database
	 * @param context Context of App
	 * @param database_name Context of App
	 * @param table_name Context of App
	 */
	public Database(Context context, String database_name, String table_name)
	{
		super(context, database_name, null, DATABASE_VERSION);

		ctx = context;

		mTool = new Tool();

		TABLE_NAME = table_name;

		LinkedHashMap<String, String> keys = new LinkedHashMap<>();
		keys.put("timestamp","");

		createDB(keys);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
		catch (Exception ex)
		{
			Log.e("DBHelper", "### Exception in onUpgrade() ###");
			mTool.printTrace(ex);
		}
	}

	public void attachDB( String attDB, String attTable )
	{
		try
		{
			mDatabase = getWritableDatabase();
			mDatabase.execSQL("ATTACH DATABASE '" + ctx.getDatabasePath(attDB).toString() + "' AS "+attDB );
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}

		TABLE_NAME = TABLE_NAME +","+attDB+"."+attTable;

		attachFlag = true;
	}

	public void detachDB( String attDB, String attTable )
	{
		//if no attach, then do not detach
		if( !attachFlag )
			return;

		try
		{
			mDatabase = getWritableDatabase();
			mDatabase.execSQL("DETACH DATABASE " + attDB);
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}

		TABLE_NAME = attTable;
	}

	public void createDB( LinkedHashMap<String, String> keys )
	{
		mDatabase = getWritableDatabase();

		try
		{
			String createTable 	= "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + " (";

			createTable += "id INTEGER PRIMARY KEY AUTOINCREMENT, ";

			for (Map.Entry<String, String> entry : keys.entrySet())
			{
				createTable += entry.getKey()+" TEXT,";
			}

			createTable = createTable.substring(0,createTable.length()-1);

			createTable += ")";

			//DEBUG
			//mTool.printOutput(createTable);

			mDatabase.execSQL(createTable);
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}

		checkColumns(keys);

		mDatabase.close();
	}

	public void createDB( JSONObject keys )
	{
		mDatabase = getWritableDatabase();

		try
		{
			String createTable 	= "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + " (";

			createTable += "id INTEGER PRIMARY KEY AUTOINCREMENT, ";

			for( Iterator<String> iter = keys.keys(); iter.hasNext(); )
			{
				createTable += iter.next()+" TEXT,";
			}

			createTable = createTable.substring(0,createTable.length()-1);

			createTable += ")";

			//DEBUG
			//mTool.printOutput(createTable);

			mDatabase.execSQL(createTable);
		}
		catch (Exception ex)
		{
			mTool.printOutput("### Exception in createDB() ###");
			mTool.printTrace(ex);
		}

		checkColumns(keys);

		mDatabase.close();
	}

	private void checkColumns( LinkedHashMap<String, String> keys )
	{
		mDatabase = getWritableDatabase();

		try
		{
			ArrayList<String> tmp = new ArrayList<>();

			String pragmaTable 	= "PRAGMA table_info(" + TABLE_NAME + ")";

			Cursor result = mDatabase.rawQuery(pragmaTable, null);

			while( result.moveToNext () )
			{
				tmp.add(result.getString(1));
			}

			result.close();

			for( Map.Entry<String,String> entry : keys.entrySet())
			{
				//Search for element, if not found -> alter table
				if (tmp.indexOf(entry.getKey()) == -1)
				{
					String alterTable = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + entry.getKey() + " TEXT ";

					mTool.printOutput("Alter Query: "+alterTable);

					mDatabase.execSQL(alterTable);
				}
			}
		}
		catch (Exception ex) { mTool.printTrace(ex); }

		mDatabase.close();
	}

	private void checkColumns( JSONObject keys )
	{
		mDatabase = getWritableDatabase();

		try
		{
			ArrayList<String> tmp = new ArrayList<>();

			String pragmaTable 	= "PRAGMA table_info(" + TABLE_NAME + ")";

			Cursor result = mDatabase.rawQuery(pragmaTable, null);

			while( result.moveToNext () )
			{
				tmp.add(result.getString(1));
			}

			result.close();

			for( Iterator<String> iter = keys.keys(); iter.hasNext(); )
			{
				String key = iter.next();

				//Search for element, if not found -> alter table
				if (tmp.indexOf(key) == -1)
				{
					String alterTable = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + key + " TEXT ";

					mTool.printOutput("Alter Query: "+alterTable);

					mDatabase.execSQL(alterTable);
				}
			}
		}
		catch (Exception ex) { mTool.printTrace(ex); }

		mDatabase.close();
	}

	public void insert(LinkedHashMap<String, String> aData)
	{
		String sql = "";
		String keys = "";
		String values = "";

		sql += "INSERT INTO "+ TABLE_NAME + " ";


		for (Map.Entry<String, String> entry : aData.entrySet())
		{
			try
			{
				keys += entry.getKey()+",";
				values += "?,";
			}
			catch(Exception ex) { mTool.printTrace(ex); }
		}

		keys = keys.substring(0,keys.length()-1);
		values = values.substring(0,values.length()-1);
		sql += "("+keys+") VALUES ("+values+");";

		//DEBUG
		//mTool.printOutput(sql);

		mDatabase = getWritableDatabase();
		mDatabase.beginTransaction();
		SQLiteStatement stmt = mDatabase.compileStatement(sql);

		int i = 1;
		for (Map.Entry<String, String> entry : aData.entrySet())
		{
			try
			{
				//DEBUG
				//mTool.printOutput( entry.getKey() + " = " + entry.getValue());

				String tmp = ""+entry.getValue();

				tmp = tmp.trim();

				//Attention: on the "bind-function, the index starts at 1, not 0"
				stmt.bindString(i++, tmp);
			}
			catch(Exception ex) { mTool.printTrace(ex); }
		}

		stmt.executeInsert();
		stmt.clearBindings();

		mDatabase.setTransactionSuccessful();
		mDatabase.endTransaction();

		mDatabase.close();
	}

	public void insert(JSONObject aData)
	{
		String sql = "";
		String keys = "";
		String values = "";

		sql += "INSERT INTO "+ TABLE_NAME + " ";

		for (Iterator<String> iter = aData.keys(); iter.hasNext(); )
		{
			keys += iter.next()+",";
			values += "?,";
		}

		keys = keys.substring(0,keys.length()-1);
		values = values.substring(0,values.length()-1);
		sql += "("+keys+") VALUES ("+values+");";

		//DEBUG
		//mTool.printOutput(sql);

		mDatabase = getWritableDatabase();
		mDatabase.beginTransaction();
		SQLiteStatement stmt = mDatabase.compileStatement(sql);

		int i = 1;
		for (Iterator<String> iter = aData.keys(); iter.hasNext(); )
		{
			try
			{
				String key = iter.next();

				//DEBUG
				//mTool.printOutput( key + " = " + aData.getString(key));

				String tmp = ""+aData.getString(key);

				tmp = tmp.trim();

				//Attention: on the "bind-function, the index starts at 1, not 0"
				stmt.bindString(i++, tmp);
			}
			catch(Exception ex) { mTool.printTrace(ex); }
		}

		stmt.executeInsert();
		stmt.clearBindings();

		mDatabase.setTransactionSuccessful();
		mDatabase.endTransaction();

		mDatabase.close();
	}

	public int update(ContentValues cValues, int nId)
	{
		int retCode = 0;

		mDatabase = this.getWritableDatabase();

		if(mDatabase != null)
		{
			retCode = mDatabase.update(TABLE_NAME,cValues,"id="+String.valueOf(nId), null);

			mDatabase.close();
		}

		return retCode;
	}

    public int deleteID(String column, String value)
	{
        int retCode = 0;

        mDatabase = this.getWritableDatabase();

        if(mDatabase != null && !value.equals("0") && !value.equals(""))
		{
			String[] args = {value};
			retCode = mDatabase.delete(TABLE_NAME,column+"=?", args);

			mDatabase.close();
		}

		return retCode;
    }

	/**
	 * get all entities from "resultsets" table of local database
	 * @return allRes List of Result.java objects
	 */
	public ArrayList<LinkedHashMap> select(String where, String order, int asc)
	{
		ArrayList<LinkedHashMap> rows = new ArrayList<>();

		try
		{
			String query = "SELECT * FROM " + TABLE_NAME + " WHERE "+ where +" ORDER BY " + order + " " + ((asc == 1) ? "ASC" : "DESC");

			//Log.e("SQL-Query",query);

			mDatabase = this.getWritableDatabase();

			Cursor result = mDatabase.rawQuery(query, null);

			while (result.moveToNext())
			{
				LinkedHashMap<String, String> columns = new LinkedHashMap<>();

				for (int i = 0; i < result.getColumnCount(); i++)
				{
					//mTool.printOutput("DB[-]: " + result.getColumnName(i) + ":" + result.getString(i));
					columns.put(result.getColumnName(i), result.getString(i));
				}
				rows.add(columns);
			}

			result.close();

			mDatabase.close();
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}

		return rows;
	}

	/**
	 * get all entities from "resultsets" table of local database
	 * @return allRes List of Result.java objects
	 */
	public ArrayList<LinkedHashMap> selectAll(String order, int asc)
	{
		ArrayList<LinkedHashMap> rows = new ArrayList<>();

		try
		{
			String query = "SELECT * FROM " + TABLE_NAME + " WHERE 1 ORDER BY " + order + " " + ((asc == 1) ? "ASC" : "DESC");

			//Log.e("SQL-Query",query);

			mDatabase = this.getWritableDatabase();

			Cursor result = mDatabase.rawQuery(query, null);

			while (result.moveToNext())
			{
				LinkedHashMap<String, String> columns = new LinkedHashMap<>();

				for (int i = 0; i < result.getColumnCount(); i++)
				{
					//mTool.printOutput("DB[-]: " + result.getColumnName(i) + ":" + result.getString(i));
					columns.put(result.getColumnName(i), result.getString(i));
				}
				rows.add(columns);
			}

			result.close();

			mDatabase.close();
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}

		return rows;
	}

	/**
	 * get all entities from "resultsets" table of local database
	 * @return Cursor
	 */
	public Cursor selectAllCursor(String order, int asc)
	{
		String ascDesc = ((asc == 1) ? "ASC" : "DESC");
		String query = "SELECT *,id as _id FROM " + TABLE_NAME + " WHERE 1 ORDER BY "+order+" "+ascDesc;

		mDatabase = this.getWritableDatabase();

		Cursor cDatabaseCursor = mDatabase.rawQuery(query, null);

		return cDatabaseCursor;
	}

	public String getValue(Cursor cCursor, String sColumn)
	{
		return cCursor.getString(cCursor.getColumnIndexOrThrow(sColumn));
	}

	public void printCursor(Cursor cData)
	{
		DatabaseUtils.dumpCursor(cData);
	}

	public int getLastID()
	{
		int nLastID = 0;
		try
		{
			String query = "SELECT MAX(id) as id FROM " + TABLE_NAME;

			//mTool.printOutput("SQL-Query: " +query);

			mDatabase = getWritableDatabase();

			Cursor result = mDatabase.rawQuery(query, null);

			result.moveToFirst();

			nLastID = result.getInt(0);

			result.close();

			mDatabase.close();
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}

		return nLastID;
	}

	public int getLastTrack()
	{
		int nLastID = 0;
		try
		{
			String query = "SELECT COUNT(track_id) FROM (SELECT  * FROM " + TABLE_NAME + " GROUP BY track_id ) as tbl";

			//mTool.printOutput("SQL-Query: " +query);

			mDatabase = getWritableDatabase();

			Cursor result = mDatabase.rawQuery(query, null);

			result.moveToFirst();

			nLastID = result.getInt(0);

			result.close();

			mDatabase.close();
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}

		return nLastID;
	}

	public void close()
	{
		mDatabase.close();
	}
}
