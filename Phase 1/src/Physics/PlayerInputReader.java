package physics;

import utility.math.Vector2;

public class PlayerInputReader extends InputModule {

    //InputField GUIInput;

    public void setInputField(/*InputField newInput */){
        //GUIInput = newInput;
        //Once GUI is created, this needs to be connected
    }

    @Override
    public Vector2 getForce() {
        return readVectorFromInputField();
    }

    private Vector2 readVectorFromInputField(){
     //return GUIInput.getVector();
     return null;   
    }

}
