package ru.ntechs.asteriskconnector.eventchain;

import java.util.ArrayList;

public class ChainContext {
	private ArrayList<Object> context;

	public ChainContext() {
		super();
		this.context = new ArrayList<>();
	}

	public void put(Object obj) {
		if (obj != null)
			context.add(obj);
	}

	public <T> ArrayList<T> get(Class<T> type) {
		ArrayList<T> result = new ArrayList<>();

		for  (Object obj : context)
			if (type.isInstance(obj))
				result.add(type.cast(obj));

		return result;
	}

	public boolean remove(Object obj) {
		return context.remove(obj);
	}

	public boolean remove(ArrayList<Object> objects) {
		return context.removeAll(objects);
	}
}
