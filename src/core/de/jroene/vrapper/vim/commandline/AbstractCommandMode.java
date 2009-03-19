package de.jroene.vrapper.vim.commandline;

import java.util.Arrays;
import java.util.List;

import de.jroene.vrapper.vim.AbstractMode;
import de.jroene.vrapper.vim.VimEmulator;
import de.jroene.vrapper.vim.VimInputEvent;

public abstract class AbstractCommandMode extends AbstractMode {

    private static final List<VimInputEvent> abortEvents = Arrays.asList(
            VimInputEvent.ESCAPE, VimInputEvent.RETURN);
    protected final StringBuffer buffer;

    public AbstractCommandMode(VimEmulator vim) {
        super(vim);
        buffer = new StringBuffer();
    }

    /**
     * Appends typed characters to the internal buffer. Deletes a char from the
     * buffer on press of the backspace key. Parses and executes the buffer on
     * press of the return key. Clears the buffer on press of the escape key.
     */
    public boolean type(VimInputEvent e) {
        if (e instanceof VimInputEvent.Character) {
            buffer.append(((VimInputEvent.Character) e).getCharacter());
        } else if (e.equals(VimInputEvent.BACKSPACE)) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        if (buffer.length() == 0 || abortEvents.contains(e)) {
            if (VimInputEvent.RETURN.equals(e)) {
                parseAndExecute();
            }
            vim.toNormalMode();
            buffer.setLength(0);
            vim.getPlatform().endChange();
        }
        vim.getPlatform().setCommandLine(buffer.toString());
        return false;
    }

    /**
     * Parses and executes the given command if possible.
     * @param first TODO
     * @param command
     *            the command to execute.
     */
    public abstract void parseAndExecute(String first, String command);


    public void toKeystrokeMode() {
        // do nothing
    }

    private void parseAndExecute() {
        parseAndExecute(buffer.substring(0,1), buffer.substring(1, buffer.length()));
    }

}