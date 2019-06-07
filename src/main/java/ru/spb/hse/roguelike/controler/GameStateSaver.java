package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.GameModel;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.Optional;

/**
 * Class for saving game state after each move. Needed to continue the game if paused.
 */
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
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    static void setSavedState(@Nonnull GameModel gameModel) {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    static void deleteState() {
        File fileForSaveGameState = new File(filenameForSaveGameState);
        fileForSaveGameState.deleteOnExit();
    }

}
