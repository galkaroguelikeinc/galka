package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.GameModel;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        File fileForSaveGameState = new File(filenameForSaveGameState);
        if (!fileForSaveGameState.exists()) {
            try {
                return fileForSaveGameState.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        try (FileOutputStream fos = new FileOutputStream(fileForSaveGameState);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(gameModel);
            fos.close();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void deleteState() {
        File fileForSaveGameState = new File(filenameForSaveGameState);
        fileForSaveGameState.deleteOnExit();
    }

}
