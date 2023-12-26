package main;

import fileio.input.UserInput;

import java.util.LinkedList;

public final class Userbase {
    private static final Userbase INSTANCE = new Userbase();
    private LinkedList<User> userbase = new LinkedList<User>();

    static class ArtistData implements Comparable<ArtistData>{
        static class SongInc implements Comparable<SongInc>{
            private Song song;
            private double inc;

            public Song getSong() {
                return song;
            }

            public void setSong(Song song) {
                this.song = song;
            }

            public double getInc() {
                return inc;
            }

            public void setInc(double inc) {
                this.inc = inc;
            }

            @Override
            public int compareTo(SongInc o) {
                if (o.getInc() - inc > 0) {
                    return 1;
                }
                if (o.getInc() - inc < 0) {
                    return -1;
                }
                return -(o.getSong().getName().compareTo(song.getName()));
            }
        }

        private String artist;
        private double songrev;
        private double merchrev;
        private LinkedList<SongInc> songIncs = new LinkedList<>();

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public double getSongrev() {
            return songrev;
        }

        public void setSongrev(double songrev) {
            this.songrev = songrev;
        }

        public double getMerchrev() {
            return merchrev;
        }

        public void setMerchrev(double merchrev) {
            this.merchrev = merchrev;
        }

        public LinkedList<SongInc> getSongIncs() {
            return songIncs;
        }

        public void setSongIncs(LinkedList<SongInc> songIncs) {
            this.songIncs = songIncs;
        }

        @Override
        public int compareTo(ArtistData o) {
            if ((o.getSongrev() + o.getMerchrev()) - (merchrev + songrev) > 0) {
                return 1;
            }
            if ((o.getSongrev() + o.getMerchrev()) - (merchrev + songrev) < 0) {
                return -1;
            }
            return -(o.getArtist().compareTo(artist));
        }
    }

    private LinkedList<ArtistData> artistData = new LinkedList<>();

    private Userbase() {
    }

    public void addStats(final String artist, final Song currSong) {
        boolean found = false;
        for (int i = 0; i < artistData.size(); i++) {
            if (artistData.get(i).getArtist().equals(artist)) {
                found = true;
                boolean foundsong = false;
                ArtistData currArt = artistData.get(i);
                for (int  j = 1; j < currArt.getSongIncs().size(); j++) {
                    String sg = currArt.getSongIncs().get(j).getSong().getName();
                    if (sg.equals(currSong.getName())) {
                        foundsong = true;
                        break;
                    }
                }
                if (!foundsong) {
                    ArtistData.SongInc songInc = new ArtistData.SongInc();
                    songInc.setSong(currSong);
                    songInc.setInc(0);
                    currArt.getSongIncs().addLast(songInc);
                }
                break;
            }
        }
        if (!found) {
            ArtistData.SongInc songInc = new ArtistData.SongInc();
            songInc.setSong(currSong);
            songInc.setInc(0);
            artistData.addLast(new ArtistData());
            artistData.getLast().getSongIncs().addLast(songInc);
            artistData.getLast().setArtist(artist);
        }
    }

    /**
     * Function that gets the current usernames of all online
     * users and returns them
     * @param command
     * @return
     */
    public ResultTop5 getOnlineUsers(final Command command) {
        ResultTop5 onlineUsers = new ResultTop5(command);
        LinkedList<String> online = new LinkedList<String>();
        for (int i = 0; i < userbase.size(); i++) {
            if (!userbase.get(i).isOffline() && userbase.get(i).getType() == 0) {
                online.addLast(userbase.get(i).getUsername());
            }
        }
        onlineUsers.setResult(online);
        return onlineUsers;
    }

    /**
     * Function gets all usernames of all users on the platform
     * @param command
     * @return
     */
    public ResultTop5 getAllUsers(final Command command) {
        ResultTop5 onlineUsers = new ResultTop5(command);
        LinkedList<String> online = new LinkedList<String>();
        for (int i = 0; i < userbase.size(); i++) {
            if (userbase.get(i).getType() == 0) {
                online.addLast(userbase.get(i).getUsername());
            }
        }
        for (int i = 0; i < userbase.size(); i++) {
            if (userbase.get(i).getType() == 1) {
                online.addLast(userbase.get(i).getUsername());
            }
        }
        for (int i = 0; i < userbase.size(); i++) {
            if (userbase.get(i).getType() == 2) {
                online.addLast(userbase.get(i).getUsername());
            }
        }
        onlineUsers.setResult(online);
        return onlineUsers;
    }

    public static Userbase getInstance() {
        return INSTANCE;
    }

    /***
     * Adds an user to tyje userbase
     * @param user
     */
    public void addUser(final UserInput user) {
        userbase.addLast(new User(user));
    }

    /**
     * The function addUser is Overloaded so that it can get a
     * UserInput or a Command as input, after that it adds
     * the user to the userbase
     * @param command
     * @return
     */
    public ResultSwitch addUser(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        String message;
        for (int i = 0; i < userbase.size(); i++) {
            if (userbase.get(i).getUsername().equals(command.getUsername())) {
                message = "The username " + command.getUsername() + " is already taken.";
                result.setMessage(message);
                return result;
            }
        }
        if (command.getType().equals("user")) {
            User newUser = new User(command);
            userbase.addLast(newUser);
        }
        if (command.getType().equals("artist")) {
            Artist newUser = new Artist(command);
            userbase.addLast(newUser);
        }
        if (command.getType().equals("host")) {
            Host newUser = new Host(command);
            userbase.addLast(newUser);
        }
        message = "The username " + command.getUsername();
        message += " has been added successfully.";
        result.setMessage(message);
        return result;
    }

    /**
     * Function that deletes an user from the userbase if it
     * is possible
     * @param command
     * @param user
     * @return
     */
    public ResultSwitch deleteUser(final Command command, final User user) {
        ResultSwitch result = new ResultSwitch(command);
        for (int i = 0; i < userbase.size(); i++) {
            User currUser = userbase.get(i);
            currUser.updatePlayer(command);
            if (currUser.getUsername().equals(user.getUsername())) {
                continue;
            }
            if (currUser.getCurrUserrPage().getUsername().equals(user.getUsername())) {
                result.setMessage(user.getUsername() + " can't be deleted.");
                return result;
            }
            if (currUser.getPlayer().getType().equals("nothing")) {
                continue;
            }
            if (user.getType() == 0 && currUser.getPlayer().getType().equals("playlist")) {
                Playlist playlist = (Playlist) currUser.getPlayer().getSource();
                if (playlist.getUser().equals(user.getUsername())) {
                    result.setMessage(user.getUsername() + " can't be deleted.");
                    return result;
                }
            }
            if (user.getType() == 2 && currUser.getPlayer().getType().equals("podcast")) {
                Podcast podcast = (Podcast) currUser.getPlayer().getSource();
                if (podcast.getOwner().equals(user.getUsername())) {
                    result.setMessage(user.getUsername() + " can't be deleted.");
                    return result;
                }
            }
            if (user.getType() == 1) {
                Object currSource = currUser.getPlayer().getSource();
                if (currUser.getPlayer().getType().equals("album")) {
                    if (((Album) currSource).getUsername().equals(user.getUsername())) {
                        result.setMessage(user.getUsername() + " can't be deleted.");
                        return result;
                    }
                }
                if (currUser.getPlayer().getType().equals("song")) {
                    Song song = (Song) currUser.getPlayer().getCurrFile();
                    if (song.getArtist().equals(user.getUsername())) {
                        result.setMessage(user.getUsername() + " can't be deleted.");
                        return result;
                    }
                }
                if (currUser.getPlayer().getType().equals("playlist")) {
                    for (int j = 0; j < ((Playlist) currSource).getSongs().size(); j++) {
                        Song currSong = ((Playlist) currSource).getSongs().get(j);
                        if (currSong.getArtist().equals(user.getUsername())) {
                            result.setMessage(user.getUsername() + " can't be deleted.");
                            return result;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < userbase.size(); i++) {
            User currUser = userbase.get(i);
            if (user.getType() == 2) {
                for (int j = 0; j < currUser.getPodcastInfos().size(); j++) {
                    if (currUser.getPodcastInfos().get(j).getOwner().equals(user.getUsername())) {
                        currUser.getPodcastInfos().remove(j);
                        j--;
                    }
                }
            }
            if (user.getType() == 0) {
                for (int j = 0; j < currUser.getFollowed().size(); j++) {
                    if (currUser.getFollowed().get(j).getUser().equals(user.getUsername())) {
                        currUser.getFollowed().remove(j);
                        j--;
                    }
                }
            }
            if (user.getType() == 1) {
                for (int j = 0; j < currUser.getPreferedSongs().size(); j++) {
                    if (currUser.getPreferedSongs().get(j).getArtist().equals(user.getUsername())) {
                        currUser.getPreferedSongs().remove(j);
                        j--;
                    }
                }
            }
        }
        if (user.getType() == 0) {
            for (int i = 0; i < PlaylistLibrary.getInstance().getPlaylists().size(); i++) {
                PlaylistLibrary playlibrar = PlaylistLibrary.getInstance();
                if (playlibrar.getPlaylists().get(i).getUser().equals(user.getUsername())) {
                    PlaylistLibrary.getInstance().getPlaylists().remove(i);
                    i--;
                }
            }
        }
        if (user.getType() == 2) {
            for (int i = 0; i < PodcastLibrary.getInstance().getPodcasts().size(); i++) {
                PodcastLibrary podlibrar = PodcastLibrary.getInstance();
                if (podlibrar.getPodcasts().get(i).getOwner().equals(user.getUsername())) {
                    PodcastLibrary.getInstance().getPodcasts().remove(i);
                    i--;
                }
            }
        }
        if (user.getType() == 1) {
            for (int i = 0; i < AlbumLibrary.getINSTANCE().getAlbums().size(); i++) {
                AlbumLibrary albmlibr = AlbumLibrary.getINSTANCE();
                if (albmlibr.getAlbums().get(i).getUsername().equals(user.getUsername())) {
                    AlbumLibrary.getINSTANCE().getAlbums().remove(i);
                    i--;
                }
            }
            for (int i = 0; i < Library.getInstance().getSongs().size(); i++) {
                Library libr = Library.getInstance();
                if (libr.getSongs().get(i).getArtist().equals(user.getUsername())) {
                    Library.getInstance().getSongs().remove(i);
                    i--;
                }
            }
            for (int i = 0; i < PlaylistLibrary.getInstance().getPlaylists().size(); i++) {
                Playlist currPlaylist = PlaylistLibrary.getInstance().getPlaylists().get(i);
                for (int j = 0; j < currPlaylist.getSongs().size(); j++) {
                    if (currPlaylist.getSongs().get(j).getArtist().equals(user.getUsername())) {
                        currPlaylist.getSongs().remove(j);
                        j--;
                    }
                }
            }
        }
        for (int i = 0; i < user.getPreferedSongs().size(); i++) {
            Song song = user.getPreferedSongs().get(i);
            song.setLikes(song.getLikes() - 1);
        }
        for (int i = 0; i < user.getFollowed().size(); i++) {
            Playlist playlist = user.getFollowed().get(i);
            playlist.setFollowers(playlist.getFollowers() - 1);
        }
        result.setMessage(user.getUsername() + " was successfully deleted.");
        userbase.remove(user);
        return result;
    }

    /**
     * Function that returns the top 5 artist by the number of likes
     * that they have
     * @param command
     * @return
     */
    public ResultTop5 getTop5Artist(final Command command) {
        ResultTop5 result = new ResultTop5(command);
        BubbleSort bubbleSort = new BubbleSort();
        final int nr = 5;
        for (int i = 0; i < userbase.size(); i++) {
            if (userbase.get(i).getType() != 1) {
                continue;
            }
            int nrlikes = 0;
            for (int j = 0; j < ((Artist) userbase.get(i)).getAlbums().size(); j++) {
                Album album = ((Artist) userbase.get(i)).getAlbums().get(j);
                for (int k = 0; k < album.getSongs().size(); k++) {
                    nrlikes += album.getSongs().get(k).getLikes();
                }
            }
            ((Artist) userbase.get(i)).setNrLikes(nrlikes);
            bubbleSort.getListOfObjects().addLast(userbase.get(i));
        }
        bubbleSort.setType("artist");
        bubbleSort.sort();
        for (int i = 0; i < nr && i < bubbleSort.getListOfObjects().size(); i++) {
            Artist currArtist = (Artist) bubbleSort.getListOfObjects().get(i);
            result.getResult().addLast(currArtist.getUsername());
        }
        return result;
    }

    public void updateArt(Song currSong, User currUser, String artist)
    {
        for (int i = 0; i < userbase.size(); i++) {
            if (userbase.get(i).getType() == 1 && userbase.get(i).getUsername().equals(artist)) {
                ((Artist) userbase.get(i)).updateWrapped(currUser, currSong);
                break;
            }
        }
    }

    public LinkedList<User> getUserbase() {
        return userbase;
    }

    public void setUserbase(final LinkedList<User> userbase) {
        this.userbase = userbase;
    }

    public LinkedList<ArtistData> getArtistData() {
        return artistData;
    }

    public void setArtistData(LinkedList<ArtistData> artistData) {
        this.artistData = artistData;
    }
}
