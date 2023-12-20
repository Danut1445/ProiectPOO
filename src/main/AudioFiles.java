package main;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;

import java.util.ArrayList;

public class AudioFiles {
    private String name;
    private int duration;

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final int getDuration() {
        return duration;
    }

    public final void setDuration(final int duration) {
        this.duration = duration;
    }
}

class Song extends AudioFiles {
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private int likes;

    Song(final SongInput song) {
        this.setName(song.getName());
        this.setDuration(song.getDuration());
        this.setAlbum(song.getAlbum());
        this.setArtist(song.getArtist());
        this.setLyrics(song.getLyrics());
        this.setGenre(song.getGenre());
        this.setReleaseYear(song.getReleaseYear());
        this.setTags(new ArrayList<String>(song.getTags()));
    }
    public String getAlbum() {
        return album;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(final int likes) {
        this.likes = likes;
    }
}

class Episode extends AudioFiles {
    private String description;

    Episode(final EpisodeInput episode) {
        this.setName(episode.getName());
        this.setDuration(episode.getDuration());
        this.setDescription(episode.getDescription());
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
