package jswingshell.demo.simple.action;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import jswingshell.IJssController;
import jswingshell.action.AbstractThreadedJssAction;
import jswingshell.action.AbstractThreadedJssAction.AbstractJssActionWorker;
import jswingshell.action.AbstractThreadedJssAction.JssActionWorkerChunk;

/**
 * Action to put the current shell to sleep.
 *
 * @author Mathieu Brunot
 */
public class WaitAction extends AbstractThreadedJssAction {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(WaitAction.class.getName());

    /**
     * This action default identifier.
     *
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "wait";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String COMMAND_BRIEF_HELP = "Wait for a given time in a separate thread.";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    /**
     * Construct the static command help.
     *
     * @param action the action reference
     *
     * @return the static command help.
     */
    public static final String getHelp(WaitAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            stringBuilder.append("\n").append("The default time for waiting is 1000 milliseconds (1 second).");
            stringBuilder.append("\n\t").append(commandIdsAsString);
            stringBuilder.append("\n").append("You can define the time for the wait (in milliseconds) as follow:");
            stringBuilder.append("\n\t").append(commandIdsAsString).append(" [time] ");
            stringBuilder.append("\n").append("This action is suited for manual use since it will wait a given time without blocking the current thread.");
            stringBuilder.append("\n").append("This action should not be called outside of the EDT.");

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
    public WaitAction(String name, Icon icon, IJssController shellController, String... args) {
        super(name, icon, shellController, args);
    }

    public WaitAction(String name, IJssController shellController, String... args) {
        super(name, shellController, args);
    }

    public WaitAction(IJssController shellController, String... args) {
        super(shellController, args);
    }

    public WaitAction(IJssController shellController) {
        super(shellController);
    }

    public WaitAction() {
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
    protected AbstractJssActionWorker prepareWorker(IJssController shellController, String... args) {
        SleepWorker worker = null;

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

        final long millis = sleepTime;
        if (millis >= 0l) {
            worker = new SleepWorker(shellController, millis);
        } else {
            shellController.publish(IJssController.PublicationLevel.ERROR, "Wait time cannot be negative: " + millis);
            LOGGER.log(Level.SEVERE, "Wait time cannot be negative: {0}", millis);
        }

        return worker;
    }

    // #########################################################################
    protected class SleepWorker extends AbstractJssActionWorker {

        final long millis;

        public SleepWorker(IJssController shellController, long millis) {
            super(shellController);
            this.millis = millis;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            int workerCommandReturnStatus = AbstractThreadedJssAction.SUCCESS;

            getShellController().lockCommandLine();
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                this.publish(WaitAction.this.new JssActionWorkerChunk(IJssController.PublicationLevel.WARNING, ex.getMessage()));
                LOGGER.log(Level.WARNING, "Wait action interrupted.", ex);
                workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
            }

            return workerCommandReturnStatus;
        }

    }

}
