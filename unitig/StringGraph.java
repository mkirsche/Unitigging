package unitig;
import java.util.*;
import java.io.*;
public class StringGraph extends Graph {
	
	HashMap<String, Integer> idMap;
	StringGraph(StringMap map)
	{
		super(map.n);
		idMap = map.map;
	}
	StringGraph(String pafFn) throws IOException
	{
		this(new StringMap(pafFn));
		Scanner input = new Scanner(new FileInputStream(new File(pafFn)));
		while(input.hasNextLine())
		{
			String line = input.nextLine();
			if(line.length() > 0)
			{
				addEdgeFromPafLine(line);
			}
		}
		input.close();
	}
	boolean addEdgeFromPafLine(String line)
	{
		String[] comps = line.split("\t");
		if(!idMap.containsKey(comps[0])) return false;
		if(!idMap.containsKey(comps[5])) return false;
		int from = idMap.get(comps[0]), to = idMap.get(comps[5]);
		int fromLength = Integer.parseInt(comps[1]), toLength = Integer.parseInt(comps[6]);
		int fromStart = Integer.parseInt(comps[2]), fromEnd = Integer.parseInt(comps[3]);
		int toStart = Integer.parseInt(comps[7]), toEnd = Integer.parseInt(comps[8]);
		Edge e = new Edge(from, to, fromStart, fromEnd, toStart, toEnd, fromLength, toLength);
		addEdge(e);
		return true;
	}
	static class StringMap
	{
		HashMap<String, Integer> map;
		int n;
		StringMap(String pafFn) throws IOException
		{
			this();
			Scanner input = new Scanner(new FileInputStream(new File(pafFn)));
			while(input.hasNextLine())
			{
				String line = input.nextLine();
				if(line.length() > 0)
				{
					addReadNamesFromPafLine(line);
				}
			}
			input.close();
		}
		StringMap()
		{
			n = 0;
			map = new HashMap<String, Integer>();
		}
		StringMap(HashMap<String, Integer> map, int n)
		{
			this.map = map;
			this.n= n;
		}
		void addReadNamesFromPafLine(String pafLine)
		{
			String[] comps = pafLine.split("\t");
			String a = comps[0], b = comps[5];
			if(!map.containsKey(a)) map.put(a, n++);
			if(!map.containsKey(b)) map.put(b, n++);
		}
	}
}
