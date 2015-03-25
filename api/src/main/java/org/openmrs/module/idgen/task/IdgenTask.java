package org.openmrs.module.idgen.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;

import java.util.TimerTask;

/**
 * Generic superclass for an Idgen task
 */
public abstract class IdgenTask extends TimerTask {
	
	private final Log log = LogFactory.getLog(getClass());
	private static DaemonToken daemonToken;
	private static boolean enabled = false;

	//***** PROPERTIES THAT NEED TO BE SET ON EACH INSTANCE

	private Class<? extends IdgenTask> taskClass;

	/**
	 * @see TimerTask#run()
	 */
	@Override
	public final void run() {
		if (daemonToken != null && enabled) {
			createAndRunTask();
		}
		else {
			log.warn("Not running scheduled task. DaemonToken = " + daemonToken + "; enabled = " + enabled);
		}
	}

	/**
	 * Construct a new instance of the configured task and execute it
	 */
	public synchronized void createAndRunTask() {
		try {
			log.info("Running idgen task: " + getClass().getSimpleName());
			Daemon.runInDaemonThread(getRunnableTask(), daemonToken);
		}
		catch (Exception e) {
			log.error("An error occurred while running scheduled idgen task", e);
		}
	}

	public abstract Runnable getRunnableTask();

	/**
	 * Sets the daemon token
	 */
	public static void setDaemonToken(DaemonToken token) {
		daemonToken = token;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		IdgenTask.enabled = enabled;
	}
}
