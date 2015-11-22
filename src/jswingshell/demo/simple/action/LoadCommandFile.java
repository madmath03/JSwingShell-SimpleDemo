package jswingshell.demo.simple.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import jswingshell.AbstractJssController;
import jswingshell.IJssController;
import jswingshell.action.AbstractThreadedJssAction;
import jswingshell.action.AbstractThreadedJssAction.AbstractJssActionWorker;
import jswingshell.action.AbstractThreadedJssAction.JssActionWorkerChunk;

/**
 * Action to load and execute a shell file.
 *
 * @author Mathieu Brunot
 */
public class LoadCommandFile extends AbstractThreadedJssAction {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LoadCommandFile.class.getName());

    /**
     * Prefix for start of lines to indicate that a command should not display
     * itself before execution.
     */
    private static final String MUTE_PREFIX = "@";

    /**
     * Prefix for start of lines to indicate a comment.
     */
    private static final String COMMENT_PREFIX = "//";

    /**
     * This action default identifier.
     * 
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "loadCommandFile";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String COMMAND_BRIEF_HELP = "Load and execute a shell file.";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    /**
     * Construct the static command help.
     *
     * @param action the action reference
     *
     * @return the static command help.
     */
    public static final String getHelp(LoadCommandFile action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            stringBuilder.append("\n").append("Load and executes the file at the given path:");
            stringBuilder.append("\n\t").append(commandIdsAsString).append(" [file_path] ");

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
    public LoadCommandFile(String name, Icon icon, IJssController shellController, String[] args) {
        super(name, icon, shellController, args);
    }

    public LoadCommandFile(String name, IJssController shellController, String[] args) {
        super(name, shellController, args);
    }

    public LoadCommandFile(IJssController shellController, String[] args) {
        super(shellController, args);
    }

    public LoadCommandFile(IJssController shellController) {
        super(shellController);
    }

    public LoadCommandFile() {
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
    protected AbstractJssActionWorker prepareWorker(IJssController shellController, String[] args) {
        LoadCommandFileWorker worker = null;

        if (shellController != null) {
            // Extract file path from parameters
            if (args != null && args.length > 1) {
                if (args.length == 2) {
                    String filePath = args[1];
                    worker = new LoadCommandFileWorker(shellController, filePath);
                } else {
                    shellController.publish(IJssController.PublicationLevel.WARNING, getHelp(shellController));
                }
            } else {
                shellController.publish(IJssController.PublicationLevel.ERROR, "File path is mandatory!");
                LOGGER.log(Level.SEVERE, "File path is mandatory!");
            }
        }

        return worker;
    }

    // #########################################################################
    protected class LoadCommandFileWorker extends AbstractJssActionWorker {

        final String filePath;

        public LoadCommandFileWorker(IJssController shellController, String filePath) {
            super(shellController);
            this.filePath = filePath;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            int workerCommandReturnStatus = AbstractThreadedJssAction.SUCCESS;

            // Start reading file
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(filePath);
                br = new BufferedReader(fr);

                int lineCount = 0;
                for (String line; (line = br.readLine()) != null;) {
                    lineCount++;
                    String command = line != null ? line.trim() : "";

                    if (command != null && !command.isEmpty()) {
                        // Add line to shell
                        if (command.startsWith(MUTE_PREFIX)) {
                            command = command.substring(MUTE_PREFIX.length());
                        } else if (command.startsWith(COMMENT_PREFIX)) {
                            continue;
                        } else {
                            getShellController().addNewLineToShell(command);
                        }

                        if (getShellController() instanceof AbstractJssController) {
                            // Execute command without adding it to history
                            workerCommandReturnStatus |= ((AbstractJssController) getShellController()).interpretCommand(command, false);
                        } else {
                            workerCommandReturnStatus |= getShellController().interpretCommand(command);
                        }
                    }

                    // Stop reading the file if an error occurs
                    if (AbstractThreadedJssAction.SUCCESS != workerCommandReturnStatus) {
                        this.publish(new JssActionWorkerChunk(IJssController.PublicationLevel.WARNING, "Stop reading file due to internal command file error: command \"" + command + "\" at line " + lineCount + " returned status code " + workerCommandReturnStatus));
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                this.publish(new JssActionWorkerChunk(IJssController.PublicationLevel.ERROR, "File not found: " + filePath));
                LOGGER.log(Level.SEVERE, "File not found: " + filePath, e);
                workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
            } catch (IOException e) {
                this.publish(new JssActionWorkerChunk(IJssController.PublicationLevel.FATAL_ERROR, "Error occured while reading file: " + e.getMessage()));
                LOGGER.log(Level.SEVERE, "Error occured while reading file.", e);
                workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ex) {
                        this.publish(new JssActionWorkerChunk(IJssController.PublicationLevel.WARNING, "Error occured while closing buffered file reader: " + ex.getMessage()));
                        LOGGER.log(Level.SEVERE, "Error occured while closing buffered file reader.", ex);
                        workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
                    }
                }
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException ex) {
                        this.publish(new JssActionWorkerChunk(IJssController.PublicationLevel.WARNING, "Error occured while closing file reader: " + ex.getMessage()));
                        LOGGER.log(Level.SEVERE, "Error occured while closing file reader.", ex);
                        workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
                    }
                }
            }

            return workerCommandReturnStatus;
        }

    }
}
