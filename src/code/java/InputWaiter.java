package code.java;

import java.awt.event.InputEvent;

/**
 * @author peter
 */
public interface InputWaiter {
    enum EventType {
      Key_Typed, Mouse_Moved
    }

    void inputEventHappened(InputEvent event);
}
