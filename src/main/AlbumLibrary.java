package main;

import fileio.input.SongInput;

import java.util.ArrayList;
import java.util.LinkedList;

public final class AlbumLibrary {
    private static final AlbumLibrary INSTANCE = new AlbumLibrary();
    private LinkedList<Album> albums = new LinkedList<Album>();

    private AlbumLibrary() {
    }

    public static AlbumLibrary getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Command that adds a new Album to the Library and to the user given
     * as param.
     * @param command
     * @param currUser
     * @return
     */
    public ResultSwitch addAlbum(final Command command, final User currUser) {
        ResultSwitch result = new ResultSwitch(command);
        String message = currUser.getUsername();
        if (!currUser.getUsername().equals(command.getUsername())) {
            result.setMessage("The username " + command.getUsername() + " doesn't exist.");
            return result;
        }
        if (currUser.getType() != 1) {
            result.setMessage(currUser.getUsername() + " is not an artist.");
            return result;
        }
        for (int i = 0; i < ((Artist) currUser).getAlbums().size(); i++) {
            if (((Artist) currUser).getAlbums().get(i).getName().equals(command.getName())) {
                result.setMessage(message + " has another album with the same name.");
                return result;
            }
        }
        Album newAlbum = new Album();
        newAlbum.setName(command.getName());
        newAlbum.setDescription(command.getDescription());
        newAlbum.setUsername(command.getUsername());
        newAlbum.setReleaseYear(command.getReleaseYear());
        ArrayList<SongInput> songs = command.getSongs();
        for (int i = 0; i < songs.size(); i++) {
            newAlbum.getSongs().addLast(new Song(songs.get(i)));
        }
        if (newAlbum.containsduplicates()) {
            result.setMessage(message + " has the same song at least twice in this album.");
            return result;
        }
        for (int i = 0; i < newAlbum.getSongs().size(); i++) {
            Library.getInstance().getSongs().addLast(newAlbum.getSongs().get(i));
        }
        albums.addLast(newAlbum);
        ((Artist) currUser).getAlbums().addLast(newAlbum);
        result.setMessage(currUser.getUsername() + " has added new album successfully.");
        Artist ht = (Artist) currUser;
        for (int i = 0; i < ht.getSubscribers().size(); i++) {
            NotifObserv.Notification notif = new NotifObserv.Notification();
            notif.setName("New Album");
            notif.setDescription("New Album from " + ht.getUsername() + ".");
            ht.getSubscribers().get(i).addNotification(notif);
        }
        return result;
    }

    /**
     * Removes an album form the library and its artist
     * @param command
     * @param currUser
     * @return
     */
    public ResultSwitch removeAlbum(final Command command, final User currUser) {
        ResultSwitch result = new ResultSwitch(command);
        String message = currUser.getUsername();
        if (!currUser.getUsername().equals(command.getUsername())) {
            result.setMessage("The username " + command.getUsername() + " doesn't exist.");
            return result;
        }
        if (currUser.getType() != 1) {
            result.setMessage(currUser.getUsername() + " is not an artist.");
            return result;
        }
        boolean hasAlbum = false;
        for (int i = 0; i < ((Artist) currUser).getAlbums().size(); i++) {
            Artist artist = ((Artist) currUser);
            if (artist.getAlbums().get(i).getName().equals(command.getName())) {
                hasAlbum = true;
                break;
            }
        }
        if (!hasAlbum) {
            message += " doesn't have an album with the given name.";
            result.setMessage(message);
            return  result;
        }
        for (int i = 0; i < Userbase.getInstance().getUserbase().size(); i++) {
            User user = Userbase.getInstance().getUserbase().get(i);
            user.updatePlayer(command);
            if (user.getUsername().equals(currUser.getUsername())) {
                continue;
            }
            String type = user.getPlayer().getType();
            if (type.equals("podcast") || type.equals("nothing")) {
                continue;
            }
            if (user.getPlayer().getType().equals("song")) {
                Song song = (Song) user.getPlayer().getCurrFile();
                if (song.getAlbum().equals(command.getName()) && song.getArtist().equals(message)) {
                    result.setMessage(currUser.getUsername() + " can't delete this album.");
                    return result;
                }
            }
            if (user.getPlayer().getType().equals("album")) {
                Album album = (Album) user.getPlayer().getSource();
                String name = command.getName();
                if (album.getName().equals(name) && album.getUsername().equals(message)) {
                    result.setMessage(currUser.getUsername() + " can't delete this album.");
                    return result;
                }
            }
            if (user.getPlayer().getType().equals("playlist")) {
                Playlist playlist = (Playlist) user.getPlayer().getSource();
                for (int j = 0; j < playlist.getSongs().size(); j++) {
                    Song song = playlist.getSongs().get(j);
                    String name = command.getName();
                    if (song.getAlbum().equals(name) && song.getArtist().equals(message)) {
                        result.setMessage(currUser.getUsername() + " can't delete this album.");
                        return result;
                    }
                }
            }
        }
        for (int i = 0; i < Userbase.getInstance().getUserbase().size(); i++) {
            User user = Userbase.getInstance().getUserbase().get(i);
            if (user.getUsername().equals(currUser.getUsername())) {
                continue;
            }
            for (int j = 0; j < user.getPreferedSongs().size(); j++) {
                Song song = user.getPreferedSongs().get(j);
                if (song.getAlbum().equals(command.getName()) && song.getArtist().equals(message)) {
                    user.getPreferedSongs().remove(j);
                    j--;
                }
            }
        }
        for (int i = 0; i < PlaylistLibrary.getInstance().getPlaylists().size(); i++) {
            Playlist playlist = PlaylistLibrary.getInstance().getPlaylists().get(i);
            for (int j = 0; j < playlist.getSongs().size(); j++) {
                Song song = playlist.getSongs().get(j);
                if (song.getAlbum().equals(command.getName()) && song.getArtist().equals(message)) {
                    playlist.getSongs().remove(j);
                    j--;
                }
            }
        }
        for (int i = 0; i < Library.getInstance().getSongs().size(); i++) {
            Song song = Library.getInstance().getSongs().get(i);
            if (song.getAlbum().equals(command.getName()) && song.getArtist().equals(message)) {
                Library.getInstance().getSongs().remove(i);
                i--;
            }
        }
        for (int i = 0; i < albums.size(); i++) {
            Album album = albums.get(i);
            if (album.getName().equals(command.getName()) && album.getUsername().equals(message)) {
                albums.remove(i);
                break;
            }
        }
        for (int i = 0; i < ((Artist) currUser).getAlbums().size(); i++) {
            Album currAlbum = ((Artist) currUser).getAlbums().get(i);
            if (currAlbum.getName().equals(command.getName())) {
                ((Artist) currUser).getAlbums().remove(i);
                break;
            }
        }
        result.setMessage(currUser.getUsername() + " deleted the album successfully.");
        return result;
    }

    /**
     * Function that removes an Album from the library
     * @param command
     * @return
     */
    public ResultTop5 getTop5Albums(final Command command) {
        ResultTop5 result = new ResultTop5(command);
        BubbleSort bubbleSort = new BubbleSort();
        final int nr = 5;
        for (int i = 0; i < albums.size(); i++) {
            int nrlikes = 0;
            for (int j = 0; j < albums.get(i).getSongs().size(); j++) {
                nrlikes += albums.get(i).getSongs().get(j).getLikes();
            }
            bubbleSort.getListOfObjects().addLast(albums.get(i));
            albums.get(i).setNrlikes(nrlikes);
        }
        bubbleSort.setType("album");
        bubbleSort.sort();
        for (int i = 0; i < nr && i < bubbleSort.getListOfObjects().size(); i++) {
            result.getResult().addLast(((Album) bubbleSort.getListOfObjects().get(i)).getName());
        }
        return result;
    }

    public LinkedList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(final LinkedList<Album> albums) {
        this.albums = albums;
    }

    /**
     * Resets the current album list
     */
    public void resetAlbums() {
        albums = new LinkedList<Album>();
    }
}
