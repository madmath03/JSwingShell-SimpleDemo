package jswingshell.demo.simple.action;

import javax.swing.Icon;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import jswingshell.gui.JssTextAreaController;

/**
 * Action to display a message in a shell.
 *
 * @author Mathieu Brunot
 */
public class EchoAction extends AbstractJssAction {

    /**
     * This action default identifier.
     * 
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "echo";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String COMMAND_BRIEF_HELP = "Displays a message in the shell.";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    /**
     * Construct the static command help.
     *
     * @param action the action reference
     *
     * @return the static command help.
     */
    public static final String getHelp(EchoAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            stringBuilder.append("\n").append("Displays everything after the command on a new line:");
            stringBuilder.append("\n\t").append(commandIdsAsString).append(" [message] ");

            String[] commandIdentifiers = action.getCommandIdentifiers();
            if (commandIdentifiers != null && commandIdentifiers.length > 0) {
                stringBuilder.append("\n");
                stringBuilder.append("\n").append("Example: ");
                stringBuilder.append("\n").append(commandIdentifiers[0]).append(" Hello world!");
                stringBuilder.append("\n").append("Hello world!");

                stringBuilder.append("\n");
                stringBuilder.append("\n").append("Example: ");
                stringBuilder.append("\n").append(commandIdentifiers[0]).append("             Hello world!");
                stringBuilder.append("\n").append("Hello world!");

                stringBuilder.append("\n");
                stringBuilder.append("\n").append("Example: ");
                stringBuilder.append("\n").append(commandIdentifiers[0]).append(" \"            Hello world!\"");
                stringBuilder.append("\n").append("            Hello world!");
            }

            stringBuilder.append("\n");
            stringBuilder.append("\n").append("If no message is provided, an empty line will be displayed.");

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
    public EchoAction(String name, Icon icon, IJssController shellController, String... args) {
        super(name, icon, shellController, args);
    }

    public EchoAction(String name, IJssController shellController, String... args) {
        super(name, shellController, args);
    }

    public EchoAction(IJssController shellController, String... args) {
        super(shellController, args);
    }

    public EchoAction(IJssController shellController) {
        super(shellController);
    }

    public EchoAction() {
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
    public int run(IJssController shellController, String... args) {
        int commandReturnStatus = AbstractJssAction.SUCCESS;

        if (shellController == null) {
            commandReturnStatus = AbstractJssAction.ERROR;
        } else {
            if (args != null && args.length > 1) {
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 1, n = args.length; i < n; i++) {
                    stringBuilder.append(args[i]).append(JssTextAreaController.COMMAND_PARAMETER_SEPARATOR);
                }

                shellController.publish(IJssController.PublicationLevel.SUCCESS, stringBuilder.toString());
            }
        }

        return commandReturnStatus;
    }

}
