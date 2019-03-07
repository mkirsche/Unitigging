/*
 * A chunk graph representing a compressed string graph
 * Each vertex, rather than representing a single read,
 * consists of one or more reads joined together.
 */

package unitig;

import java.util.*;

public class ChunkGraph extends Graph {

	// The list of reads (in order) represented by each vertex
	ArrayList<Integer>[] readList;
	
	// Overlap information within each chunk
	ArrayList<InnerOverlap>[] overlapList;
	
	// For each read, the ID of the chunk it belongs to
	int[] chunkId;
	
	@SuppressWarnings("unchecked")
	ChunkGraph(int n)
	{
		super(n);
		readList = new ArrayList[this.n];
		overlapList = new ArrayList[this.n];
		for(int i = 0; i<n; i++)
		{
			readList[i] = new ArrayList<Integer>();
			readList[i].add(i);
			
			overlapList[i] = new ArrayList<InnerOverlap>();
		}
		chunkId = new int[n];
		for(int i = 0; i<n; i++)
		{
			chunkId[i] = i;
		}
	}
	
	ChunkGraph(Graph g)
	{
		this(g.n);
		this.adj = g.adj;
	}
	
	/*
	 * Appends chunk in vertex j to chunk in vertex i with an overlap from edge e
	 * If reverse is true, the reads in vertex j are added in reverse order
	 * 
	 * A few invariants should be maintained:
	 *   Every read x in readList[i] should have chunkId[x] = r
	 *   adj[type][i] should have only edges from first and last reads in 
	 *     readList[i] to reads outside of readList[i]
	 *   | overlapList[i] | = [ readList[i] - 1 |
	 */
	@SuppressWarnings("unchecked")
	void merge(int i, int j, Edge e, boolean reverse)
	{
		if(readList[i].size() > 0 && readList[i].get(0) == e.from)
		{
			// first list must be reversed
			Collections.reverse(readList[i]);
			Collections.reverse(overlapList[i]);
			for(InnerOverlap io : overlapList[i])
			{
				io.reverse();
			}
		}
		// First reverse j's read list if needed read lists
		ArrayList<Integer> jList = readList[j];
		if(reverse) Collections.reverse(jList);
		
		// Next, update overlap lists
		ArrayList<InnerOverlap> joList = overlapList[j];
		if(reverse)
		{
			Collections.reverse(joList);
			for(InnerOverlap io : joList)
			{
				io.reverse();
			}
		}
		int lastReadI = readList[i].get(readList[i].size() - 1);
		int firstReadJ = readList[j].get(reverse ? (readList[j].size() - 1) : 0);
		overlapList[i].add(new InnerOverlap(e, lastReadI, firstReadJ));
		overlapList[i].addAll(joList);
		overlapList[j].clear();
		
		// Now, update the edge list
		ArrayList<Edge>[] newEdgeList = new ArrayList[adj.length];
		for(int type = 0; type<adj.length; type++) 
		{
			newEdgeList[type] = new ArrayList<Edge>();
		}
		for(int type = 0; type<adj.length; type++)
		{
			// For current edge type, move all edges over
			for(Edge curEdge : adj[type][j])
			{
				if(curEdge.from == firstReadJ && curEdge.mySuffix == e.theirSuffix)
				{
					// Edge from part of chunk which is already being connected to end of chunk i 
					continue;
				}
				else if(chunkId[curEdge.to] == i)
				{
					// Edge which is now to something else within the same component
					continue;
				}
				else
				{
					newEdgeList[type].add(curEdge);
				}
			}
			for(Edge curEdge : adj[type][i])
			{
				if(curEdge.from == lastReadI && curEdge.mySuffix == e.mySuffix)
				{
					// Edge from part of chunk which is already being connected to end of chunk j
					continue;
				}
				else if(chunkId[curEdge.to] == j)
				{
					// Edge which is now to something else within the same component
					continue;
				}
				else
				{
					newEdgeList[type].add(curEdge);
				}
			}
		}
		
		for(int type = 0; type < adj.length; type++)
		{
			adj[type][i] = newEdgeList[type];
			adj[type][j].clear();
		}
		
		// Next, change the chunk ID of all the reads in vertex j to i
		for(int x : readList[j])
		{
			chunkId[x] = i;
		}
		
		// Finally, update read lists
		readList[i].addAll(jList);
		readList[j].clear();
		
	}
	
	public String toString()
	{
		StringBuilder res = new StringBuilder("");
		for(int i = 0; i<n; i++)
		{
			if(readList[i].size() == 0)
			{
				continue;
			}
			res.append("Chunk ID " + i + "\n");
			res.append("Vertex IDs in chunk:\n" + readList[i] + "\n");
			res.append("Edges connecting vertices in chunk:\n" + overlapList[i] + "\n");
			res.append("Edges to reads in other chunks:\n");
			for(int type = 0; type<adj.length; type++)
			{
				if(adj[type][i].size() == 0)
				{
					continue;
				}
				res.append("Type " + type + "\n");
				for(Edge e : adj[type][i])
					res.append(e.toString() + "\n");
			}
			res.append("\n");
		}
		return res.toString();
	}
	
	/*
	 * Stores information about overlaps of reads within chunks
	 * For now it just keeps the length, but can be extended to have more info
	 */
	static class InnerOverlap
	{
		int hang1, hang2;
		int type;
		InnerOverlap() {}
		InnerOverlap(Edge e, int first, int second)
		{
			hang1 = 0;
			hang2 = 0;
			if(first == e.from)
			{
				hang1 = e.myHang;
				hang2 = e.theirHang;
			}
			else
			{
				hang1 = e.theirHang;
				hang2 = e.myHang;
			}
			type = e.getType();
		}
		void reverse()
		{
			int tmp = hang1;
			hang1 = hang2;
			hang2 = tmp;
			type = swapBits(type); 
		}
		public String toString()
		{
			return "(" + hang1 + ", " + hang2 + ", "+type+")";
		}
	}

}
