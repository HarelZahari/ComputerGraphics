package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL21.*;

public class Door implements IRenderable {
    private Box door = new Box(Specification.DOOR_WIDTH, Specification.DOOR_HEIGHT, Specification.DOOR_DEPTH);

    public void render() {
        glPushMatrix();
        // Door rendering
        Materials.setMaterialDoor();
        door.render();
        glPopMatrix();
    }

    @Override
    public void init() {

    }
}