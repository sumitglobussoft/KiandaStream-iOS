package com.kiandastream.database;


import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kiandastream.model.PlayingSongListmodel;



@SuppressLint("UseValueOf")
public class LocalData extends SQLiteOpenHelper {

	public static final String db_name = "kiandastream";

	public static final String table_name = "kiandasonglist";

	public static final String KEY_SongName = "songname";

	public static final String KEY_SongId = "songid";

	public static final String KEY_SongPath = "songpath";

	public static final String KEY_SongArtistName="songartistname";

	public static final String KEY_SongImagePath="songimagepath";
	//

	/*public static final String sch_table_name = "schedullertable";

	public static final String KEY_SchTID = "schtid";

	public static final String KEY_FeedText = "feedtext";

	public static final String KEY_FeedImagePath ="feedImagePath";

	public static final String KEY_FeedTime = "feedtimestamp";*/



	public LocalData(Context context) {

		super(context, db_name, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onCreate(db);
	}

	public void CreateTable() {

		String querry = "CREATE TABLE IF NOT EXISTS " + table_name + "("
				+ KEY_SongId + " TEXT," + KEY_SongName + " TEXT," + KEY_SongPath + " TEXT,"
				+ KEY_SongImagePath + " TEXT," + KEY_SongArtistName + " TEXT)";


		/*String querry2 = "CREATE TABLE IF NOT EXISTS " + sch_table_name + "("
				+ KEY_SchTID + " INTEGER,"+ KEY_UserID + " TEXT,"  + KEY_FeedText + " TEXT,"
				+ KEY_FeedImagePath + " TEXT," + KEY_FeedTime + " INTEGER)";*/


		SQLiteDatabase database = this.getWritableDatabase();

		database.execSQL(querry);
		//database.execSQL(querry2);

	}

	public void addNewSong(ModelUserDatas modelUserDatas) {

		// String query = "INSERT INTO " + table_name + "";
		SQLiteDatabase database = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_SongId, modelUserDatas.getSongid());
		contentValues.put(KEY_SongName, modelUserDatas.getSongname());
		contentValues.put(KEY_SongPath, modelUserDatas.getSongpath());
		contentValues.put(KEY_SongImagePath,modelUserDatas.getSongimagepath());
		contentValues.put(KEY_SongArtistName,modelUserDatas.getSongartistname());

		database.insert(table_name, null, contentValues);

		System.out.println("addNewUserAccount " + contentValues);

	}

	public ModelUserDatas getSongsData(String userId) {

		ModelUserDatas modelUserDatas = null;

		String query = "SELECT * FROM " + table_name + " WHERE " + KEY_SongId
				+ " = '" + userId + "'";

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		if (cursor.moveToFirst()) {

			modelUserDatas = new ModelUserDatas();
			modelUserDatas.setSongid(cursor.getString(0));
			modelUserDatas.setSongname(cursor.getString(1));
			modelUserDatas.setSongpath(cursor.getString(2));
			modelUserDatas.setSongimagepath(cursor.getString(3));
			modelUserDatas.setSongartistname(cursor.getString(4));
		}

		return modelUserDatas;
	}

	public void getAllSongs() {

		String query = "SELECT * FROM " + table_name;

		System.out.println(query);

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		ModelUserDatas modelUserDatas;
		//MainSingleTon.userdetails.clear();
		//MainSingleTon.useridlist.clear(); 
		if (cursor.moveToFirst()) {

			do {

				modelUserDatas = new ModelUserDatas();
				modelUserDatas.setSongid(cursor.getString(0));
				modelUserDatas.setSongname(cursor.getString(1));
				modelUserDatas.setSongpath(cursor.getString(2));
				modelUserDatas.setSongimagepath(cursor.getString(3));
				modelUserDatas.setSongartistname(cursor.getString(4));
				//MainSingleTon.userdetails.put(cursor.getString(0), modelUserDatas);
				//MainSingleTon.useridlist.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		


	}

	public void updateSongData(ModelUserDatas modelUserDatas) {

		SQLiteDatabase database = this.getWritableDatabase();

		String updateQuery = "UPDATE " + table_name + " SET "
				+ KEY_SongArtistName+ " = '" + modelUserDatas.getSongartistname()+"' , "
				+ KEY_SongImagePath+ " = '" + modelUserDatas.getSongimagepath()+"' , "
				+ KEY_SongName+ " = '" + modelUserDatas.getSongname() + "' , "
				+ KEY_SongPath + " = '" + modelUserDatas.getSongpath() + "' " + " WHERE "
				+ KEY_SongId + " = '" + modelUserDatas.getSongid() + "'";

		System.out.println(updateQuery);

		database.execSQL(updateQuery);
	}

	public HashMap<String, PlayingSongListmodel> getAllSonglist() {

		String query = "SELECT * FROM " + table_name;

		HashMap<String, PlayingSongListmodel> localsong=new HashMap<String, PlayingSongListmodel>();

		System.out.println(query);

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		PlayingSongListmodel modelUserDatas;

		if (cursor.moveToFirst()) {

			do {

				modelUserDatas = new PlayingSongListmodel();
				modelUserDatas.setSong_id(cursor.getString(0));
				modelUserDatas.setSong_name(cursor.getString(1));
				modelUserDatas.setSongurl(cursor.getString(2));
				modelUserDatas.setSong_image(cursor.getString(3));
				modelUserDatas.setSong_artist(cursor.getString(4));
				localsong.put(cursor.getString(0), modelUserDatas);

			} while (cursor.moveToNext());
		}

		return localsong;
	}



	public void deleteAllRows() {

		SQLiteDatabase database = this.getWritableDatabase();

		String query = "DELETE FROM " + table_name;
		System.out.println(query);
		database.execSQL(query);
	}

	public void deleteThisSong(String userID) {

		SQLiteDatabase database = this.getWritableDatabase();

		String query = "DELETE FROM " + table_name + " WHERE " + KEY_SongId
				+ " = " + userID;

		System.out.println(query);
		database.execSQL(query);

	}     



	// SCHEDULLED TWEET;

	/*public void addNewSchedulledTweet(LocalSongListmodel schTweetModel) {

		SQLiteDatabase database = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();

		contentValues.put(KEY_SchTID, schTweetModel.getFeedId());

		contentValues.put(KEY_UserID, schTweetModel.getUserID());

		contentValues.put(KEY_FeedText, schTweetModel.getFeedText());

		contentValues.put(KEY_FeedImagePath, schTweetModel.getFeedImagePath());

		contentValues.put(KEY_FeedTime, schTweetModel.getFeedtime());

		database.insert(sch_table_name, null, contentValues);

		System.out.println("addNewSchedulledTweet " + contentValues);

	}*/

	/*public LocalSongListmodel getSchedulledTweet(String schId) {

		LocalSongListmodel schFeedModel = null;

		String query = "SELECT * FROM " + sch_table_name + " WHERE "
				+ KEY_SchTID + " = '" + schId + "'";

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			
			int feedID = cursor.getInt(0);

			String userID = cursor.getString(1);

			String feedText = cursor.getString(2);

			String feedImagePath  = cursor.getString(3);

			Long feedtime  = new Long(cursor.getString(4));

			schFeedModel = new LocalSongListmodel(feedID, userID, feedText, feedImagePath, feedtime.longValue());


		}

		return schFeedModel;
	}*/

	/*public ArrayList<LocalSongListmodel> getAllSchedulledFeeds() {

		String query = "SELECT * FROM " + sch_table_name;

		ArrayList<LocalSongListmodel> allschTweets = new ArrayList<LocalSongListmodel>();

		System.out.println(query);

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		LocalSongListmodel schTweetModel;

		if (cursor.moveToFirst()) {

			do {
				
				int feedID =cursor.getInt(0);
				
				String userID = cursor.getString(1);

				String feedText = cursor.getString(2);

				String feedImagePath  = cursor.getString(3);

				Long feedtime  = new Long(cursor.getString(4));

				schTweetModel   = new LocalSongListmodel(feedID, userID, feedText, feedImagePath, feedtime.longValue());

				allschTweets.add(schTweetModel);


			} while (cursor.moveToNext());
		}

		return allschTweets;

	}
*/
	/*public void deleteThisPost(int schid) {

		SQLiteDatabase database = this.getWritableDatabase();

		String query = "DELETE FROM " + sch_table_name + " WHERE " + KEY_SchTID
				+ " = " + schid;

		System.out.println(query);

		database.execSQL(query);
			}*/

}
