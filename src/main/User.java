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

public class User implements Visitable {
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

    public User(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.currentPage = "HomePage";
        this.currUserrPage = this;
        player.setType("nothing");
    }

    public User(final Command command) {
        player.setType("nothing");
        this.username = command.getUsername();
        this.age = command.getAge();
        this.city = command.getCity();
        this.currentPage = "HomePage";
        this.currUserrPage = this;
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
        JsonNode filters = command.getFilters();
        LinkedList<String> tags = new LinkedList<String>();
        if (player.getType().equals("podcast")) {
            player.status(command);
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
                    if (!currSong.getName().startsWith(filters.get("name").textValue())) {
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
            for (int i = 0; i < AlbumLibrary.getINSTANCE().getAlbums().size(); i++) {
                Album currAlbum = AlbumLibrary.getINSTANCE().getAlbums().get(i);
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
            currUserrPage = (Artist) searcheditems.get(command.getItemNumber() - 1);
            currentPage = "ArtistPage";
        }
        if (lastsearch.equals("host")) {
            String name = ((Host) searcheditems.get(command.getItemNumber() - 1)).getUsername();
            result.setMessage("Successfully selected " + name + "'s page.");
            currUserrPage = (Host) searcheditems.get(command.getItemNumber() - 1);
            currentPage = "HostPage";
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
        }
        if (lastsearch.equals("playlist")) {
            int aux = ((Playlist) selectedItem).getSongs().size();
            player.setTimeremaining(((Playlist) selectedItem).getSongs().get(0).getDuration());
            player.setType("playlist");
            player.setCurrFile(((Playlist) selectedItem).getSongs().get(0));
            player.setCurrentObject(0);
            player.setOrder(IntStream.range(0, aux).toArray());
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
        }
        if (lastsearch.equals("album")) {
            int aux = ((Album) selectedItem).getSongs().size();
            player.setTimeremaining(((Album) selectedItem).getSongs().get(0).getDuration());
            player.setType("album");
            player.setCurrFile(((Album) selectedItem).getSongs().get(0));
            player.setCurrentObject(0);
            player.setOrder(IntStream.range(0, aux).toArray());
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
        } else {
            resultFollow.setMessage("Playlist followed successfully.");
            ((Playlist) selectedItem).setFollowers(((Playlist) selectedItem).getFollowers() + 1);
            followed.addLast((Playlist) selectedItem);
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
        if (!nextPage.equals("Home") && !nextPage.equals("LikedContent")) {
            result.setMessage(username + " is trying to access a non-existent page.");
            return result;
        }
        currUserrPage = this;
        if (command.getNextPage().equals("Home")) {
            currentPage = "HomePage";
        } else {
            currentPage = "LikedContentPage";
        }
        result.setMessage(username + " accessed " + command.getNextPage() + " successfully.");
        return  result;
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
}

final class Artist extends User {
    private LinkedList<Album> albums = new LinkedList<Album>();
    private int nrLikes;

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
        return result;
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
}

final class Host extends User {
    private LinkedList<Podcast> podcasts = new LinkedList<Podcast>();

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
}
