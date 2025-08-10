package com.achui.client;

import com.jcraft.jsch.Logger;
import org.slf4j.LoggerFactory;
 
public class JSchLogger implements com.jcraft.jsch.Logger {
	
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JSchLogger.class);
 
    @Override
    public boolean isEnabled(int level) {
        switch (level) {
            case Logger.DEBUG:
                return LOG.isDebugEnabled();
            case Logger.INFO:
                return LOG.isInfoEnabled();
            case Logger.WARN:
                return LOG.isWarnEnabled();
            case Logger.ERROR:
                return LOG.isErrorEnabled();
            default:
                return false;
        }
    }
 
    @Override
    public void log(int level, String message) {
        switch (level) {
            case Logger.DEBUG:
                LOG.debug(message);
                break;
            case Logger.INFO:
                LOG.info(message);
                break;
            case Logger.WARN:
                LOG.warn(message);
                break;
            case Logger.ERROR:
                LOG.error(message);
                break;
        }
    }
}
