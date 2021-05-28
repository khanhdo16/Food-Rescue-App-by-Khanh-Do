package com.example.foodrescue.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.example.foodrescue.model.Post;
import com.example.foodrescue.model.User;
import com.example.foodrescue.util.Util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_DATABASE = "CREATE TABLE " + Util.USER_TABLE_NAME + "(" + Util.USER_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + Util.USER_NAME + " TEXT, " + Util.USER_EMAIL +
            " TEXT UNIQUE, " + Util.USER_PHONE + " TEXT, " + Util.USER_ADDRESS + " TEXT, "
            + Util.USER_PASSWORD + " TEXT)";

        String CREATE_POST_DATABASE = "CREATE TABLE " + Util.POST_TABLE_NAME + "(" + Util.POST_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + Util.POST_IMAGE + " BLOB, " + Util.POST_TITLE +
            " TEXT, " + Util.POST_PRICE + " FLOAT, " + Util.POST_DES + " TEXT, " + Util.POST_DATE + " DATE, "
            + Util.POST_TIME + " TEXT, " + Util.POST_QUANTITY + " NUMERIC, "
            + Util.POST_LOCATION + " TEXT, " + Util.POST_USER + " TEXT)";

        db.execSQL(CREATE_USER_DATABASE);
        db.execSQL(CREATE_POST_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Util.USER_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + Util.POST_TABLE_NAME);
            onCreate(db);
        }
    }

    public long createUser (User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.USER_NAME, user.getName());
        contentValues.put(Util.USER_EMAIL, user.getEmail());
        contentValues.put(Util.USER_PHONE, user.getPhone());
        contentValues.put(Util.USER_ADDRESS, user.getAddress());
        contentValues.put(Util.USER_PASSWORD, user.getPassword());
        long newRowId = db.insert(Util.USER_TABLE_NAME, null, contentValues);
        db.close();
        return newRowId;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.USER_NAME, user.getName());
        contentValues.put(Util.USER_EMAIL, user.getEmail());
        contentValues.put(Util.USER_PHONE, user.getPhone());
        contentValues.put(Util.USER_ADDRESS, user.getAddress());

        return db.update (Util.USER_TABLE_NAME, contentValues, Util.USER_EMAIL + "=?",
            new String[] {user.getEmail()});
    }

    public int updateUserEmail(String currentEmail, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.USER_EMAIL, newEmail);


        int result = db.update (Util.USER_TABLE_NAME, contentValues, Util.USER_EMAIL + "=?",
            new String[] {currentEmail});

        contentValues.clear();
        contentValues.put(Util.POST_USER, newEmail);

        db.update (Util.POST_TABLE_NAME, contentValues, Util.POST_USER + "=?",
            new String[] {currentEmail});

        if (result > 0) {
            return 1;
        }
        else {
            contentValues.clear();
            contentValues.put(Util.USER_EMAIL, currentEmail);
            db.update(Util.USER_TABLE_NAME, contentValues, Util.USER_EMAIL + "=?",
                new String[]{newEmail});

            contentValues.clear();
            contentValues.put(Util.POST_USER, currentEmail);
            db.update(Util.POST_TABLE_NAME, contentValues, Util.POST_USER + "=?",
                new String[]{newEmail});
            return -1;
        }
    }

    public int updateUserPassword(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.USER_PASSWORD, user.getPassword());

        int result = updateUser(user);
        int resultPassword = db.update (Util.USER_TABLE_NAME, contentValues, Util.USER_EMAIL + "=?",
            new String[] {user.getEmail()});

        if (result > 0 && resultPassword > 0)
            return 1;
        else
            return -1;
    }

    public boolean checkUser (String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.USER_TABLE_NAME, new String[] {Util.USER_ID}, Util.USER_EMAIL + "=? and " + Util.USER_PASSWORD + "=?",
            new String[]{email, password}, null, null, null);
        int numberOfRows = cursor.getCount();
        db.close();

        if( numberOfRows == 1)
            return true;
        else
            return false;
    }

    public User fetchUser (String email) {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.USER_TABLE_NAME, null, Util.USER_EMAIL + "=?",
            new String[]{email}, null, null, null);

        if (cursor.moveToFirst()) {
            user = new User(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
            );
        }

        return user;
    }


    public long createPost (Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.POST_IMAGE, post.getImage());
        contentValues.put(Util.POST_TITLE, post.getTitle());
        contentValues.put(Util.POST_PRICE, post.getPrice());
        contentValues.put(Util.POST_DES, post.getDescription());
        contentValues.put(Util.POST_DATE, post.getDate());
        contentValues.put(Util.POST_TIME, post.getTime());
        contentValues.put(Util.POST_QUANTITY, post.getQuantity());
        contentValues.put(Util.POST_LOCATION, post.getLocation());
        contentValues.put(Util.POST_USER, post.getEmail());
        long newRowId = db.insert(Util.POST_TABLE_NAME, null, contentValues);
        db.close();
        return newRowId;
    }

    public int updatePost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.POST_IMAGE, post.getImage());
        contentValues.put(Util.POST_TITLE, post.getTitle());
        contentValues.put(Util.POST_PRICE, post.getPrice());
        contentValues.put(Util.POST_DES, post.getDescription());
        contentValues.put(Util.POST_DATE, post.getDate());
        contentValues.put(Util.POST_TIME, post.getTime());
        contentValues.put(Util.POST_QUANTITY, post.getQuantity());
        contentValues.put(Util.POST_LOCATION, post.getLocation());

        return db.update (Util.POST_TABLE_NAME, contentValues, Util.POST_ID + "=?",
            new String[] {Integer.toString(post.getId())});
    }

    public int deletePost(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Util.POST_TABLE_NAME, Util.POST_ID + "=?",
            new String[] {Integer.toString(id)});
    }


    public Post fetchPost(int id) {
        Post post = new Post();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.POST_TABLE_NAME, null, Util.POST_ID + "=?",
            new String[] {Integer.toString(id)}, null, null, null);

        if(cursor.moveToFirst())
        {
            post = new Post(
                cursor.getInt(0),
                cursor.getBlob(1),
                cursor.getString(2),
                cursor.getFloat(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getInt(7),
                cursor.getString(8),
                cursor.getString(9)
            );
        }

        return post;
    }

    public List<Post> fetchUserPosts(String email) {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Util.POST_TABLE_NAME, null, Util.POST_USER + "=?",
            new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Post post = new Post(
                    cursor.getInt(0),
                    cursor.getBlob(1),
                    cursor.getString(2),
                    cursor.getFloat(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getString(9)
                );
                postList.add(post);

            }while(cursor.moveToNext());
        }

        return postList;
    }

    public List<Post> fetchAllPosts() {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectAll = "SELECT * FROM " + Util.POST_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);
        if (cursor.moveToFirst()) {
            do {
                Post post = new Post(
                    cursor.getInt(0),
                    cursor.getBlob(1),
                    cursor.getString(2),
                    cursor.getFloat(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getString(9)
                );

                postList.add(post);

            }while(cursor.moveToNext());
        }

        return postList;
    }

    public byte[] imageToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public Bitmap bytesToImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
