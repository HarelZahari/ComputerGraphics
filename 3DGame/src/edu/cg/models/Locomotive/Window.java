package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL21.*;

public class Window implements IRenderable {
    private Box window = new Box(Specification.WINDOW_WIDTH, Specification.WINDOW_HEIGHT, Specification.WINDOW_DEPTH);

    public void render() {
        glPushMatrix();
        // Window rendering
        Materials.setMaterialWindow();
        window.render();
        glPopMatrix();
    }

    @Override
    public void init() {

    }
}

