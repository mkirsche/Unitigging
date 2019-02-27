package unitig;
import java.util.*;

public class Graph {
	int n;
	ArrayList<Edge>[][] adj;
	@SuppressWarnings("unchecked")
	Graph(int n)
	{
		this.n = n;
		adj = new ArrayList[4][n];
		for(int i = 0; i<4; i++)
			for(int j = 0; j<n; j++)
			{
				adj[i][j] = new ArrayList<Edge>();
			}
	}
	void sort()
	{
		for(ArrayList<Edge>[] edgeType : adj)
			for(ArrayList<Edge> edgeList : edgeType)
				Collections.sort(edgeList);
	}
	boolean sorted()
	{
		for(ArrayList<Edge>[] edgeType : adj)
			for(ArrayList<Edge> edgeList : edgeType)
				for(int i = 0; i<edgeList.size()-1; i++)
					if(edgeList.get(i).compareTo(edgeList.get(i+1)) > 0)
						return false;
		return true;
	}
	void addEdge(Edge e)
	{
		int type = e.getType();
		adj[type][e.from].add(e);
		adj[type][e.to].add(e.reverse());
	}
	public String toString()
	{
		StringBuilder res = new StringBuilder("");
		for(int i = 0; i<n; i++)
		{
			res.append("Edges from " + i + "\n");
			for(int j = 0; j<4; j++)
			{
				for(Edge e : adj[j][i])
				{
					res.append(e.toString() + "\n");
				}
			}
		}
		return res.toString();
	}
static class Edge implements Comparable<Edge>
{
	int from, to;
	double length;
	double myHang, theirHang;
	boolean mySuffix, theirSuffix;
	Edge(){}
	Edge(int from, int to, double length)
	{
		this.from = from;
		this.to= to;
		this.length = length;
	}
	Edge(int from, int to, int myStart, int myEnd, int theirStart, int theirEnd, int myReadLength, int theirReadLength)
	{
		this.from = from;
		this.to = to;
		int myMax = Math.max(myStart, myEnd);
		int myMin = myStart + myEnd - myMax;
		int theirMax = Math.max(theirStart, theirEnd);
		int theirMin = theirStart + theirEnd - theirMax;
		int theirLength = theirMax - theirMin;
		int myLength = myMax - myMin;
		length = .5 * (myLength + theirLength);
		myHang = myReadLength - myLength;
		theirHang = theirReadLength - theirLength;
		mySuffix = myMax == myReadLength;
		theirSuffix = theirMax == theirReadLength;
	}
	
	public String toString()
	{
		return "Overlap Length " + length + "; " + from + " (" 
				+ (mySuffix ? "suffix" : "prefix")
				+ ", " + myHang + " hanging) to "+ to + " (" 
				+ (theirSuffix ? "suffix" : "prefix")
				+ ", " + theirHang + " hanging)";
	}
	
	int getType()
	{
		return (mySuffix ? 1 : 0) + (theirSuffix ? 2 : 0);
	}
	
	boolean isContainedFrom()
	{
		return myHang == 0;
	}
	
	boolean isContainedTo()
	{
		return theirHang == 0;
	}
	
	/*
	 * The edge with from and to swapped
	 */
	Edge reverse()
	{
		Edge res = new Edge();
		res.from = to; res.to = from;
		res.length = length;
		res.myHang = theirHang; res.theirHang = myHang;
		res.mySuffix = theirSuffix; res.theirSuffix = mySuffix;
		return res;
	}
	
	/*
	 * Sort in increasing order of (from, to, length)
	 */
	public int compareTo(Edge o) {
		if(from != o.from) return from - o.from;
		if(to != o.to) return to - o.to;
		if(length != o.length) return (int)Double.compare(length, o.length);
		return 0;
	}
	
	/*
	 * Gets the first index in list which has an edge >= e
	 */
	static int ceilingIndex(ArrayList<Edge> list, Edge e)
	{
		int lo = -1, hi = list.size();
		while(lo < hi - 1)
		{
			int mid = (lo+hi)>>1;
			if(list.get(mid).compareTo(e) < 0) lo = mid;
			else hi = mid;
		}
		return hi;
	}
}
}
