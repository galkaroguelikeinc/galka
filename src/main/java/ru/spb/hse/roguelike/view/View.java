package ru.spb.hse.roguelike.view;

import ru.spb.hse.roguelike.model.GameModel;

/**
 * A View, as in Model-View-Controller, which displays the game state to the user.
 * <p>
 * The way to display it to the user is up to the extending class.
 * <p>
 * It could be via text printed to console, terminal pseudo-graphics, JavaFX or any other 2D graphics, 3D, VR, etc.
 */
public abstract class View {
    GameModel gameModel;
    /**
     * Create a View.
     *
     * @param gameModel stores game data which needs to be shown to user
     */
    View(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    /**
     * Notify the View that gameModel has changes. It should redraw the gameModel shown to user.
     */
    public abstract void showChanges();

    public abstract String readCommand();
}
