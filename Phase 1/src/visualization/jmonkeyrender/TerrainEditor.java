package visualization.jmonkeyrender;

public class TerrainEditor {
    private Renderer renderer;
    private boolean flyCam;

    public TerrainEditor(Renderer renderer) {
        this.renderer = renderer;
        this.flyCam = true;

        switchCamera();
    }

    public void switchCamera(){
        renderer.getFlyByCamera().setEnabled(flyCam);
        renderer.chaseCam.setEnabled(!flyCam);
        renderer.getFlyByCamera().setDragToRotate(!flyCam);
        renderer.getFlyByCamera().setMoveSpeed(50);

        flyCam = !flyCam;
    }

    public void editor(){

    }


}
