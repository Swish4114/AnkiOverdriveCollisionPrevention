package de.project.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

//stolen from: https://stackoverflow.com/a/197361

public final class LogFormatter extends Formatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        sb.append(new SimpleDateFormat(DATE_FORMAT).format(new Date(record.getMillis())))
                .append(" ")
                .append(Thread.currentThread().getName())
                .append(" ")
                .append(record.getSourceMethodName())
                .append(" ")
                .append(record.getLevel().getLocalizedName())
                .append(": ")
                .append(formatMessage(record))
                .append(LINE_SEPARATOR);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore
            }
        }

        return sb.toString();
    }
}