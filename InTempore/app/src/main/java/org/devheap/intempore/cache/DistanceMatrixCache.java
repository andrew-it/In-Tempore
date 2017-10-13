package org.devheap.intempore.cache;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DistanceMatrixCache {
    SQLiteDatabase database;
    Gson gson = new Gson();
    Type dataType = new TypeToken<Pair<Distance, Duration>[]>(){}.getType();

    public class CacheEntry {
        public String from;
        public String to;
        public List<Pair<Distance, Duration>> data;

        public CacheEntry(String from, String to, List<Pair<Distance, Duration>> data) {
            this.from = from;
            this.to = to;
            this.data = data;
        }
    }

    public DistanceMatrixCache() {
        //String DB_PATH = "/data/data/com.devheap.intempore/databases/dmcache.db";
        String DB_PATH = "/sdcard/dmcache.db";
        database = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS DMCache(" +
                "from_id VARCHAR," +
                "to_id VARCHAR," +
                "json TEXT);");
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx ON DMCache (from_id, to_id);");
    }

    public void store(String from, String to, List<Pair<Distance, Duration>> data) {
        String json = gson.toJson(data);
        Object[] args = new Object[] {from, to, json};
        database.execSQL("REPLACE INTO DMCache VALUES(?, ?, ?);", args);
    }

    public Pair<Distance, Duration>[] retrieve(String from, String to) {
        String[] args = new String[] {from, to};
        Cursor resultSet = database.rawQuery("SELECT json FROM DMCache WHERE from_id=? AND to_id=?;", args);

        if(resultSet.getCount() <= 0)
            return null;

        resultSet.moveToFirst();
        String json = resultSet.getString(0);

        return gson.fromJson(json, dataType);
    }
}
