package main;

import com.fasterxml.jackson.databind.JsonNode;
import fileio.input.UserInput;

import java.util.*;
import java.util.stream.IntStream;

interface Visitor {
    ResultSwitch visit(User user, Command command);
    ResultSwitch visit(Artist artist, Command command);
    ResultSwitch visit(Host host, Command command);
}

interface Visitable  {
    ResultSwitch currentPage(Visitor v, Command command);
}

interface notifObserv {
    static class Notification {
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
    void addNotification(Notification Notific);
}

interface CommandPage {
    void execute();
    void undo();
}

public class User implements Visitable, notifObserv{
    private String username;
    private int age;
    private String city;
    private boolean offline;
    private int type;
    private String lastsearch = "nothing";
    private LinkedList<Object> searcheditems = new LinkedList<Object>();
    private Object selectedItem;
    private Player player = new Player();
    private LinkedList<Song> preferedSongs = new LinkedList<Song>();
    private LinkedList<Playlist> playlists = new LinkedList<Playlist>();
    private LinkedList<Playlist> followed = new LinkedList<Playlist>();
    private String currentPage;
    private User currUserrPage;
    private LinkedList<Notification> notifications = new LinkedList<>();

    class PodcastInfo {
        private String podcastname;
        private int currepisode;
        private int remainingtime;
        private String owner;

        public String getPodcastname() {
            return podcastname;
        }

        public void setPodcastname(final String podcastname) {
            this.podcastname = podcastname;
        }

        public int getCurrepisode() {
            return currepisode;
        }

        public void setCurrepisode(final int currepisode) {
            this.currepisode = currepisode;
        }

        public int getRemainingtime() {
            return remainingtime;
        }

        public void setRemainingtime(final int remainingtime) {
            this.remainingtime = remainingtime;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(final String owner) {
            this.owner = owner;
        }
    }

    private LinkedList<PodcastInfo> podcastInfos = new LinkedList<PodcastInfo>();

    class Wrapped {
        static class ArtistListen implements Comparable<ArtistListen>{
            private String artist;
            private int listen;

            public String getArtist() {
                return artist;
            }

            public void setArtist(final String artist) {
                this.artist = artist;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(ArtistListen o) {
                if (this.listen == o.getListen()) {
                    return this.artist.compareTo(o.getArtist());
                } else {
                    return o.listen - this.listen;
                }
            }
        }

        static class SongListen implements Comparable<SongListen>{
            private Song song;
            private int listen;

            public Song getSong() {
                return song;
            }

            public void setSong(Song song) {
                this.song = song;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(SongListen o) {
                if (this.listen == o.getListen()) {
                    return this.song.getName().compareTo(o.getSong().getName());
                } else {
                    return o.listen - this.listen;
                }
            }
        }

        static class GenreListen implements Comparable<GenreListen>{
            private String genre;
            private int listen;

            public String getGenre() {
                return genre;
            }

            public void setGenre(String genre) {
                this.genre = genre;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(GenreListen o) {
                if (this.listen == o.getListen()) {
                    return this.genre.compareTo(o.getGenre());
                } else {
                    return o.listen - this.listen;
                }
            }
        }

        static class AlbumListen implements Comparable<AlbumListen>{
            private String album;
            private int listen;

            public String getAlbum() {
                return album;
            }

            public void setAlbum(String album) {
                this.album = album;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(AlbumListen o) {
                if (this.listen == o.getListen()) {
                    return this.album.compareTo(o.getAlbum());
                } else {
                    return o.listen - this.listen;
                }
            }
        }

        static class PodcastListen implements Comparable<PodcastListen>{
            private Episode episode;
            private int listen;

            public Episode getEpisode() {
                return episode;
            }

            public void setEpisode(Episode episode) {
                this.episode = episode;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(PodcastListen o) {
                if (this.listen == o.getListen()) {
                    return this.episode.getName().compareTo(o.getEpisode().getName());
                } else {
                    return o.listen - this.listen;
                }
            }
        }

        private LinkedList<ArtistListen> topArtist = new LinkedList<>();
        private LinkedList<AlbumListen> topAlbum = new LinkedList<>();
        private LinkedList<SongListen> topSong = new LinkedList<>();
        private LinkedList<PodcastListen> topEpisode = new LinkedList<>();
        private LinkedList<GenreListen> topGenre = new LinkedList<>();

        public LinkedList<ArtistListen> getTopArtist() {
            return topArtist;
        }

        public void setTopArtist(LinkedList<ArtistListen> topArtist) {
            this.topArtist = topArtist;
        }

        public LinkedList<AlbumListen> getTopAlbum() {
            return topAlbum;
        }

        public void setTopAlbum(LinkedList<AlbumListen> topAlbum) {
            this.topAlbum = topAlbum;
        }

        public LinkedList<SongListen> getTopSong() {
            return topSong;
        }

        public void setTopSong(LinkedList<SongListen> topSong) {
            this.topSong = topSong;
        }

        public LinkedList<PodcastListen> getTopEpisode() {
            return topEpisode;
        }

        public void setTopEpisode(LinkedList<PodcastListen> topEpisode) {
            this.topEpisode = topEpisode;
        }

        public LinkedList<GenreListen> getTopGenre() {
            return topGenre;
        }

        public void setTopGenre(LinkedList<GenreListen> topGenre) {
            this.topGenre = topGenre;
        }
    }

    private Wrapped wrapped = new Wrapped();

    static class Premium {
        private LinkedList<Wrapped.SongListen> songs = new LinkedList<>();
        private int totalls;

        public void reserList() {
            songs = new LinkedList<>();
            totalls = 0;
        }

        public LinkedList<Wrapped.SongListen> getSongs() {
            return songs;
        }

        public void setSongs(LinkedList<Wrapped.SongListen> songs) {
            this.songs = songs;
        }

        public int getTotalls() {
            return totalls;
        }

        public void setTotalls(int totalls) {
            this.totalls = totalls;
        }
    }

    final Premium premium = new Premium();
    boolean isPremium = false;
    LinkedList<String> mymerch = new LinkedList<>();

    class PageHistory {
        static class Page implements CommandPage {
            private User user;
            private String nextPage;
            private String prevPage;
            private User nextUser;
            private User prevUser;
            Page(final User nextUser, final String nextPage, final User user) {
                this.user = user;
                this.nextPage = nextPage;
                this.nextUser = nextUser;
                this.prevPage = user.getCurrentPage();
                this.prevUser = user.getCurrUserrPage();
            }

            @Override
            public void execute() {
                user.setCurrUserrPage(nextUser);
                user.setCurrentPage(nextPage);
            }

            @Override
            public void undo() {
                user.setCurrentPage(prevPage);
                user.setCurrUserrPage(prevUser);
            }

            public String getNextPage() {
                return nextPage;
            }

            public void setNextPage(String nextPage) {
                this.nextPage = nextPage;
            }

            public String getPrevPage() {
                return prevPage;
            }

            public void setPrevPage(String prevPage) {
                this.prevPage = prevPage;
            }

            public User getNextUser() {
                return nextUser;
            }

            public void setNextUser(User nextUser) {
                this.nextUser = nextUser;
            }

            public User getPrevUser() {
                return prevUser;
            }

            public void setPrevUser(User prevUser) {
                this.prevUser = prevUser;
            }

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }
        }
        LinkedList<Page> undoLs = new LinkedList<>();
        LinkedList<Page> redoLs = new LinkedList<>();

        void ResetRedoLs() {
            redoLs = new LinkedList<>();
        }

        void undo() {
            if (undoLs.isEmpty()) {
                return;
            }
            undoLs.getLast().undo();
            redoLs.addLast(undoLs.getLast());
            undoLs.removeLast();
        }

        void redo() {
            if (redoLs.isEmpty()) {
                return;
            }
            redoLs.getLast().execute();
            undoLs.addLast(redoLs.getLast());
            redoLs.removeLast();
        }

        public LinkedList<Page> getUndoLs() {
            return undoLs;
        }

        public void setUndoLs(LinkedList<Page> undoLs) {
            this.undoLs = undoLs;
        }

        public LinkedList<Page> getRedoLs() {
            return redoLs;
        }

        public void setRedoLs(LinkedList<Page> redoLs) {
            this.redoLs = redoLs;
        }
    }

    PageHistory pageHistory = new PageHistory();

    class TopGenres {
        class GenreLike implements Comparable<GenreLike>{
            private String genre;
            private int likes;

            public String getGenre() {
                return genre;
            }

            public void setGenre(String genre) {
                this.genre = genre;
            }

            public int getLikes() {
                return likes;
            }

            public void setLikes(int likes) {
                this.likes = likes;
            }

            @Override
            public int compareTo(GenreLike o) {
                if (o.getLikes() == likes) {
                    return -(o.getGenre().compareTo(genre));
                } else {
                    return o.getLikes() - likes;
                }
            }
        }

        private LinkedList<GenreLike> genres = new LinkedList<>();

        public void addGenre(final String name) {
            boolean found = false;
            for (int i = 0; i < genres.size(); i++) {
                if (genres.get(i).genre.equals(name)) {
                    found = true;
                    int likes = genres.get(i).getLikes();
                    genres.get(i).setLikes(likes + 1);
                    break;
                }
            }
            if (!found) {
                genres.addLast(new GenreLike());
                genres.getLast().setGenre(name);
                genres.getLast().setLikes(1);
            }
        }

        public LinkedList<GenreLike> getGenres() {
            return genres;
        }

        public void setGenres(LinkedList<GenreLike> genres) {
            this.genres = genres;
        }
    }

    TopGenres topGenres = new TopGenres();
    private LinkedList<Song> genre1 = new LinkedList<>();
    private LinkedList<Song> genre2 = new LinkedList<>();
    private LinkedList<Song> genre3 = new LinkedList<>();
    private LinkedList<Song> recSongs = new LinkedList<>();
    private LinkedList<Playlist> recPlaylist = new LinkedList<>();
    private int nrTopGenres;
    private String lastAddRecom;

    public User(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.currentPage = "HomePage";
        this.currUserrPage = this;
        player.setType("nothing");
        player.setCurrUser(this);
        lastAddRecom = "nothing";
        mymerch = new LinkedList<>();
    }

    public User(final Command command) {
        player.setType("nothing");
        this.username = command.getUsername();
        this.age = command.getAge();
        this.city = command.getCity();
        this.currentPage = "HomePage";
        this.currUserrPage = this;
        player.setCurrUser(this);
        lastAddRecom = "nothing";
        mymerch = new LinkedList<>();
    }

    /**
     * Tries to see if a given podcast has been previously played
     * by this user
     * @param name
     * @return
     */
    public PodcastInfo containsPodcast(final String name) {
        for (int i = 0; i < podcastInfos.size(); i++) {
            if (podcastInfos.get(i).getPodcastname().equals(name)) {
                return podcastInfos.get(i);
            }
        }
        return null;
    }

    /**
     * Updates the player and the PodcastInfo of a given Podcast to what
     * it shuld be at the given timestamp
     * @param command
     */
    public void updatePlayer(final Command command) {
        String previousStatus = player.getType();
        PodcastInfo info = null;
        if (player.getType().equals("podcast")) {
            info = containsPodcast(((Podcast) player.getSource()).getName());
        }
        player.status(command);
        if (previousStatus.equals("podcast")) {
            if (info != null) {
                info.setCurrepisode(player.getCurrentObject());
                info.setRemainingtime(player.getTimeremaining());
                if (player.getType().equals("nothing")) {
                    podcastInfos.remove(info);
                }
            }
        }
    }

    /**
     * Searches for a given element
     * @param command
     * @return
     */
    public ResultSearch search(final Command command) {
        final int nrmax = 5;
        searcheditems = new LinkedList<Object>();
        ResultSearch result = new ResultSearch(command);
        if (offline) {
            result.setMessage(username + " is offline.");
            return result;
        }
        player.status(command);
        JsonNode filters = command.getFilters();
        LinkedList<String> tags = new LinkedList<String>();
        if (player.getType().equals("podcast")) {
            PodcastInfo info = containsPodcast(((Podcast) player.getSource()).getName());
            if (info != null) {
                info.setCurrepisode(player.getCurrentObject());
                info.setRemainingtime(player.getTimeremaining());
                info.setOwner(((Podcast) player.getSource()).getOwner());
                if (player.getType().equals("nothing")) {
                    podcastInfos.remove(info);
                }
            } else {
                info = new PodcastInfo();
                info.setPodcastname(((Podcast) player.getSource()).getName());
                info.setRemainingtime(player.getTimeremaining());
                info.setCurrepisode(player.getCurrentObject());
                info.setOwner(((Podcast) player.getSource()).getOwner());
                podcastInfos.addLast(info);
            }
        }
        player.setSource(null);
        player.setType("nothing");
        lastsearch = "nothing";
        player.setAd(null);
        player.setNextAd(0);
        player.setAdrev(0);
        if (filters.get("tags") != null) {
            for (JsonNode tag : filters.get("tags")) {
                tags.addLast(tag.asText());
            }
        }
        if (command.getType().equals("song")) {
            lastsearch = "song";
            for (int i = 0; i < Library.getInstance().getSongs().size(); i++) {
                Song currSong = Library.getInstance().getSongs().get(i);
                if (filters.get("name") != null) {
                    if (!currSong.getName().toLowerCase().startsWith(filters.get("name").textValue().toLowerCase())) {
                        continue;
                    }
                }
                if (filters.get("album") != null) {
                    if (!currSong.getAlbum().equals(filters.get("album").textValue())) {
                        continue;
                    }
                }
                if (filters.get("tags") != null) {
                    if (!currSong.getTags().containsAll(tags)) {
                        continue;
                    }
                }
                if (filters.get("lyrics") != null) {
                    String aux = filters.get("lyrics").textValue().toLowerCase();
                    if (!currSong.getLyrics().toLowerCase().contains(aux)) {
                        continue;
                    }
                }
                if (filters.get("genre") != null) {
                    if (!currSong.getGenre().equalsIgnoreCase(filters.get("genre").textValue())) {
                        continue;
                    }
                }
                if (filters.get("releaseYear") != null) {
                    Integer year;
                    year = Integer.valueOf(filters.get("releaseYear").textValue().substring(1));
                    if (filters.get("releaseYear").textValue().charAt(0) == '<') {
                        if (year <= currSong.getReleaseYear()) {
                            continue;
                        }
                    } else {
                        if (year >= currSong.getReleaseYear()) {
                            continue;
                        }
                    }
                }
                if (filters.get("artist") != null) {
                    if (!currSong.getArtist().equals(filters.get("artist").textValue())) {
                        continue;
                    }
                }
                searcheditems.addLast(currSong);
                if (searcheditems.size() >= nrmax) {
                    break;
                }
            }
        }
        if (command.getType().equals("podcast")) {
            lastsearch = "podcast";
            for (int i = 0; i < PodcastLibrary.getInstance().getPodcasts().size(); i++) {
                Podcast currPodcast = PodcastLibrary.getInstance().getPodcasts().get(i);
                if (filters.get("name") != null) {
                    if (!currPodcast.getName().startsWith(filters.get("name").textValue())) {
                        continue;
                    }
                }
                if (filters.get("owner") != null) {
                    if (!currPodcast.getOwner().equals(filters.get("owner").textValue())) {
                        continue;
                    }
                }
                searcheditems.addLast(currPodcast);
                if (searcheditems.size() >= nrmax) {
                    break;
                }
            }
        }
        if (command.getType().equals("playlist")) {
            lastsearch = "playlist";
            for (int i = 0; i < PlaylistLibrary.getInstance().getPlaylists().size(); i++) {
                Playlist playlist = PlaylistLibrary.getInstance().getPlaylists().get(i);
                String privacy = playlist.getPrivacy();
                if (privacy.equals("private") && !playlist.getUser().equals(username)) {
                    continue;
                }
                if (filters.get("name") != null) {
                    if (!playlist.getName().startsWith(filters.get("name").textValue())) {
                        continue;
                    }
                }
                if (filters.get("owner") != null) {
                    if (!playlist.getUser().equals(filters.get("owner").textValue())) {
                        continue;
                    }
                }
                searcheditems.addLast(playlist);
                if (searcheditems.size() >= nrmax) {
                    break;
                }
            }
        }
        if (command.getType().equals("artist")) {
            lastsearch = "artist";
            for (int i = 0; i < Userbase.getInstance().getUserbase().size(); i++) {
                User currUser = Userbase.getInstance().getUserbase().get(i);
                if (currUser.getType() != 1) {
                    continue;
                }
                if (filters.get("name") != null) {
                    if (!currUser.getUsername().startsWith(filters.get("name").textValue())) {
                        continue;
                    }
                }
                searcheditems.addLast(currUser);
                if (searcheditems.size() >= nrmax) {
                    break;
                }
            }
        }
        if (command.getType().equals("host")) {
            lastsearch = "host";
            for (int i = 0; i < Userbase.getInstance().getUserbase().size(); i++) {
                User currUser = Userbase.getInstance().getUserbase().get(i);
                if (currUser.getType() != 2) {
                    continue;
                }
                if (filters.get("name") != null) {
                    if (!currUser.getUsername().startsWith(filters.get("name").textValue())) {
                        continue;
                    }
                }
                searcheditems.addLast(currUser);
                if (searcheditems.size() >= nrmax) {
                    break;
                }
            }
        }
        if (command.getType().equals("album")) {
            lastsearch = "album";
            for (int i = 0; i < Userbase.getInstance().getUserbase().size(); i++) {
                User currUser = Userbase.getInstance().getUserbase().get(i);
                if (currUser.getType() != 1) {
                    continue;
                }
                Artist art = (Artist) currUser;
                for (int j = 0; j < art.getAlbums().size(); j++) {
                    Album currAlbum = art.getAlbums().get(j);
                    if (filters.get("name") != null) {
                        if (!currAlbum.getName().startsWith(filters.get("name").textValue())) {
                            continue;
                        }
                    }
                    if (filters.get("owner") != null) {
                        if (!currAlbum.getUsername().startsWith(filters.get("owner").textValue())) {
                            continue;
                        }
                    }
                    if (filters.get("description") != null) {
                        String descr = currAlbum.getDescription();
                        if (!descr.startsWith(filters.get("description").textValue())) {
                            continue;
                        }
                    }
                    searcheditems.addLast(currAlbum);
                    if (searcheditems.size() >= nrmax) {
                        break;
                    }
                }
                if (searcheditems.size() >= nrmax) {
                    break;
                }
            }
        }
        result.setMessage("Search returned " + searcheditems.size() + " results");
        if (lastsearch.equals("song")) {
            for (int i = 0; i < searcheditems.size(); i++) {
                AudioFiles currFile = (AudioFiles) searcheditems.get(i);
                result.getResults().addLast(currFile.getName());
            }
        }
        if (lastsearch.equals("podcast")) {
            for (int i = 0; i < searcheditems.size(); i++) {
                Podcast currpodcast = (Podcast) searcheditems.get(i);
                result.getResults().addLast(currpodcast.getName());
            }
        }
        if (lastsearch.equals("playlist")) {
            for (int i = 0; i < searcheditems.size(); i++) {
                Playlist playlist = (Playlist) searcheditems.get(i);
                result.getResults().addLast(playlist.getName());
            }
        }
        if (lastsearch.equals("artist")) {
            for (int i = 0; i < searcheditems.size(); i++) {
                User currUser = (User) searcheditems.get(i);
                result.getResults().addLast(currUser.getUsername());
            }
        }
        if (lastsearch.equals("host")) {
            for (int i = 0; i < searcheditems.size(); i++) {
                User currUser = (User) searcheditems.get(i);
                result.getResults().addLast(currUser.getUsername());
            }
        }
        if (lastsearch.equals("album")) {
            for (int i = 0; i < searcheditems.size(); i++) {
                Album currAlbum = (Album) searcheditems.get(i);
                result.getResults().addLast(currAlbum.getName());
            }
        }
        selectedItem = null;
        return result;
    }

    /**
     * Selects the element with the given id
     * @param command
     * @return
     */
    public ResultSelect select(final Command command) {
        ResultSelect result = new ResultSelect(command);
        if (offline) {
            result.setMessage(username + " is offline.");
            return result;
        }
        if (lastsearch.equals("nothing")) {
            result.setMessage("Please conduct a search before making a selection.");
            return result;
        }
        if (command.getItemNumber() > searcheditems.size()) {
            result.setMessage("The selected ID is too high.");
            return result;
        }
        selectedItem = searcheditems.get(command.getItemNumber() - 1);
        if (lastsearch.equals("song")) {
            String name = ((Song) searcheditems.get(command.getItemNumber() - 1)).getName();
            result.setMessage("Successfully selected " + name + ".");
        }
        if (lastsearch.equals("podcast")) {
            String name = ((Podcast) searcheditems.get(command.getItemNumber() - 1)).getName();
            result.setMessage("Successfully selected " +  name + ".");
        }
        if (lastsearch.equals("playlist")) {
            String name = ((Playlist) searcheditems.get(command.getItemNumber() - 1)).getName();
            result.setMessage("Successfully selected " + name + ".");
        }
        if (lastsearch.equals("artist")) {
            String name = ((Artist) searcheditems.get(command.getItemNumber() - 1)).getUsername();
            result.setMessage("Successfully selected " + name + "'s page.");
            Artist art = (Artist) searcheditems.get(command.getItemNumber() - 1);
            PageHistory.Page newPage = new PageHistory.Page(art, "ArtistPage", this);
            newPage.execute();
            pageHistory.ResetRedoLs();
            pageHistory.getUndoLs().addLast(newPage);
        }
        if (lastsearch.equals("host")) {
            String name = ((Host) searcheditems.get(command.getItemNumber() - 1)).getUsername();
            result.setMessage("Successfully selected " + name + "'s page.");
            Host art = (Host) searcheditems.get(command.getItemNumber() - 1);
            PageHistory.Page newPage = new PageHistory.Page(art, "HostPage", this);
            newPage.execute();
            pageHistory.ResetRedoLs();
            pageHistory.getUndoLs().addLast(newPage);
        }
        if (lastsearch.equals("album")) {
            String name = ((Album) searcheditems.get(command.getItemNumber() - 1)).getName();
            result.setMessage("Successfully selected " + name + ".");
        }
        return result;
    }

    /**
     * Loads in the player the previously selected element
     * @param command
     * @return
     */
    public ResultLoad load(final Command command) {
        ResultLoad resultLoad = new ResultLoad(command);
        if (offline) {
            resultLoad.setMessage(username + " is offline.");
            return resultLoad;
        }
        if (selectedItem == null) {
            resultLoad.setMessage("Please select a source before attempting to load.");
            return resultLoad;
        }
        resultLoad.setMessage("Playback loaded successfully.");
        if (lastsearch.equals("song")) {
            player.setTimeremaining(((Song) selectedItem).getDuration());
            player.setType("song");
            player.setCurrFile((Song) selectedItem);
            player.setPause(false);
            updateWrapped("song", player.getCurrFile());
            updateWrapped("artist", ((Song) selectedItem).getArtist());
            updateWrapped("genre", ((Song) selectedItem).getGenre());
            updateWrapped("album", ((Song) selectedItem).getAlbum());
            Userbase.getInstance().addStats(((Song) selectedItem).getArtist(), (Song) selectedItem);
            Userbase.getInstance().updateArt((Song) selectedItem, this, ((Song) selectedItem).getArtist());
            player.addSg((Song) selectedItem);
        }
        if (lastsearch.equals("playlist")) {
            int aux = ((Playlist) selectedItem).getSongs().size();
            player.setTimeremaining(((Playlist) selectedItem).getSongs().get(0).getDuration());
            player.setType("playlist");
            player.setCurrFile(((Playlist) selectedItem).getSongs().get(0));
            player.setCurrentObject(0);
            player.setOrder(IntStream.range(0, aux).toArray());
            Song currSong = ((Playlist) selectedItem).getSongs().get(0);
            updateWrapped("song", currSong);
            updateWrapped("artist", currSong.getArtist());
            updateWrapped("genre", currSong.getGenre());
            updateWrapped("album", currSong.getAlbum());
            Userbase.getInstance().addStats(currSong.getArtist(), currSong);
            Userbase.getInstance().updateArt(currSong, this, currSong.getArtist());
            player.addSg(currSong);
        }
        if (lastsearch.equals("podcast")) {
            PodcastInfo info = containsPodcast(((Podcast) selectedItem).getName());
            if (info == null) {
                Episode aux = ((Podcast) selectedItem).getEpisodes().get(0);
                player.setTimeremaining(aux.getDuration());
                player.setType("podcast");
                player.setCurrFile(aux);
                player.setCurrentObject(0);
            } else {
                Episode aux = ((Podcast) selectedItem).getEpisodes().get(info.getCurrepisode());
                player.setTimeremaining(info.remainingtime);
                player.setType("podcast");
                player.setCurrFile(aux);
                player.setCurrentObject(info.getCurrepisode());
            }
            Podcast currPod = (Podcast) selectedItem;
            updateWrapped("episode", currPod.getEpisodes().get(player.getCurrentObject()));
            Episode aux = currPod.getEpisodes().get(player.getCurrentObject());
            Userbase.getInstance().updateHost(aux, this, currPod.getOwner());
        }
        if (lastsearch.equals("album")) {
            int aux = ((Album) selectedItem).getSongs().size();
            player.setTimeremaining(((Album) selectedItem).getSongs().get(0).getDuration());
            player.setType("album");
            player.setCurrFile(((Album) selectedItem).getSongs().get(0));
            player.setCurrentObject(0);
            player.setOrder(IntStream.range(0, aux).toArray());
            Song currSong = ((Album) selectedItem).getSongs().get(0);
            updateWrapped("song", currSong);
            updateWrapped("artist", currSong.getArtist());
            updateWrapped("genre", currSong.getGenre());
            updateWrapped("album", currSong.getAlbum());
            Userbase.getInstance().addStats(currSong.getArtist(), currSong);
            Userbase.getInstance().updateArt(currSong, this, currSong.getArtist());
            player.addSg(currSong);
        }
        player.setSource(selectedItem);
        player.setLasttimestamp(command.getTimestamp());
        player.setPause(false);
        player.setRepeat(0);
        player.setShuffle(false);
        selectedItem = null;
        searcheditems = new LinkedList<Object>();
        lastsearch = "nothing";
        return resultLoad;
    }

    /**
     * Pauses or unpauses the player
     * @param command
     * @return
     */
    public ResultPlayPause playPause(final Command command) {
        ResultPlayPause resultPlayPause = new ResultPlayPause(command);
        if (offline) {
            resultPlayPause.setMessage(username + " is offline.");
            return resultPlayPause;
        }
        updatePlayer(command);
        if (player.getSource() == null) {
            resultPlayPause.setMessage("Please load a source before"
                                        + " attempting to pause or resume playback.");
            return resultPlayPause;
        }
        if (player.isPause()) {
            player.setPause(false);
            resultPlayPause.setMessage("Playback resumed successfully.");
        } else {
            player.setPause(true);
            resultPlayPause.setMessage("Playback paused successfully.");
        }
        return resultPlayPause;
    }

    /**
     * Shows the current status of the player
     * @param command
     * @return
     */
    public ResultStatus status(final Command command) {
        if (!offline) {
            updatePlayer(command);
        }
        return new ResultStatus(command, player);
    }

    /**
     * Creates a new playlist
     * @param command
     * @return
     */
    public ResultCreatePlaylist createPlaylist(final Command command) {
        ResultCreatePlaylist resultCreatePlaylist = new ResultCreatePlaylist(command);
        if (offline) {
            resultCreatePlaylist.setMessage(username + " is offline.");
            return resultCreatePlaylist;
        }
        String name = command.getPlaylistName();
        for (int i = 0; i < PlaylistLibrary.getInstance().getPlaylists().size(); i++) {
            if (PlaylistLibrary.getInstance().getPlaylists().get(i).getName().equals(name)) {
                resultCreatePlaylist.setMessage("A playlist with the same name already exists.");
                return resultCreatePlaylist;
            }
        }
        Playlist playlist = new Playlist();
        playlist.setPrivacy("public");
        playlist.setUser(this.username);
        playlist.setName(name);
        resultCreatePlaylist.setMessage("Playlist created successfully.");
        playlists.addLast(playlist);
        PlaylistLibrary.getInstance().getPlaylists().addLast(playlist);
        return resultCreatePlaylist;
    }

    /**
     * Adds the currently played song to a given playlist
     * @param command
     * @return
     */
    public ResultAddRemove addRemove(final Command command) {
        ResultAddRemove resultAddRemove = new ResultAddRemove(command);
        if (offline) {
            resultAddRemove.setMessage(username + " is offline.");
            return resultAddRemove;
        }
        int add;
        updatePlayer(command);
        if (command.getPlaylistId() > playlists.size()) {
            resultAddRemove.setMessage("The specified playlist does not exist.");
            return resultAddRemove;
        }
        if (player.getType().equals("nothing")) {
            String message = "Please load a source before adding";
            resultAddRemove.setMessage(message + " to or removing from the playlist.");
            return resultAddRemove;
        }
        if (player.getType().equals("podcast")) {
            resultAddRemove.setMessage("The loaded source is not a song.");
            return resultAddRemove;
        }
        add = playlists.get(command.getPlaylistId() - 1).addRemove((Song) player.getCurrFile());
        if (add == 0) {
            resultAddRemove.setMessage("Successfully removed from playlist.");
        } else {
            resultAddRemove.setMessage("Successfully added to playlist.");
        }
        return resultAddRemove;
    }

    /**
     * Changes the repeat status of the player
     * @param command
     * @return
     */
    public ResultRepeat repeat(final Command command) {
        ResultRepeat resultRepeat = new ResultRepeat(command);
        if (offline) {
            resultRepeat.setMessage(username + " is offline.");
            return resultRepeat;
        }
        updatePlayer(command);
        if (player.getType().equals("nothing")) {
            resultRepeat.setMessage("Please load a source before setting the repeat status.");
            return resultRepeat;
        }
        if (player.getRepeat() == 2) {
            player.setRepeat(0);
        } else {
            player.setRepeat(player.getRepeat() + 1);
        }
        if (player.getRepeat() == 0) {
            resultRepeat.setMessage("Repeat mode changed to no repeat.");
        }
        if (player.getRepeat() == 1) {
            if (player.getType().equals("playlist") || player.getType().equals("album")) {
                resultRepeat.setMessage("Repeat mode changed to repeat all.");
            } else {
                resultRepeat.setMessage("Repeat mode changed to repeat once.");
            }
        }
        if (player.getRepeat() == 2) {
            if (player.getType().equals("playlist") || player.getType().equals("album")) {
                resultRepeat.setMessage("Repeat mode changed to repeat current song.");
            } else {
                resultRepeat.setMessage("Repeat mode changed to repeat infinite.");
            }
        }
        return resultRepeat;
    }

    /**
     * Changes the shuffle status of the player
     * @param command
     * @return
     */
    public ResultShuffle shuffle(final Command command) {
        ResultShuffle resultShuffle = new ResultShuffle(command);
        if (offline) {
            resultShuffle.setMessage(username + " is offline.");
            return resultShuffle;
        }
        updatePlayer(command);
        if (player.getType().equals("nothing")) {
            resultShuffle.setMessage("Please load a source before using the shuffle function.");
            return resultShuffle;
        }
        if (!player.getType().equals("playlist") && !player.getType().equals("album")) {
            resultShuffle.setMessage("The loaded source is not a playlist or an album.");
            return resultShuffle;
        }
        if (!player.isShuffle()) {
            int[] ordine = player.getOrder();
            int currPosition = player.getCurrentObject();
            int newCurrPosition = 0;
            LinkedList<Integer> newordine = new LinkedList<Integer>();
            for (int i = 0; i < ordine.length; i++) {
                newordine.addLast(ordine[i]);
            }
            Collections.shuffle(newordine, new Random(command.getSeed()));
            for (int i = 0; i < ordine.length; i++) {
                ordine[i] = newordine.get(i);
            }
            player.setOrder(ordine);
            for (int i = 0; i < ordine.length; i++) {
                if (ordine[i] == currPosition) {
                    newCurrPosition = i;
                    break;
                }
            }
            player.setCurrentObject(newCurrPosition);
            player.setShuffle(true);
            resultShuffle.setMessage("Shuffle function activated successfully.");
            return resultShuffle;
        }
        if (player.getType().equals("playlist")) {
            Playlist aux = (Playlist) player.getSource();
            player.setOrder(IntStream.range(0, aux.getSongs().size()).toArray());
            player.setCurrentObject(aux.getSongs().indexOf((Song) player.getCurrFile()));
        } else {
            Album aux = (Album) player.getSource();
            player.setOrder(IntStream.range(0, aux.getSongs().size()).toArray());
            player.setCurrentObject(aux.getSongs().indexOf((Song) player.getCurrFile()));
        }
        player.setShuffle(false);
        resultShuffle.setMessage("Shuffle function deactivated successfully.");
        return resultShuffle;
    }

    /**
     * Advances the player forward by 90 seconds
     * @param command
     * @return
     */
    public ResultForwardBackward forward(final Command command) {
        ResultForwardBackward resultForwardBackward = new ResultForwardBackward(command);
        if (offline) {
            resultForwardBackward.setMessage(username + " is offline.");
            return resultForwardBackward;
        }
        updatePlayer(command);
        if (player.getType().equals("nothing")) {
            resultForwardBackward.setMessage("Please load a source before attempting to forward.");
            return  resultForwardBackward;
        }
        if (!player.getType().equals("podcast")) {
            resultForwardBackward.setMessage("The loaded source is not a podcast.");
            return  resultForwardBackward;
        }
        PodcastInfo info = containsPodcast(((Podcast) player.getSource()).getName());
        player.forward(command);
        if (player.getType().equals("nothing")) {
            podcastInfos.remove(info);
        }
        resultForwardBackward.setMessage("Skipped forward successfully.");
        return resultForwardBackward;
    }

    /**
     * Moves back the player by 90 seconds
     * @param command
     * @return
     */
    public ResultForwardBackward backward(final Command command) {
        ResultForwardBackward resultForwardBackward = new ResultForwardBackward(command);
        if (offline) {
            resultForwardBackward.setMessage(username + " is offline.");
            return resultForwardBackward;
        }
        updatePlayer(command);
        if (player.getType().equals("nothing")) {
            resultForwardBackward.setMessage("Please select a source before rewinding.");
            return  resultForwardBackward;
        }
        if (!player.getType().equals("podcast")) {
            resultForwardBackward.setMessage("The loaded source is not a podcast.");
            return  resultForwardBackward;
        }
        player.backward(command);
        resultForwardBackward.setMessage("Rewound successfully.");
        return resultForwardBackward;
    }

    /**
     * Changes to the next source
     * @param command
     * @return
     */
    public ResultForwardBackward next(final Command command) {
        ResultForwardBackward resultForwardBackward = new ResultForwardBackward(command);
        String message;
        if (offline) {
            resultForwardBackward.setMessage(username + " is offline.");
            return resultForwardBackward;
        }
        updatePlayer(command);
        String previousstatus = player.getType();
        PodcastInfo info = null;
        if (previousstatus.equals("podcast")) {
            info = containsPodcast(((Podcast) player.getSource()).getName());
        }
        if (player.getNextAd() == 2) {
            message = "Can not skip over an add";
            resultForwardBackward.setMessage(message);
            return  resultForwardBackward;
        }
        if (player.getType().equals("nothing")) {
            message = "Please load a source before skipping to the next track.";
            resultForwardBackward.setMessage(message);
            return  resultForwardBackward;
        }
        player.next(command);
        if (info != null && player.getType().equals("nothing")) {
            podcastInfos.remove(info);
        }
        if (player.getType().equals("nothing")) {
            message = "Please load a source before skipping to the next track.";
            resultForwardBackward.setMessage(message);
            return  resultForwardBackward;
        }
        message = "Skipped to next track successfully. The current track is ";
        resultForwardBackward.setMessage(message + player.getCurrFile().getName() + ".");
        player.setPause(false);
        return resultForwardBackward;
    }

    /**
     * Changes to the previous source
     * @param command
     * @return
     */
    public ResultForwardBackward prev(final Command command) {
        String message;
        ResultForwardBackward resultForwardBackward = new ResultForwardBackward(command);
        if (offline) {
            resultForwardBackward.setMessage(username + " is offline.");
            return resultForwardBackward;
        }
        updatePlayer(command);
        if (player.getNextAd() == 2) {
            message = "Can not skip over an add";
            resultForwardBackward.setMessage(message);
            return  resultForwardBackward;
        }
        if (player.getType().equals("nothing")) {
            message = "Please load a source before returning to the previous track.";
            resultForwardBackward.setMessage(message);
            return  resultForwardBackward;
        }
        player.prev(command);
        message = "Returned to previous track successfully. The current track is ";
        resultForwardBackward.setMessage(message + player.getCurrFile().getName() + ".");
        player.setPause(false);
        return resultForwardBackward;
    }

    /**
     * Shows all the playlists of the current user
     * @param command
     * @return
     */
    public ResultShowPlaylists showPlaylists(final Command command) {
        ResultShowPlaylists resultShowPlaylists = new ResultShowPlaylists(command, playlists);
        return resultShowPlaylists;
    }

    /**
     * Likes the current song that is playing
     * @param command
     * @return
     */
    public ResultLike like(final Command command) {
        ResultLike resultLike = new ResultLike(command);
        if (offline) {
            resultLike.setMessage(username + " is offline.");
            return resultLike;
        }
        updatePlayer(command);
        if (player.getType().equals("nothing")) {
            resultLike.setMessage("Please load a source before liking or unliking.");
            return resultLike;
        }
        if (player.getType().equals("podcast")) {
            resultLike.setMessage("Loaded source is not a song.");
            return resultLike;
        }
        Song song = (Song) player.getCurrFile();
        if (preferedSongs.contains(song)) {
            resultLike.setMessage("Unlike registered successfully.");
            song.setLikes(song.getLikes() - 1);
            preferedSongs.remove(song);
        } else {
            resultLike.setMessage("Like registered successfully.");
            song.setLikes(song.getLikes() + 1);
            preferedSongs.addLast(song);
        }
        return resultLike;
    }

    /**
     * Shows all the songs liked by the user
     * @param command
     * @return
     */
    public ResultShowPrefered showPrefered(final Command command) {
        ResultShowPrefered resultShowPrefered = new ResultShowPrefered(command);
        LinkedList<String> prefered = new LinkedList<String>();
        for (int i = 0; i < preferedSongs.size(); i++) {
            prefered.addLast(preferedSongs.get(i).getName());
        }
        resultShowPrefered.setResult(prefered);
        return resultShowPrefered;
    }

    /**
     * Follows the given playlist
     * @param command
     * @return
     */
    public ResultLike follow(final Command command) {
        ResultLike resultFollow = new ResultLike(command);
        if (offline) {
            resultFollow.setMessage(username + " is offline.");
            return resultFollow;
        }
        if (selectedItem == null) {
            resultFollow.setMessage("Please select a source before following or unfollowing.");
            return resultFollow;
        }
        if (!lastsearch.equals("playlist")) {
            resultFollow.setMessage("The selected source is not a playlist.");
            return resultFollow;
        }
        if (((Playlist) selectedItem).getUser().equals(username)) {
            resultFollow.setMessage("You cannot follow or unfollow your own playlist.");
            return resultFollow;
        }
        if (followed.contains((Playlist) selectedItem)) {
            resultFollow.setMessage("Playlist unfollowed successfully.");
            ((Playlist) selectedItem).setFollowers(((Playlist) selectedItem).getFollowers() - 1);
            followed.remove((Playlist) selectedItem);
            User usr = Userbase.getInstance().searchUser(((Playlist) selectedItem).getUser());
            if (usr != null) {
                Notification notif = new Notification();
                notif.setName("Unfollowed Playlist");
                notif.setDescription(this.username + " unfollowed one of your playlists");
                usr.addNotification(notif);
            }
        } else {
            resultFollow.setMessage("Playlist followed successfully.");
            ((Playlist) selectedItem).setFollowers(((Playlist) selectedItem).getFollowers() + 1);
            followed.addLast((Playlist) selectedItem);
            User usr = Userbase.getInstance().searchUser(((Playlist) selectedItem).getUser());
            if (usr != null) {
                Notification notif = new Notification();
                notif.setName("Followed Playlist");
                notif.setDescription(this.username + " started following one of your playlists");
                usr.addNotification(notif);
            }
        }
        return resultFollow;
    }

    /**
     * Switches the visibility of a playlist
     * @param command
     * @return
     */
    public ResultSwitch switchVisibility(final Command command) {
        ResultSwitch resultSwitch = new ResultSwitch(command);
        if (offline) {
            resultSwitch.setMessage(username + " is offline.");
            return resultSwitch;
        }
        if (command.getPlaylistId() > playlists.size()) {
            resultSwitch.setMessage("The specified playlist ID is too high.");
            return resultSwitch;
        }
        Playlist playlist = playlists.get(command.getPlaylistId() - 1);
        if (playlist.getPrivacy().equals("public")) {
            resultSwitch.setMessage("Visibility status updated successfully to private.");
            playlist.setPrivacy("private");
        } else {
            resultSwitch.setMessage("Visibility status updated successfully to public.");
            playlist.setPrivacy("public");
        }
        return resultSwitch;
    }

    /**
     * Function that switches the current connection status of the user
     * and also updates the player of the current user
     * @param command
     * @return
     */
    public final ResultSwitch switchConnection(final Command command) {
        ResultSwitch resultSwitch = new ResultSwitch(command);
        if (type != 0) {
            resultSwitch.setMessage(username + " is not a normal user.");
            return resultSwitch;
        }
        if (!offline) {
            updatePlayer(command);
            offline = true;
        } else {
            player.setLasttimestamp(command.getTimestamp());
            offline = false;
        }
        resultSwitch.setMessage(username + " has changed status successfully.");
        return resultSwitch;
    }

    /**
     * Function that changes on which page is the current user
     * @param command
     * @return
     */
    public final ResultSwitch changePage(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        if (type != 0) {
            result.setMessage(username + " is not a normal user");
            return result;
        }
        String nextPage = command.getNextPage();
        PageHistory.Page newPage = new PageHistory.Page(null, nextPage, this);
        if (!nextPage.equals("Home") && !nextPage.equals("LikedContent")) {
            if (nextPage.equals("Artist")) {
                if ((player.getType().equals("nothing")) || player.getType().equals("podcast")) {
                    result.setMessage(username + " is trying to access a non-existent page.");
                    return result;
                }
                Userbase ub = Userbase.getInstance();
                boolean found = false;
                for (int i = 0; i < ub.getUserbase().size(); i++) {
                    User us = ub.getUserbase().get(i);
                    if (us.getUsername().equals(((Song) player.getCurrFile()).getArtist())) {
                        found = true;
                        newPage.setNextUser(us);
                        newPage.setNextPage("ArtistPage");
                    }
                }
                if (!found) {
                    result.setMessage(username + " is trying to access a non-existent page.");
                    return result;
                }
            }
            if (nextPage.equals("Host")) {
                if ((!player.getType().equals("podcast"))) {
                    result.setMessage(username + " is trying to access a non-existent page.");
                    return result;
                }
                Userbase ub = Userbase.getInstance();
                boolean found = false;
                for (int i = 0; i < ub.getUserbase().size(); i++) {
                    User us = ub.getUserbase().get(i);
                    if (us.getUsername().equals(((Podcast) player.getSource()).getOwner())) {
                        found = true;
                        newPage.setNextUser(us);
                        newPage.setNextPage("HostPage");
                        break;
                    }
                }
                if (!found) {
                    result.setMessage(username + " is trying to access a non-existent page.");
                    return result;
                }
            }
        }
        if (command.getNextPage().equals("Home")) {
            newPage.setNextUser(this);
            newPage.setNextPage("HomePage");
        } else if (command.getNextPage().equals("LikedContent")){
            newPage.setNextUser(this);
            newPage.setNextPage("LikedContentPage");
        }
        newPage.execute();
        pageHistory.ResetRedoLs();
        pageHistory.getUndoLs().addLast(newPage);
        result.setMessage(username + " accessed " + command.getNextPage() + " successfully.");
        return  result;
    }

    public ResultSwitch nextPage(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        if (pageHistory.getRedoLs().isEmpty()) {
            result.setMessage("There are no pages left to go forward.");
            return result;
        }
        pageHistory.redo();
        result.setMessage("The user " + username + " has navigated successfully to the next page.");
        return result;
    }

    public ResultSwitch previousPage(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        if (pageHistory.getUndoLs().isEmpty()) {
            result.setMessage("There are no pages left to go back.");
            return result;
        }
        pageHistory.undo();
        result.setMessage("The user " + username + " has navigated successfully to the previous page.");
        return result;
    }

    /**
     * Function that prints the current page that the user is on
     * @param v
     * @param command
     * @return
     */
    public final ResultSwitch currentPage(final Visitor v, final Command command) {
        if (offline) {
            ResultSwitch result = new ResultSwitch(command);
            result.setMessage(username + " is offline.");
            return result;
        }
        if (type != 0) {
            ResultSwitch result = new ResultSwitch(command);
            result.setMessage("User in not normal.");
            return result;
        }
        if (this.currentPage.equals("ArtistPage")) {
            return v.visit((Artist) currUserrPage, command);
        }
        if (this.currentPage.equals("HostPage")) {
            return v.visit((Host) currUserrPage, command);
        }
        return v.visit(currUserrPage, command);
    }

    public void updateWrapped(String type, Object obj) {
        boolean exists = false;
        if (type.equals("song")) {
            Song currSong = (Song) obj;
            for (int i = 0; i < wrapped.getTopSong().size(); i++) {
                Song wrsg = wrapped.getTopSong().get(i).getSong();
                if (wrsg.getName().equals(currSong.getName())) {
                    exists = true;
                    int listens = wrapped.getTopSong().get(i).getListen();
                    wrapped.getTopSong().get(i).setListen(listens + 1);
                    break;
                }
            }
            if (!exists) {
                wrapped.getTopSong().addLast(new Wrapped.SongListen());
                wrapped.getTopSong().getLast().setListen(1);
                wrapped.getTopSong().getLast().setSong(currSong);
            }
            if (isPremium) {
                exists = false;
                premium.setTotalls(premium.getTotalls() + 1);
                for (int i = 0; i < premium.getSongs().size(); i++) {
                    Song wrsg = premium.getSongs().get(i).getSong();
                    if (wrsg.getName().equals(currSong.getName())) {
                        exists = true;
                        int listens = premium.getSongs().get(i).getListen();
                        premium.getSongs().get(i).setListen(listens + 1);
                        break;
                    }
                }
                if (!exists) {
                    premium.getSongs().addLast(new Wrapped.SongListen());
                    premium.getSongs().getLast().setListen(1);
                    premium.getSongs().getLast().setSong(currSong);
                }
            }
            return;
        }
        if (type.equals("album")) {
            String currAlb = (String) obj;
            for (int i = 0; i < wrapped.getTopAlbum().size(); i++) {
                String wral = wrapped.getTopAlbum().get(i).getAlbum();
                if (wral.equals(currAlb)) {
                    exists = true;
                    int listens = wrapped.getTopAlbum().get(i).getListen();
                    wrapped.getTopAlbum().get(i).setListen(listens + 1);
                    break;
                }
            }
            if (!exists) {
                wrapped.getTopAlbum().addLast(new Wrapped.AlbumListen());
                wrapped.getTopAlbum().getLast().setListen(1);
                wrapped.getTopAlbum().getLast().setAlbum(currAlb);
            }
            return;
        }
        if (type.equals("episode")) {
            Episode currPod = (Episode) obj;
            for (int i = 0; i < wrapped.getTopEpisode().size(); i++) {
                Episode wrpd = wrapped.getTopEpisode().get(i).getEpisode();
                if (wrpd.getName().equals(currPod.getName())) {
                    exists = true;
                    int listens = wrapped.getTopEpisode().get(i).getListen();
                    wrapped.getTopEpisode().get(i).setListen(listens + 1);
                    break;
                }
            }
            if (!exists) {
                wrapped.getTopEpisode().addLast(new Wrapped.PodcastListen());
                wrapped.getTopEpisode().getLast().setListen(1);
                wrapped.getTopEpisode().getLast().setEpisode(currPod);
            }
            return;
        }
        if (type.equals("artist")) {
            String currArt = (String) obj;
            for (int i = 0; i < wrapped.getTopArtist().size(); i++) {
                String wrat = wrapped.getTopArtist().get(i).getArtist();
                if (wrat.equals(currArt)) {
                    exists = true;
                    int listens = wrapped.getTopArtist().get(i).getListen();
                    wrapped.getTopArtist().get(i).setListen(listens + 1);
                    break;
                }
            }
            if (!exists) {
                wrapped.getTopArtist().addLast(new Wrapped.ArtistListen());
                wrapped.getTopArtist().getLast().setListen(1);
                wrapped.getTopArtist().getLast().setArtist(currArt);
            }
            return;
        }
        if (type.equals("genre")) {
            String currGen = (String) obj;
            for (int i = 0; i < wrapped.getTopGenre().size(); i++) {
                String wrgn = wrapped.getTopGenre().get(i).getGenre();
                if (wrgn.equals(currGen)) {
                    exists = true;
                    int listens = wrapped.getTopGenre().get(i).getListen();
                    wrapped.getTopGenre().get(i).setListen(listens + 1);
                    break;
                }
            }
            if (!exists) {
                wrapped.getTopGenre().addLast(new Wrapped.GenreListen());
                wrapped.getTopGenre().getLast().setListen(1);
                wrapped.getTopGenre().getLast().setGenre(currGen);
            }
        }
    }

    public CommandResults printWrapped(final Command command) {
        player.status(command);
        int tot = wrapped.getTopAlbum().size();
        tot += wrapped.getTopArtist().size();
        tot += wrapped.getTopGenre().size();
        tot += wrapped.getTopEpisode().size();
        tot += wrapped.getTopSong().size();
        if (tot == 0) {
            ResultSwitch res = new ResultSwitch(command);
            res.setMessage("No data to show for user " + username + ".");
            return res;
        }
        Collections.sort(wrapped.getTopAlbum());
        Collections.sort(wrapped.getTopArtist());
        Collections.sort(wrapped.getTopEpisode());
        Collections.sort(wrapped.getTopSong());
        Collections.sort(wrapped.getTopGenre());
        ResultWrappedUser result = new ResultWrappedUser(command, this);
        return result;
    }

    public ResultSwitch buyPremium(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        updatePlayer(command);
        if (type != 0) {
            result.setMessage(username + " is not a normal user.");
            return result;
        }
        if (isPremium) {
            result.setMessage(username + " is already a premium user.");
            return result;
        }
        isPremium = true;
        premium.reserList();
        result.setMessage(username + " bought the subscription successfully.");
        return result;
    }

    public ResultSwitch cancelPremium(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        updatePlayer(command);
        if (type != 0) {
            result.setMessage(username + " is not a normal user.");
            return result;
        }
        if (!isPremium) {
            result.setMessage(username + " is not a premium user.");
            return result;
        }
        Userbase ub = Userbase.getInstance();
        for (int i = 0; i < premium.getSongs().size(); i++) {
            Song crSg = premium.getSongs().get(i).getSong();
            for (int j = 0; j < ub.getArtistData().size(); j++) {
                Userbase.ArtistData artd = ub.getArtistData().get(j);
                if (artd.getArtist().equals(crSg.getArtist())) {
                    for (int k = 0; k < artd.getSongIncs().size(); k++) {
                        String crname = artd.getSongIncs().get(k).getSong().getName();
                        if (crname.equals(crSg.getName())) {
                            double inc = artd.getSongIncs().get(k).getInc();
                            inc += ((double) 1000000 / premium.getTotalls()) * premium.getSongs().get(i).getListen();
                            artd.getSongIncs().get(k).setInc(inc);
                            break;
                        }
                    }
                    double inc = artd.getSongrev();
                    inc += ((double) 1000000 / premium.getTotalls()) * premium.getSongs().get(i).getListen();
                    artd.setSongrev(inc);
                    break;
                }
            }
        }
        isPremium = false;
        result.setMessage(username + " cancelled the subscription successfully.");
        return result;
    }

    public ResultSwitch adBreak(final Command command) {
        ResultSwitch res = new ResultSwitch(command);
        updatePlayer(command);
        if (player.getType().equals("nothing") || player.getType().equals("podcast")) {
            res.setMessage(username + " is not playing any music.");
            return res;
        }
        for (int i = 0; i < Library.getInstance().getSongs().size(); i++) {
            Song crsg = Library.getInstance().getSongs().get(i);
            if (crsg.getName().equals("Ad Break")) {
                player.setAd(crsg);
                player.setNextAd(1);
                player.setAdrev(command.getPrice());
                break;
            }
        }
        res.setMessage("Ad inserted successfully.");
        return res;
    }

    @Override
    public void addNotification(Notification Notific) {
        notifications.addLast(Notific);
    }

    public ResultNotification getNotif(final Command command) {
        ResultNotification res = new ResultNotification(command);
        res.setNotifications(notifications);
        notifications = new LinkedList<>();
        return res;
    }

    public ResultSwitch subscribe(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        if (isOffline()) {
            result.setMessage("User is offline");
            return result;
        }
        if (type != 0) {
            result.setMessage(username + " is not a normal user");
            return result;
        }
        if (!currentPage.equals("HostPage") && !currentPage.equals("ArtistPage")) {
            result.setMessage("To subscribe you need to be on the page of an artist or host.");
            return result;
        }
        if (currentPage.equals("ArtistPage")) {
            Artist art = (Artist) currUserrPage;
            int index = art.searchSubscr(this.username);
            if (index != -1) {
                art.getSubscribers().remove(index);
                result.setMessage(username + " unsubscribed from " + art.getUsername() + " successfully.");
                return result;
            }
            art.getSubscribers().addLast(this);
            result.setMessage(username + " subscribed to " + art.getUsername() + " successfully.");
            return result;
        }
        Host host = (Host) currUserrPage;
        int index = host.searchSubscr(this.username);
        if (index != -1) {
            host.getSubscribers().remove(index);
            result.setMessage(username + " unsubscribed from " +host.getUsername() + " successfully.");
            return result;
        }
        host.getSubscribers().addLast(this);
        result.setMessage(username + " subscribed to " + host.getUsername() + " successfully.");
        return result;
    }

    public ResultSwitch buyMerch(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        if (!currentPage.equals("ArtistPage")) {
            result.setMessage("Cannot buy merch from this page.");
            return result;
        }
        boolean found = false;
        double price = 0;
        Artist art = (Artist) currUserrPage;
        for (int i = 0; i < art.getMerch().size(); i++) {
            if (art.getMerch().get(i).getMerchname().equals(command.getName())) {
                mymerch.addLast(command.getName());
                found = true;
                price = art.getMerch().get(i).getMerchprice();
                break;
            }
        }
        if (!found) {
            result.setMessage("The merch " + command.getName() + " doesn't exist.");
            return result;
        }
        int index = Userbase.getInstance().searchStat(currUserrPage.getUsername());
        price += Userbase.getInstance().getArtistData().get(index).getMerchrev();
        Userbase.getInstance().getArtistData().get(index).setMerchrev(price);
        result.setMessage(username + " has added new merch successfully.");
        return result;
    }

    public ResultMerch seeMerch(final Command command) {
        ResultMerch result = new ResultMerch(command);
        LinkedList<String> results = new LinkedList<>();
        for (int i = 0; i < mymerch.size(); i++) {
            results.addLast(mymerch.get(i));
        }
        result.setResult(results);
        return result;
    }

    public void updateTopGenres() {
        topGenres.setGenres(new LinkedList<>());
        for (int i = 0; i < preferedSongs.size(); i++) {
            topGenres.addGenre(preferedSongs.get(i).getGenre());
        }
        for (int i = 0; i < followed.size(); i++) {
            Playlist play = followed.get(i);
            for (int j = 0; j < play.getSongs().size(); j++) {
                topGenres.addGenre(play.getSongs().get(j).getGenre());
            }
        }
        for (int i = 0; i < playlists.size(); i++) {
            Playlist play = playlists.get(i);
            for (int j = 0; j < play.getSongs().size(); j++) {
                topGenres.addGenre(play.getSongs().get(j).getGenre());
            }
        }
        Collections.sort(topGenres.genres);
        nrTopGenres = topGenres.genres.size();
        genre1 = new LinkedList<>();
        genre2 = new LinkedList<>();
        genre3 = new LinkedList<>();
        Library lb = Library.getInstance();
        for (int i = 0; i < lb.getSongs().size(); i++) {
            Song sg = lb.getSongs().get(i);
            if (nrTopGenres != 0) {
                if (sg.getGenre().equals(topGenres.genres.get(0).getGenre())) {
                    genre1.addLast(sg);
                }
            }
            if (nrTopGenres > 1) {
                if (sg.getGenre().equals(topGenres.genres.get(1).getGenre())) {
                    genre2.addLast(sg);
                }
            }
            if (nrTopGenres > 2) {
                if (sg.getGenre().equals(topGenres.genres.get(2).getGenre())) {
                    genre3.addLast(sg);
                }
            }
        }
        Collections.sort(genre1);
        Collections.sort(genre2);
        Collections.sort(genre3);
    }

    public ResultSwitch updateRecom(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        updatePlayer(command);
        if (command.getRecommendationType().equals("random_song")) {
            if (!player.getType().equals("song")) {
                result.setMessage("No new recommendations were found");
                return result;
            }
            int passed = ((Song) player.getCurrFile()).getDuration();
            passed -= player.getTimeremaining();
            if (passed < 30) {
                result.setMessage("No new recommendations were found");
                return result;
            }
            String genre = ((Song) player.getCurrFile()).getGenre();
            genre1 = new LinkedList<>();
            Library lb = Library.getInstance();
            for (int i = 0; i < lb.getSongs().size(); i++) {
                Song sg = lb.getSongs().get(i);
                if (sg.getGenre().equals(genre)) {
                    genre1.addLast(sg);
                }
            }
            Random random = new Random(passed);
            int nrsong = random.nextInt(genre1.size());
            Song sg = genre1.get(nrsong);
            for (int i = 0; i < recSongs.size(); i++) {
                if (recSongs.get(i).getName().equals(sg.getName())) {
                    result.setMessage("No new recommendations were found");
                    return result;
                }
            }
            recSongs.addLast(sg);
            result.setMessage("The recommendations for user " + username + " have been updated successfully.");
            lastAddRecom = "song";
            return result;
        }
        if (command.getRecommendationType().equals("fans_playlist")) {
            final int nr = 5;
            Playlist playlist = new Playlist();
            playlist.setUser(this.getUsername());
            playlist.setPrivacy("public");
            if (!player.getType().equals("playlist")) {
                result.setMessage("No new recommendations were found");
                return result;
            }
            String name = ((Song) player.getCurrFile()).getArtist();
            Userbase ub = Userbase.getInstance();
            Artist art = null;
            for (int i = 0; i < ub.getUserbase().size(); i++) {
                if (ub.getUserbase().get(i).getUsername().equals(name)) {
                    art = (Artist) ub.getUserbase().get(i);
                    break;
                }
            }
            if (art == null) {
                result.setMessage("No new recommendations were found");
                return result;
            }
            Collections.sort(art.getWrappedartist().getUserListens());
            int size = art.getWrappedartist().getUserListens().size();
            for (int i = 0; i < nr && i < size; i++) {
                String usrnm = art.getWrappedartist().getUserListens().get(i).getUser();
                User user = null;
                for (int j = 0; j < ub.getUserbase().size(); j++) {
                    if (ub.getUserbase().get(j).getUsername().equals(name)) {
                        user = ub.getUserbase().get(j);
                        break;
                    }
                }
                if (user == null) {
                    break;
                }
                int nr2 = 5;
                genre1 = new LinkedList<>();
                for (int j = 0; j < user.getPreferedSongs().size(); j++) {
                    genre1.addLast(user.getPreferedSongs().get(j));
                }
                Collections.sort(genre1);
                for (int j = 0; j < genre1.size(); j++) {
                    Song sg = genre1.get(j);
                    boolean duplicate = false;
                    for (int k = 0; k < playlist.getSongs().size(); k++) {
                        if (playlist.getSongs().get(k).getName().equals(sg.getName())) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        nr2--;
                        playlist.getSongs().addLast(sg);
                        if (nr2 == 0) {
                            break;
                        }
                    }
                }
            }
            playlist.setName(art.getUsername() + " Fan Club recommendations");
            recPlaylist.addLast(playlist);
            result.setMessage("The recommendations for user " + username + " have been updated successfully.");
            lastAddRecom = "playlist";
            return result;
        }
        updateTopGenres();
        Playlist playlist = new Playlist();
        playlist.setName(username + "'s recommendations");
        playlist.setPrivacy("public");
        playlist.setUser(username);
        final int nr1 = 5, nr2 = 3, nr3 = 2;
        if (nrTopGenres == 0) {
            result.setMessage("No new recommendations were found");
            return result;
        }
        for (int i = 0; i < nr1 && i < genre1.size(); i++) {
            playlist.getSongs().addLast(genre1.get(i));
        }
        if (nrTopGenres > 1) {
            for (int i = 0; i < nr2 && i < genre2.size(); i++) {
                playlist.getSongs().addLast(genre2.get(i));
            }
        }
        if (nrTopGenres > 2) {
            for (int i = 0; i < nr3 && i < genre3.size(); i++) {
                playlist.getSongs().addLast(genre3.get(i));
            }
        }
        recPlaylist.addLast(playlist);
        result.setMessage("The recommendations for user " + username + " have been updated successfully.");
        lastAddRecom = "playlist";
        return result;
    }

    public ResultLoad loadRecom(final Command command) {
        ResultLoad result = new ResultLoad(command);
        if (isOffline()) {
            result.setMessage(username + " is offline.");
            return result;
        }
        if (lastAddRecom.equals("nothing")) {
            result.setMessage("No recommendations available.");
            return result;
        }
        player.status(command);
        if (player.getType().equals("podcast")) {
            PodcastInfo info = containsPodcast(((Podcast) player.getSource()).getName());
            if (info != null) {
                info.setCurrepisode(player.getCurrentObject());
                info.setRemainingtime(player.getTimeremaining());
                info.setOwner(((Podcast) player.getSource()).getOwner());
                if (player.getType().equals("nothing")) {
                    podcastInfos.remove(info);
                }
            } else {
                info = new PodcastInfo();
                info.setPodcastname(((Podcast) player.getSource()).getName());
                info.setRemainingtime(player.getTimeremaining());
                info.setCurrepisode(player.getCurrentObject());
                info.setOwner(((Podcast) player.getSource()).getOwner());
                podcastInfos.addLast(info);
            }
        }
        player.setSource(null);
        player.setType("nothing");
        lastsearch = "nothing";
        player.setAd(null);
        player.setNextAd(0);
        player.setAdrev(0);
        if (lastAddRecom.equals("song")) {
            Song sg = recSongs.get(recSongs.size() - 1);
            player.setTimeremaining(sg.getDuration());
            player.setType("song");
            player.setCurrFile(sg);
            player.setPause(false);
            updateWrapped("song", player.getCurrFile());
            updateWrapped("artist", sg.getArtist());
            updateWrapped("genre", sg.getGenre());
            updateWrapped("album", sg.getAlbum());
            Userbase.getInstance().addStats(sg.getArtist(), sg);
            Userbase.getInstance().updateArt(sg, this, sg.getArtist());
            player.addSg(sg);
            player.setSource(sg);
            player.setLasttimestamp(command.getTimestamp());
            player.setPause(false);
            player.setRepeat(0);
            player.setShuffle(false);
        }
        if (lastAddRecom.equals("playlist")) {
            Playlist playlist = recPlaylist.get(recPlaylist.size() - 1);
            int aux = playlist.getSongs().size();
            player.setTimeremaining(playlist.getSongs().get(0).getDuration());
            player.setType("playlist");
            player.setCurrFile(playlist.getSongs().get(0));
            player.setCurrentObject(0);
            player.setOrder(IntStream.range(0, aux).toArray());
            Song currSong = playlist.getSongs().get(0);
            updateWrapped("song", currSong);
            updateWrapped("artist", currSong.getArtist());
            updateWrapped("genre", currSong.getGenre());
            updateWrapped("album", currSong.getAlbum());
            Userbase.getInstance().addStats(currSong.getArtist(), currSong);
            Userbase.getInstance().updateArt(currSong, this, currSong.getArtist());
            player.addSg(currSong);
            player.setSource(playlist);
            player.setLasttimestamp(command.getTimestamp());
            player.setPause(false);
            player.setRepeat(0);
            player.setShuffle(false);
        }
        result.setMessage("Playback loaded successfully.");
        return result;
    }

    public final boolean isOffline() {
        return offline;
    }

    public final void setOffline(final boolean offline) {
        this.offline = offline;
    }

    public final int getType() {
        return type;
    }

    public final void setType(final int type) {
        this.type = type;
    }

    public final void setSearcheditems(final LinkedList<Object> searcheditems) {
        this.searcheditems = searcheditems;
    }

    public final Object getSelectedItem() {
        return selectedItem;
    }

    public final void setSelectedItem(final Object selectedItem) {
        this.selectedItem = selectedItem;
    }

    public final Player getPlayer() {
        return player;
    }

    public final void setPlayer(final Player player) {
        this.player = player;
    }

    public final LinkedList<Song> getPreferedSongs() {
        return preferedSongs;
    }

    public final void setPreferedSongs(final LinkedList<Song> preferedSongs) {
        this.preferedSongs = preferedSongs;
    }

    public final LinkedList<Playlist> getPlaylists() {
        return playlists;
    }

    public final void setPlaylists(final LinkedList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public final LinkedList<Playlist> getFollowed() {
        return followed;
    }

    public final void setFollowed(final LinkedList<Playlist> followed) {
        this.followed = followed;
    }

    public final LinkedList<PodcastInfo> getPodcastInfos() {
        return podcastInfos;
    }

    public final void setPodcastInfos(final LinkedList<PodcastInfo> podcastInfos) {
        this.podcastInfos = podcastInfos;
    }

    public final String getLastsearch() {
        return lastsearch;
    }

    public final void setLastsearch(final String lastsearch) {
        this.lastsearch = lastsearch;
    }

    public final LinkedList<Object> getSearcheditems() {
        return searcheditems;
    }

    public final void setSearchedsongs(final LinkedList<Object> searcheditem) {
        this.searcheditems = searcheditem;
    }

    public final String getUsername() {
        return username;
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final int getAge() {
        return age;
    }

    public final void setAge(final int age) {
        this.age = age;
    }

    public final String getCity() {
        return city;
    }

    public final void setCity(final String city) {
        this.city = city;
    }

    public final String getCurrentPage() {
        return currentPage;
    }

    public final void setCurrentPage(final String currentPage) {
        this.currentPage = currentPage;
    }

    public final User getCurrUserrPage() {
        return currUserrPage;
    }

    public final void setCurrUserrPage(final User currUserrPage) {
        this.currUserrPage = currUserrPage;
    }

    public Wrapped getWrapped() {
        return wrapped;
    }

    public void setWrapped(Wrapped wrapped) {
        this.wrapped = wrapped;
    }

    public LinkedList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(LinkedList<Notification> notifications) {
        this.notifications = notifications;
    }

    public Premium getPremium() {
        return premium;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public LinkedList<String> getMymerch() {
        return mymerch;
    }

    public void setMymerch(LinkedList<String> mymerch) {
        this.mymerch = mymerch;
    }

    public PageHistory getPageHistory() {
        return pageHistory;
    }

    public void setPageHistory(PageHistory pageHistory) {
        this.pageHistory = pageHistory;
    }

    public TopGenres getTopGenres() {
        return topGenres;
    }

    public void setTopGenres(TopGenres topGenres) {
        this.topGenres = topGenres;
    }

    public LinkedList<Song> getRecSongs() {
        return recSongs;
    }

    public void setRecSongs(LinkedList<Song> recSongs) {
        this.recSongs = recSongs;
    }

    public LinkedList<Playlist> getRecPlaylist() {
        return recPlaylist;
    }

    public void setRecPlaylist(LinkedList<Playlist> recPlaylist) {
        this.recPlaylist = recPlaylist;
    }
}

final class Artist extends User {
    private LinkedList<Album> albums = new LinkedList<Album>();
    private int nrLikes;
    private LinkedList<User> subscribers = new LinkedList<>();

    class Event {
        private String eventname;
        private String eventdescription;
        private String eventdate;

        public String getEventname() {
            return eventname;
        }

        public void setEventname(final String eventname) {
            this.eventname = eventname;
        }

        public String getEventdescription() {
            return eventdescription;
        }

        public void setEventdescription(final String eventdescription) {
            this.eventdescription = eventdescription;
        }

        public String getEventdate() {
            return eventdate;
        }

        public void setEventdate(final String eventdate) {
            this.eventdate = eventdate;
        }
    }

    private LinkedList<Event> events = new LinkedList<Event>();

    class Merch {
        private String merchname;
        private String merchdescription;
        private int merchprice;

        public String getMerchname() {
            return merchname;
        }

        public void setMerchname(final String merchname) {
            this.merchname = merchname;
        }

        public String getMerchdescription() {
            return merchdescription;
        }

        public void setMerchdescription(final String merchdescription) {
            this.merchdescription = merchdescription;
        }

        public int getMerchprice() {
            return merchprice;
        }

        public void setMerchprice(final int merchprice) {
            this.merchprice = merchprice;
        }
    }

    private LinkedList<Merch> merch = new LinkedList<Merch>();

    class Wrapped {
        static class SongListen implements Comparable<SongListen>{
            private String song;
            private int listen;

            public String getSong() {
                return song;
            }

            public void setSong(String song) {
                this.song = song;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(SongListen o) {
                if (o.getListen() - listen == 0) {
                    return -(o.getSong().compareTo(song));
                } else {
                    return o.getListen() - listen;
                }
            }
        }

        static class AlbumListen implements Comparable<AlbumListen>{
            private String album;
            private int listen;

            public String getAlbum() {
                return album;
            }

            public void setAlbum(String album) {
                this.album = album;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(AlbumListen o) {
                if (o.getListen() - listen == 0) {
                    return -(o.getAlbum().compareTo(album));
                } else {
                    return o.getListen() - listen;
                }
            }
        }

        static class UserListen implements Comparable<UserListen>{
            private String user;
            private int listen;

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(UserListen o) {
                if (o.getListen() - listen == 0) {
                    return -(o.getUser().compareTo(user));
                } else {
                    return o.getListen() - listen;
                }
            }
        }

        LinkedList<SongListen> songListens = new LinkedList<>();
        LinkedList<AlbumListen> albumListens = new LinkedList<>();
        LinkedList<UserListen> userListens = new LinkedList<>();

        public LinkedList<SongListen> getSongListens() {
            return songListens;
        }

        public void setSongListens(LinkedList<SongListen> songListens) {
            this.songListens = songListens;
        }

        public LinkedList<AlbumListen> getAlbumListens() {
            return albumListens;
        }

        public void setAlbumListens(LinkedList<AlbumListen> albumListens) {
            this.albumListens = albumListens;
        }

        public LinkedList<UserListen> getUserListens() {
            return userListens;
        }

        public void setUserListens(LinkedList<UserListen> userListens) {
            this.userListens = userListens;
        }
    }

    Wrapped wrappedartist = new Wrapped();

    Artist(final Command command) {
        super(command);
        this.setType(1);
    }

    /**
     * Function that shows all the Albums of an Artist
     * @param command
     * @return
     */
    public ResultAlbum showAlbums(final Command command) {
        ResultAlbum resultAlbum = new ResultAlbum(command, albums);
        return resultAlbum;
    }

    /**
     * Function that adds a new Event to an Artist
     * @param command
     * @return
     */
    public ResultSwitch addEvent(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getEventname().equals(command.getName())) {
                result.setMessage(this.getUsername() + " has another event with the same name.");
                return result;
            }
        }
        if (!command.getDate().matches("\\d{2}-\\d{2}-\\d{4}")) {
            result.setMessage("Event for " + this.getUsername() + " does not have a valid date.");
            return result;
        }
        final int c1 = 2, c2 = 3, c3 = 5, c4 = 6, c5 = 10;
        int day = Integer.valueOf(command.getDate().substring(0, c1));
        int month = Integer.valueOf(command.getDate().substring(c2, c3));
        int year = Integer.valueOf(command.getDate().substring(c4, c5));
        final int intdays = 29, intmonths = 2;
        if (day > intdays && month == intmonths) {
            result.setMessage("Event for " + this.getUsername() + " does not have a valid date.");
            return result;
        }
        final int d = 31, m = 12, y1 = 1900, y2 = 2023;
        if (day > d || day < 0 || month < 0 || month > m || year < y1 || year > y2) {
            result.setMessage("Event for " + this.getUsername() + " does not have a valid date.");
            return result;
        }
        Event newEvent = new Event();
        newEvent.setEventdate(command.getDate());
        newEvent.setEventname(command.getName());
        newEvent.setEventdescription(command.getDescription());
        events.addLast(newEvent);
        result.setMessage(this.getUsername() + " has added new event successfully.");
        for (int i = 0; i < subscribers.size(); i++) {
            Notification notif = new Notification();
            notif.setName("New Event");
            notif.setDescription("New Event from " + this.getUsername() + ".");
            subscribers.get(i).addNotification(notif);
        }
        return result;
    }

    /**
     * Function that deletes an Event from an Artist
     * @param command
     * @return
     */
    public ResultSwitch deleteEvent(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getEventname().equals(command.getName())) {
                events.remove(i);
                result.setMessage(this.getUsername() + " deleted the event successfully.");
                return result;
            }
        }
        result.setMessage(this.getUsername() + " doesn't have an event with the given name.");
        return result;
    }

    /**
     * Function that adds a new type of merch to an Artist
     * @param command
     * @return
     */
    public ResultSwitch addMerch(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        for (int i = 0; i < merch.size(); i++) {
            if (merch.get(i).getMerchname().equals(command.getName())) {
                result.setMessage(this.getUsername() + " has merchandise with the same name.");
                return result;
            }
        }
        if (command.getPrice() < 0) {
            result.setMessage("Price for merchandise can not be negative.");
            return result;
        }
        Merch newMerch = new Merch();
        newMerch.setMerchdescription(command.getDescription());
        newMerch.setMerchname(command.getName());
        newMerch.setMerchprice(command.getPrice());
        merch.addLast(newMerch);
        result.setMessage(this.getUsername() + " has added new merchandise successfully.");
        for (int i = 0; i < subscribers.size(); i++) {
            Notification notif = new Notification();
            notif.setName("New Merchandise");
            notif.setDescription("New Merchandise from " + this.getUsername() + ".");
            subscribers.get(i).addNotification(notif);
        }
        return result;
    }

    public void updateWrapped(User currUser, Song currSong) {
        boolean found = false;
        for (int i = 0; i < wrappedartist.albumListens.size(); i++) {
            String name = wrappedartist.albumListens.get(i).getAlbum();
            if (name.equals(currSong.getAlbum())) {
                int nr = wrappedartist.albumListens.get(i).getListen();
                wrappedartist.albumListens.get(i).setListen(nr + 1);
                found=true;
                break;
            }
        }
        if (!found) {
            Wrapped.AlbumListen alb = new Wrapped.AlbumListen();
            alb.setAlbum(currSong.getAlbum());
            alb.setListen(1);
            wrappedartist.albumListens.addLast(alb);
        }
        found = false;
        for (int i = 0; i < wrappedartist.songListens.size(); i++) {
            String name = wrappedartist.songListens.get(i).getSong();
            if (name.equals(currSong.getName())) {
                int nr = wrappedartist.songListens.get(i).getListen();
                wrappedartist.songListens.get(i).setListen(nr + 1);
                found=true;
                break;
            }
        }
        if (!found) {
            Wrapped.SongListen sg = new Wrapped.SongListen();
            sg.setSong(currSong.getName());
            sg.setListen(1);
            wrappedartist.songListens.addLast(sg);
        }
        found = false;
        for (int i = 0; i < wrappedartist.userListens.size(); i++) {
            String name = wrappedartist.userListens.get(i).getUser();
            if (name.equals(currUser.getUsername())) {
                int nr = wrappedartist.userListens.get(i).getListen();
                wrappedartist.userListens.get(i).setListen(nr + 1);
                found=true;
                break;
            }
        }
        if (!found) {
            Wrapped.UserListen usr = new Wrapped.UserListen();
            usr.setUser(currUser.getUsername());
            usr.setListen(1);
            wrappedartist.userListens.addLast(usr);
        }
    }

    public CommandResults wrappedArt(final Command command) {
        Userbase userbase = Userbase.getInstance();
        for (int i = 0; i < userbase.getUserbase().size(); i++) {
            User currUser = userbase.getUserbase().get(i);
            if (currUser.getType() == 0 && !currUser.isOffline()) {
                currUser.getPlayer().status(command);
            }
        }
        Collections.sort(wrappedartist.getAlbumListens());
        Collections.sort(wrappedartist.getSongListens());
        Collections.sort(wrappedartist.getUserListens());
        int tot = wrappedartist.getAlbumListens().size();
        tot += wrappedartist.getSongListens().size();
        tot += wrappedartist.userListens.size();
        if (tot == 0) {
            ResultSwitch res = new ResultSwitch(command);
            res.setMessage("No data to show for artist " + this.getUsername() + ".");
            return res;
        }
        ResultWrappedArt result = new ResultWrappedArt(command, this);
        return result;
    }

    public int searchSubscr(final String name) {
        for (int i = 0; i < subscribers.size(); i++) {
            if (subscribers.get(i).getUsername().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public LinkedList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(final LinkedList<Album> albums) {
        this.albums = albums;
    }

    public LinkedList<Event> getEvents() {
        return events;
    }

    public void setEvents(final LinkedList<Event> events) {
        this.events = events;
    }

    public LinkedList<Merch> getMerch() {
        return merch;
    }

    public void setMerch(final LinkedList<Merch> merch) {
        this.merch = merch;
    }

    public int getNrLikes() {
        return nrLikes;
    }

    public void setNrLikes(final int nrLikes) {
        this.nrLikes = nrLikes;
    }

    public Wrapped getWrappedartist() {
        return wrappedartist;
    }

    public void setWrappedartist(Wrapped wrappedartist) {
        this.wrappedartist = wrappedartist;
    }

    public LinkedList<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(LinkedList<User> subscribers) {
        this.subscribers = subscribers;
    }
}

final class Host extends User {
    private LinkedList<Podcast> podcasts = new LinkedList<Podcast>();
    private LinkedList<User> subscribers = new LinkedList<>();

    class Announcement {
        private String annName;
        private String annDes;

        public String getAnnName() {
            return annName;
        }

        public void setAnnName(final String announcementName) {
            annName = announcementName;
        }

        public String getAnnDes() {
            return annDes;
        }

        public void setAnnDes(final String announcementDescription) {
            annDes = announcementDescription;
        }
    }

    private LinkedList<Announcement> announcements = new LinkedList<Announcement>();

    class WrappedHost {
        static class EpisodeListen implements Comparable<EpisodeListen>{
            private String episode;
            private int listen;

            public String getEpisode() {
                return episode;
            }

            public void setEpisode(String episode) {
                this.episode = episode;
            }

            public int getListen() {
                return listen;
            }

            public void setListen(int listen) {
                this.listen = listen;
            }

            @Override
            public int compareTo(EpisodeListen o) {
                if (o.getListen() == listen) {
                    return -(o.getEpisode().compareTo(episode));
                } else {
                    return o.getListen() - listen;
                }
            }
        }
        private LinkedList<EpisodeListen> episodes = new LinkedList<>();
        private LinkedList<String> users =  new LinkedList<>();

        public LinkedList<EpisodeListen> getEpisodes() {
            return episodes;
        }

        public void setEpisodes(LinkedList<EpisodeListen> episodes) {
            this.episodes = episodes;
        }

        public LinkedList<String> getUsers() {
            return users;
        }

        public void setUsers(LinkedList<String> users) {
            this.users = users;
        }
    }

    private WrappedHost wrapped = new WrappedHost();

    Host(final Command command) {
        super(command);
        this.setType(2);
    }

    /**
     * Function that shows all the podcasts of a Host
     * @param command
     * @return
     */
    public ResultPodcast showPodcasts(final Command command) {
        ResultPodcast resultPodcast = new ResultPodcast(command, this.getPodcasts());
        return resultPodcast;
    }

    /**
     * Function that adds an announcement to this Host
     * @param command
     * @return
     */
    public ResultSwitch addAnnouncement(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        for (int i = 0; i < announcements.size(); i++) {
            if (announcements.get(i).getAnnName().equals(command.getName())) {
                String message;
                message = this.getUsername() + " has already added an announcement with this name.";
                result.setMessage(message);
                return result;
            }
        }
        Announcement newAnn = new Announcement();
        newAnn.setAnnDes(command.getDescription());
        newAnn.setAnnName(command.getName());
        announcements.addLast(newAnn);
        result.setMessage(this.getUsername() + " has successfully added new announcement.");
        for (int i = 0; i < subscribers.size(); i++) {
            Notification notif = new Notification();
            notif.setName("New Announcement");
            notif.setDescription("New Announcement from " + this.getUsername() + ".");
            subscribers.get(i).addNotification(notif);
        }
        return result;
    }

    /**
     * Function that removes an announcement from a Host
     * @param command
     * @return
     */
    public ResultSwitch removeAnnouncement(final Command command) {
        ResultSwitch result = new ResultSwitch(command);
        for (int i = 0; i < announcements.size(); i++) {
            if (announcements.get(i).getAnnName().equals(command.getName())) {
                String message;
                message = this.getUsername() + " has successfully deleted the announcement.";
                result.setMessage(message);
                announcements.remove(i);
                return result;
            }
        }
        result.setMessage(this.getUsername() + " has no announcement with the given name.");
        return result;
    }

    public int searchSubscr(final String name) {
        for (int i = 0; i < subscribers.size(); i++) {
            if (subscribers.get(i).getUsername().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void updateWrapped(final Episode episode, final User user) {
        String name = episode.getName();
        boolean found = false;
        for (int i = 0; i < wrapped.getEpisodes().size(); i++) {
            WrappedHost.EpisodeListen epis = wrapped.getEpisodes().get(i);
            if (epis.getEpisode().equals(name)) {
                found = true;
                epis.setListen(epis.getListen() + 1);
                break;
            }
        }
        if (!found) {
            WrappedHost.EpisodeListen epis = new WrappedHost.EpisodeListen();
            epis.setEpisode(name);
            epis.setListen(1);
            wrapped.getEpisodes().addLast(epis);
        }
        found = false;
        name = user.getUsername();
        for (int i = 0; i < wrapped.getUsers().size(); i++) {
            if (wrapped.getUsers().get(i).equals(name)) {
                found = true;
                break;
            }
        }
        if (!found) {
            wrapped.getUsers().addLast(name);
        }
    }

    public ResultWrappedHost printWrappedHost(final Command command) {
        Collections.sort(wrapped.getEpisodes());
        ResultWrappedHost result = new ResultWrappedHost(command, this);
        return result;
    }

    public LinkedList<Podcast> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final LinkedList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public LinkedList<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(final LinkedList<Announcement> announcements) {
        this.announcements = announcements;
    }

    public LinkedList<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(LinkedList<User> subscribers) {
        this.subscribers = subscribers;
    }

    public WrappedHost getWrappedHost() {
        return wrapped;
    }

    public void setWrappedHost(WrappedHost wrapped) {
        this.wrapped = wrapped;
    }
}
