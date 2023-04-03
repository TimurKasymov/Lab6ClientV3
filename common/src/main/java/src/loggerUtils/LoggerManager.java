package src.loggerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerManager {
    public static <T> Logger getLogger(T loggingClass){
        return LoggerFactory.getLogger(LoggerManager.class);
    }
}
