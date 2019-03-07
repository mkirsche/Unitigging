package unitig;

import java.util.HashMap;

public class Test {
public static void main(String[] args)
{
	containmentRemovalTest();
	singleStrandTestUniqueJoinCollapsing();
	seqTest();
}
/*
 * Makes a test graph with a contained read
 */
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
/*
 * The parameters for a set of reads which spans a genome of length 100 with simple overlap structure
 */
static int n = 5;
static int[] lengths = new int[] {10, 20, 30, 40, 50};
static int[] startPos = new int[] {29, 80, 0, 50, 35};
static Graph makeTestGraph()
{
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
/*
 * Tests that contained reads are properly removed
 */
static void containmentRemovalTest()
{
	StringGraph g = makeTestGraph2();
	System.out.println("Full graph:");
	System.out.println(g);
	
	StringGraph updated = RemoveContainedNodes.removeContainedNodes(g);
	System.out.println("Graph without contained nodes:");
	System.out.println(updated);
}
/*
 * Tests the end-to-end unitigging on a simple example
 * The assembled sequence should be exactly the genome or its reverse complement
 */
static void seqTest()
{
	String genome = "";
	for(int i = 0; i<20; i++) genome = genome + "ACGTA";
	System.out.println("Genome:\n"+genome);
	String[] reads = new String[n];
	for(int i = 0; i<n; i++) reads[i] = genome.substring(startPos[i], startPos[i] + lengths[i]);
	Graph g = makeTestGraph();
	Graph updated = TransitiveEdgeRemoval.removeTransitiveEdges(g);
	ChunkGraph collapsed = UniqueJoinCollapsing.uniqueJoins(updated);
	ConstructSequences.Assembly a = ConstructSequences.produceAssembly(collapsed, reads);
	System.out.println("Assembly:");
	for(String s : a.unitigs)
	{
		System.out.println(s);
	}
}
/*
 * Tests that the transitive edge removal module works on a simple example
 */
static void singleStrandTestTransitiveEdgeRemoval()
{
	Graph g = makeTestGraph();
	System.out.println("Full graph:");
	System.out.println(g);
	
	Graph updated = TransitiveEdgeRemoval.removeTransitiveEdges(g);
	
	System.out.println("Graph without transitive edges:");
	System.out.println(updated);
}
/*
 * Tests that the unique join collapsing module works on a simple example
 */
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
