//Author:nissan

package deadlock;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class DeadLock {
	public static ArrayList<String> DeadProcess;
	public static ArrayList<String> DeadResource;
	public static String rootNode;

	public static void main(String[] args) {

		RAG graph = new RAG();

		Scanner reader = new Scanner(System.in); // Create a Scanner object
		String userChoice = reader.nextLine(); // Read user input
		URL path = DeadLock.class.getResource(userChoice);
		File f = new File(path.getFile());
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = br.readLine()) != null) {

				String Process = "Process " + line.substring(0, 1);
				String Resource = "Resource " + line.substring(line.length() - 1);

				if (line.contains("W")) {
					graph.addNode(Resource);
					graph.addNode(Process);
					System.out.print(Process + " wants " + Resource);
					if (graph.graphNodes.get(Resource).isEmpty()) {
						graph.addEdge(Resource, Process);
						System.out.print(" - " + Resource + " is allocated to " + Process + "\n");
					} else {
						graph.addEdge(Process, Resource);
						System.out.print(" – " + Process + " must wait\n");
					}

					for (String key : graph.graphNodes.keySet()) {
						// check for cycle
						if (graph.graphNodes.get(key).isEmpty())
							continue;
						rootNode = key;
						DeadProcess = new ArrayList<String>();
						DeadResource = new ArrayList<String>();
						if (key.contains("Process")) {
							DeadProcess.add(key);
						} else {
							DeadResource.add(key);
						}
						checkCycle(key, graph);
					}
				} else {
					Boolean bool = false;
					graph.removeEdge(Resource, Process);
					System.out.print(Process + " releases " + Resource);
					for (String key : graph.graphNodes.keySet()) {
						if (graph.graphNodes.get(key).isEmpty())
							continue;
						if (graph.graphNodes.get(key).get(0).equals(Resource)) {
							graph.addEdge(Resource, key);
							graph.removeEdge(key, Resource);
							System.out.print(" – " + Resource + " is allocated to " + key + "\n");
							bool = true;
							break;
						}

					}
					if (!bool)
						System.out.println(" - " + Resource + " is now free ");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("EXECUTION COMPLETED: No deadlock encountered");
	}

	static void checkCycle(String node, RAG graph) {
		if (graph.graphNodes.get(node).isEmpty())
			return;
		String nextNode = graph.graphNodes.get(node).get(0);
		if (nextNode.equals(rootNode)) {
			System.out.print("DEADLOCK DETECTED: ");
			System.out.println("Processes "
					+ DeadProcess.toString().replace("[", "").replace("Process ", "").replace("]", "") + " and "
					+ "Resources " + DeadResource.toString().replace("[", "").replace("Resource ", "").replace("]", "")
					+ " are found in a cycle");
			System.exit(0);
		} else {
			if (nextNode.contains("Process")) {
				DeadProcess.add(nextNode);
			} else
				DeadResource.add(nextNode);
			checkCycle(nextNode, graph);
		}
		return;
	}
}

class RAG {
	Map<String, List<String>> graphNodes;

	RAG() {
		graphNodes = new LinkedHashMap<String, List<String>>();
	}

	void addNode(String label) {
		graphNodes.putIfAbsent(label, new ArrayList<>());
	}

	void removeNode(String label) {
		graphNodes.values().stream().forEach(e -> e.remove(label));
		graphNodes.remove(label);
	}

	void addEdge(String label1, String label2) {
		graphNodes.get(label1).add(label2);
	}

	void removeEdge(String label1, String label2) {
		List<String> rmvEd = graphNodes.get(label1);
		if (rmvEd != null)
			rmvEd.remove(label2);
	}

	List<String> getAdjNodes(String label) {
		return graphNodes.get(label);
	}

}
