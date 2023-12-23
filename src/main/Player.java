package main;

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

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User currUser) {
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

    /**
     * Updates the player to see what it is status in the given command timestamp
     * @param command
     */
    public void status(final Command command) {
        if (type.equals("song") && !pause) {
            int passedtime = command.getTimestamp() - lasttimestamp;
            if (repeat == 1) {
                if (timeremaining < command.getTimestamp() - lasttimestamp) {
                    passedtime -= timeremaining;
                    repeat = 0;
                    timeremaining = currFile.getDuration();
                } else {
                    passedtime = 0;
                    timeremaining = timeremaining - (command.getTimestamp() - lasttimestamp);
                }
            }
            if (repeat == 0) {
                if (timeremaining < passedtime) {
                    type = "nothing";
                } else {
                    timeremaining = timeremaining - passedtime;
                }
            }
            if (repeat == 2) {
                while (passedtime > 0) {
                    if (passedtime >= timeremaining) {
                        passedtime -= timeremaining;
                        timeremaining = currFile.getDuration();
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
                        break;
                    }
                    currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                    timeremaining = (currFile).getDuration();
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
                    timeremaining = (currFile).getDuration();
                }
            }
            if (repeat == 2) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    timeremaining = currFile.getDuration();
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
                        break;
                    }
                    currFile = ((Album) source).getSongs().get(order[currentObject]);
                    timeremaining = (currFile).getDuration();
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
                    timeremaining = (currFile).getDuration();
                }
            }
            if (repeat == 2) {
                while (passedTime > 0) {
                    if (passedTime < timeremaining) {
                        timeremaining = timeremaining - passedTime;
                        break;
                    }
                    passedTime = passedTime - timeremaining;
                    timeremaining = currFile.getDuration();
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
              }
            }
        }
        if (repeat == 1) {
            if (timeremaining > timetomove) {
                timeremaining -= timetomove;
            } else {
                repeat = 0;
                timeremaining = currFile.getDuration();
            }
        }
        if (repeat == 2) {
            if (timeremaining > timetomove) {
                timeremaining -= timetomove;
            } else {
                timeremaining = currFile.getDuration();
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
                }
            }
            if (type.equals("playlist")) {
                if (currentObject >= ((Playlist) source).getSongs().size()) {
                    type = "nothing";
                } else {
                    currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                    timeremaining = currFile.getDuration();
                }
            }
            if (type.equals("album")) {
                if (currentObject >= ((Album) source).getSongs().size()) {
                    type = "nothing";
                } else {
                    currFile = ((Album) source).getSongs().get(order[currentObject]);
                    timeremaining = currFile.getDuration();
                }
            }
        }
        if (repeat == 1) {
            if (type.equals("song")) {
                timeremaining = currFile.getDuration();
                repeat = 0;
            }
            currentObject++;
            if (type.equals("podcast")) {
                currentObject--;
                repeat = 0;
                currFile = ((Podcast) source).getEpisodes().get(currentObject);
                timeremaining = currFile.getDuration();
            }
            if (type.equals("playlist")) {
                if (currentObject >= ((Playlist) source).getSongs().size()) {
                    currentObject = 0;
                }
                currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
            }
            if (type.equals("album")) {
                if (currentObject >= ((Album) source).getSongs().size()) {
                    currentObject = 0;
                }
                currFile = ((Album) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
            }
        }
        if (repeat == 2) {
            if (type.equals("song")) {
                timeremaining = currFile.getDuration();
            }
            if (type.equals("podcast")) {
                currFile = ((Podcast) source).getEpisodes().get(currentObject);
                timeremaining = currFile.getDuration();
            }
            if (type.equals("playlist")) {
                currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
            }
            if (type.equals("album")) {
                currFile = ((Album) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
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
        } else {
            currentObject--;
            if (type.equals("podcast")) {
                currFile = ((Podcast) source).getEpisodes().get(currentObject);
                timeremaining = currFile.getDuration();
            }
            if (type.equals("playlist")) {
                currFile = ((Playlist) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
            }
            if (type.equals("album")) {
                currFile = ((Album) source).getSongs().get(order[currentObject]);
                timeremaining = currFile.getDuration();
            }
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
