package code.java;


import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import code.java.InputWaiter.*;

/**
 * @author peter
 */
public class InputListener implements KeyListener, MouseMotionListener {


    private Map<EventType, List<InputWaiter>> inputWaiterList;
    private Canvas canvas;

    public InputListener(Canvas canvas) {
        this.canvas = canvas;
        this.inputWaiterList = new HashMap<>();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        eventOccurred(EventType.Key_Typed, e);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        eventOccurred(EventType.Mouse_Moved, e);
    }

    public void addInputWaiter (EventType eventType, InputWaiter inputWaiter) {
        List<InputWaiter> waiterList;
        if (inputWaiterList.containsKey(eventType)){
            waiterList = inputWaiterList.get(eventType);
        } else {
            waiterList = new LinkedList<>();
            inputWaiterList.put(eventType, waiterList);
            switch (eventType) {
                case Key_Typed:
                    canvas.addKeyListener(this);
                    break;
                case Mouse_Moved:
                    canvas.addMouseMotionListener(this);
                    break;
            }
        }
        waiterList.add(inputWaiter);
    }

    public void removeInputWaiter(EventType eventType, InputWaiter inputWaiter) {
       inputWaiterList.remove(eventType, inputWaiter);
    }

    private void eventOccurred(EventType eventType, InputEvent event) {
        List<InputWaiter> waiterList = inputWaiterList.get(eventType);
        for (InputWaiter inputWaiter : waiterList) {
            inputWaiter.inputEventHappened(event);
        }
    }
}
