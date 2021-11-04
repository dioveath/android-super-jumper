package com.charicha.superjumper;

import com.charicha.FileIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Charicha on 1/13/2018.
 */

public class Settings {

    public static int[] localHighScores = {100, 50, 30, 20, 10};
    public static String[] localPlayerNames = {"Charicha", "Charicha", "Charicha", "Charicha", "Charicha"};
    public static boolean soundEnabled = true;

    public static void loadLocalSettings(FileIO fileIO){
        try {
            InputStream iStream = fileIO.readFile("settings.sj");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));

            soundEnabled = Boolean.parseBoolean(bReader.readLine());
            for(int i = 0; i < 5; i++){
                localPlayerNames[i] = bReader.readLine();
                localHighScores[i] = Integer.parseInt(bReader.readLine());
            }
        } catch(IOException ioe){
        }
    }

    public static void saveLocalSettings(FileIO fileIO) {
        try {
            OutputStream oStream = fileIO.writeFile("settings.sj");
            BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(oStream));

            bWriter.write(Boolean.toString(soundEnabled));
            bWriter.newLine();
            for(int i = 0; i < 5; i++){
                bWriter.write(localPlayerNames[i]);
                bWriter.newLine();
                bWriter.write(Integer.toString(localHighScores[i]));
                bWriter.newLine();
            }
            oStream.close();
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static void addScore(int score){
        for(int i = 0; i < 5; i++){
            if(score > localHighScores[i]){
                for(int j = 4; j > i; j--){
                    localHighScores[j] = localHighScores[j - 1];
                }
                localHighScores[i] = score;
                return;
            }
        }
    }

}
