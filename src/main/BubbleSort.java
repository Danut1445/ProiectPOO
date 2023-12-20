package main;

import java.util.LinkedList;

public final class BubbleSort {
    private LinkedList<Object> listOfObjects = new LinkedList<Object>();
    private String type;

    public LinkedList<Object> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(final LinkedList<Object> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    /**
     * Sorts a List of Objects with bubble sort
     */
    public void sort() {
        for (int i = 0; i < listOfObjects.size(); i++) {
            for (int j = 0; j < listOfObjects.size() - 1; j++) {
                Object aux;
                if (type.equals("song")) {
                    int likes = ((Song) listOfObjects.get(j)).getLikes();
                    if (likes < ((Song) listOfObjects.get(j + 1)).getLikes()) {
                        aux = listOfObjects.get(j);
                        listOfObjects.set(j, listOfObjects.get(j + 1));
                        listOfObjects.set(j + 1, aux);
                    }
                }
                if (type.equals("playlist")) {
                    int followers = ((Playlist) listOfObjects.get(j)).getFollowers();
                    if (followers < ((Playlist) listOfObjects.get(j + 1)).getFollowers()) {
                        aux = listOfObjects.get(j);
                        listOfObjects.set(j, listOfObjects.get(j + 1));
                        listOfObjects.set(j + 1, aux);
                    }
                }
                if (type.equals("playlistlikes")) {
                    int likes = ((Playlist) listOfObjects.get(j)).getLikes();
                    if (likes < ((Playlist) listOfObjects.get(j + 1)).getLikes()) {
                        aux = listOfObjects.get(j);
                        listOfObjects.set(j, listOfObjects.get(j + 1));
                        listOfObjects.set(j + 1, aux);
                    }
                }
                if (type.equals("album")) {
                    int likes = ((Album) listOfObjects.get(j)).getNrlikes();
                    if (likes < ((Album) listOfObjects.get(j + 1)).getNrlikes()) {
                        aux = listOfObjects.get(j);
                        listOfObjects.set(j, listOfObjects.get(j + 1));
                        listOfObjects.set(j + 1, aux);
                    } else {
                        if (likes == ((Album) listOfObjects.get(j + 1)).getNrlikes()) {
                            String name1 = ((Album) listOfObjects.get(j)).getName();
                            String name2 = ((Album) listOfObjects.get(j + 1)).getName();
                            if (name1.compareTo(name2) > 0) {
                                aux = listOfObjects.get(j);
                                listOfObjects.set(j, listOfObjects.get(j + 1));
                                listOfObjects.set(j + 1, aux);
                            }
                        }
                    }
                }
                if (type.equals("artist")) {
                    int likes = ((Artist) listOfObjects.get(j)).getNrLikes();
                    if (likes < ((Artist) listOfObjects.get(j + 1)).getNrLikes()) {
                        aux = listOfObjects.get(j);
                        listOfObjects.set(j, listOfObjects.get(j + 1));
                        listOfObjects.set(j + 1, aux);
                    }
                }
            }
        }
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
