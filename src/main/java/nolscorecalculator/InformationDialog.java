/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import javax.swing.JOptionPane;

/**
 *
 * @author shep
 */
public class InformationDialog {

    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "Orienteering Series Score Calculator: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public static String selectionBox(String[] options, int defaultOption, String comment, String titleBar) {
        String selection = (String) JOptionPane.showInputDialog(null,
                comment,
                titleBar,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[defaultOption]);

        return selection;
    }
}
