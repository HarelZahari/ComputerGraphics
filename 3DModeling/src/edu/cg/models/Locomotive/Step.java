package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL21.*;

public class Step implements IRenderable {
    private Box step = new Box(Specification.STEP_WIDTH, Specification.STEP_HEIGHT, Specification.STEP_DEPTH);

    public void render() {
        glPushMatrix();
        // Step rendering
        Materials.setMaterialStep();
        step.render();
        glPopMatrix();
    }

    @Override
    public void init() {

    }
}