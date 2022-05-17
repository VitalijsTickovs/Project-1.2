package gui;

import gameengine.Game;

public abstract class BallVelocityInput {
    protected Game game;

    public BallVelocityInput(Game game) {
        this.game = game;
    }

    public abstract void readyForNextInput();
}
