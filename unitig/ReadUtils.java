package unitig;

import java.util.*;
import java.io.*;
public class ReadUtils {
	static enum FileType {
		EMPTY, FASTA, FASTQ;
	};
static void testGetLengths() throws IOException
{
	FileType[] fts = new FileType[] {FileType.FASTA, FileType.FASTQ, FileType.FASTA, FileType.FASTQ, FileType.FASTA};
	int[][] lengths = new int[][] {
		{10, 20, 30, 40, 50},
		{10, 20, 30, 40, 50},
		{4, 4, 4, 4, 3, 3, 3, 2, 2, 1},
		{4, 4, 4, 4, 3, 3, 3, 2, 2, 1},
		{100}
	};
	int numTests = fts.length;
	for(int i = 0; i<numTests; i++)
	{
		boolean result = test(fts[i], lengths[i]);
		System.out.println("TEST " + (i+1) + ": "+fts[i] + " " 
				+ Arrays.toString(lengths[i]) + " " + (result ? "PASSED" : "FAILED"));
	}
}
static boolean test(FileType ft, int[] lengths) throws IOException
{
	String fn = "test.txt";
	File f = new File(fn);
	PrintWriter out = new PrintWriter(f);
	for(int i = 0; i<lengths.length; i++)
	{
		if(ft == FileType.FASTA)
		{
			int lineLength = 80;
			out.println(">read"+(i+1));
			String read = randomRead(lengths[i], i);
			for(int j = 0; j+lineLength<lengths[i]; j+=lineLength)
			{
				out.println(read.substring(j, Math.min(lengths[i], j+lineLength)));
			}
		}
		else
		{
			out.println(">read"+(i+1));
			String read = randomRead(lengths[i], i);
			out.println(read);
			out.println("+");
			for(int j = 0; j<lengths[i]; j++) out.print("*");
			out.println();
		}
	}
	out.close();
	
	OrderedFrequencyMap<Integer> myLengths = getLengths(fn);
	OrderedFrequencyMap<Integer> trueLengths = getLengths(fn);
	
	f.delete();
	
	return myLengths.toString().equals(trueLengths.toString());
}
/*
 * Generaets a random read of a fixed length for testing purposes
 */
static String randomRead(int length, int seed)
{
	char[] bases = new char[] {'A', 'C', 'G', 'T'};
	Random r = new Random(seed);
	char[] res = new char[length];
	for(int i = 0; i<length; i++)
	{
		res[i] = bases[r.nextInt(bases.length)];
	}
	return new String(res);
}
/*
 * Gets the multi-set of read lengths from a read file
 */
static OrderedFrequencyMap<Integer> getLengths(String fn) throws IOException
{
	OrderedFrequencyMap<Integer> res = new OrderedFrequencyMap<Integer>();
	PeekableScanner  input = new PeekableScanner(new FileInputStream(new File(fn)));
	FileType ft = getFileType(input);
	if(ft == FileType.EMPTY)
	{
		return res;
	}
	while(true)
	{
		String curRead = getUnlabelledRead(input, ft);
		if(curRead == null || curRead.length() == 0)
		{
			break;
		}
		res.add(curRead.length());
	}
	return res;
}
public static String getName(PeekableScanner input, FileType ft)
{
	if(!input.hasNext())
	{
		return null;
	}
	if(ft == FileType.FASTQ)
	{
		String res = "";
		for(int i = 0; i<4; i++)
		{
			if(!input.hasNext())
			{
				return null;
			}
			if(i == 0)
			{
				res = input.nextLine();
				if(res.length() != 0)
				{
					res = res.substring(1);
				}
				else
				{
					res = null;
				}
			}
			else
			{
				input.nextLine();
			}
		}
		return res;
	}
	else if(ft == FileType.FASTA)
	{
		if(!input.hasNext())
		{
			return null;
		}
		String res = input.nextLine();
		
		if(res.length() != 0)
		{
			res =  res.substring(1);
		}
		else
		{
			res = null;
		}
		
		while(input.hasNext())
		{
			String curLine = input.peekLine();
			if(curLine.length() == 0 || curLine.startsWith(">"))
			{
				break;
			}
			input.nextLine();
		}
		
		return res;
	}
	else
	{
		return null;
	}
}
/*
 * Scans the next read and returns its name and sequence
 */
static String[] getLabelledRead(PeekableScanner input, FileType ft)
{
	String[] res = new String[2];
	if(!input.hasNext())
	{
		return null;
	}
	if(ft == FileType.FASTQ)
	{
		for(int i = 0; i<4; i++)
		{
			if(!input.hasNext())
			{
				return null;
			}
			if(i == 0) res[0] = input.nextLine();
			else if(i == 1) res[1] = input.nextLine();
			else input.nextLine();
		}
		if(res[0].length() == 0)
		{
			return null;
		}
		else
		{
			res[0] = res[0].substring(1);
		}
		return res;
	}
	else if(ft == FileType.FASTA)
	{
		if(!input.hasNext())
		{
			return null;
		}
		res[0] = input.nextLine();
		
		StringBuilder sb = new StringBuilder("");
		
		while(input.hasNext())
		{
			String curLine = input.peekLine();
			if(curLine.length() == 0 || curLine.startsWith(">"))
			{
				break;
			}
			sb.append(input.nextLine());
		}
		
		res[1] = sb.toString();
		
		if(res[0].length() == 0)
		{
			return null;
		}
		else
		{
			res[0] = res[0].substring(1);
		}
		
		return res;
	}
	else
	{
		return null;
	}
}
/*
 * Scans the next read and returns the sequence associated with it
 */
static String getUnlabelledRead(PeekableScanner input, FileType ft)
{
	if(!input.hasNext())
	{
		return null;
	}
	if(ft == FileType.FASTQ)
	{
		String res = "";
		for(int i = 0; i<4; i++)
		{
			if(!input.hasNext())
			{
				return null;
			}
			if(i == 1) res = input.nextLine();
			else input.nextLine();
		}
		return res;
	}
	else if(ft == FileType.FASTA)
	{
		if(!input.hasNext())
		{
			return null;
		}
		input.nextLine();
		
		StringBuilder res = new StringBuilder("");
		
		while(input.hasNext())
		{
			String curLine = input.peekLine();
			if(curLine.length() == 0 || curLine.startsWith(">"))
			{
				break;
			}
			res.append(input.nextLine());
		}
		
		return res.toString();
	}
	else
	{
		return null;
	}
}
/*
 * Gets the filetype of a read file: fasta, fastq, or other/empty
 */
static FileType getFileType(PeekableScanner input) throws IOException
{
	if(!input.hasNext())
	{
		return FileType.EMPTY;
	}
	String line = input.peekLine();
	if(line.startsWith(">"))
	{
		return FileType.FASTA;
	}
	else if(line.startsWith("@"))
	{
		return FileType.FASTQ;
	}
	else
	{
		return FileType.EMPTY;
	}
}
/*
 * Similar to a TreeSet but keeps a map from element to frequency instead
 */
static class OrderedFrequencyMap<T>
{
	TreeMap<T, Integer> freq;
	OrderedFrequencyMap()
	{
		freq = new TreeMap<T, Integer>();
	}
	OrderedFrequencyMap(T[] data)
	{
		freq = new TreeMap<T, Integer>();
		for(T x : data)
		{
			add(x);
		}
	}
	void add(T x)
	{
		freq.put(x, freq.containsKey(x) ? (1 + freq.get(x)) : 1);
	}
	int count(T x)
	{
		return freq.containsKey(x) ? freq.get(x) : 0;
	}
	public String toString()
	{
		return freq.toString();
	}
}
/*
 * File scanner with the additional ability to peek at the next line
 */
static class PeekableScanner
{
	String lastLine;
	boolean hasLastLine;
	Scanner sc;
	PeekableScanner(InputStream is)
	{
		sc = new Scanner(is);
		hasLastLine = false;
	}
	String peekLine()
	{
		if(hasLastLine)
		{
			return lastLine;
		}
		else
		{
			lastLine = sc.nextLine();
			hasLastLine = true;
			return lastLine;
		}
	}
	String nextLine()
	{
		if(hasLastLine)
		{
			hasLastLine = false;
			String res = lastLine;
			lastLine = null;
			return res;
		}
		else
		{
			return sc.nextLine();
		}
	}
	boolean hasNext()
	{
		return hasLastLine || sc.hasNext();
	}
}
}