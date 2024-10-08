package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.LinkedList;

public class CommandResults {
    private String command;
    private String user;
    private int timestamp;

    public CommandResults(final Command command) {
        this.command = command.getCommand();
        this.user = command.getUsername();
        this.timestamp = command.getTimestamp();
    }

    public final String getCommand() {
        return command;
    }

    public final void setCommand(final String command) {
        this.command = command;
    }

    public final String getUser() {
        return user;
    }

    public final void setUser(final String user) {
        this.user = user;
    }

    public final int getTimestamp() {
        return timestamp;
    }

    public final void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }
}

class ResultSearch extends CommandResults {
    private String message;
    private LinkedList<String> results = new LinkedList<String>();

    ResultSearch(final Command command) {
        super(command);
    }

    public LinkedList<String> getResults() {
        return results;
    }

    public void setSongs(final LinkedList<String> result) {
        this.results = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultSelect extends CommandResults {
    private String message;
    ResultSelect(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultLoad extends CommandResults {
    private String message;
    ResultLoad(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultPlayPause extends CommandResults {
    private String message;
    ResultPlayPause(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultStatus extends CommandResults {
    private class Stats {
        private String name;
        private int remainedTime;
        private String repeat;
        private boolean shuffle;
        private boolean paused;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public int getRemainedTime() {
            return remainedTime;
        }

        public void setRemainedTime(final int remainedTime) {
            this.remainedTime = remainedTime;
        }

        public String getRepeat() {
            return repeat;
        }

        public void setRepeat(final String repeat) {
            this.repeat = repeat;
        }

        public boolean isShuffle() {
            return shuffle;
        }

        public void setShuffle(final boolean shuffle) {
            this.shuffle = shuffle;
        }

        public boolean isPaused() {
            return paused;
        }

        public void setPaused(final boolean paused) {
            this.paused = paused;
        }
    }
    private Stats stats;
    ResultStatus(final Command command, final Player player) {
        super(command);
        this.stats = new Stats();
        stats.setPaused(player.isPause());
        stats.setShuffle(player.isShuffle());
        if (player.getRepeat() == 0) {
            stats.setRepeat("No Repeat");
        }
        if (player.getRepeat() == 1) {
            if (player.getType().equals("playlist")) {
                stats.setRepeat("Repeat All");
            } else {
                stats.setRepeat("Repeat Once");
            }
        }
        if (player.getRepeat() == 2) {
            if (player.getType().equals("playlist")) {
                stats.setRepeat("Repeat Current Song");
            } else {
                stats.setRepeat("Repeat Infinite");
            }
        }
        stats.setRemainedTime(player.getTimeremaining());
        if (player.getType().equals("song")) {
            stats.setName(((Song) player.getSource()).getName());
        }
        if (player.getType().equals(("nothing"))) {
            stats.setName("");
        }
        if (!player.getType().equals("song") && !player.getType().equals("nothing")) {
            stats.setName(player.getCurrFile().getName());
        }
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(final Stats stats) {
        this.stats = stats;
    }
}

class ResultCreatePlaylist extends CommandResults {
    private String message;

    ResultCreatePlaylist(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultAddRemove extends CommandResults {
    private String message;
    ResultAddRemove(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultShowPlaylists extends CommandResults {
    private class Result {
        private String name;
        private LinkedList<String> songs;
        private String visibility;
        private int followers;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public LinkedList<String> getSongs() {
            return songs;
        }

        public void setSongs(final LinkedList<String> songs) {
            this.songs = songs;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(final String visibility) {
            this.visibility = visibility;
        }

        public int getFollowers() {
            return followers;
        }

        public void setFollowers(final int followers) {
            this.followers = followers;
        }
    }

    private LinkedList<Result> result = new LinkedList<Result>();

    ResultShowPlaylists(final Command command, final LinkedList<Playlist> playlists) {
        super(command);
        for (int i = 0; i < playlists.size(); i++) {
            Result currResult = new Result();
            Playlist currPlaylist = playlists.get(i);
            currResult.setName(currPlaylist.getName());
            currResult.setFollowers(currPlaylist.getFollowers());
            LinkedList<String> songnames = new LinkedList<String>();
            for (int j = 0; j < currPlaylist.getSongs().size(); j++) {
                songnames.addLast(currPlaylist.getSongs().get(j).getName());
            }
            currResult.setSongs(songnames);
            currResult.setVisibility(currPlaylist.getPrivacy());
            result.addLast(currResult);
        }
    }

    public LinkedList<Result> getResult() {
        return result;
    }

    public void setResult(final LinkedList<Result> result) {
        this.result = result;
    }
}

class ResultLike extends CommandResults {
    private String message;

    ResultLike(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultShowPrefered extends CommandResults {
    private LinkedList<String> result = new LinkedList<String>();

    ResultShowPrefered(final Command command) {
        super(command);
    }

    public LinkedList<String> getResult() {
        return result;
    }

    public void setResult(final LinkedList<String> result) {
        this.result = result;
    }
}

class ResultRepeat extends CommandResults {
    private String message;

    ResultRepeat(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultShuffle extends CommandResults {
    private String message;

    ResultShuffle(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultForwardBackward extends CommandResults {
    private String message;
    ResultForwardBackward(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultSwitch extends CommandResults {
    private String message;

    ResultSwitch(final Command command) {
        super(command);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}

class ResultAlbum extends CommandResults {
    class AlbumStats {
        private String name;
        private LinkedList<String> songs = new LinkedList<String>();

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public LinkedList<String> getSongs() {
            return songs;
        }

        public void setSongs(final LinkedList<String> songs) {
            this.songs = songs;
        }
    }

    private LinkedList<AlbumStats> result = new LinkedList<AlbumStats>();

    ResultAlbum(final Command command, final LinkedList<Album> albums) {
        super(command);
        for (int i = 0; i < albums.size(); i++) {
            AlbumStats albumStats = new AlbumStats();
            albumStats.setName(albums.get(i).getName());
            for (int j = 0; j < albums.get(i).getSongs().size(); j++) {
                albumStats.getSongs().addLast(albums.get(i).getSongs().get(j).getName());
            }
            result.addLast(albumStats);
        }
    }

    public LinkedList<AlbumStats> getResult() {
        return result;
    }

    public void setResult(final LinkedList<AlbumStats> result) {
        this.result = result;
    }
}

class ResultPodcast extends CommandResults {
    class PodcastStats {
        private String name;
        private LinkedList<String> episodes = new LinkedList<String>();

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public LinkedList<String> getEpisodes() {
            return episodes;
        }

        public void setEpisodes(final LinkedList<String> episodes) {
            this.episodes = episodes;
        }
    }

    private LinkedList<PodcastStats> result = new LinkedList<PodcastStats>();

    ResultPodcast(final Command command, final LinkedList<Podcast> podcasts) {
        super(command);
        for (int i = 0; i < podcasts.size(); i++) {
            PodcastStats currPodcast = new PodcastStats();
            currPodcast.setName(podcasts.get(i).getName());
            for (int j = 0; j < podcasts.get(i).getEpisodes().size(); j++) {
                currPodcast.getEpisodes().addLast(podcasts.get(i).getEpisodes().get(j).getName());
            }
            result.addLast(currPodcast);
        }
    }

    public LinkedList<PodcastStats> getResult() {
        return result;
    }

    public void setResult(final LinkedList<PodcastStats> result) {
        this.result = result;
    }
}

class ResultWrappedUser extends CommandResults {
    private final ObjectMapper mapper = new ObjectMapper();
    class Result {
        private final ObjectNode topArtists = mapper.createObjectNode();
        private final ObjectNode topGenres = mapper.createObjectNode();
        private final ObjectNode topSongs = mapper.createObjectNode();
        private final ObjectNode topAlbums = mapper.createObjectNode();
        private final ObjectNode topEpisodes = mapper.createObjectNode();

        public ObjectNode getTopArtists() {
            return topArtists;
        }

        public ObjectNode getTopGenres() {
            return topGenres;
        }

        public ObjectNode getTopSongs() {
            return topSongs;
        }

        public ObjectNode getTopAlbums() {
            return topAlbums;
        }

        public ObjectNode getTopEpisodes() {
            return topEpisodes;
        }
    }

    private Result result = new Result();

    ResultWrappedUser(final Command command, final User user) {
        super(command);
        final int basecase = 5;
        LinkedList<User.Wrapped.SongListen> songs = user.getWrapped().getTopSong();
        for (int i = 0; i < basecase && i < songs.size(); i++) {
            result.getTopSongs().put(songs.get(i).getSong().getName(), songs.get(i).getListen());
        }
        LinkedList<User.Wrapped.ArtistListen> artist = user.getWrapped().getTopArtist();
        for (int i = 0; i < basecase && i < artist.size(); i++) {
            result.getTopArtists().put(artist.get(i).getArtist(), artist.get(i).getListen());
        }
        LinkedList<User.Wrapped.PodcastListen> podcast = user.getWrapped().getTopEpisode();
        for (int i = 0; i < basecase && i < podcast.size(); i++) {
            String name = podcast.get(i).getEpisode().getName();
            result.getTopEpisodes().put(name, podcast.get(i).getListen());
        }
        LinkedList<User.Wrapped.GenreListen> genre = user.getWrapped().getTopGenre();
        for (int i = 0; i < basecase && i < genre.size(); i++) {
            result.getTopGenres().put(genre.get(i).getGenre(), genre.get(i).getListen());
        }
        LinkedList<User.Wrapped.AlbumListen> album = user.getWrapped().getTopAlbum();
        for (int i = 0; i < basecase && i < album.size(); i++) {
            result.getTopAlbums().put(album.get(i).getAlbum(), album.get(i).getListen());
        }
    }

    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }
}

class ResultWrappedArt extends CommandResults {
    private final ObjectMapper mapper = new ObjectMapper();
    class Result {
        private final ObjectNode topAlbums = mapper.createObjectNode();
        private final ObjectNode topSongs = mapper.createObjectNode();
        private final LinkedList<String> topFans = new LinkedList<>();
        private int listeners;

        public ObjectNode getTopAlbums() {
            return topAlbums;
        }

        public ObjectNode getTopSongs() {
            return topSongs;
        }

        public LinkedList<String> getTopFans() {
            return topFans;
        }

        public int getListeners() {
            return listeners;
        }

        public void setListeners(final int listeners) {
            this.listeners = listeners;
        }
    }

    private ResultWrappedArt.Result result = new ResultWrappedArt.Result();

    ResultWrappedArt(final Command command, final Artist artist) {
        super(command);
        final int basecase = 5;
        LinkedList<Artist.Wrapped.AlbumListen> albums = artist.getWrappedartist().getAlbumListens();
        for (int i = 0; i < basecase && i < albums.size(); i++) {
            result.getTopAlbums().put(albums.get(i).getAlbum(), albums.get(i).getListen());
        }
        LinkedList<Artist.Wrapped.SongListen> songs = artist.getWrappedartist().getSongListens();
        for (int i = 0; i < basecase && i < songs.size(); i++) {
            result.getTopSongs().put(songs.get(i).getSong(), songs.get(i).getListen());
        }
        LinkedList<Artist.Wrapped.UserListen> user = artist.getWrappedartist().getUserListens();
        for (int i = 0; i < basecase && i < user.size(); i++) {
            result.getTopFans().addLast(user.get(i).getUser());
        }
        result.setListeners(user.size());
    }

    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }
}

class ResultWrappedHost extends CommandResults {
    private final ObjectMapper mapper = new ObjectMapper();
    class Result {
        private final ObjectNode topEpisodes = mapper.createObjectNode();
        private int listeners;

        public ObjectNode getTopEpisodes() {
            return topEpisodes;
        }

        public int getListeners() {
            return listeners;
        }

        public void setListeners(final int listeners) {
            this.listeners = listeners;
        }
    }

    private Result result = new Result();

    ResultWrappedHost(final Command command, final Host host) {
        super(command);
        final int basecase = 5;
        LinkedList<Host.WrappedHost.EpisodeListen> episodes = host.getWrappedHost().getEpisodes();
        for (int i = 0; i < episodes.size() && i < basecase; i++) {
            result.getTopEpisodes().put(episodes.get(i).getEpisode(), episodes.get(i).getListen());
        }
        result.setListeners(host.getWrappedHost().getUsers().size());
    }

    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }
}

class ResultEnd {
    private final String command = "endProgram";
    private final ObjectMapper mapper = new ObjectMapper();
    private ObjectNode result = mapper.createObjectNode();

    ResultEnd() {
        double inc;
        final double whole = 100;
        Userbase userbase = Userbase.getInstance();
        Collections.sort(userbase.getArtistData());
        for (int i = 0; i < userbase.getArtistData().size(); i++) {
            Userbase.ArtistData currArt = userbase.getArtistData().get(i);
            ObjectNode res = mapper.createObjectNode();
            ObjectNode finalRes = mapper.createObjectNode();
            inc = currArt.getMerchrev();
            inc = Math.round(inc * whole) / whole;
            finalRes.put("merchRevenue", inc);
            inc = currArt.getSongrev();
            inc = Math.round(inc * whole) / whole;
            finalRes.put("songRevenue", inc);
            finalRes.put("ranking", i + 1);
            Collections.sort(currArt.getSongIncs());
            if (currArt.getSongIncs().isEmpty()) {
                finalRes.put("mostProfitableSong", "N/A");
            } else {
                if (currArt.getSongIncs().get(0).getInc() != 0) {
                    String name = currArt.getSongIncs().get(0).getSong().getName();
                    finalRes.put("mostProfitableSong", name);
                } else {
                    finalRes.put("mostProfitableSong", "N/A");
                }
            }
            result.put(currArt.getArtist(), finalRes);
        }
        for (User u : userbase.getUserbase()) {
            if (u.getType() != 0)
                continue;
            User.Wrapped wrapped = u.getWrapped();
            for (User.Wrapped.SongListen sg : wrapped.getTopSong()) {
                if (sg.getSong().getAlbum().equals("Greatest Hits") && sg.getSong().getArtist().equals("Bruce Springsteen"))
                    System.out.println(sg.getSong().getName() + " " + sg.getListen() + " " + u.getUsername());
            }
        }
    }

    public String getCommand() {
        return command;
    }

    public ObjectNode getResult() {
        return result;
    }

    public void setResult(final ObjectNode result) {
        this.result = result;
    }
}

class ResultNotification extends CommandResults {
    private LinkedList<NotifObserv.Notification> notifications;
    ResultNotification(final Command command) {
        super(command);
    }

    public LinkedList<NotifObserv.Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(final LinkedList<NotifObserv.Notification> notifications) {
        this.notifications = notifications;
    }
}

class ResultMerch extends  CommandResults {
    private LinkedList<String> result;
    ResultMerch(final Command command) {
        super(command);
    }

    public LinkedList<String> getResult() {
        return result;
    }

    public void setResult(final LinkedList<String> result) {
        this.result = result;
    }
}
