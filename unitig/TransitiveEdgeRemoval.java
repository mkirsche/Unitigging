package unitig;
import java.util.*;
public class TransitiveEdgeRemoval {
	static double EPS = 0.1;
	static int alpha = 3;
@SuppressWarnings("unchecked")
static Graph removeTransitiveEdges(Graph graph)
{
	// Sort the list of edges for each string-type pair for fast edge lookups
	if(!graph.sorted()) graph.sort();
	
	int n = graph.n;
	Graph res = new Graph(n);
	// Iterate over f, the first node in a f-g-h f-h transitive relationship
	for(int f = 0; f<n; f++)
	{
		// Both the f-g edge and the f-h must have the same value of suff_f, 
		// which is the 0th bit in edgeType,
		// so try all pairs of g_suff in f-g and h_suff in f-h
		for(int edgeType = 0; edgeType<2; edgeType++)
		{
			ArrayList<Graph.Edge> edges = graph.adj[edgeType][f];
			ArrayList<Graph.Edge> cedges = graph.adj[edgeType^2][f];
			
			// Number of edges coming from f with f_suff = edgeType
			int curSize = edges.size() + cedges.size();
			
			if(curSize == 0) continue;
			
			// Keep track for each edge whether or not it is transitive
			boolean[] isTransitive = new boolean[curSize];
			
			// Iterate over all pairs of edges, and search for a corresponding g-h edge
			for(int i = 0; i<curSize; i++)
			{
				for(int j = 0; j<curSize; j++)
				{
					if(isTransitive[j]) continue;
					Graph.Edge first = i < edges.size() ? edges.get(i) : cedges.get(i-edges.size());
					Graph.Edge second = j < edges.size() ? edges.get(j) : cedges.get(j-edges.size());
					int g = first.to, h = second.to;
					if(g == h) continue;

					// Compute edgeType of g-h edge based on other edge types
					int oldGSuff = i < edges.size() ? 0 : 1; 
					int oldHSuff = j < edges.size() ? 0 : 1;					
					int newGSuff = 1 - oldGSuff;
					int newHSuff = oldHSuff;
					int newType = newGSuff + (newHSuff << 1);
					
					// Find set of possible triangle edges
					ArrayList<Graph.Edge> edgesFromG = graph.adj[newType][g];
					
					// Where the run of edges to h would start
					int index = Graph.Edge.ceilingIndex(edgesFromG, new Graph.Edge(g, h, 0));
					
					// Iterate over possible g-h edges and see if they make the f-h edge transitive
					Graph.Edge cur = null;
					double hangDifferenceThreshold = EPS * second.length + alpha;
					while(index < edgesFromG.size())
					{
						cur = edgesFromG.get(index);
						index++;
						if(cur.to != h)
						{
							break;
						}
						double leftHangDifference = Math.abs(first.myHang - second.myHang + cur.myHang);
						double rightHangDifference = Math.abs(first.theirHang - second.theirHang + cur.theirHang);
						if(Math.max(leftHangDifference, rightHangDifference) <= hangDifferenceThreshold)
						{
							isTransitive[j] = true;
							break;
						}
					}
				}
			}
			// Rebuild edge lists with transitive edges removed
			ArrayList<Graph.Edge>[] newLists = new ArrayList[2];
			for(int i = 0; i<newLists.length; i++) newLists[i] = new ArrayList<Graph.Edge>();
			for(int i = 0; i<curSize; i++)
			{
				if(isTransitive[i]) continue;
				if(i < edges.size())
				{
					newLists[0].add(edges.get(i));
				}
				else
				{
					newLists[1].add(cedges.get(i-edges.size()));
				}
			}
			res.adj[edgeType][f] = newLists[0];
			res.adj[edgeType^2][f] = newLists[1];
		}
	}
	return res;
}
}
