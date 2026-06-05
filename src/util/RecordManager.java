package util;

import java.io.*;
import java.util.*;

public class RecordManager {
    private static final String FILE_NAME = "records.txt";

    public static void saveRecord(String winner, String loser) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(winner + "," + loser);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> getWinCounts() {
        Map<String, Integer> wins = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String winner = parts[0];
                    wins.put(winner, wins.getOrDefault(winner, 0) + 1);
                }
            }
        } catch (IOException e) { }
        return wins;
    }

    public static Map<String, Integer> getLosCounts() {
        Map<String, Integer> Loss = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String loser = parts[1];
                    Loss.put(loser, Loss.getOrDefault(loser, 0) + 1);
                }
            }
        } catch (IOException e) { }
        return Loss;
    }
}