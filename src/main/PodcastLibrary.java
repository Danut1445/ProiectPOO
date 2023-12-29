package main;

import fileio.input.EpisodeInput;

import java.util.ArrayList;
import java.util.LinkedList;

public final class PodcastLibrary {
    private static final PodcastLibrary INSTANCE = new PodcastLibrary();
    private LinkedList<Podcast> podcasts = new LinkedList<Podcast>();

    private PodcastLibrary() {
    }

    public static PodcastLibrary getInstance() {
        return INSTANCE;
    }

    /**
     * Function that adds a new Podcast to the podcast library
     * @param command
     * @param currUser
     * @return
     */
    public ResultSwitch addPodcast(final Command command, final User currUser) {
        ResultSwitch result = new ResultSwitch(command);
        String message;
        if (!currUser.getUsername().equals(command.getUsername())) {
            result.setMessage("The username " + command.getUsername() + " doesn't exist.");
            return result;
        }
        if (currUser.getType() != 2) {
            result.setMessage(currUser.getUsername() + " is not a host.");
            return result;
        }
        for (int i = 0; i < ((Host) currUser).getPodcasts().size(); i++) {
            if (((Host) currUser).getPodcasts().get(i).getName().equals(command.getName())) {
                message = currUser.getUsername() + " has another podcast with the same name.";
                result.setMessage(message);
                return result;
            }
        }
        Podcast newPodcast = new Podcast();
        newPodcast.setName(command.getName());
        newPodcast.setOwner(command.getUsername());
        ArrayList<EpisodeInput> episodes = command.getEpisodes();
        for (int i = 0; i < episodes.size(); i++) {
            newPodcast.getEpisodes().addLast(new Episode(episodes.get(i)));
        }
        if (newPodcast.containsduplicates()) {
            message = currUser.getUsername();
            message += " has the same episode at least twice in this podcast.";
            result.setMessage(message);
            return result;
        }
        podcasts.addLast(newPodcast);
        ((Host) currUser).getPodcasts().addLast(newPodcast);
        result.setMessage(currUser.getUsername() + " has added new podcast successfully.");
        Host ht = (Host) currUser;
        for (int i = 0; i < ht.getSubscribers().size(); i++) {
            notifObserv.Notification notif = new notifObserv.Notification();
            notif.setName("New Podcast");
            notif.setDescription("New Podcast from " + ht.getUsername() + ".");
            ht.getSubscribers().get(i).addNotification(notif);
        }
        return result;
    }

    /**
     * Function that removes a podcast from the library
     * @param command
     * @param currUser
     * @return
     */
    public ResultSwitch removePodcast(final Command command, final User currUser) {
        ResultSwitch result = new ResultSwitch(command);
        String message = command.getUsername();
        if (!currUser.getUsername().equals(command.getUsername())) {
            result.setMessage("The username " + command.getUsername() + " doesn't exist.");
            return result;
        }
        if (currUser.getType() != 2) {
            result.setMessage(currUser.getUsername() + " is not a host.");
            return result;
        }
        boolean hasPodcast = false;
        for (int i = 0; i < ((Host) currUser).getPodcasts().size(); i++) {
            LinkedList<Podcast> userpod = ((Host) currUser).getPodcasts();
            if (userpod.get(i).getName().equals(command.getName())) {
                hasPodcast = true;
                break;
            }
        }
        if (!hasPodcast) {
            message += " doesn't have a podcast with the given name.";
            result.setMessage(message);
            return result;
        }
        for (int i = 0; i < Userbase.getInstance().getUserbase().size(); i++) {
            User user = Userbase.getInstance().getUserbase().get(i);
            user.updatePlayer(command);
            if (user.getUsername().equals(currUser.getUsername())) {
                continue;
            }
            if (!user.getPlayer().getType().equals("podcast")) {
                continue;
            } else {
                Podcast podcast = (Podcast) user.getPlayer().getSource();
                String owner = podcast.getOwner();
                if (podcast.getName().equals(command.getName()) && owner.equals(message)) {
                    message += " can't delete this podcast.";
                    result.setMessage(message);
                    return result;
                }
            }
        }
        for (int i = 0; i < Userbase.getInstance().getUserbase().size(); i++) {
            User user = Userbase.getInstance().getUserbase().get(i);
            for (int j = 0; j < user.getPodcastInfos().size(); j++) {
                User.PodcastInfo info = user.getPodcastInfos().get(j);
                String name = info.getPodcastname();
                if (info.getOwner().equals(message) && name.equals(command.getName())) {
                    user.getPodcastInfos().remove(j);
                    break;
                }
            }
        }
        for (int i = 0; i < podcasts.size(); i++) {
            Podcast podcast = podcasts.get(i);
            String name = podcast.getName();
            if (podcast.getOwner().equals(message) && name.equals(command.getName())) {
                podcasts.remove(i);
                break;
            }
        }
        for (int i = 0; i < ((Host) currUser).getPodcasts().size(); i++) {
            Podcast podcast = ((Host) currUser).getPodcasts().get(i);
            if (podcast.getName().equals(command.getName())) {
                ((Host) currUser).getPodcasts().remove(i);
                break;
            }
        }
        result.setMessage(currUser.getUsername() + " deleted the podcast successfully.");
        return result;
    }

    public LinkedList<Podcast> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final LinkedList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    /**
     * Add a new podcast to the library
     * @param podcast
     */
    public void addPodcast(final Podcast podcast) {
        podcasts.addLast(podcast);
    }
}
