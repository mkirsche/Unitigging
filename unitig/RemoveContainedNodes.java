package unitig;

import java.util.ArrayList;
import java.util.HashMap;

public class RemoveContainedNodes {

	static StringGraph removeContainedNodes(StringGraph g)
	{
		int n = g.n;
		boolean[] contained = new boolean[n];
		
		for(int i = 0; i<4; i++)
			for(int j = 0; j<n; j++)
			{
				for(Graph.Edge e : g.adj[i][j])
				{
					if(e.isContainedFrom())
					{
						contained[j] = true;
					}
					if(e.isContainedTo())
					{
						contained[e.to] = true;
					}
				}
			}
		
		// Create new graph with contained edges removed
		int nn = 0;
		int[] map = new int[n];
		for(int i = 0; i<n; i++)
		{
			if(contained[i])
			{
				map[i] = -1;
			}
			else
			{
				map[i] = nn++;
			}
		}
		
		// TODO update string map
		HashMap<String, Integer> newIDMap = new HashMap<String, Integer>();
		for(String s : g.idMap.keySet())
		{
			int node = g.idMap.get(s);
			if(!contained[node])
			{
				newIDMap.put(s, map[node]);
			}
		}
		
		StringGraph.StringMap sm = new StringGraph.StringMap(newIDMap, nn);
		
		StringGraph res = new StringGraph(sm);
		for(int i = 0; i<n; i++)
		{
			if(contained[i])
			{
				continue;
			}
			else
			{
				for(int j = 0; j<4; j++)
				{
					ArrayList<Graph.Edge> cur = g.adj[j][i];
					for(Graph.Edge e :  cur)
					{
						if(map[e.to] != -1)
						{
							e.to = map[e.to];
							res.adj[j][map[i]].add(e);
						}
					}
				}
			}
		}
		return res;
	}
}
