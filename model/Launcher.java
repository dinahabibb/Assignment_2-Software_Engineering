package model;

import javax.swing.*;

/**
 * Entry point of the application.
 * Launches the GUI using Swing's Event Dispatch Thread (EDT).
 */
public class Launcher {

    /**
     * Starts the application.
     * Ensures Swing components are created on the EDT for thread safety.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow main = new MainWindow();
                main.show();
            }
        });
    }
}
