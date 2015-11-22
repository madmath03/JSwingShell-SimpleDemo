package jswingshell.demo.simple.action;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;

/**
 * Action to put the current thread to sleep.
 *
 * @author Mathieu Brunot
 */
public class SleepAction extends AbstractJssAction {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SleepAction.class.getName());

    /**
     * This action default identifier.
     * 
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "sleep";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String COMMAND_BRIEF_HELP = "Puts the current thread to sleep.";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    /**
     * Construct the static command help.
     *
     * @param action the action reference
     *
     * @return the static command help.
     */
    public static final String getHelp(SleepAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            stringBuilder.append("\n").append("The default time for sleep is 1000 milliseconds (1 second).");
            stringBuilder.append("\n\t").append(commandIdsAsString);
            stringBuilder.append("\n").append("You can define the time for the sleep (in milliseconds) as follow:");
            stringBuilder.append("\n\t").append(commandIdsAsString).append(" [time] ");

            commandHelp = stringBuilder.toString();
            commandHelpInitialized = true;
        }
        return commandHelp;
    }
    
    /**
     * Reset the static help to force reconstruction on next call.
     * 
     * @since 1.4
     */
    public static final void resetHelp() {
        commandHelpInitialized = false;
        commandHelp = null;
    }

    // #########################################################################
    public SleepAction(String name, Icon icon, IJssController shellController, String[] args) {
        super(name, icon, shellController, args);
    }

    public SleepAction(String name, IJssController shellController, String[] args) {
        super(name, shellController, args);
    }

    public SleepAction(IJssController shellController, String[] args) {
        super(shellController, args);
    }

    public SleepAction(IJssController shellController) {
        super(shellController);
    }

    public SleepAction() {
        super();
    }

    // #########################################################################
    @Override
    public String[] getCommandIdentifiers() {
        return IDENTIFIERS;
    }

    @Override
    public String getBriefHelp() {
        return COMMAND_BRIEF_HELP;
    }

    @Override
    public String getHelp(IJssController shellController) {
        return getHelp(this);
    }

    @Override
    public int run(IJssController shellController, String[] args) {
        int commandReturnStatus = AbstractJssAction.SUCCESS;

        // Sleep default value
        long sleepTime = 1000;
        // Extract time from parameters
        if (args != null && args.length > 1) {
            try {
                sleepTime = Long.valueOf(args[1]);
            } catch (NumberFormatException e) {
                shellController.publish(IJssController.PublicationLevel.WARNING, "Invalid number format: " + args[1] + " (" + e.getMessage() + ")");
                LOGGER.log(Level.WARNING, "Invalid number format: " + args[1], e);
            }
        }

        // Put the current thread to sleep
        final long millis = sleepTime;
        if (millis >= 0l) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                shellController.publish(IJssController.PublicationLevel.FATAL_ERROR, ex.getMessage());
                LOGGER.log(Level.SEVERE, null, ex);
                commandReturnStatus = AbstractJssAction.ERROR;
            }
        } else {
            shellController.publish(IJssController.PublicationLevel.ERROR, "Sleep time cannot be negative: " + millis);
            LOGGER.log(Level.SEVERE, "Sleep time cannot be negative: {0}", millis);
            commandReturnStatus = AbstractJssAction.ERROR;
        }

        return commandReturnStatus;
    }

}
