package main;

import java.util.LinkedList;

 public final class ResultTop5 {
     private String command;
     private int timestamp;
     private LinkedList<String> result = new LinkedList<String>();

    public ResultTop5(final Command command) {
        this.command = command.getCommand();
        this.timestamp = command.getTimestamp();
    }

    public LinkedList<String> getResult() {
        return result;
    }

    public void setResult(final LinkedList<String> result) {
        this.result = result;
    }

     public String getCommand() {
         return command;
     }

     public void setCommand(final String command) {
         this.command = command;
     }

     public int getTimestamp() {
         return timestamp;
     }

     public void setTimestamp(final int timestamp) {
         this.timestamp = timestamp;
     }
 }
