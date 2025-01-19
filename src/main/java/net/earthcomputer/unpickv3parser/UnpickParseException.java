package net.earthcomputer.unpickv3parser;

import java.io.IOException;

public class UnpickParseException extends IOException {
    public final int line;
    public final int column;

    public UnpickParseException(String message, int line, int column) {
        super(line + ":" + column + ": " + message);
        this.line = line;
        this.column = column;
    }
}
