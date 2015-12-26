package general;

import java.util.*;

/**Created on Jul 15, 2005
 * @author mgbarsky
 * All general usage functions are in this class.
 * They are all static.
 */

public class Utils 
{
	public static int max(int first,int second)
	{
		if(first>second)
			return first;
		return second;
	}

	public static int min(int first,int second)
	{
		if(first<second)
			return first;
		return second;
	}

	public static int min(int first,int second,int third)
	{
		int ret=first;
		if(second<ret)
			ret=second;
		if(third<ret)
			ret=third;		

		return ret;
	}

	public static void printEDMatrix(int [][]matrix, char[] s1, char [] s2)
	{

		System.out.print("\t\t");

		for(int j=0;j<matrix[0].length-1;j++)

			System.out.print(s2[j]+"\t");

		System.out.print("\n");

		for(int i=0;i<matrix.length;i++)

		{
			if(i>0)
			System.out.print(s1[i-1]+"\t");
			else
				System.out.print("\t");
			if(matrix[i]!=null)

			{

				for(int j=0;j<matrix[i].length;j++)

					System.out.print(matrix[i][j]+"\t");

			}

			else

				System.out.print("null");

			System.out.print("\n");

		}

	}
	
	public static void printEDMatrix(int [][]matrix)
	{
		for(int i=0;i<matrix.length;i++)
		{
			
			if(matrix[i]!=null)

			{

				for(int j=0;j<matrix[i].length;j++)

					System.out.print(matrix[i][j]+"\t");

			}
			else
				System.out.print("null");

			System.out.print("\n");
		}
	}
	
	public static void printEDMatrix(Integer [][]matrix)
	{
		for(int i=0;i<matrix.length;i++)
		{
			for(int j=0;j<matrix[i].length;j++)

				System.out.print(matrix[i][j]+"\t");
			System.out.print("\n");

		}
	}	
	
	public static void  printMatchingMatrix(boolean [][] matr)

	{

		for(int i=0;i<matr.length;i++)

		{

			for(int j=0;j<matr[i].length;j++)

			{	
				if (matr[i][j])
					System.out.print(1+"\t");
				else
					System.out.print(0+"\t");
			}
			System.out.println("\n");

		}

	}
	
	
	
	public static int edScore(char first,char second)
	{
		if(first==second)
			return 0;

		return 1;
	}

	
}


