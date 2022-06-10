package gui.shotinput;

import visualization.InputInt;

public abstract class BallVelocityInput {
    protected InputInt game;

    public BallVelocityInput(InputInt game){this.game = game;}

    public abstract void readyForNextInput();

    public abstract void hideInputWindow();
}
