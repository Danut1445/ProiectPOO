package main;

import java.util.LinkedList;

public final class PrintCurrentPage implements Visitor {
    /**
     * Prints the current page that the user is on
     * @param user
     * @param command
     * @return
     */
    public ResultSwitch visit(final User user, final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        BubbleSort bubbleSort = new BubbleSort();
        final int nr = 5;
        String page = "Liked songs:\n\t[";
        if (user.getCurrentPage().equals("HomePage")) {
            LinkedList<Object> likedsongs = new LinkedList<Object>();
            for (int i = 0; i < user.getPreferedSongs().size(); i++) {
                likedsongs.addLast(user.getPreferedSongs().get(i));
            }
            bubbleSort.setListOfObjects(likedsongs);
            bubbleSort.setType("song");
            bubbleSort.sort();
            for (int i = 0; i < nr && i < likedsongs.size(); i++) {
                if (i != 0) {
                    page += ", ";
                }
                page += ((Song) likedsongs.get(i)).getName();
            }
            page += "]\n\nFollowed playlists:\n\t[";
            LinkedList<Object> playlists = new LinkedList<Object>();
            for (int i = 0; i < user.getFollowed().size(); i++) {
                int likes = 0;
                for (int j = 0; j < user.getFollowed().get(i).getSongs().size(); j++) {
                    likes += user.getFollowed().get(i).getSongs().get(j).getLikes();
                }
                user.getFollowed().get(i).setLikes(likes);
                playlists.addLast(user.getFollowed().get(i));
            }
            bubbleSort.setListOfObjects(playlists);
            bubbleSort.setType("playlistlikes");
            bubbleSort.sort();
            for (int i = 0; i < nr && i < playlists.size(); i++) {
                if (i != 0) {
                    page += ", ";
                }
                page += ((Playlist) playlists.get(i)).getName();
            }
            page += "]\n\nSong recommendations:\n\t[";
            for (int i = 0; i < user.getRecSongs().size(); i++) {
                if (i != 0) {
                    page += ", ";
                }
                page += ((Song) user.getRecSongs().get(i)).getName();
            }
            page += "]\n\nPlaylists recommendations:\n\t[";
            for (int i = 0; i < user.getRecPlaylist().size(); i++) {
                if (i != 0) {
                    page += ", ";
                }
                page += ((Playlist) user.getRecPlaylist().get(i)).getName();
            }
            page += "]";
            result.setMessage(page);
        } else {
            LinkedList<Object> likedsongs = new LinkedList<Object>();
            for (int i = 0; i < user.getPreferedSongs().size(); i++) {
                likedsongs.addLast(user.getPreferedSongs().get(i));
            }
            for (int i = 0; i < likedsongs.size(); i++) {
                if (i != 0) {
                    page += ", ";
                }
                page += ((Song) likedsongs.get(i)).getName() + " - ";
                page += ((Song) likedsongs.get(i)).getArtist();
            }
            page += "]\n\nFollowed playlists:\n\t[";
            LinkedList<Object> playlists = new LinkedList<Object>();
            for (int i = 0; i < user.getFollowed().size(); i++) {
                int likes = 0;
                for (int j = 0; j < user.getFollowed().get(i).getSongs().size(); j++) {
                    likes += user.getFollowed().get(i).getSongs().get(j).getLikes();
                }
                user.getFollowed().get(i).setLikes(likes);
                playlists.addLast(user.getFollowed().get(i));
            }
            for (int i = 0; i < playlists.size(); i++) {
                if (i != 0) {
                    page += ", ";
                }
                page += ((Playlist) playlists.get(i)).getName() + " - ";
                page += ((Playlist) playlists.get(i)).getUser();
            }
            page += "]";
            result.setMessage(page);
        }
        return result;
    }

    /**
     * Prints the current ArtistPage that the user is on
     * @param artist
     * @param command
     * @return
     */
    public ResultSwitch visit(final Artist artist, final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        String page = "Albums:\n\t[";
        for (int i = 0; i < artist.getAlbums().size(); i++) {
            if (i != 0) {
                page += ", ";
            }
            page += artist.getAlbums().get(i).getName();
        }
        page += "]\n\nMerch:\n\t[";
        for (int i = 0; i < artist.getMerch().size(); i++) {
            if (i != 0) {
                page += ", ";
            }
            page += artist.getMerch().get(i).getMerchname() + " - ";
            page += artist.getMerch().get(i).getMerchprice();
            page += ":\n\t" + artist.getMerch().get(i).getMerchdescription();
        }
        page += "]\n\nEvents:\n\t[";
        for (int i = 0; i < artist.getEvents().size(); i++) {
            if (i != 0) {
                page += ", ";
            }
            page += artist.getEvents().get(i).getEventname() + " - ";
            page += artist.getEvents().get(i).getEventdate();
            page += ":\n\t" + artist.getEvents().get(i).getEventdescription();
        }
        page += "]";
        result.setMessage(page);
        return result;
    }

    /**
     * Prints the current hostpage that the user is on
     * @param host
     * @param command
     * @return
     */
    public ResultSwitch visit(final Host host, final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        String page = "Podcasts:\n\t[";
        for (int i = 0; i < host.getPodcasts().size(); i++) {
            if (i != 0) {
                page += "]\n, ";
            }
            page += host.getPodcasts().get(i).getName() + ":\n\t[";
            Podcast currPod = host.getPodcasts().get(i);
            for (int j = 0; j < currPod.getEpisodes().size(); j++) {
                if (j != 0) {
                    page += ", ";
                }
                page += currPod.getEpisodes().get(j).getName() + " - ";
                page += currPod.getEpisodes().get(j).getDescription();
            }
        }
        page += "]\n]\n\nAnnouncements:\n\t[";
        for (int i = 0; i < host.getAnnouncements().size(); i++) {
            Host.Announcement currAnn = host.getAnnouncements().get(i);
            if (i != 0) {
                page += ", ";
            }
            page += currAnn.getAnnName() + ":\n\t" + currAnn.getAnnDes();
        }
        if (!host.getAnnouncements().isEmpty()) {
            page += "\n]";
        } else {
            page += "]";
        }
        result.setMessage(page);
        return result;
    }
}
