package unitig;
public class Test {
public static void main(String[] args)
{
	singleStrandTestTransitiveEdgeRemoval();
}
static void singleStrandTestTransitiveEdgeRemoval()
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
	System.out.println("Full graph:");
	System.out.println(g);
	
	Graph updated = TransitiveEdgeRemoval.removeTransitiveEdges(g);
	
	System.out.println("Graph without transitive edges:");
	System.out.println(updated);
}
}
