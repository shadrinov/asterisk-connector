package ru.ntechs.asteriskconnector.scripting;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

public class MethodExternalCallAttachRecord extends Method {

	public MethodExternalCallAttachRecord(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super(scriptFactory, eventChain, action, message);
	}

	@Override
	public void exec() {
		// TODO Auto-generated method stub
	}

	public boolean waitForRecord(String filename) {
		WatchService watcher = null;
		Path dir = null;
		Path file = null;

		try {
			watcher = FileSystems.getDefault().newWatchService();
			file = FileSystems.getDefault().getPath(filename);

			dir = file.getParent();
			file = file.getFileName();

			dir.register(watcher,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY);

			File testfile = new File(filename);
			if (testfile.exists())
				return true;

			// wait for key to be signaled
			WatchKey key = watcher.take();

			for (;;) {
			    for (WatchEvent<?> event: key.pollEvents()) {
			        WatchEvent.Kind<?> kind = event.kind();

			        // This key is registered only
			        // for ENTRY_CREATE events,
			        // but an OVERFLOW event can
			        // occur regardless if events
			        // are lost or discarded.
			        if (kind == OVERFLOW)
			            continue;

			        // The filename is the
			        // context of the event.
			        @SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
			        Path pFilename = ev.context();

			        // Resolve the filename against the directory.
					// If the filename is "test" and the directory is "foo",
					// the resolved name is "test/foo".
					Path child = dir.resolve(pFilename);
					if (child.compareTo(dir) == 0)
						return true;
			    }

			    // Reset the key -- this step is critical if you want to
			    // receive further watch events.  If the key is no longer valid,
			    // the directory is inaccessible so exit the loop.
			    if (!key.reset())
			        break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (watcher != null) {
				try {
					watcher.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return false;
	}
}
