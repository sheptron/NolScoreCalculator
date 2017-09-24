/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NolProgressBar extends JPanel {

  JProgressBar pbar;
  private JTextArea taskOutput;

  static final int MY_MINIMUM = 0;

  static final int MY_MAXIMUM = 100;

  public NolProgressBar() {
      
    // initialize Progress Bar
    pbar = new JProgressBar();
    pbar.setMinimum(MY_MINIMUM);
    pbar.setMaximum(MY_MAXIMUM);
    // add to JPanel
    add(pbar);
    
    taskOutput = new JTextArea(5, 40);
    taskOutput.setMargin(new Insets(5,5,5,5));
    taskOutput.setEditable(false);
    
    add(new JScrollPane(taskOutput), BorderLayout.CENTER);

    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  }

  public void updateBar(int newValue, String message) {
    pbar.setValue(newValue);
    taskOutput.append(message);
  }
}