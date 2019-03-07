/*
 * Sequence construction module of unitigging
 * 
 * Given a compressed string graph and reads it represents, produce the unitig sequence
 * for each node in the graph 
 */

package unitig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ConstructSequences {
	
	static String reverseComplement(String s)
	{
		s = s.toUpperCase();
		StringBuilder res = new StringBuilder("");
		for(int i = 0; i<s.length(); i++)
		{
			char cur = s.charAt(s.length() - i - 1);
			if(cur == 'A') res.append('T');
			else if(cur == 'C') res.append('G');
			else if(cur == 'G') res.append('C');
			else if(cur == 'T') res.append('A');
			else res.append(cur);
		}
		return res.toString();
	}
	
	static String[] getReadList(String file, StringGraph.StringMap sm) throws IOException
	{
		int n = sm.n;
		String[] res =new String[n];
		ReadUtils.PeekableScanner input = new ReadUtils.PeekableScanner(new FileInputStream(new File(file)));
		ReadUtils.FileType ft = ReadUtils.getFileType(input);
		while(input.hasNext())
		{
			String[] read = ReadUtils.getLabelledRead(input, ft);
			if(sm.map.containsKey(read[0]))
			{
				int readIndex = sm.map.get(read[0]);
				res[readIndex] = read[1];
			}
		}
		return res;
	}
	static Assembly produceAssembly(ChunkGraph cg, String[] reads)
	{
		Assembly res = new Assembly();
		for(int i = 0; i<cg.readList.length; i++)
		{
			if(cg.readList[i].size() > 0)
			{
				StringBuilder seq = new StringBuilder("");
				
				String firstRead = reads[cg.readList[i].get(0)];
				
				if(cg.readList[i].size() == 1)
				{
					seq.append(firstRead);
					res.addContig(seq.toString());
					continue;
				}
				
				boolean lastReversed = (cg.overlapList[i].get(0).type & 2) > 0;
				
				if(lastReversed)
				{
					seq.append(reverseComplement(firstRead));
				}
				else
				{
					seq.append(firstRead);
				}
				
				for(int j = 0; j<cg.overlapList[i].size(); j++)
				{
					ChunkGraph.InnerOverlap curOverlap = cg.overlapList[i].get(j);
					boolean lastSuffix = (curOverlap.type & 1) > 0;
					boolean curSuffix = (curOverlap.type & 2) > 0;
					if(lastSuffix == lastReversed)
					{
						// Invalid overlap
						System.out.println("Invalid overlap: " + lastSuffix+" "+lastReversed);
						return null;
					}
					int length = curOverlap.hang2;
					String nextRead = reads[cg.readList[i].get(j+1)];
					if(curSuffix)
					{
						lastReversed = true;
						seq.append(reverseComplement(nextRead.substring(0, length)));
					}
					else
					{
						lastReversed = false;
						seq.append(nextRead.substring(nextRead.length() - length));
					}
				}
				res.addContig(seq.toString());
			}
		}
		return res;
	}
	
	/*
	 * Representation of an assembly as a collection of strings
	 */
	static class Assembly
	{
		ArrayList<String> unitigs;
		Assembly()
		{
			unitigs = new ArrayList<String>();
		}
		void addContig(String s)
		{
			unitigs.add(s);
		}
		void printToFile(String filename) throws IOException
		{
			PrintWriter out = new PrintWriter(new File(filename));
			for(int i = 0; i<unitigs.size(); i++)
			{
				out.println(">unitig" + String.format("%04d", i+1));
				out.println(unitigs.get(i));
			}
			out.close();
		}
	}
}
