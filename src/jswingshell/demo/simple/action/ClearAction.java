package jswingshell.demo.simple.action;

import javax.swing.Icon;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;

/**
 * Action to clear all messages in a shell.
 * 
 * @author Mathieu Brunot
 */
public class ClearAction extends AbstractJssAction {

    /**
     * This action default identifier.
     * 
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "clear";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String COMMAND_BRIEF_HELP = "Clears all messages in the shell.";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    /**
     * Construct the static command help.
     *
     * @param action the action reference
     *
     * @return the static command help.
     */
    public static final String getHelp(ClearAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            stringBuilder.append("\n\t").append(commandIdsAsString);

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
    public ClearAction(String name, Icon icon, IJssController shellController, String... args) {
        super(name, icon, shellController, args);
    }

    public ClearAction(String name, IJssController shellController, String... args) {
        super(name, shellController, args);
    }

    public ClearAction(IJssController shellController, String... args) {
        super(shellController, args);
    }

    public ClearAction(IJssController shellController) {
        super(shellController);
    }

    public ClearAction() {
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
            shellController.clearShell();
        }

        return commandReturnStatus;
    }

}
