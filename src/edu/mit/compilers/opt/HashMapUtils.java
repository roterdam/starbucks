package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapUtils {

	static public <K, V> HashMap<K, List<V>> deepCloneList(
			HashMap<K, List<V>> map) {
		HashMap<K, List<V>> out = new HashMap<K, List<V>>();
		for (K key : map.keySet()) {
			out.put(key, new ArrayList<V>(map.get(key)));
		}
		return out;
	}

	static public <K, V> HashMap<K, V> deepClone(HashMap<K, V> map) {
		HashMap<K, V> out = new HashMap<K, V>();
		for (K key : map.keySet()) {
			out.put(key, map.get(key));
		}
		return out;
	}

	public static <K, V> String toMapString(Map<K, V> map) {
		String out = "{";
		for (K key : map.keySet()) {
			out += "\n" + key.toString() + " = " + map.get(key).toString()
					+ ",";
		}
		out += "\n}";
		return out;
	}

}
