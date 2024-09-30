package main;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;

public final class Player {
    private int lasttimestamp;
    private int timeremaining;
    private boolean shuffle;
    private boolean pause;
    private int repeat;
    private String type;
    private Object source;
    private int[] order;
    private AudioFiles currFile;
    private int currentObject;
    private User currUser;
    private User.Premium listSg = new User.Premium();
    private int nextAd;
    private Song ad;
    private double adrev;

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(final User currUser) {
        this.currUser = currUser;
    }

    public int getLasttimestamp() {
        return lasttimestamp;
    }

    public void setLasttimestamp(final int lasttimestamp) {
        this.lasttimestamp = lasttimestamp;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(final boolean pause) {
        this.pause = pause;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(final int repeat) {
        this.repeat = repeat;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(final Object source) {
        this.source = source;
    }

    public int getTimeremaining() {
        return timeremaining;
    }

    public void setTimeremaining(final int timeremaining) {
        this.timeremaining = timeremaining;
    }

    public User.Premium getListSg() {
        return listSg;
    }

    public void setListSg(final User.Premium listSg) {
        this.listSg = listSg;
    }

    public int getNextAd() {
        return nextAd;
    }

    public void setNextAd(final int nextAd) {
        this.nextAd = nextAd;
    }

    public Song getAd() {
        return ad;
    }

    public void setAd(final Song ad) {
        this.ad = ad;
    }

    public double getAdrev() {
        return adrev;
    }

    public void setAdrev(final double adrev) {
        this.adrev = adrev;
    }

    /**
     * Updates the player to see what it is status in the given command timestamp
     * @param command
     */
    public void status(final Command command) {
        if (nextAd == 2 && !pause) {
            int passedtime = command.getTimestamp() - lasttimestamp;
            if (timeremaining < command.getTimestamp() - lasttimestamp) {
                passedtime -= timeremaining;
                lasttimestamp += timeremaining;
                if (!type.equals("nothing")) {
                    timeremaining = currFile.getDuration();
                    currUser.updateWrapped("song", currFile);
                    currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                    currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                    currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                    String art = ((Song) currFile).getArtist();
                    Userbase.getInstance().addStats(art, (Song) currFile);
                    Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                    addSg((Song) currFile);
                }
                nextAd = 0;
            } else {
                passedtime = 0;
                lasttimestamp = command.getTimestamp();
                timeremaining = timeremaining - (command.getTimestamp() - lasttimestamp);
            }
        }
        if (type.equals("song") && !pause) {
            int passedtime = command.getTimestamp() - lasttimestamp;
            if (repeat == 1) {
                if (timeremaining < command.getTimestamp() - lasttimestamp) {
                    passedtime -= timeremaining;
                    repeat = 0;
                    if (nextAd == 1) {
                        loadAd(passedtime);
                        passedtime -= ad.getDuration();
                    } else {
                        timeremaining = currFile.getDuration();
                        currUser.updateWrapped("song", currFile);
                        currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                        currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                        currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                        String art = ((Song) currFile).getArtist();
                        Userbase.getInstance().addStats(art, (Song) currFile);
                        Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                        addSg((Song) currFile);
                    }
                } else {
                    passedtime = 0;
                    timeremaining = timeremaining - (command.getTimestamp() - lasttimestamp);
                }
            }
            if (repeat == 0) {
                if (timeremaining < passedtime) {
                    type = "nothing";
                    if (nextAd == 1) {
                        loadAd(passedtime);
                    }
                } else {
                    timeremaining = timeremaining - passedtime;
                }
            }
            if (repeat == 2) {
                while (passedtime > 0) {
                    if (passedtime >= timeremaining) {
                        passedtime -= timeremaining;
                        if (nextAd == 1) {
                            loadAd(passedtime);
                            passedtime -= ad.getDuration();
                        } else {
                            timeremaining = currFile.getDuration();
                            currUser.updateWrapped("song", currFile);
                            currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                            currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                            currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                            String art = ((Song) currFile).getArtist();
                            Userbase.getInstance().addStats(art, (Song) currFile);
                            Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                            addSg((Song) currFile);
                        }
                    } else {
                        timeremaining -= passedtime;
                        passedtime = 0;
                    }
                }
            }
        }
        if (type.equals("playlist") && !pause) {
            int passedTime = command.getTimestamp() - lasttimestamp;
            if (repeat == 0) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;

                    currentObject++;
                    if (currentObject >= order.length) {
                        type = "nothing";
                        if (nextAd == 1) {
                            loadAd(passedTime);
                        }
                        break;
                    }
                    currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                    if (nextAd == 1) {
                        loadAd(passedTime);
                        passedTime -= ad.getDuration();
                    } else {
                        timeremaining = currFile.getDuration();
                        currUser.updateWrapped("song", currFile);
                        currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                        currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                        currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                        String art = ((Song) currFile).getArtist();
                        Userbase.getInstance().addStats(art, (Song) currFile);
                        Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                        addSg((Song) currFile);
                    }
                }
            }
            if (repeat == 1) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    currentObject++;
                    if (currentObject >= order.length) {
                        currentObject = 0;
                    }
                    currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                    if (nextAd == 1) {
                        loadAd(passedTime);
                        passedTime -= ad.getDuration();
                    } else {
                        timeremaining = currFile.getDuration();
                        currUser.updateWrapped("song", currFile);
                        currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                        currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                        currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                        String art = ((Song) currFile).getArtist();
                        Userbase.getInstance().addStats(art, (Song) currFile);
                        Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                        addSg((Song) currFile);
                    }
                }
            }
            if (repeat == 2) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    if (nextAd == 1) {
                        loadAd(passedTime);
                        passedTime -= ad.getDuration();
                    } else {
                        timeremaining = currFile.getDuration();
                        currUser.updateWrapped("song", currFile);
                        currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                        currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                        currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                        String art = ((Song) currFile).getArtist();
                        Userbase.getInstance().addStats(art, (Song) currFile);
                        Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                        addSg((Song) currFile);
                    }
                }
            }
        }
        if (type.equals("album") && !pause) {
            int passedTime = command.getTimestamp() - lasttimestamp;
            if (repeat == 0) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;

                    currentObject++;
                    if (currentObject >= order.length) {
                        type = "nothing";
                        if (nextAd == 1) {
                            loadAd(passedTime);
                        }
                        break;
                    }
                    currFile = ((Album) source).getSongs().get(order[currentObject]);
                    if (nextAd == 1) {
                        loadAd(passedTime);
                        passedTime -= ad.getDuration();
                    } else {
                        timeremaining = currFile.getDuration();
                        currUser.updateWrapped("song", currFile);
                        currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                        currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                        currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                        String art = ((Song) currFile).getArtist();
                        Userbase.getInstance().addStats(art, (Song) currFile);
                        Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                        addSg((Song) currFile);
                    }
                }
            }
            if (repeat == 1) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    currentObject++;
                    if (currentObject >= order.length) {
                        currentObject = 0;
                    }
                    currFile = ((Album) source).getSongs().get(order[currentObject]);
                    if (nextAd == 1) {
                        loadAd(passedTime);
                        passedTime -= ad.getDuration();
                    } else {
                        timeremaining = currFile.getDuration();
                        currUser.updateWrapped("song", currFile);
                        currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                        currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                        currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                        String art = ((Song) currFile).getArtist();
                        Userbase.getInstance().addStats(art, (Song) currFile);
                        Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                        addSg((Song) currFile);
                    }
                }
            }
            if (repeat == 2) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    if (nextAd == 1) {
                        loadAd(passedTime);
                        passedTime -= ad.getDuration();
                    } else {
                        timeremaining = currFile.getDuration();
                        currUser.updateWrapped("song", currFile);
                        currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                        currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                        currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                        String art = ((Song) currFile).getArtist();
                        Userbase.getInstance().addStats(art, (Song) currFile);
                        Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                        addSg((Song) currFile);
                    }
                }
            }
        }
        if (type.equals("podcast") && !pause) {
            int passedTime = command.getTimestamp() - lasttimestamp;
            if (repeat == 1) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    repeat = 0;
                    currFile = ((Podcast) source).getEpisodes().get(currentObject);
                    timeremaining = (currFile).getDuration();
                    currUser.updateWrapped("episode", currFile);
                    String ow = ((Podcast) source).getOwner();
                    Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
                    break;
                }
            }
            if (repeat == 0) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    currentObject++;
                    if (currentObject >= ((Podcast) source).getEpisodes().size()) {
                        type = "nothing";
                        break;
                    }
                    currFile = ((Podcast) source).getEpisodes().get(currentObject);
                    timeremaining = (currFile).getDuration();
                    currUser.updateWrapped("episode", currFile);
                    String ow = ((Podcast) source).getOwner();
                    Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
                }
            }
            if (repeat == 2) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    currFile = ((Podcast) source).getEpisodes().get(currentObject);
                    timeremaining = (currFile).getDuration();
                    currUser.updateWrapped("episode", currFile);
                    String ow = ((Podcast) source).getOwner();
                    Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
                }
            }
        }
        if (type.equals("nothing")) {
            timeremaining = 0;
            pause = true;
            repeat = 0;
            source = null;
            shuffle = false;
        }
        lasttimestamp = command.getTimestamp();
    }

    /**
     * Skips forward by 90 seconds
     * @param command
     */
    public void forward(final Command command) {
        final int timetomove = 90;
        if (repeat == 0) {
            if (timeremaining > timetomove) {
                timeremaining -= timetomove;
            } else {
              currentObject++;
              if (currentObject >= ((Podcast) source).getEpisodes().size()) {
                  type = "nothing";
              } else {
                  currFile = ((Podcast) source).getEpisodes().get(currentObject);
                  timeremaining = currFile.getDuration();
                  currUser.updateWrapped("episode", currFile);
                  String ow = ((Podcast) source).getOwner();
                  Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
              }
            }
        }
        if (repeat == 1) {
            if (timeremaining > timetomove) {
                timeremaining -= timetomove;
            } else {
                repeat = 0;
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("episode", currFile);
                String ow = ((Podcast) source).getOwner();
                Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
            }
        }
        if (repeat == 2) {
            if (timeremaining > timetomove) {
                timeremaining -= timetomove;
            } else {
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("episode", currFile);
                String ow = ((Podcast) source).getOwner();
                Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
            }
        }
        if (type.equals("nothing")) {
            timeremaining = 0;
            pause = true;
            repeat = 0;
            source = null;
            shuffle = false;
        }
        lasttimestamp = command.getTimestamp();
    }

    /***
     * Moves backward by 90 seconds, if the current episode has played
     * for at least 90 seconds, or t goes to the begging of the episode
     * @param command
     */
    public void backward(final Command command) {
        final int timetomove = 90;
        if (currFile.getDuration() - timeremaining > timetomove) {
            timeremaining += timetomove;
        } else {
            timeremaining = currFile.getDuration();
        }
        lasttimestamp = command.getTimestamp();
    }

    /**
     * Skips to the next source
     * @param command
     */
    public void next(final Command command) {
        if (repeat == 0) {
            if (type.equals("song")) {
                type = "nothing";
            }
            currentObject++;
            if (type.equals("podcast")) {
                if (currentObject >= ((Podcast) source).getEpisodes().size()) {
                    type = "nothing";
                } else {
                    currFile = ((Podcast) source).getEpisodes().get(currentObject);
                    timeremaining = currFile.getDuration();
                    currUser.updateWrapped("episode", currFile);
                    String ow = ((Podcast) source).getOwner();
                    Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
                }
            }
            if (type.equals("playlist")) {
                if (currentObject >= ((Playlist) source).getSongs().size()) {
                    type = "nothing";
                } else {
                    currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                    timeremaining = currFile.getDuration();
                    currUser.updateWrapped("song", currFile);
                    currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                    currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                    currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                    String art = ((Song) currFile).getArtist();
                    Userbase.getInstance().addStats(art, (Song) currFile);
                    Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                    addSg((Song) currFile);
                }
            }
            if (type.equals("album")) {
                if (currentObject >= ((Album) source).getSongs().size()) {
                    type = "nothing";
                } else {
                    currFile = ((Album) source).getSongs().get(order[currentObject]);
                    timeremaining = currFile.getDuration();
                    currUser.updateWrapped("song", currFile);
                    currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                    currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                    currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                    String art = ((Song) currFile).getArtist();
                    Userbase.getInstance().addStats(art, (Song) currFile);
                    Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                    addSg((Song) currFile);
                }
            }
        }
        if (repeat == 1) {
            if (type.equals("song")) {
                timeremaining = currFile.getDuration();
                repeat = 0;
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
            currentObject++;
            if (type.equals("podcast")) {
                currentObject--;
                repeat = 0;
                currFile = ((Podcast) source).getEpisodes().get(currentObject);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("episode", currFile);
                String ow = ((Podcast) source).getOwner();
                Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
            }
            if (type.equals("playlist")) {
                if (currentObject >= ((Playlist) source).getSongs().size()) {
                    currentObject = 0;
                }
                currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
            if (type.equals("album")) {
                if (currentObject >= ((Album) source).getSongs().size()) {
                    currentObject = 0;
                }
                currFile = ((Album) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
        }
        if (repeat == 2) {
            if (type.equals("song")) {
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
            if (type.equals("podcast")) {
                currFile = ((Podcast) source).getEpisodes().get(currentObject);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("episode", currFile);
                String ow = ((Podcast) source).getOwner();
                Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
            }
            if (type.equals("playlist")) {
                currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
            if (type.equals("album")) {
                currFile = ((Album) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
        }
        if (type.equals("nothing")) {
            timeremaining = 0;
            pause = true;
            repeat = 0;
            source = null;
            shuffle = false;
        }
    }

    /**
     * Moves backward to the previous source
     * @param command
     */
    public void prev(final Command command) {
        if (currentObject == 0 || currFile.getDuration() != timeremaining || type.equals("song")) {
            timeremaining = currFile.getDuration();
            currUser.updateWrapped("song", currFile);
            currUser.updateWrapped("album", ((Song) currFile).getAlbum());
            currUser.updateWrapped("genre", ((Song) currFile).getGenre());
            currUser.updateWrapped("artist", ((Song) currFile).getArtist());
            String art = ((Song) currFile).getArtist();
            Userbase.getInstance().addStats(art, (Song) currFile);
            Userbase.getInstance().updateArt((Song) currFile, currUser, art);
            addSg((Song) currFile);
        } else {
            currentObject--;
            if (type.equals("podcast")) {
                currFile = ((Podcast) source).getEpisodes().get(currentObject);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("episode", currFile);
                String ow = ((Podcast) source).getOwner();
                Userbase.getInstance().updateHost((Episode) currFile, currUser, ow);
            }
            if (type.equals("playlist")) {
                currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
            if (type.equals("album")) {
                currFile = ((Album) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
        }
    }

    /**
     * Function that plays an ad, give the respected artist
     * their share of money and resets the song list between ads
     * @param passedtime
     */
    public void loadAd(final int passedtime) {
        timeremaining = ad.getDuration();
        Userbase ub = Userbase.getInstance();
        for (int i = 0; i < listSg.getSongs().size(); i++) {
            Song crSg = listSg.getSongs().get(i).getSong();
            for (int j = 0; j < ub.getArtistData().size(); j++) {
                Userbase.ArtistData artd = ub.getArtistData().get(j);
                if (artd.getArtist().equals(crSg.getArtist())) {
                    for (int k = 0; k < artd.getSongIncs().size(); k++) {
                        String crname = artd.getSongIncs().get(k).getSong().getName();
                        if (crname.equals(crSg.getName())) {
                            double inc = artd.getSongIncs().get(k).getInc();
                            int list = listSg.getSongs().get(i).getListen();
                            inc += (adrev / listSg.getTotalls()) * list;
                            artd.getSongIncs().get(k).setInc(inc);
                            //System.out.println("User " + currUser.getUsername() + " paid " + artd.getArtist() + " " + (adrev / listSg.getTotalls()) * list + " money for song " + crSg.getName());
                            break;
                        }
                    }
                    double inc = artd.getSongrev();
                    inc += (adrev / listSg.getTotalls()) * listSg.getSongs().get(i).getListen();
                    artd.setSongrev(inc);
                    break;
                }
            }
        }

        //System.out.println("Ad break for user " + currUser.getUsername() + " paid " + adrev + " in total.");
        Library.getInstance().getList().addLast(listSg.getSongs());

        listSg.reserList();
        nextAd = 2;
        if (timeremaining < passedtime) {
            if (!type.equals("nothing")) {
                timeremaining = currFile.getDuration();
                currUser.updateWrapped("song", currFile);
                currUser.updateWrapped("album", ((Song) currFile).getAlbum());
                currUser.updateWrapped("genre", ((Song) currFile).getGenre());
                currUser.updateWrapped("artist", ((Song) currFile).getArtist());
                String art = ((Song) currFile).getArtist();
                Userbase.getInstance().addStats(art, (Song) currFile);
                Userbase.getInstance().updateArt((Song) currFile, currUser, art);
                addSg((Song) currFile);
            }
            nextAd = 0;
        } else {
            timeremaining = timeremaining - passedtime;
        }
    }

    /**
     * Function that adds a song to the list of songs between adds
     * This list will be used to decide which artist gets how much money
     * he deserves
     * @param sg
     */
    public void addSg(final Song sg) {
        boolean exists = false;
        if (currUser.isPremium()) {
            return;
        }
        listSg.setTotalls(listSg.getTotalls() + 1);
        for (int i = 0; i < listSg.getSongs().size(); i++) {
            Song wrsg = listSg.getSongs().get(i).getSong();
            if (wrsg.getName().equals(sg.getName())) {
                exists = true;
                int listens = listSg.getSongs().get(i).getListen();
                listSg.getSongs().get(i).setListen(listens + 1);
                break;
            }
        }
        if (!exists) {
            listSg.getSongs().addLast(new User.Wrapped.SongListen());
            listSg.getSongs().getLast().setListen(1);
            listSg.getSongs().getLast().setSong(sg);
        }
    }

    public AudioFiles getCurrFile() {
        return currFile;
    }

    public void setCurrFile(final AudioFiles currFile) {
        this.currFile = currFile;
    }

    public int getCurrentObject() {
        return currentObject;
    }

    public void setCurrentObject(final int currentObject) {
        this.currentObject = currentObject;
    }

    public int[] getOrder() {
        return order;
    }

    public void setOrder(final int[] order) {
        this.order = order;
    }
}
