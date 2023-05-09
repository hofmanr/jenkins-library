package nl.rhofman.jenkins.utils.logging

import nl.rhofman.jenkins.utils.Config
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException

/**
 * Logging functionality for pipeline scripts.
 */
class Logger implements Serializable {

    private static final long serialVersionUID = 1L

    /**
     * Reference to the CpsScript/WorkflowScript
     */
    static Script script

    /**
     * The log level
     */
    static LogLevel level = LogLevel.TRACE

    /**
     * The name of the logger
     */
    public String name = ""

    /**
     * Flag if the logger is initialized
     */
    public static Boolean initialized = false

    /**
     * @param name The name of the logger
     */
    Logger(String name = "") {
        this.name = name
    }

    /**
     * @param logScope The object the logger is for. The name of the logger is autodetected.
     */
    Logger(Object logScope) {
        if (logScope instanceof Object) {
            this.name = getClassName(logScope)
            if (this.name == null) {
                this.name = "$logScope"
            }
        }
    }

    /**
     * Initializes the logger with DSL/steps object and LogLevel
     *
     * @param logLvl The log level to use during execution of the pipeline script
     * @deprecated still used by tests
     */
    static void init(LogLevel logLvl = LogLevel.INFO) {
        if (logLvl == null) logLvl = LogLevel.INFO
        level = logLvl
        if (Logger.initialized == true) {
            return
        }
        initialized = true
        Logger tmpLogger = new Logger('Logger')
        tmpLogger.deprecated('Logger.init(DSL dsl, logLevel)','Logger.init(Script script, logLevel)')
    }

    /**
     * Initializes the logger with CpsScript object and LogLevel
     *
     * @param script CpsScript object of the current pipeline script (available via this in pipeline scripts)
     * @param map The configuration object of the pipeline
     */
    static void init(Script script, LogLevel logLvl = LogLevel.INFO) {
        if (logLvl == null) logLvl = LogLevel.INFO
        level = logLvl
        if (Logger.initialized == true) {
            return
        }
        this.script = script
        initialized = true
    }

    /**
     * Initializes the logger with CpsScript object and configuration map
     *
     * @param script CpsScript object of the current pipeline script (available via this in pipeline scripts)
     * @param map The configuration object of the pipeline
     */
    static void init(Script script, Map map) {
        LogLevel lvl
        if (map) {
            lvl = map[Config.LOGLEVEL] ?: LogLevel.INFO
        } else {
            lvl = LogLevel.INFO
        }
        init(script, lvl)
    }

    /**
     * Initializes the logger with CpsScript object and loglevel as string
     *
     * @param script CpsScript object of the current pipeline script (available via this in pipeline scripts)
     * @param sLevel the log level as string
     */
    static void init(Script script, String sLevel) {
        if (sLevel == null) sLevel = LogLevel.INFO
        init(script, LogLevel.fromString(sLevel))
    }

    /**
     * Initializes the logger with DSL/steps object and loglevel as integer
     *
     * @param script CpsScript object of the current pipeline script (available via this in pipeline scripts)
     * @param iLevel the log level as integer
     *
     */
    static void init(Script script, Integer iLevel) {
        if (iLevel == null) iLevel = LogLevel.INFO.getLevel()
        init(script, LogLevel.fromInteger(iLevel))
    }

    /**
     * Logs a trace message followed by object dump
     *
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void trace(String message, Object object) {
        log(LogLevel.TRACE, message, object)
    }

    /**
     * Logs a info message followed by object dump
     *
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void info(String message, Object object) {
        log(LogLevel.INFO, message, object)
    }

    /**
     * Logs a debug message followed by object dump
     *
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void debug(String message, Object object) {
        log(LogLevel.DEBUG, message, object)
    }

    /**
     * Logs warn message followed by object dump
     *
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void warn(String message, Object object) {
        log(LogLevel.WARN, message, object)
    }

    /**
     * Logs a error message followed by object dump
     *
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void error(String message, Object object) {
        log(LogLevel.ERROR, message, object)
    }

    /**
     * Logs a fatal message followed by object dump
     *
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void fatal(String message, Object object) {
        log(LogLevel.FATAL, message, object)
    }

    /**
     * Logs a trace message
     *
     * @param message The message to be logged
     */
    void trace(String message) {
        log(LogLevel.TRACE, message)
    }

    /**
     * Logs a trace message
     *
     * @param message The message to be logged
     */
    void info(String message) {
        log(LogLevel.INFO, message)
    }

    /**
     * Logs a debug message
     *
     * @param message The message to be logged
     */
    void debug(String message) {
        log(LogLevel.DEBUG, message)
    }

    /**
     * Logs a warn message
     *
     * @param message The message to be logged
     */
    void warn(String message) {
        log(LogLevel.WARN, message)
    }

    /**
     * Logs a error message
     *
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void error(String message) {
        log(LogLevel.ERROR, message)
    }

    /**
     * Logs a deprecation message with deprecated and replacement
     *
     * @param deprecatedItem The item that is depcrecated
     * @param newItem The replacement (if exist)
     */
    void deprecated(String deprecatedItem, String newItem) {
        String message = "The step/function/class '$deprecatedItem' is marked as deprecated and will be removed in future releases. " +
                "Please use '$newItem' instead."
        deprecated(message)
    }

    /**
     * Logs a fatal message
     *
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void fatal(String message) {
        log(LogLevel.FATAL, message)
    }

    /**
     * Helper function for logging/dumping a object at the given log level
     *
     * @param logLevel the loglevel to be used
     * @param message The message to be logged
     * @param object The object to be dumped
     */
    void log(LogLevel logLevel, String message, Object object) {
        if (doLog(logLevel)) {
            def objectName = getClassName(object)
            if (objectName != null) {
                objectName = "($objectName) "
            } else {
                objectName = ""
            }

            def objectString = object.toString()
            String msg = "$name : $message -> $objectName$objectString"
            writeLogMsg(logLevel, msg)
        }
    }

    /**
     * Helper function for logging at the given log level
     *
     * @param logLevel the loglevel to be used
     * @param message The message to be logged
     */
    void log(LogLevel logLevel, String message) {
        if (doLog(logLevel)) {
            String msg = "$name : $message"
            writeLogMsg(logLevel, msg)
        }
    }

    /**
     * Utility function for writing to the jenkins console
     *
     * @param logLevel the loglevel to be used
     * @param msg The message to be logged
     */
    private static void writeLogMsg(LogLevel logLevel, String msg) {
        String lvlString = "[${logLevel.toString()}]"

        lvlString = wrapColor(logLevel.getColorCode(), lvlString)

        println "$lvlString $msg"
    }

    /**
     * Wraps a string with color codes when terminal is available
     * @param logLevel
     * @param str
     * @return
     */
    private static String wrapColor(String colorCode, String str) {
        String ret = str
        if (hasTermEnv()) {
            ret = "\u001B[${colorCode}m${str}\u001B[0m"
        }
        return ret
    }

    /**
     * Helper function to detect if a term environment is available
     * @return
     */
    private static Boolean hasTermEnv() {
        String termEnv = null
        if (script != null) {
            try {
                termEnv = script.env.TERM
            } catch (Exception ex) {

            }
        }
        return termEnv != null
    }

    /**
     * Utiltiy function to determine if the given logLevel is active
     *
     * @param logLevel
     * @return true , when the loglevel should be displayed, false when the loglevel is disabled
     */
    private static boolean doLog(LogLevel logLevel) {
        if (logLevel.getLevel() >= level.getLevel()) {
            return true
        }
        return false
    }

    /**
     * Helper function to get the name of the object
     * @param object
     * @return
     */
    private static String getClassName(Object object) {
        String objectName = null
        // try to retrieve as much information as possible about the class
        try {
            Class objectClass = object.getClass()
            objectName = objectClass.getName().toString()
            objectName = objectClass.getCanonicalName().toString()
        } catch (RejectedAccessException e) {
            // do nothing
        }

        return objectName
    }
}
