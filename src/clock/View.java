package clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
    
    ClockPanel panel;
    
    public View(Model model) {
        JFrame frame = new JFrame();
        panel = new ClockPanel(model);
        //frame.setContentPane(panel);
        frame.setTitle("Java Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Start of border layout code
        
        // I've just put a single button in each of the border positions:
        // PAGE_START (i.e. top), PAGE_END (bottom), LINE_START (left) and
        // LINE_END (right). You can omit any of these, or replace the button
        // with something else like a label or a menu bar. Or maybe you can
        // figure out how to pack more than one thing into one of those
        // positions. This is the very simplest border layout possible, just
        // to help you get started.
        
        Container pane = frame.getContentPane();
         
        panel.setPreferredSize(new Dimension(200, 200));
        pane.add(panel, BorderLayout.CENTER);

        // iCalendar file chooser.
        JDialog chooserWindow = new JDialog();
        chooserWindow.setSize(new Dimension(200, 200));
        chooserWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JFileChooser chooser = new JFileChooser(new File("C:\\Users\\finn\\OneDrive\\Code\\Java Projects\\Clock\\data"));
        chooser.setSize(new Dimension(300, 400));

        int result = chooser.showSaveDialog(chooserWindow);

        chooserWindow.add(chooser);

        chooserWindow.setVisible(true);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            Controller.loadAlarms(file);

            chooserWindow.setVisible(false);
        }
        
        // End of borderlayout code
        
        frame.pack();
        frame.setVisible(true);
    }
    
    public void update(Observable o, Object arg) {
        panel.repaint();
    }
}
