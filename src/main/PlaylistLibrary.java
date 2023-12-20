package main;

import java.util.LinkedList;

public final class PlaylistLibrary {
    private static final PlaylistLibrary INSTANCE = new PlaylistLibrary();
    private LinkedList<Playlist> playlists = new LinkedList<Playlist>();

    private PlaylistLibrary() {
    }

    public LinkedList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(final LinkedList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public static PlaylistLibrary getInstance() {
        return INSTANCE;
    }

    /**
     * Resets the current playlist library
     */
    public void resetPlaylists() {
        playlists = new LinkedList<Playlist>();
    }

    /***
     * Returns the top 5 Playlists sorted by the number of followers
     * @param command
     * @return
     */
    public ResultTop5 top5Playlist(final Command command) {
        final int nrmax = 5;
        ResultTop5 resultTop5 = new ResultTop5(command);
        LinkedList<Object> topplaylists = new LinkedList<Object>();
        for (int i = 0; i < playlists.size(); i++) {
            topplaylists.addLast(playlists.get(i));
        }
        BubbleSort bubbleSort = new BubbleSort();
        bubbleSort.setListOfObjects(topplaylists);
        bubbleSort.setType("playlist");
        bubbleSort.sort();
        topplaylists = bubbleSort.getListOfObjects();
        LinkedList<String> top5 = new LinkedList<String>();
        for (int i = 0; i < nrmax && i < topplaylists.size(); i++) {
            top5.addLast(((Playlist) topplaylists.get(i)).getName());
        }
        resultTop5.setResult(top5);
        return resultTop5;
    }
}
