/*
 * Unique join collapsing module of unitigging
 * 
 * If an edge is the only prefix or only suffix overlap for both reads
 * it connects, merge those reads' nodes into a single chunk.
 */

package unitig;

import java.util.ArrayList;

public class UniqueJoinCollapsing {
public static ChunkGraph uniqueJoins(Graph g)
{
	ChunkGraph res = new ChunkGraph(g);
	
	Graph.Edge[] uniquePrefix = new Graph.Edge[g.n];
	Graph.Edge[] uniqueSuffix = new Graph.Edge[g.n];
	
	boolean[] prefixJoined = new boolean[g.n];
	boolean[] suffixJoined = new boolean[g.n];
	
	for(int i = 0; i<g.n; i++)
	{
		if(g.adj[0][i].size() + g.adj[2][i].size() == 1)
		{
			uniquePrefix[i] = g.adj[0][i].size() > 0 ? g.adj[0][i].get(0) : g.adj[2][i].get(0);
		}
		if(g.adj[1][i].size() + g.adj[3][i].size() == 1)
		{
			uniqueSuffix[i] = g.adj[1][i].size() > 0 ? g.adj[1][i].get(0) : g.adj[3][i].get(0);
		}
	}
	
	for(int i = 0; i<g.n; i++)
	{
		ArrayList<Integer> rl = res.readList[res.chunkId[i]];
		if(i != rl.get(rl.size()-1)) continue;
		if(uniquePrefix[i] != null && !prefixJoined[i])
		{
			// See if this unique prefix match is unique for its neighbor too
			Graph.Edge cur = uniquePrefix[i];
			
			boolean sharedUnique = false;
			
			if(cur.theirSuffix)
			{
				if(suffixJoined[cur.to]) continue;
				sharedUnique |= uniqueSuffix[cur.to] != null && uniqueSuffix[cur.to].compareTo(cur.reverse()) == 0;
			}
			else
			{
				if(prefixJoined[cur.to]) continue;
				sharedUnique |= uniquePrefix[cur.to] != null && uniquePrefix[cur.to].compareTo(cur.reverse()) == 0;
			}
			
			if(sharedUnique)
			{
				if(cur.theirSuffix)
				{
					suffixJoined[cur.to] = true;
				}
				else
				{
					prefixJoined[cur.to] = true;
				}
				prefixJoined[i] = true;
				res.merge(res.chunkId[i], res.chunkId[cur.to], uniquePrefix[i], cur.to != res.readList[res.chunkId[cur.to]].get(0));
			}
		}
		
		if(uniqueSuffix[i] != null && !suffixJoined[i])
		{
			// See if this unique prefix match is unique for its neighbor too
			Graph.Edge cur = uniqueSuffix[i];
			
			boolean sharedUnique = false;
			
			if(cur.theirSuffix)
			{
				if(suffixJoined[cur.to]) continue;
				sharedUnique |= uniqueSuffix[cur.to] != null && uniqueSuffix[cur.to].compareTo(cur.reverse()) == 0;
			}
			else
			{
				if(prefixJoined[cur.to]) continue;
				sharedUnique |= uniquePrefix[cur.to] != null && uniquePrefix[cur.to].compareTo(cur.reverse()) == 0;
			}
			
			if(sharedUnique)
			{
				if(cur.theirSuffix)
				{
					suffixJoined[cur.to] = true;
				}
				else
				{
					prefixJoined[cur.to] = true;
				}
				suffixJoined[i] = true;
				res.merge(res.chunkId[i], res.chunkId[cur.to], uniqueSuffix[i], cur.to != res.readList[res.chunkId[cur.to]].get(0));
			}
		}
	}
	return res;
}
}
