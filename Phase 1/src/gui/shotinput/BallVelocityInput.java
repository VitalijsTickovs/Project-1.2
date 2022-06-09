package gui.shotinput;

import visualization.gameengine.Game;
import visualization.jmonkeyrender.Renderer;

public abstract class BallVelocityInput {
    protected Game game;
    protected Renderer renderer;

    public BallVelocityInput(Game game) {
        this.game = game;
    }
    public BallVelocityInput(Renderer renderer){this.renderer = renderer;}

    public abstract void readyForNextInput();
}
