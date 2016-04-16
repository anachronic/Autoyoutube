package org.anachronic.autoyoutube;

import org.anachronic.autoyoutube.app.AppContext;
import org.anachronic.autoyoutube.ui.MainFrame;

import javax.swing.*;

public class UIMain {

    public static void main(String[] args) {


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        MainFrame frame = new MainFrame(AppContext.APP_NAME);

        JLabel label = new JLabel("Just a Label");
        frame.getContentPane().add(label);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }


}
