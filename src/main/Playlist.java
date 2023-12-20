package main;

import java.util.LinkedList;

public final class Playlist {
    private String name;
    private LinkedList<Song> songs = new LinkedList<Song>();
    private String user;
    private String privacy;
    private int followers;
    private int likes;

    public void setSongs(final LinkedList<Song> songs) {
        this.songs = songs;
    }

    public LinkedList<Song> getSongs() {
        return songs;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(final String privacy) {
        this.privacy = privacy;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(final int followers) {
        this.followers = followers;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(final int likes) {
        this.likes = likes;
    }

    /**
     * Remove a given song from the Playlist
     * @param song
     * @return
     */
    public int addRemove(final Song song) {
        for (int i = 0; i < songs.size(); i++) {
            Song currsong = songs.get(i);
            if (currsong.equals(song)) {
                songs.remove(i);
                return 0;
            }
        }
        songs.addLast(song);
        return 1;
    }
}
