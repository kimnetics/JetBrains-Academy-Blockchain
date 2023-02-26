package blockchain.Utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Logger {
    private final java.util.logging.Logger julLogger;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public Logger(String className, String logFilePattern) {
        this.julLogger = java.util.logging.Logger.getLogger(className);
        this.julLogger.setUseParentHandlers(false);
        Handler fileHandler;
        try {
            fileHandler = new FileHandler(logFilePattern);
            fileHandler.setFormatter(new MyFormatter());
        } catch (IOException | SecurityException e) {
            throw new RuntimeException("Unexpected error opening logging file. " + e.getMessage());
        }
        this.julLogger.addHandler(fileHandler);
    }

    public void console(String message) {
        System.out.println(message);
        info(message);
    }

    public void info(String message) {
        julLogger.info(message);
    }

    private class MyFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("%s %s [%s] - %s\n",
                    dateFormat.format(new Date(record.getMillis())),
                    record.getLevel(),
                    record.getLongThreadID(),
                    record.getMessage()
            );
        }
    }
}
