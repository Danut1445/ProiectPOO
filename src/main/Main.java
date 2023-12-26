package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.LibraryInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        LinkedList<Command> commands = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePathInput), new TypeReference<LinkedList<Command>>() { });
        ArrayNode outputs = objectMapper.createArrayNode();
        LinkedList<Object> results = new LinkedList<Object>();

        Library librarySongs = Library.getInstance();
        librarySongs.setSongs(new LinkedList<Song>());
        library.getSongs().forEach((element) -> librarySongs.addSong(element));

        Userbase userbase = Userbase.getInstance();
        userbase.setUserbase(new LinkedList<User>());
        userbase.setArtistData(new LinkedList<>());
        for (int i = 0; i < library.getUsers().size(); i++) {
            userbase.addUser(library.getUsers().get(i));
        }

        PodcastLibrary podcastLibrary = PodcastLibrary.getInstance();
        podcastLibrary.setPodcasts(new LinkedList<Podcast>());
        for (int i = 0; i < library.getPodcasts().size(); i++) {
            Podcast podcast = new Podcast(library.getPodcasts().get(i));
            for (int j = 0; j < library.getPodcasts().get(i).getEpisodes().size(); j++) {
                podcast.addEpisodes(library.getPodcasts().get(i).getEpisodes().get(j));
            }
            podcastLibrary.addPodcast(podcast);
        }

        AlbumLibrary albumLibrary = AlbumLibrary.getINSTANCE();
        albumLibrary.resetAlbums();

        PlaylistLibrary playlistLibrary = PlaylistLibrary.getInstance();
        playlistLibrary.resetPlaylists();

        PrintCurrentPage printCurrentPage = new PrintCurrentPage();

        // TODO add your implementation
        System.out.println("This is the size: " + commands.size());

        for (int i = 0; i < commands.size(); i++) {
            Command currComm = commands.get(i);
            User currUser = userbase.getUserbase().get(0);
            String message;
            for (int j = 0; j < userbase.getUserbase().size(); j++) {
                currUser = userbase.getUserbase().get(j);
                if (currUser.getUsername().equals(currComm.getUsername())) {
                    break;
                }
            }
            if (currComm.getCommand().equals("switchConnectionStatus")) {
                if (!currUser.getUsername().equals(currComm.getUsername())) {
                    ResultSwitch resultSwitch = new ResultSwitch(currComm);
                    message = "The username " + currComm.getUsername() + " doesn't exist.";
                    resultSwitch.setMessage(message);
                    results.addLast(resultSwitch);
                    continue;
                }
                results.addLast(currUser.switchConnection(currComm));
                continue;
            }
            if (currComm.getCommand().equals("search")) {
                results.addLast(currUser.search(currComm));
            }
            if (currComm.getCommand().equals("select")) {
                results.addLast(currUser.select(currComm));
            }
            if (currComm.getCommand().equals("load")) {
                results.addLast(currUser.load(currComm));
            }
            if (currComm.getCommand().equals("status")) {
                results.addLast(currUser.status(currComm));
            }
            if (currComm.getCommand().equals("playPause")) {
                results.addLast(currUser.playPause(currComm));
            }
            if (currComm.getCommand().equals("createPlaylist")) {
                results.addLast(currUser.createPlaylist(currComm));
            }
            if (currComm.getCommand().equals("addRemoveInPlaylist")) {
                results.addLast(currUser.addRemove(currComm));
            }
            if (currComm.getCommand().equals("showPlaylists")) {
                results.addLast(currUser.showPlaylists(currComm));
            }
            if (currComm.getCommand().equals("showPreferredSongs")) {
                results.addLast(currUser.showPrefered(currComm));
            }
            if (currComm.getCommand().equals("like")) {
                results.addLast(currUser.like(currComm));
            }
            if (currComm.getCommand().equals("repeat")) {
                results.addLast(currUser.repeat(currComm));
            }
            if (currComm.getCommand().equals("shuffle")) {
                results.addLast(currUser.shuffle(currComm));
            }
            if (currComm.getCommand().equals("forward")) {
                results.addLast(currUser.forward(currComm));
            }
            if (currComm.getCommand().equals("backward")) {
                results.addLast(currUser.backward(currComm));
            }
            if (currComm.getCommand().equals("next")) {
                results.addLast(currUser.next(currComm));
            }
            if (currComm.getCommand().equals("prev")) {
                results.addLast(currUser.prev(currComm));
            }
            if (currComm.getCommand().equals("follow")) {
                results.addLast(currUser.follow(currComm));
            }
            if (currComm.getCommand().equals("switchVisibility")) {
                results.addLast(currUser.switchVisibility(currComm));
            }
            if (currComm.getCommand().equals("getTop5Songs")) {
                results.addLast(librarySongs.top5Songs(currComm));
            }
            if (currComm.getCommand().equals("getTop5Playlists")) {
                results.addLast(playlistLibrary.top5Playlist(currComm));
            }
            if (currComm.getCommand().equals("getOnlineUsers")) {
                results.addLast(userbase.getOnlineUsers(currComm));
            }
            if (currComm.getCommand().equals("addUser")) {
                results.addLast(userbase.addUser(currComm));
            }
            if (currComm.getCommand().equals("addAlbum")) {
                results.addLast(albumLibrary.addAlbum(currComm, currUser));
            }
            if (currComm.getCommand().equals("showAlbums")) {
                results.addLast(((Artist) currUser).showAlbums(currComm));
            }
            if (currComm.getCommand().equals("printCurrentPage")) {
                results.addLast(currUser.currentPage(printCurrentPage, currComm));
            }
            if (currComm.getCommand().equals("addEvent")) {
                ResultSwitch result = new ResultSwitch(currComm);
                if (!currUser.getUsername().equals(currComm.getUsername())) {
                    result.setMessage("The username " + currComm.getUsername() + " doesn't exist.");
                    results.addLast(result);
                    continue;
                }
                if (currUser.getType() != 1) {
                    result.setMessage(currUser.getUsername() + " is not an artist.");
                    results.addLast(result);
                    continue;
                }
                results.addLast(((Artist) currUser).addEvent(currComm));
            }
            if (currComm.getCommand().equals("addMerch")) {
                ResultSwitch result = new ResultSwitch(currComm);
                if (!currUser.getUsername().equals(currComm.getUsername())) {
                    result.setMessage("The username " + currComm.getUsername() + " doesn't exist.");
                    results.addLast(result);
                    continue;
                }
                if (currUser.getType() != 1) {
                    result.setMessage(currUser.getUsername() + " is not an artist.");
                    results.addLast(result);
                    continue;
                }
                results.addLast(((Artist) currUser).addMerch(currComm));
            }
            if (currComm.getCommand().equals("getAllUsers")) {
                results.addLast(userbase.getAllUsers(currComm));
            }
            if (currComm.getCommand().equals("deleteUser")) {
                if (!currComm.getUsername().equals(currUser.getUsername())) {
                    ResultSwitch result = new ResultSwitch(currComm);
                    result.setMessage("The username " + currComm.getUsername() + " doesn't exist.");
                    results.addLast(result);
                    continue;
                }
                results.addLast(userbase.deleteUser(currComm, currUser));
            }
            if (currComm.getCommand().equals("addPodcast")) {
                results.addLast(podcastLibrary.addPodcast(currComm, currUser));
            }
            if (currComm.getCommand().equals("addAnnouncement")) {
                ResultSwitch result = new ResultSwitch(currComm);
                if (!currUser.getUsername().equals(currComm.getUsername())) {
                    result.setMessage("The username " + currComm.getUsername() + " doesn't exist.");
                    results.addLast(result);
                    continue;
                }
                if (currUser.getType() != 2) {
                    result.setMessage(currUser.getUsername() + " is not a host.");
                    results.addLast(result);
                    continue;
                }
                results.addLast(((Host) currUser).addAnnouncement(currComm));
            }
            if (currComm.getCommand().equals("showPodcasts")) {
                results.addLast(((Host) currUser).showPodcasts(currComm));
            }
            if (currComm.getCommand().equals("removeAnnouncement")) {
                ResultSwitch result = new ResultSwitch(currComm);
                if (!currUser.getUsername().equals(currComm.getUsername())) {
                    result.setMessage("The username " + currComm.getUsername() + " doesn't exist.");
                    results.addLast(result);
                    continue;
                }
                if (currUser.getType() != 2) {
                    result.setMessage(currUser.getUsername() + " is not an host.");
                    results.addLast(result);
                    continue;
                }
                results.addLast(((Host) currUser).removeAnnouncement(currComm));
            }
            if (currComm.getCommand().equals("removeAlbum")) {
                results.addLast(albumLibrary.removeAlbum(currComm, currUser));
            }
            if (currComm.getCommand().equals("changePage")) {
                results.addLast(currUser.changePage(currComm));
            }
            if (currComm.getCommand().equals("removePodcast")) {
                results.addLast(podcastLibrary.removePodcast(currComm, currUser));
            }
            if (currComm.getCommand().equals("removeEvent")) {
                ResultSwitch result = new ResultSwitch(currComm);
                if (!currUser.getUsername().equals(currComm.getUsername())) {
                    result.setMessage("The username " + currComm.getUsername() + " doesn't exist.");
                    results.addLast(result);
                    continue;
                }
                if (currUser.getType() != 1) {
                    result.setMessage(currUser.getUsername() + " is not an artist.");
                    results.addLast(result);
                    continue;
                }
                results.addLast(((Artist) currUser).deleteEvent(currComm));
            }
            if (currComm.getCommand().equals("getTop5Albums")) {
                results.addLast(albumLibrary.getTop5Albums(currComm));
            }
            if (currComm.getCommand().equals("getTop5Artists")) {
                results.addLast(userbase.getTop5Artist(currComm));
            }
            if (currComm.getCommand().equals("wrapped")) {
                if (currUser.getType() == 0) {
                    results.addLast(currUser.printWrapped(currComm));
                }
                if (currUser.getType() == 1) {
                    results.addLast(((Artist) currUser).wrappedArt(currComm));
                }
            }
        }
        ResultEnd resultEnd = new ResultEnd();
        results.addLast(resultEnd);

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), results);
    }
}
