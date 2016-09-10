/*
Esta clase guarda los metodos que permiten el manejo del raton mediante un mando analogico de PS3.
Se hace uso de la libreria net.java.games y la Clase Robot.
--------------------------------------------------------------------------------------------------
This class includes the procedures that allow handling mouse using a PS3 controller.
The class uses the net.java.games library and Robot class.
*/
package joystick;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class MoverMouse{
    private ArrayList<Controller> foundControllers;
    private Controller c;
    private Robot mouse = null;
    
    public MoverMouse() {
        foundControllers = new ArrayList<>();
        searchForControllers();
        try{ mouse = new Robot(); }catch(Exception ex){}
       test();
    }
       
    /*
    Este es el metodo principal el cual reconoce los componentes del controller escogido.
    Una vez reconocidos comprueba los eventos que se produzcan (presion de botones, movimiento de los analogicos, etc),
    y actuara en consecuencia.
    ----------------------------------------------------------------------------------------------------------------------------
    This is the main procedure, which reconigze the components of the controller choosen.
    Once reconigze, the procedure checks the events that occurs (press button, analog movements, etc),
    and act in consecuence.
    */
    private void test(){
        float xAxisPercentage = 800;
        float yAxisPercentage = 800;
        String buttonIndex="";
        while(true){
            Controller c = foundControllers.get(0);
            if( !c.poll() ){
                break;
            }
            
            Component[] comp = c.getComponents();
            for(int i=0;i<comp.length;i++){
                Component component = comp[i];
                Component.Identifier componentIdentifier = component.getIdentifier();
                
                if(componentIdentifier.getName().matches("^[0-9]*$")){ 
                    boolean isItPressed = true;
                    buttonIndex = component.getIdentifier().toString();
                    if(component.getPollData() == 0.0f){
                        isItPressed = false;
                        if(buttonIndex.equals("2")){
                            mouse.mouseRelease(InputEvent.BUTTON1_MASK);
                        }
                    }
                    if(isItPressed){
                        switch(buttonIndex){
                            case "0":
                               mouse.mousePress(InputEvent.BUTTON1_MASK);
                               mouse.mouseRelease(InputEvent.BUTTON1_MASK);
                            case "1":
                                mouse.mousePress(InputEvent.BUTTON3_MASK);
                                mouse.mouseRelease(InputEvent.BUTTON3_MASK);
                            case "2":
                                mouse.mousePress(InputEvent.BUTTON1_MASK);
                        }
                        try {
                        Thread.sleep(40);
                    } catch (InterruptedException ex) { }
                    }
                    continue;
                }
                
                if(component.isAnalog()){
                        float axisValue2 = component.getPollData();
                        float aux2 = pasar(axisValue2);
                        float axisValue = getAxisValue(axisValue2);
                        if(aux2 > 0.1){
                        if(componentIdentifier == Component.Identifier.Axis.X){
                            xAxisPercentage = xAxisPercentage + axisValue; continue;
                        }
                        // Y axis
                        if(componentIdentifier == Component.Identifier.Axis.Y){
                            yAxisPercentage = yAxisPercentage + axisValue; continue;
                        }
                        if(componentIdentifier == Component.Identifier.Axis.RY){
                            mouse.mouseWheel((int) ((int) axisValue*0.01)); continue;
                        }
                        }
                }
            }
            mouse.mouseMove((int) xAxisPercentage,(int) yAxisPercentage);
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) { }
        }
    }       
    
    /*
    Este metodo compreba los dispositivos que hay conectados en el PC. Coge el primero por defecto.
    -----------------------------------------------------------------------------------------------
    This procedure checks the devices that are connected to the PC. Take the first by default.
    */
    private void searchForControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i = 0; i < controllers.length; i++){
            Controller controller = controllers[i];
            
            if (
                    controller.getType() == Controller.Type.STICK || 
                    controller.getType() == Controller.Type.GAMEPAD || 
                    controller.getType() == Controller.Type.WHEEL ||
                    controller.getType() == Controller.Type.FINGERSTICK
               )
                
                foundControllers.add(controller);
        }
    }
    
    //Convierte en positivo los numeros.
    public float pasar(float a){
        float aux = 0;
        if(a>0) aux = a;
        else aux = a * (-1);
        return aux;
    }
    
    public float getAxisValue(float axisValue){
        return (float) (axisValue*(8.5));
    }
    
    public static void main( String args[] ){
           new MoverMouse();
    }
}
