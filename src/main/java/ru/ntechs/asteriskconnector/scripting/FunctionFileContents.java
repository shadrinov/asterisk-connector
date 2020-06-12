package ru.ntechs.asteriskconnector.scripting;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64InputStream;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;

@Slf4j
public class FunctionFileContents extends Function {
	public static final String NAME    = "FileContents";
	public static final String LC_NAME = "filecontents";
	public static final long RECORD_FILE_TIMEOUT = 30000l;

	private String filename;
	private long waitTimestamp;

	public FunctionFileContents(ScriptFactory scriptFactory, ArrayList<Scalar> params) throws BitrixLocalException {
		super(scriptFactory, params);
		init(params);
	}

	public FunctionFileContents(ScriptFactory scriptFactory, Message message, ArrayList<Scalar> params) throws BitrixLocalException {
		super(scriptFactory, message, params);
		init(params);
	}

	private void init(ArrayList<Scalar> params) throws BitrixLocalException {
		if (params.size() != 1)
			throw new BitrixLocalException(String.format("%s doesn't match prototype %s(Filename)",
					toString(), NAME));

		this.filename = params.get(0).asString();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Scalar eval() throws IOException, BitrixLocalException {
		File file = new File(filename);

		if (!waitForRecord(file))
			throw new BitrixLocalException(String.format("file is unavailable: %s", filename));

		ScalarStringSplitted result = new ScalarStringSplitted("<file>");
		Base64InputStream test = null;
		FileInputStream fio = new FileInputStream(file);

		try {
			int b;
			test = new Base64InputStream(fio, true);

			while ((b = test.read()) != -1)
				result.append((char)b);
		}
		finally {
			if (test != null)
				test.close();
		}

		return result;
	}

	@Override
	public ArrayList<? extends Object> getIntermediateBeans() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean waitForRecord(File file) {
		WatchService watcher = null;
		boolean result = false;

		if (file.exists())
			return true;

		try {
			waitTimestamp = System.currentTimeMillis();
			watcher = FileSystems.getDefault().newWatchService();
			result = waitForRecord(watcher, file.toPath());
			log.info("got: {}", file);
		} catch (IOException e) {
			log.info("waiting for file failed, i/o exception: {}", e.getMessage());
		} catch (InterruptedException e) {
			log.info("waiting for file failed, timeout: {}", e.getMessage());
		} finally {
			if (watcher != null) {
				try {
					watcher.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (file.exists())
				result = true;

			waitTimestamp = 0;
		}

		return result;
	}

	private boolean waitForRecord(WatchService watcher, Path obj) throws IOException, InterruptedException {
		log.info("waiting requested for: {}", obj);

		if (obj == null)
			return false;

		Path dir = obj.getParent();
		Path sub = obj.getFileName();

		if ((dir == null) || (sub == null))
			return false;

		try {
			dir.register(watcher, ENTRY_CREATE);
		}
		catch (NoSuchFileException e) {
			waitForRecord(watcher, dir);
			dir.register(watcher, ENTRY_CREATE);
		}

		if (obj.toFile().exists())
			return true;

		if ((System.currentTimeMillis() - waitTimestamp) > RECORD_FILE_TIMEOUT)
			return false;

		log.info("now waiting for: {}", obj);

		for (;;) {
			// wait for key to be signaled
			WatchKey key = watcher.poll(RECORD_FILE_TIMEOUT, TimeUnit.MILLISECONDS);
//			WatchKey key = watcher.take();

			if (key == null) {
				log.info("watcher.poll() returned null... wait timeout");
				break;
			}

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
				if (sub.equals(pFilename)) {
					log.info("positive, got: {}", child);
					return true;
				}
				else {
					log.info("negative, still waiting for: {}", obj);
				}
		    }

		    // Reset the key -- this step is critical if you want to
		    // receive further watch events.  If the key is no longer valid,
		    // the directory is inaccessible so exit the loop.
		    if (!key.reset())
		        break;
		}

		return false;
	}
}
