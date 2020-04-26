package me.tom.sparse.spigot.chat.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class LogFilter implements Filter {

    public LogFilter() {
        ((Logger) LogManager.getRootLogger()).addFilter(this);
    }

    private Result filter(String message) {
        if (message.contains("/cmapi")) return Result.DENY;
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(LogEvent event) {
        return filter(event.getMessage().getFormattedMessage());
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
        return filter(message.toString());
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
        return filter(message.getFormattedMessage());
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6,
                         Object arg7) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6,
                         Object arg7, Object arg8) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6,
                         Object arg7, Object arg8, Object arg9) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6,
                         Object arg7, Object arg8, Object arg9, Object arg10) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6,
                         Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6,
                         Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return filter(message);
    }

    @Override
    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6,
                         Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13) {
        return filter(message);
    }

    @Override
    public State getState() {return State.STARTED;}

    @Override
    public void initialize() {}

    @Override
    public boolean isStarted() {return true;}

    @Override
    public boolean isStopped() {return false;}

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }
}