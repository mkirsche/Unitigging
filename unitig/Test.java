package unitig;

import java.util.HashMap;

public class Test {
public static void main(String[] args)
{
	containmentRemovalTest();
	singleStrandTestUniqueJoinCollapsing();
}
static StringGraph makeTestGraph2()
{
	int n = 6;
	int[] lengths = new int[] {10, 20, 30, 40, 50, 15};
	int[] startPos = new int[] {30, 80, 0, 50, 35, 50};
	
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	for(int i = 0; i<n; i++) map.put("read" + (i+1), i);
	
	StringGraph g = new StringGraph(new StringGraph.StringMap(map, n));
	
	for(int i = n-1; i>=0; i--)
		for(int j = n-1; j>i; j--)
		{
			int endi = startPos[i] + lengths[i];
			int endj = startPos[j] + lengths[j];
			if(startPos[i] <= startPos[j] && endi > startPos[j])
			{
				Graph.Edge cur = new Graph.Edge(
						i, j,
						startPos[j] - startPos[i], Math.min(lengths[i], endj - startPos[i]),
						0, Math.min(lengths[j], endi - startPos[j]),
						lengths[i], lengths[j]);
				g.addEdge(cur);
			}
			else if(startPos[j] <= startPos[i] && endj > startPos[i])
			{
				Graph.Edge cur = new Graph.Edge(
						j, i,
						startPos[i] - startPos[j], Math.min(lengths[j], endi - startPos[j]),
						0, Math.min(lengths[i], endj - startPos[i]),
						lengths[j], lengths[i]);
				g.addEdge(cur);
			}
		}
	return g;
}
static Graph makeTestGraph()
{
	int n = 5;
	int[] lengths = new int[] {10, 20, 30, 40, 50};
	int[] startPos = new int[] {30, 80, 0, 50, 35};
	
	Graph g = new Graph(n);
	for(int i = n-1; i>=0; i--)
		for(int j = n-1; j>i; j--)
		{
			int endi = startPos[i] + lengths[i];
			int endj = startPos[j] + lengths[j];
			if(startPos[i] <= startPos[j] && endi > startPos[j])
			{
				Graph.Edge cur = new Graph.Edge(
						i, j,
						startPos[j] - startPos[i], Math.min(lengths[i], endj - startPos[i]),
						0, Math.min(lengths[j], endi - startPos[j]),
						lengths[i], lengths[j]);
				g.addEdge(cur);
			}
			else if(startPos[j] <= startPos[i] && endj > startPos[i])
			{
				Graph.Edge cur = new Graph.Edge(
						j, i,
						startPos[i] - startPos[j], Math.min(lengths[j], endi - startPos[j]),
						0, Math.min(lengths[i], endj - startPos[i]),
						lengths[j], lengths[i]);
				g.addEdge(cur);
			}
		}
	return g;
}
static void containmentRemovalTest()
{
	StringGraph g = makeTestGraph2();
	System.out.println("Full graph:");
	System.out.println(g);
	
	StringGraph updated = RemoveContainedNodes.removeContainedNodes(g);
	System.out.println("Graph without contained nodes:");
	System.out.println(updated);
}
static void singleStrandTestTransitiveEdgeRemoval()
{
	Graph g = makeTestGraph();
	System.out.println("Full graph:");
	System.out.println(g);
	
	Graph updated = TransitiveEdgeRemoval.removeTransitiveEdges(g);
	
	System.out.println("Graph without transitive edges:");
	System.out.println(updated);
}
static void singleStrandTestUniqueJoinCollapsing()
{
	Graph g = makeTestGraph();
	System.out.println("Full graph:");
	System.out.println(g);
	
	Graph updated = TransitiveEdgeRemoval.removeTransitiveEdges(g);
	
	System.out.println("Graph without transitive edges:");
	System.out.println(updated);
	
	ChunkGraph collapsed = UniqueJoinCollapsing.uniqueJoins(updated);
	
	System.out.println("Graph with collapsed unique edges:");
	System.out.println(collapsed);
}
}
