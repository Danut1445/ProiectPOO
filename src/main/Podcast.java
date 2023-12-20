package main;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;

import java.util.LinkedList;

public final class Podcast {
    private String name;
    private String owner;
    private LinkedList<Episode> episodes = new LinkedList<Episode>();

    public Podcast(final PodcastInput podcastInput) {
        this.name = podcastInput.getName();
        this.owner = podcastInput.getOwner();
    }

    public Podcast() {
    }

    /**
     * Function that checks to see if there are any duplicate
     * episodes in the current Podcast Object
     * @return
     */
    public boolean containsduplicates() {
        boolean contains = false;
        for (int i = 0; i < episodes.size(); i++) {
            for (int j = 0; j < episodes.size(); j++) {
                if (i == j) {
                    continue;
                }
                if (episodes.get(i).getName().equals(episodes.get(j).getName())) {
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public LinkedList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final LinkedList<Episode> episodes) {
        this.episodes = episodes;
    }

    /**
     * Add a new elemnt to the episodes list
     * @param episode
     */
    public void addEpisodes(final EpisodeInput episode) {
        episodes.addLast(new Episode(episode));
    }
}
