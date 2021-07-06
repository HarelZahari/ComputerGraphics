package edu.cg;

import edu.cg.algebra.Vec;
import edu.cg.models.Locomotive.Locomotive;
import edu.cg.models.Track.Track;
import edu.cg.models.Track.TrackSegment;
import edu.cg.util.glu.Project;

import static org.lwjgl.opengl.GL21.*;

/**
 * An OpenGL model viewer
 */
public class Viewer {
    int canvasWidth, canvasHeight;
    private final GameState gameState; // Tracks the vehicle movement and orientation
    private final Locomotive car; // The locomotive we wish to render.
    private final Track gameTrack; // The track we wish to render.
    // driving direction, or looking down on the scene from above.
    private Vec carCameraTranslation; // The accumulated translation that should be applied on the car and camera.
    private boolean isModelInitialized = false; // Indicates whether initModel was called.
    private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
    private boolean isBirdseyeView = false; // Indicates whether the camera's perspective corresponds to the vehicle's

    // TODO: Set the initial position of the vehicle in the scene by assigning a value to carInitialPosition.
    private final double[] carInitialPosition;

    // TODO: set the car scale as you wish - we uniformly scale the car by 3.0.

    // TODO: You can add additional fields to assist your implementation, for example:
    // - Camera initial position for standard 3rd person mode(should be fixed)
    // - Camera initial position for birdseye view)
    // - Light colors
    // Or in short anything reusable - this make it easier for your to keep track of your implementation.
    private Vec personViewPosition;
    private Vec birdEyePosition;

    public Viewer(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        this.gameState = new GameState();
        this.gameTrack = new Track();
        this.carCameraTranslation = new Vec(0.0);
        this.car = new Locomotive();

        this.carInitialPosition = new double[]{0.0f, 1f, -5f};
        this.personViewPosition = new Vec(this.carInitialPosition[0], carInitialPosition[1] + 2f, carInitialPosition[2] + 4.5f);
        this.birdEyePosition = new Vec(this.carInitialPosition[0], 80f, carInitialPosition[2] - 30f);
    }

    public void render() {
        if (!this.isModelInitialized)
            initModel();
        // TODO : Define background color for the scene in day mode and in night.
        if (this.isDayMode) {
            // TODO: Setup background when day mode is on
            // use gl.glClearColor() function.
            glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1.0f);
        } else {
            // TODO: Setup background when night mode is on.
            glClearColor(192 / 255f, 192 / 255f, 192 / 255f, 1.0f);
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // TODO: Read this part of the code, understand the flow that goes into rendering the scene.
        // Step (1) Update the accumulated translation that needs to be
        // applied on the car, camera and light sources.
        updateCarCameraTranslation();
        // Step (2) Position the camera and setup its orientation.
        setupCamera();
        // Step (3) setup the lights.
        setupLights();
        // Step (4) render the car.
        renderVehicle();
        // Step (5) render the track.
        renderTrack();
    }

    public void init() {
        // TODO(*) In your final submission you need to make sure that BACK FACE culling is enabled.
        //      You may disable face culling while building your model, and only later return it.
        //      Note that doing so may require you to modify the way you present the vertices to OPENGL in order for the
        //      normal of all surface be facing outside. See recitation 8 for more information about face culling.
        glCullFace(GL_BACK);    // Set Culling Face To Back Face
        glEnable(GL_CULL_FACE); // Enable back face culling

        // Enable other flags for OPENGL.
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);

        reshape(0, 0, canvasWidth, canvasHeight);
    }

    private void updateCarCameraTranslation() {
        // Here we update the car and camera translation values (not the ModelView-Matrix).
        // - We always keep track of the car offset relative to the starting
        // point.
        // - We change the track segments here if necessary.
        // getNextTranslation returns the delta - the change to be accounted for in the translation.
        // getNextTranslation returns the delta - the change to be accounted for in the translation.
        Vec ret = this.gameState.getNextTranslation();
        this.carCameraTranslation = this.carCameraTranslation.add(ret);
        // Min and Max calls to make sure we do not exceed the lateral boundaries of the track.
        double dx = Math.max(this.carCameraTranslation.x, -TrackSegment.ASPHALT_LENGTH / 2 - 2);
        this.carCameraTranslation.x = (float) Math.min(dx, TrackSegment.ASPHALT_LENGTH / 2 + 2);
        // If the car reaches the end of the track segment, we generate a new segment.
        if (Math.abs(this.carCameraTranslation.z) >= TrackSegment.TRACK_SEGMENT_LENGTH - this.carInitialPosition[2]) {
            this.carCameraTranslation.z = -((float) (Math.abs(this.carCameraTranslation.z) % TrackSegment.TRACK_SEGMENT_LENGTH));
            this.gameTrack.changeTrackSegment();
        }
    }

    private void setupCamera() {
        // TODO: In this method you are advised to use :
        //       GLU glu = new GLU();
        //       glu.gluLookAt();
        Vec cameraCoordinate = getCameraCoordination();
        Vec normalizeUp = getNormalizeUp();

        if (this.isBirdseyeView) {
            // TODO Setup camera for the Birds-eye view (You need to configure the viewing transformation accordingly).
            Project.gluLookAt(cameraCoordinate.x, cameraCoordinate.y, cameraCoordinate.z, cameraCoordinate.x, cameraCoordinate.y - 5, cameraCoordinate.z, normalizeUp.x, normalizeUp.y, normalizeUp.z);
        } else {
            // TODO Setup camera for standard 3rd person view.
            Project.gluLookAt(cameraCoordinate.x, cameraCoordinate.y, cameraCoordinate.z, cameraCoordinate.x, cameraCoordinate.y, cameraCoordinate.z - 20, normalizeUp.x, normalizeUp.y, normalizeUp.z);
        }
    }

    private Vec getNormalizeUp() {
        Vec normalizeUpVec = null;

        //Calculate the up vector
        if (!isBirdseyeView) {
            normalizeUpVec = new Vec(0, 1, 0);
        } else {
            normalizeUpVec = new Vec(0, 0, -1);
        }

        return normalizeUpVec;
    }

    private Vec getCameraCoordination() {
        float xCoordinate;
        float yCoordinate;
        float zCoordinate;

        float xCarCameraTranslation = carCameraTranslation.x;
        float yCarCameraTranslation = carCameraTranslation.y;
        float zCarCameraTranslation = carCameraTranslation.z;

        // Calculate the camera coordination according to the view status
        if (!isBirdseyeView) {
            xCoordinate = personViewPosition.x;
            yCoordinate = personViewPosition.y;
            zCoordinate = personViewPosition.z;
        } else {
            xCoordinate = birdEyePosition.x;
            yCoordinate = birdEyePosition.y;
            zCoordinate = birdEyePosition.z;
        }

        return new Vec(xCoordinate + xCarCameraTranslation, yCoordinate + yCarCameraTranslation, zCoordinate + zCarCameraTranslation);
    }

    private void setupLights() {
        if (this.isDayMode) {
            // TODO Setup day lighting.
            // * Remember: switch-off any light sources that were used in night mode and are not use in day mode.
            // Set day light
            glDisable(GL_LIGHT1);
            glLightfv(GL_LIGHT1, GL_AMBIENT, new float[]{0.3f, 0.3f, 0.3f, 1.0f});
            glLightfv(GL_LIGHT1, GL_DIFFUSE, new float[]{0.8f, 0.8f, 0.8f, 1.0f});
            glLightfv(GL_LIGHT1, GL_SPECULAR, new float[]{0.7f, 0.7f, 0.7f, 1.0f});
            glLightfv(GL_LIGHT1, GL_POSITION, new float[]{0, 1, 1, 0.0f});
            glEnable(GL_LIGHT1);
        } else {
            // TODO Setup night lighting - here you should only set the ambient light source.
            //      The locomotive's spotlights should be defined in the car local coordinate system.
            //      so it is better to define the car light properties right before your render the locomotive rather
            // Set night light
            glLightModelfv(GL_AMBIENT, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
        }
    }

    private void renderTrack() {
        glPushMatrix();
        // TODO : Note that if you wish to support textures, the render method of gameTrack must be changed.
        this.gameTrack.render();
        glPopMatrix();
    }

    private void renderVehicle() {
        // TODO: Render the vehicle.
        // * Remember: the vehicle's position should be the initial position + the accumulated translation.
        //             This will simulate the car movement.
        // * Remember: the car was modeled locally, you may need to rotate/scale and translate the car appropriately.
        // * Recommendation: it is recommended to define fields (such as car initial position) that can be used during rendering.
        // * You should set up the car lights right before you render the locomotive after the appropriate transformations
        // * have been applied. This ensures that the light sources are fixed to the locomotive (ofcourse all of this
        // * is only relevant to rendering the vehicle in night mode).

        glPushMatrix();

        // Moving to carPosition + Translation
        glTranslated(carInitialPosition[0] + carCameraTranslation.x, carInitialPosition[1] + carCameraTranslation.y,
                carInitialPosition[2] + carCameraTranslation.z);
        // Scaling the car as required 3 uniformly
        glScaled(3, 3, 3);
        glRotated(180 - gameState.getCarRotation(), 0, 1, 0);

        // Lighting care at night
        if (!this.isDayMode) {
            // Set car light if night
            turnOnCarLights();
        }

        // Render the Locomotive
        this.car.render();

        glPopMatrix();
    }

    private void turnOnCarLights() {
        float[] frontDirection = new float[]{0, 0, 1, 0};

        glDisable(GL_LIGHT0);
        glLightfv(GL_LIGHT0, GL_POSITION, new float[]{0.3f, 0.5f, 0.3f, 1.0f});
        glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, frontDirection);
        glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 70);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[]{0.8f, 0.8f, 0.8f, 1.0f});
        glLightfv(GL_LIGHT0, GL_SPECULAR, new float[]{0.5f, 0.5f, 0.5f, 1.0f});
        glEnable(GL_LIGHT0);

        glDisable(GL_LIGHT1);
        glLightfv(GL_LIGHT1, GL_POSITION, new float[]{-0.3f, 0.5f, 0.3f, 1.0f});
        glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, frontDirection);
        glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 70);
        glLightfv(GL_LIGHT1, GL_DIFFUSE, new float[]{0.8f, 0.8f, 0.8f, 1.0f});
        glLightfv(GL_LIGHT1, GL_SPECULAR, new float[]{0.5f, 0.5f, 0.5f, 1.0f});
        glEnable(GL_LIGHT1);
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void initModel() {
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_SMOOTH);
        this.gameTrack.init();
        this.car.init();
        this.isModelInitialized = true;
    }

    public void reshape(int x, int y, int width, int height) {
        // We recommend using gluPerspective, which receives the field of view in the y-direction. You can use this
        // method by importing it via:
        // >> import static edu.cg.util.glu.Project.gluPerspective;
        // Further information about this method can be found in the recitation materials.
        glViewport(x, y, width, height);
        canvasWidth = width;
        canvasHeight = height;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = width / height;
        if (this.isBirdseyeView) {
            // TODO : Set a projection matrix for birdseye view mode.
            setPerspective(50, aspectRatio, 10, 85);
        } else {
            // TODO : Set a projection matrix for third person mode.
            Project.gluPerspective(120, aspectRatio, 1f, 500);
        }
    }

    private void setPerspective(float fovy, float aspectRatio, float zNear, float zFar) {
        Project.gluPerspective(fovy, aspectRatio, zNear, zFar);
    }

    public void toggleNightMode() {
        this.isDayMode = !this.isDayMode;
    }

    public void changeViewMode() {
        this.isBirdseyeView = !this.isBirdseyeView;
    }
}