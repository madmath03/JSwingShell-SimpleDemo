package jswingshell.demo.simple;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import jswingshell.demo.simple.action.ClearAction;
import jswingshell.demo.simple.action.EchoAction;
import jswingshell.demo.simple.action.ExitAction;
import jswingshell.demo.simple.action.HelpAction;
import jswingshell.demo.simple.action.LoadCommandFile;
import jswingshell.demo.simple.action.SleepAction;
import jswingshell.demo.simple.action.TimeAction;
import jswingshell.demo.simple.action.WaitAction;
import jswingshell.gui.JssTextAreaController;

/**
 *
 * @author brunot
 */
public class JssSimpleDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.LookAndFeelInfo[] installedLookAndFeels = javax.swing.UIManager.getInstalledLookAndFeels();
            for (UIManager.LookAndFeelInfo installedLookAndFeel : installedLookAndFeels) {
                if ("Nimbus".equals(installedLookAndFeel.getName())) {
                    javax.swing.UIManager.setLookAndFeel(installedLookAndFeel.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JssSimpleDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        // #####################################################################
        // Let's create our first shell
        JssTextAreaController shellController = new JssTextAreaController("Welcome to the Simple JSwingShell demonstration!!\n");

        // Add some actions to it...
        shellController.getModel().add(new LoadCommandFile());
        shellController.getModel().add(new ExitAction());
        shellController.getModel().add(new ClearAction());
        
        shellController.getModel().add(new HelpAction());
        
        shellController.getModel().add(new EchoAction());
        shellController.getModel().add(new TimeAction());
        shellController.getModel().add(new SleepAction());
        shellController.getModel().add(new WaitAction());

        // and now put its view to a JFrame
        final JFrame shellFrame = new JFrame("My application shell added to a JFrame");

        JScrollPane jShellScrollPane = new javax.swing.JScrollPane();

        shellFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        shellFrame.setPreferredSize(new java.awt.Dimension(320, 240));

        if (shellController.getView() instanceof java.awt.Component) {
            jShellScrollPane.setViewportView((java.awt.Component) shellController.getView());
        }

        shellFrame.getContentPane().add(jShellScrollPane, java.awt.BorderLayout.CENTER);

        // #####################################################################
        // Create and display the form
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                shellFrame.setMinimumSize(new Dimension(640, 480));
                shellFrame.setVisible(true);
            }
        });

    }

}
