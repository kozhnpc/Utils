package work.kozh.xutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

/**
 * 该类用于保存音乐模块的一些偏好设置
 */

public class MusicPreferencesUtils {

    private static final String ARTIST_SORT_ORDER = "artist_sort_order";
    private static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";
    private static final String ARTIST_ART_URL = "artist_art_url_";
    private static final String ALBUM_SORT_ORDER = "album_sort_order";
    private static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";
    private static final String SONG_SORT_ORDER = "song_sort_order";
    private static final String TOGGLE_ARTIST_GRID = "toggle_artist_grid";
    private static final String TOGGLE_ALBUM_GRID = "toggle_album_grid";
    private static final String TOGGLE_PLAYLIST_VIEW = "toggle_playlist_view";
    private static final String START_PAGE_INDEX = "start_page_index";
    private static MusicPreferencesUtils sInstance;

    private static volatile SharedPreferences mPreferences;

    public MusicPreferencesUtils(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static MusicPreferencesUtils getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (MusicPreferencesUtils.class) {
                if (sInstance == null) {
                    sInstance = new MusicPreferencesUtils(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public void setOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public boolean isArtistsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ARTIST_GRID, true);
    }

    public void setArtistsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_ARTIST_GRID, b);
        editor.apply();
    }

    public boolean isAlbumsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ALBUM_GRID, true);
    }

    public void setAlbumsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_ALBUM_GRID, b);
        editor.apply();
    }

    public int getStartPageIndex() {
        return mPreferences.getInt(START_PAGE_INDEX, 0);
    }

    @SuppressLint("StaticFieldLeak")
    public void setStartPageIndex(final int index) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(START_PAGE_INDEX, index);
                editor.apply();
                return null;
            }
        }.execute();
    }

    private void setSortOrder(final String key, final String value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(final String value) {
        setSortOrder(ARTIST_SORT_ORDER, value);
    }

    public final String getArtistSongSortOrder() {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z);
    }

    public final String getAlbumSortOrder() {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(final String value) {
        setSortOrder(ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
    }

    public void setAlbumSongSortOrder(final String value) {
        setSortOrder(ALBUM_SONG_SORT_ORDER, value);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(final String value) {
        setSortOrder(SONG_SORT_ORDER, value);
    }

    public void setArtistArt(long artistID, String jsonString) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(ARTIST_ART_URL + artistID, jsonString);
        editor.apply();
    }

    public String getArtistArt(long artistID) {
        return mPreferences.getString(ARTIST_ART_URL + artistID, "");
    }

    public int getPlaylistView() {
        return mPreferences.getInt(TOGGLE_PLAYLIST_VIEW, 0);
    }

    public void setPlaylistView(final int i) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(TOGGLE_PLAYLIST_VIEW, i);
        editor.apply();
    }


    /**
     * 下面的类是用于设置排序的
     **/

    public static class SortOrder {

        /**
         * This class is never instantiated
         */
        public SortOrder() {
        }

        /**
         * Artist sort order entries.  歌手排序规则
         */
        public interface ArtistSortOrder {
            /* Artist sort order A-Z */
            String ARTIST_A_Z = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;

            /* Artist sort order Z-A */
            String ARTIST_Z_A = ARTIST_A_Z + " DESC";

            /* Artist sort order number of songs */
            String ARTIST_NUMBER_OF_SONGS = MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                    + " DESC";

            /* Artist sort order number of albums */
            String ARTIST_NUMBER_OF_ALBUMS = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                    + " DESC";
        }

        /**
         * Album sort order entries.   专辑排序规则
         */
        public interface AlbumSortOrder {
            /* Album sort order A-Z */
            String ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

            /* Album sort order Z-A */
            String ALBUM_Z_A = ALBUM_A_Z + " DESC";

            /* Album sort order songs */
            String ALBUM_NUMBER_OF_SONGS = MediaStore.Audio.Albums.NUMBER_OF_SONGS
                    + " DESC";

        }

        /**
         * Song sort order entries.   歌曲排序规则
         */
        public interface SongSortOrder {
            /* Song sort order A-Z */
            String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

            /* Song sort order Z-A */
            String SONG_Z_A = SONG_A_Z + " DESC";

            /* Song sort order artist */
            String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;

            /* Song sort order album */
            String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;

            /* Song sort order duration */
            String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";

            /* Song sort order date */
            String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC";

            /* Song sort order filename */
            String SONG_FILENAME = MediaStore.Audio.Media.DATA;
        }

        /**
         * Album song sort order entries.  专辑中歌曲的排序规则
         */
        public interface AlbumSongSortOrder {
            /* Album song sort order A-Z */
            String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

            /* Album song sort order Z-A */
            String SONG_Z_A = SONG_A_Z + " DESC";

            /* Album song sort order track list */
            String SONG_TRACK_LIST = MediaStore.Audio.Media.TRACK + ", "
                    + MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

            /* Album song sort order duration */
            String SONG_DURATION = SongSortOrder.SONG_DURATION;

            /* Album Song sort order year */
            String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";

        }

        /**
         * Artist song sort order entries.  歌手中歌曲的排序规则
         */
        public interface ArtistSongSortOrder {
            /* Artist song sort order A-Z */
            String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        }

    }


}
