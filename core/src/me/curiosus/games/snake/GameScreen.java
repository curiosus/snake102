package me.curiosus.games.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class GameScreen extends ScreenAdapter {


    private static final float MOVE_TIME = 1f;
    private static final int SNAKE_MOVEMENT = 32;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;


    private SpriteBatch batch;
    private Texture snakeHead;
    private Texture snakeBody;
    private int snakeX;
    private int snakeY;
    private int snakeDirection;
    private int snakeScore;
    private Texture apple;
    private boolean appleAvailable;
    private int appleX;
    private int appleY;
    private float timer;
    private Array<BodyPart> bodyParts;
    private int snakeXPrevious;
    private int snakeYPrevious;


    @Override
    public void show() {
        batch = new SpriteBatch();
        snakeDirection = RIGHT;
        appleX = 0;
        appleY = 0;
        timer = MOVE_TIME;
        snakeHead = new Texture(Gdx.files.internal("snake.png"));
        snakeBody = new Texture(Gdx.files.internal("snakebody.png"));
        apple = new Texture(Gdx.files.internal("apple.png"));
        bodyParts = new Array<BodyPart>();
    }

    @Override
    public void render(float delta) {

        queryForInput();

        timer -= delta;
        if (timer < 0) {
            timer = MOVE_TIME;
            placeApple();
            moveSnake();
            updateBodyParts();
        }
        clearScreen();
        draw();
        handleCollision();
        checkForOutOfBounds();
    }

    private void handleCollision() {
        if (collision()) {
            appleAvailable = false;
            placeApple();
            snakeScore++;
            addBodyPart();
        }
    }

    private void addBodyPart() {
        BodyPart bodyPart = new BodyPart(snakeBody);
        bodyPart.setPosition(snakeXPrevious, snakeYPrevious);
        bodyParts.insert(0, bodyPart);
    }

    private void updateBodyParts() {
        if (bodyParts.size > 0) {
            BodyPart bodyPart = bodyParts.removeIndex(0);
            bodyPart.setPosition(snakeXPrevious, snakeYPrevious);
            bodyParts.add(bodyPart);
        }
    }

    private boolean collision() {
        return (snakeX == appleX && snakeY == appleY);
    }

    private void draw() {
        batch.begin();
        if (appleAvailable) {
            batch.draw(apple, appleX, appleY);
        } else {
            placeApple();
        }
        batch.draw(snakeHead, snakeX, snakeY);
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.draw(batch);
        }
        batch.end();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }



    private void placeApple() {
        if (!appleAvailable) {
            boolean intersectsSnake = true;
            while (intersectsSnake) {
                appleX = MathUtils.random(Gdx.graphics.getWidth() / SNAKE_MOVEMENT -1) * SNAKE_MOVEMENT;
                appleY = MathUtils.random(Gdx.graphics.getHeight() / SNAKE_MOVEMENT -1) * SNAKE_MOVEMENT;
                intersectsSnake = (snakeX == appleX && appleY == snakeY);
            }
            appleAvailable = true;
        }
    }

    private void moveSnake() {

        snakeXPrevious = snakeX;
        snakeYPrevious = snakeY;

        switch (snakeDirection) {
            case RIGHT: {
                snakeX += SNAKE_MOVEMENT;
                break;
            }
            case LEFT: {
                snakeX -= SNAKE_MOVEMENT;
                break;
            }
            case UP: {
                snakeY += SNAKE_MOVEMENT;
                break;
            }
            case DOWN: {
                snakeY -= SNAKE_MOVEMENT;
                break;
            }
        }
    }

    private void queryForInput() {

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            snakeDirection = LEFT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            snakeDirection = RIGHT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            snakeDirection = UP;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            snakeDirection = DOWN;
        }

    }

    private void checkForOutOfBounds() {

        if (snakeX >= Gdx.graphics.getWidth()) {
            snakeX = 0;
        } else if (snakeX < 0) {
            snakeX = Gdx.graphics.getWidth() - SNAKE_MOVEMENT;
        }

        if (snakeY >= Gdx.graphics.getHeight()) {
            snakeY = 0;
        } else if (snakeY < 0) {
            snakeY = Gdx.graphics.getHeight() - SNAKE_MOVEMENT;
        }

    }

    private  class BodyPart {

       private int x;
       private int y;
       private Texture texture;

       BodyPart(Texture texture) {
          this.texture = texture;
       }

       void setPosition(int x, int y) {
           this.x = x;
           this.y = y;
       }


       void draw(Batch batch) {
           if (!(x == snakeX && y == snakeY)) {
              batch.draw(texture, x, y);
           }
       }

    }
}
