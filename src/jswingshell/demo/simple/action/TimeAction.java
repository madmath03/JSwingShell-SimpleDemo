package jswingshell.demo.simple.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;

/**
 * Action to display the current time and date in the shell.
 *
 * @author Mathieu Brunot
 */
public class TimeAction extends AbstractJssAction {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TimeAction.class.getName());

    /**
     * This action default identifier.
     * 
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "time";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String COMMAND_BRIEF_HELP = "Displays the current time and date in the shell.";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    /**
     * Construct the static command help.
     *
     * @param action the action reference
     *
     * @return the static command help.
     */
    public static final String getHelp(TimeAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            stringBuilder.append("\n").append("Returns the current time and date:");
            stringBuilder.append("\n\t").append(commandIdsAsString);
            stringBuilder.append("\n").append("The date/time format can be specified as well as the language:");
            stringBuilder.append("\n\t").append(commandIdsAsString).append(" [format] [language] [country] [variant] ");

            String[] commandIdentifiers = action.getCommandIdentifiers();
            if (commandIdentifiers != null && commandIdentifiers.length > 0) {
                stringBuilder.append("\n");
                stringBuilder.append("\n").append("Example: ");
                stringBuilder.append("\n").append(commandIdentifiers[0]).append(" yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                stringBuilder.append("\n").append("2001-07-04T12:08:56.235-0700");

                stringBuilder.append("\n");
                stringBuilder.append("\n").append("Example: ");
                stringBuilder.append("\n").append(commandIdentifiers[0]).append(" \"EEE, MMM d, ''yy\" en us");
                stringBuilder.append("\n").append("Wed, Jul 4, '01");

                stringBuilder.append("\n");
                stringBuilder.append("\n").append("Example: ");
                stringBuilder.append("\n").append(commandIdentifiers[0]).append(" \"EEEE dd MMMM yyyy\" th th th");
                stringBuilder.append("\n").append("วันพุธ 04 กรกฎาคม 2001");

                stringBuilder.append("\n");
                stringBuilder.append("\n").append("Example: ");
                stringBuilder.append("\n").append(commandIdentifiers[0]).append(" \"EEEE dd MMMM yyyy\" th TH TH");
                stringBuilder.append("\n").append("วันพุธ ๐๘ กรกฎาคม ๒๕๔๔");
            }

            stringBuilder.append("\n");
            stringBuilder.append("\n").append("If no format and language are provided, the default date format and locale will be used.");

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
    public TimeAction(String name, Icon icon, IJssController shellController, String[] args) {
        super(name, icon, shellController, args);
    }

    public TimeAction(String name, IJssController shellController, String[] args) {
        super(name, shellController, args);
    }

    public TimeAction(IJssController shellController, String[] args) {
        super(shellController, args);
    }

    public TimeAction(IJssController shellController) {
        super(shellController);
    }

    public TimeAction() {
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

        if (shellController == null) {
            commandReturnStatus = 1;
        } else {
            Date currentDate = new Date();

            DateFormat dateFormat;
            if (args != null && args.length > 1) {
                switch (args.length) {
                    // If format and locale are provided
                    case 5:
                        try {
                            Locale displayLocale = new Locale(args[2], args[3], args[4]);
                            dateFormat = new SimpleDateFormat(args[1], displayLocale);
                        } catch (IllegalArgumentException e) {
                            shellController.publish(IJssController.PublicationLevel.SUCCESS, "Error occurred while parsing date/time format and language: " + e.getMessage());
                            LOGGER.log(Level.SEVERE, "Error occurred while parsing format {1} for language {2}, country {3} and variant {4}: " + e.getMessage(), args);
                            dateFormat = null;
                        }
                        break;
                    case 4:
                        try {
                            Locale displayLocale = new Locale(args[2], args[3]);
                            dateFormat = new SimpleDateFormat(args[1], displayLocale);
                        } catch (IllegalArgumentException e) {
                            shellController.publish(IJssController.PublicationLevel.SUCCESS, "Error occurred while parsing date/time format and language: " + e.getMessage());
                            LOGGER.log(Level.SEVERE, "Error occurred while parsing format {1} for language {2} and country {3}: " + e.getMessage(), args);
                            dateFormat = null;
                        }
                        break;
                    case 3:
                        try {
                            Locale displayLocale = new Locale(args[2]);
                            dateFormat = new SimpleDateFormat(args[1], displayLocale);
                        } catch (IllegalArgumentException e) {
                            shellController.publish(IJssController.PublicationLevel.SUCCESS, "Error occurred while parsing date/time format and language: " + e.getMessage());
                            LOGGER.log(Level.SEVERE, "Error occurred while parsing format {1} for language {2}: " + e.getMessage(), args);
                            dateFormat = null;
                        }
                        break;
                    // Assume only format was provided by default
                    case 2:
                    default:
                        try {
                            dateFormat = new SimpleDateFormat(args[1]);
                        } catch (IllegalArgumentException e) {
                            shellController.publish(IJssController.PublicationLevel.SUCCESS, "Error occurred while parsing format: " + e.getMessage());
                            LOGGER.log(Level.SEVERE, "Error occurred while parsing date/time format {1}: " + e.getMessage(), args);
                            dateFormat = null;
                        }
                        break;
                }

            } else {
                dateFormat = SimpleDateFormat.getInstance();
            }

            // If date format was properly defined
            if (dateFormat != null) {
                shellController.publish(IJssController.PublicationLevel.SUCCESS, dateFormat.format(currentDate));
            } else {
                commandReturnStatus = AbstractJssAction.ERROR;
            }
        }

        return commandReturnStatus;
    }

}
