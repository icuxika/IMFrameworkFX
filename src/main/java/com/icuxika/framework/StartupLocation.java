package com.icuxika.framework;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.awt.*;
import java.util.List;

/**
 * 来源 https://stackoverflow.com/questions/25714573/open-javafx-application-on-active-screen-or-monitor-in-multi-screen-setup
 */
public class StartupLocation {

    private double xPos = 0;
    private double yPos = 0;

    /**
     * Get Top Left X and Y Positions for a Window to centre it on the
     * currently active screen at application startup
     *
     * @param windowWidth  - Window Width
     * @param windowHeight - Window Height
     */
    public StartupLocation(double windowWidth, double windowHeight) {
        // Get X Y of start-up location on Active Screen
        // simple_JavaFX_App
        try {
            // Get current mouse location, could return null if mouse is moving Super-Man fast
            Point p = MouseInfo.getPointerInfo().getLocation();
            // Get list of available screens
            List<Screen> screens = Screen.getScreens();
            if (p != null && screens != null && screens.size() > 1) {
                // Screen bounds as rectangle
                Rectangle2D screenBounds;
                // Go through each screen to see if the mouse is currently on that screen
                for (Screen screen : screens) {
                    screenBounds = screen.getVisualBounds();
                    // Trying to compute Left Top X-Y position for the Application Window
                    // If the Point p is in the Bounds
                    if (screenBounds.contains(p.x, p.y)) {
                        // Fixed Size Window Width and Height
                        xPos = screenBounds.getMinX() + ((screenBounds.getMaxX() - screenBounds.getMinX() - windowWidth) / 2);
                        yPos = screenBounds.getMinY() + ((screenBounds.getMaxY() - screenBounds.getMinY() - windowHeight) / 2);
                        return;
                    }
                }
            }
        } catch (HeadlessException headlessException) {
            // Catch and report exceptions
            headlessException.printStackTrace();
        }
    }

    /**
     * @return the top left X Position of Window on Active Screen
     */
    public double getXPos() {
        return xPos;
    }

    /**
     * @return the top left Y Position of Window on Active Screen
     */
    public double getYPos() {
        return yPos;
    }

}
