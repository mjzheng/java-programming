import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple custom lock that allows simultaneously read operations, but
 * disallows simultaneously write and read/write operations.
 * 
 * You do not need to implement any form or priority to read or write
 * operations. The first thread that acquires the appropriate lock should be
 * allowed to continue.
 * 
 * @author Musica Zheng
 * @author University of San Francisco
 */
public class MultiReaderLock {

	private static final Logger logger = LogManager
			.getLogger(MultiReaderLock.class.getName());

	private int reader;
	private boolean writer;

	/**
	 * Initializes a multi-reader (single-writer) lock.
	 */
	public MultiReaderLock() {
		reader = 0;
		writer = false;
	}

	/**
	 * Will wait until there are no active writers in the system, and then will
	 * increase the number of active readers.
	 */
	public synchronized void lockRead() {
		while (writer != false) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				logger.debug("lockRead was interrupted", e);
			}
		}
		reader++;
	}

	/**
	 * Will decrease the number of active readers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockRead() {
		reader--;
		if (reader <= 0) {
			this.notifyAll();
		}
	}

	/**
	 * Will wait until there are no active readers or writers in the system, and
	 * then will increase the number of active writers.
	 */
	public synchronized void lockWrite() {
		while ((reader > 0) || (writer != false)) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				logger.debug("lockWrite was interrupted", e);
			}
		}
		writer = true;
	}

	/**
	 * Will decrease the number of active writers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockWrite() {
		writer = false;
		this.notifyAll();
	}
}