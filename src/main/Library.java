package main;

import fileio.input.SongInput;

import java.util.LinkedList;

public final class Library {
    private static final Library INSTANCE = new Library();
    private LinkedList<Song> songs = new LinkedList<Song>();

    LinkedList<LinkedList<User.Wrapped.SongListen>> list = new LinkedList<>();

    private Library() {
    }

    public static Library getInstance() {
        return INSTANCE;
    }

    /***
     * Adds a new song to the library
     * @param song
     */
    public void addSong(final SongInput song) {
        songs.addLast(new Song(song));
    }

    public LinkedList<Song> getSongs() {
        return songs;
    }

    public void setSongs(final LinkedList<Song> songs) {
        this.songs = songs;
    }

    /***
     * FInds the top5 Song with the most likes, or if there aren`t enough,
     * just return the current songs sorted by the number of likes
     * @param command
     * @return
     */
    public ResultTop5 top5Songs(final Command command) {
        final int nrmax = 5;
        ResultTop5 resultTop5 = new ResultTop5(command);
        LinkedList<Object> topSongs = new LinkedList<Object>();
        for (int i = 0; i < songs.size(); i++) {
            topSongs.addLast(songs.get(i));
        }
        BubbleSort bubbleSort = new BubbleSort();
        bubbleSort.setListOfObjects(topSongs);
        bubbleSort.setType("song");
        bubbleSort.sort();
        topSongs = bubbleSort.getListOfObjects();
        LinkedList<String> top5 = new LinkedList<String>();
        for (int i = 0; i < nrmax && i < topSongs.size(); i++) {
            top5.addLast(((Song) topSongs.get(i)).getName());
        }
        resultTop5.setResult(top5);
        return resultTop5;
    }

    public LinkedList<LinkedList<User.Wrapped.SongListen>> getList() {
        return list;
    }

    public void setList(LinkedList<LinkedList<User.Wrapped.SongListen>> list) {
        this.list = list;
    }
}
