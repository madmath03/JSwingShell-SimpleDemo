package jswingshell.demo.simple.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import jswingshell.action.IJssAction;

/**
 * Action to display available commands.
 *
 * @author Mathieu Brunot
 */
public class HelpAction extends AbstractJssAction {

    /**
     * This action default identifier.
     * 
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "help";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER, "man"};

    private static final String COMMAND_BRIEF_HELP = "Display available commands.";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    /**
     * Construct the action's command help.
     *
     * @param action the action reference
     *
     * @return the action's command help.
     */
    public static final String getHelp(HelpAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            stringBuilder.append("\n").append("For more information on a command, enter ").append(commandIdsAsString).append(" followed by the command:");
            stringBuilder.append("\n\t").append(commandIdsAsString).append(" [command] ");

            commandHelp = stringBuilder.toString();
            return commandHelp;
        } else {
            return null;
        }
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
    public HelpAction(String name, Icon icon, IJssController shellController, String[] args) {
        super(name, icon, shellController, args);
    }

    public HelpAction(String name, IJssController shellController, String[] args) {
        super(name, shellController, args);
    }

    public HelpAction(IJssController shellController, String[] args) {
        super(shellController, args);
    }

    public HelpAction(IJssController shellController) {
        super(shellController);
    }

    public HelpAction() {
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
    public String getHelp() {
        return getHelp(this);
    }

    @Override
    public String getHelp(IJssController shellController) {
        return getHelp(this);
    }

    @Override
    public int run(IJssController shellController, String[] args) {
        int commandReturnStatus = AbstractJssAction.SUCCESS;

        if (shellController == null) {
            commandReturnStatus = 1;
        } else {
            if (args == null || args.length <= 1) {
                StringBuilder stringBuilder = new StringBuilder();

                String commandIdsAsString = this.getCommandIdentifiersAsString();
                stringBuilder.append("For more information on a command, enter ").append(commandIdsAsString).append(" followed by the command.");
                stringBuilder.append("\n");
                stringBuilder.append("\n").append("Available commands:");
                Collection<IJssAction> shellAvailableActions = shellController.getAvailableActions();
                if (shellAvailableActions != null) {
                    List<IJssAction> availableActions = new ArrayList<>(shellAvailableActions);
                    Collections.sort(availableActions);
                    for (IJssAction availableAction : availableActions) {
                        if (availableAction != null) {
                            stringBuilder.append("\n\t").append(availableAction.getCommandIdentifiersAsString()).append(" ").append(availableAction.getBriefHelp());
                        }
                    }
                }

                shellController.publish(IJssController.PublicationLevel.SUCCESS, stringBuilder.toString());
            } else if (args.length == 2) {
                IJssAction action = shellController.getActionForCommandIdentifier(args[1]);
                if (action != null) {
                    shellController.publish(IJssController.PublicationLevel.SUCCESS, action.getHelp(shellController));
                } else {
                    shellController.publish(IJssController.PublicationLevel.WARNING, "Command not found: " + args[1]);
                }
            } else {
                shellController.publish(IJssController.PublicationLevel.WARNING, getHelp(shellController));
            }
        }

        return commandReturnStatus;
    }

}
