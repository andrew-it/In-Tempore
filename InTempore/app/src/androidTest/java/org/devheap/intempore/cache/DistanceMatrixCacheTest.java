package org.devheap.intempore.cache;

import android.util.Pair;

import com.google.maps.model.Distance;
import com.google.maps.model.Duration;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by mike on 10/13/17.
 */
public class DistanceMatrixCacheTest {
    @Test
    public void store() throws Exception {
        String from = "ChIJ4-it80yrXkERI6GK5y1usZs";
        String to = "ChIJ0zaR5RCtXkERA7GurKB4KEs";
        ArrayList<Pair<Distance, Duration>> data = new ArrayList<>();
        for(int i = 0; i < 24; i++) {
            Distance dist = new Distance();
            dist.inMeters = i * 124;
            dist.humanReadable = "" + dist.inMeters + " meters";
            Duration dur = new Duration();
            dur.inSeconds = i * 1345;
            dur.humanReadable = "" + dur.inSeconds + " seconds";
            data.add(Pair.create(dist, dur));
        }

        DistanceMatrixCache dmcache = new DistanceMatrixCache();
        dmcache.store(from, to, data);

        Pair<Distance, Duration>[] retrieved = dmcache.retrieve(from, to);
        assertEquals(data.size(), retrieved.length);
        for(int i = 0; i < data.size(); i++) {
            assertEquals(data.get(i).first.inMeters, retrieved[i].first.inMeters);
            assertEquals(data.get(i).second.inSeconds, retrieved[i].second.inSeconds);
        }
    }
}