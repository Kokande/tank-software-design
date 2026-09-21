package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.objects.Tank;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.S;

public class ControlsProcessor {
    enum Movements {
        UPWARDS(new int[]{Input.Keys.W, Input.Keys.UP}, Direction.UP),
        DOWNWARDS(new int[]{DOWN, S}, Direction.DOWN),
        LEFTWARDS(new int[]{LEFT, A}, Direction.LEFT),
        RIGHTWARDS(new int[]{RIGHT, D}, Direction.RIGHT);

        final int[] oneOfKeys;
        final Direction moveDirection;

        Movements(int[] equalKeys, Direction direction) {
            oneOfKeys = equalKeys;
            moveDirection = direction;
        }

        public int[] getEquivalentKeys() {
            return oneOfKeys;
        }

        public Direction getMoveDirection() {
            return moveDirection;
        }
    }

    private Tank playerMovement;
    private GameMap environment;

    public ControlsProcessor(Tank player, GameMap map) {
        playerMovement = player;
        environment = map;
    }

    private boolean eitherPressed(int[] keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    public void processMovement() {
        Direction move;
        for (Movements movement : Movements.values()) {
            if (eitherPressed(movement.getEquivalentKeys())) {
                 move = movement.getMoveDirection();
                playerMovement.tryMovement(move.dx(), move.dy(), move.rotation(), environment);
            }
        }
    }

    public void processInput() {
        processMovement();
    }
}
