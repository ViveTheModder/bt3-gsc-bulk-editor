package cmd;
//BT3 GSC Bulk Editor by ViveTheModder
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main 
{
	static final String CSV_PATH = "./csv/", OUT_PATH = "./out/";
	static final File[] CSV_FILES = {new File(CSV_PATH+"chars.csv"),new File(CSV_PATH+"items.csv")};
	static String[] itemNames;
	private static boolean isValidGscDir(File folder)
	{
		int gscCnt=0;
		File[] dirs = folder.listFiles();
		if (dirs!=null)
		{
			for (File f: dirs)
			{
				File[] gscFilesInSubfolder = f.listFiles((dir, name) -> (name.startsWith("GSC-B-") && name.toLowerCase().endsWith(".gsc")));
				if (gscFilesInSubfolder==null || gscFilesInSubfolder.length==0)
				{
					String name = f.getName();
					if (name.startsWith("GSC-B-") && name.toLowerCase().endsWith(".gsc")) return true;
				}
				else 
				{
					gscCnt = gscFilesInSubfolder.length;
					if (f.isDirectory() && gscCnt!=0) return true;
				}
			}
		}
		return false;
	}
	public static String[] getAnyDataFromCsv(int index) throws IOException
	{
		Scanner sc = new Scanner(CSV_FILES[index]);
		ArrayList<String> listOfNames = new ArrayList<String>();
		int nameCnt=0;
		while (sc.hasNextLine())
		{
			String input = sc.nextLine();
			String[] inputArr = input.split(",");
			String name = inputArr[1];
			listOfNames.add(name);
			nameCnt++;
		}
		String[] names = new String[nameCnt];
		listOfNames.toArray(names);
		sc.close();
		return names;
	}
	public static int[][] getEnemyDataFromCsv(File csv) throws IOException
	{
		int numEnemies=0;
		int[][] enemyData = new int[5][2];
		Scanner sc = new Scanner(csv);
		String header = sc.nextLine();
		if (!header.equals("chara-name,com-diff-lvl,strat-item")) enemyData=null;
		while (sc.hasNextLine())
		{
			String input = sc.nextLine();
			String[] inputArr = input.split(",");
			int diffLevel = Integer.parseInt(inputArr[1]);
			int stratItemID=0;
			String stratItem = inputArr[2];
			//did I really have to use linear search for this...
			for (int i=0; i<itemNames.length; i++)
			{
				if (stratItem.equals(itemNames[i])) stratItemID=137+i;
			}
			enemyData[numEnemies][0] = diffLevel;
			enemyData[numEnemies][1] = stratItemID;
			numEnemies++;
		}
		sc.close();
		int[][] newEnemyData = new int[numEnemies][2];
		System.arraycopy(enemyData, 0, newEnemyData, 0, numEnemies);
		return newEnemyData;
	}
	public static void main(String[] args) 
	{
		try
		{
			File srcFolder=null;
			Scanner sc = new Scanner(System.in);
			while (srcFolder==null)
			{
				System.out.println("Enter a valid path to a folder containing either GSC files or subfolders\n"
				+ "with one GSC file each (meant to be used alongside bt3-file-dump-organizer):");
				String input = sc.nextLine();
				File temp = new File(input);
				if (temp.isDirectory() && isValidGscDir(temp)) srcFolder=temp;
			}
			
			int gscCnt=0;
			File[] gscFileRefs = srcFolder.listFiles();
			for (File dir: gscFileRefs)
				if (dir.isFile() && dir.getName().startsWith("GSC-B-") && dir.getName().toLowerCase().endsWith(".gsc")) gscCnt++;
			GSC[] gscFiles=null;
			if (gscCnt!=0) 
			{
				gscFiles = new GSC[gscCnt];
				for (int i=0; i<gscFiles.length; i++)
					gscFiles[i] = new GSC(gscFileRefs[i]);
			}
			else
			{
				gscFiles = new GSC[gscFileRefs.length];
				for (int i=0; i<gscFiles.length; i++)
				{
					if (gscFileRefs[i].isDirectory())
						gscFiles[i] = new GSC(gscFileRefs[i].listFiles((dir, name) -> (name.startsWith("GSC-B-") && name.toLowerCase().endsWith(".gsc")))[0]);
				}
			}

			String option="";
			while (option!=null)
			{
				System.out.println("\nEnter a valid option:\n1. R (Reading GSC files)\n2. W (Writing GSC files; CSV files are required)");
				option = sc.nextLine();
				String optionLower = option.toLowerCase();
				if (optionLower.equals("r")) option="r";
				else if (optionLower.equals("w")) option="w";
				else 
				{
					option=null; break;
				}
				
				long start = System.currentTimeMillis();
				itemNames=getAnyDataFromCsv(1);
				if (option.equals("r"))
				{
					for (int i=0; i<gscFiles.length; i++)
					{
						System.out.println("Reading "+gscFiles[i].getFileName()+"...");
						String[] enemyData = gscFiles[i].getEnemyData();
						for (int j=0; j<enemyData.length; j++) System.out.println("("+j+"): "+enemyData[j]);
						System.out.println();
						gscFiles[i].writeEnemyDataToCsv(enemyData);
					}
				}
				else if (option.equals("w"))
				{
					File outFolder = new File(OUT_PATH);
					File[] csvFiles = outFolder.listFiles();
					for (int i=0; i<csvFiles.length; i++)
					{
						System.out.println("Writing "+gscFiles[i].getFileName()+"...");
						gscFiles[i].writeEnemyDataToGsc(getEnemyDataFromCsv(csvFiles[i]),option);
					}
				}
				long end = System.currentTimeMillis();
				System.out.println("Time elapsed: "+(end-start)/1000.0+" s");
			}
			sc.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}