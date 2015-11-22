package jswingshell.demo.simple.action;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;

/**
 * Action to exit application.
 * 
 * @author Mathieu Brunot
 */
public class ExitAction extends AbstractJssAction {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ExitAction.class.getName());

    /**
     * This action default identifier.
     * 
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "exit";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER, "quit"};

    private static final String COMMAND_BRIEF_HELP = "Exits application.";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    /**
     * Construct the static command help.
     *
     * @param action the action reference
     *
     * @return the static command help.
     */
    public static final String getHelp(ExitAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            stringBuilder.append("\n").append("Exits the application with default return code (0):");
            stringBuilder.append("\n\t").append(commandIdsAsString);
            stringBuilder.append("\n").append("You can define exit status code as follow:");
            stringBuilder.append("\n\t").append(commandIdsAsString).append(" [integer] ");

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
    public ExitAction(String name, Icon icon, IJssController shellController, String[] args) {
        super(name, icon, shellController, args);
    }

    public ExitAction(String name, IJssController shellController, String[] args) {
        super(name, shellController, args);
    }

    public ExitAction(IJssController shellController, String[] args) {
        super(shellController, args);
    }

    public ExitAction(IJssController shellController) {
        super(shellController);
    }

    public ExitAction() {
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

        if (args == null || args.length <= 1) {
            System.exit(commandReturnStatus);
        } else if (args.length > 1) {
            try {
                commandReturnStatus = Integer.valueOf(args[1]);
                System.exit(commandReturnStatus);
            } catch (NumberFormatException e) {
                if (shellController != null) {
                    shellController.publish(IJssController.PublicationLevel.ERROR, "Invalid number format: " + args[1]);
                }
                LOGGER.log(Level.SEVERE, "Invalid number format: " + args[1], e);
                System.exit(commandReturnStatus);
            }
        }

        return commandReturnStatus;
    }

}
