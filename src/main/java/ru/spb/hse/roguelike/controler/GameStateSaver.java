package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.GameModel;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.Optional;

public class GameStateSaver {
    private static final String filenameForSaveGameState = "galka";

    public static Optional<GameModel> getSavedState() {
        File fileForSaveGameState = new File(filenameForSaveGameState);
        if (!fileForSaveGameState.exists()) {
            return Optional.empty();
        }
        try (FileInputStream fis = new FileInputStream(fileForSaveGameState);
             ObjectInputStream in = new ObjectInputStream(fis)) {
            GameModel gameModel = (GameModel) in.readObject();
            in.close();
            fis.close();
            return Optional.of(gameModel);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public static boolean setSavedState(@Nonnull GameModel gameModel) {
        try {
            Thread thread = new Thread(() -> {
                File fileForSaveGameState = new File(filenameForSaveGameState);
                if (!fileForSaveGameState.exists()) {
                    try {
                        fileForSaveGameState.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try (FileOutputStream fos = new FileOutputStream(fileForSaveGameState);
                     ObjectOutputStream out = new ObjectOutputStream(fos)) {
                    out.writeObject(gameModel);
                } catch (Exception e) {
                    fileForSaveGameState.deleteOnExit();
                    e.printStackTrace();
                }
            });
            thread.start();
            thread.join();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void deleteState() {
        File fileForSaveGameState = new File(filenameForSaveGameState);
        fileForSaveGameState.deleteOnExit();
    }

}
