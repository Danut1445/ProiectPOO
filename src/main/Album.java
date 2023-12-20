package main;

import java.util.LinkedList;

public final class Album {
    private String username;
    private String name;
    private int releaseYear;
    private String description;
    private LinkedList<Song> songs = new LinkedList<Song>();
    private int nrlikes;

    /**
     * Checks if the current album contains any duplicate song
     * names
     * @return
     */
    public boolean containsduplicates() {
        boolean contains = false;
        for (int i = 0; i < songs.size(); i++) {
            for (int j = 0; j < songs.size(); j++) {
                if (i == j) {
                    continue;
                }
                if (songs.get(i).getName().equals(songs.get(j).getName())) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                break;
            }
        }
        return contains;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public LinkedList<Song> getSongs() {
        return songs;
    }

    public void setSongs(final LinkedList<Song> songs) {
        this.songs = songs;
    }

    public int getNrlikes() {
        return nrlikes;
    }

    public void setNrlikes(final int nrlikes) {
        this.nrlikes = nrlikes;
    }
}
