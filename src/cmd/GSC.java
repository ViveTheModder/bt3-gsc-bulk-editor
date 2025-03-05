package cmd;
//GSC Object by ViveTheModder
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GSC 
{
	private RandomAccessFile gsc;
	private String fileName;
	private static int[][] enemyDataPositions;
	private static int numEnemies;
	private static final int CHARA_HEADER = 0x010E0C00, TEAM_HEADER = 0x01020B00;
	private static final int GSDT = 0x47534454, SCENE_4 = 0xFCFFFFFF;
	public GSC(File f)
	{
		try 
		{
			gsc = new RandomAccessFile(f,"rw");
			fileName = f.getName();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public String getFileName() 
	{
		return fileName;
	}
	public String[] getEnemyData() throws IOException
	{
		int curr, fileSize=(int)gsc.length(), sceneSize=0, pos=0;
		while (pos!=fileSize)
		{
			curr = gsc.readInt();
			if (curr==SCENE_4)
			{
				pos-=4; gsc.seek(pos);
				sceneSize = LittleEndian.getInt(gsc.readInt());
				pos+=8; gsc.seek(pos);
				break;
			}
			pos+=4;
		}
		
		int endOfScene = pos+sceneSize, gsdtPos = getGsdtPos();
		int charaCnt=0, enemyCnt=0, teamCnt=0;
		int[] numBytesToSkip = {5,12,4}, numTeammates = new int[2];
		int[][] charaData=null;
		String[] charaNames=Main.getAnyDataFromCsv(0), enemyData=null;
		
		gsc.seek(pos);
		while (pos<endOfScene)
		{
			curr = gsc.readInt();
			pos+=4;
			if (curr==TEAM_HEADER)
			{
				pos+=5; gsc.seek(pos); //skip team index
				short numPlayers = gsc.readShort();
				pos+=3; gsc.seek(pos);
				numTeammates[teamCnt] = getIntFromIndex(LittleEndian.getShort(numPlayers),gsdtPos);
				gsc.seek(pos);
				if (teamCnt==1) 
				{
					numEnemies = numTeammates[teamCnt];
					enemyData = new String[numTeammates[teamCnt]];
					charaData = new int[numEnemies][3];
					enemyDataPositions = new int[numTeammates[teamCnt]][3];
				}
				teamCnt++;
			}
			else if (curr==CHARA_HEADER)
			{
				charaCnt++;
				if (charaCnt<=numTeammates[0]) continue;
				else
				{
					if (enemyCnt<enemyData.length) 
					{
						enemyData[enemyCnt]="";
						for (int i=0; i<numBytesToSkip.length; i++)
						{
							pos+=numBytesToSkip[i]; gsc.seek(pos);
							enemyDataPositions[enemyCnt][i] = pos;
							charaData[enemyCnt][i] = getIntFromIndex(LittleEndian.getShort(gsc.readShort()),gsdtPos);
							gsc.seek(pos);
						}
						pos+=3; gsc.seek(pos);
						enemyData[enemyCnt] += charaNames[charaData[enemyCnt][0]]+","+charaData[enemyCnt][1]+","+Main.itemNames[charaData[enemyCnt][2]-137];
					}
					enemyCnt++;
				}
			}
		}
		return enemyData;
	}
	private int getGsdtPos() throws IOException
	{
		int curr,fileSize=(int)gsc.length(),pos=0;
		while (pos!=fileSize)
		{
			curr = gsc.readInt();
			if (curr==GSDT) break;
			pos+=4; gsc.seek(pos);
		}
		return pos+16;
	}
	private byte[] getGsdt(int gsdtPos) throws IOException
	{
		byte[] gsdt;
		gsc.seek(gsdtPos-8);
		int gsdtSize = LittleEndian.getInt(gsc.readInt());
		gsdt = new byte[gsdtSize];
		gsc.seek(gsdtPos);
		gsc.read(gsdt);
		return gsdt;
	}
	private int getIndexFromInt(byte[] gsdt, int data)
	{
		byte[] floatOrInt = new byte[4];
		int index=-1;
		for (int i=0; i<gsdt.length; i+=4)
		{
			System.arraycopy(gsdt, i, floatOrInt, 0, 4);
			if (LittleEndian.getIntFromByteArray(floatOrInt)==data)
			{
				index=i/4; break;
			}
		}
		return index;
	}
	private int getIntFromIndex(short index, int gsdtPos) throws IOException
	{
		gsc.seek(gsdtPos+(4*index));
		return LittleEndian.getInt(gsc.readInt());
	}
	private int getPosOfLastOccupiedGsdtData(byte[] gsdt)
	{
		byte[] floatOrInt = new byte[4];
		int pos=0;
		for (int i=gsdt.length-4; i>0; i-=4)
		{
			System.arraycopy(gsdt, i, floatOrInt, 0, 4);
			if (LittleEndian.getIntFromByteArray(floatOrInt)!=0)
			{
				pos=i; break;
			}
		}
		return pos;
	}
	//a shorter version of getEnemyData() that is used in case no prior reading operations were performed
	private int[][] getEnemyDataPositions() throws IOException
	{
		int curr, fileSize=(int)gsc.length(), sceneSize=0, pos=0;
		while (pos!=fileSize)
		{
			curr = gsc.readInt();
			if (curr==SCENE_4)
			{
				pos-=4; gsc.seek(pos);
				sceneSize = LittleEndian.getInt(gsc.readInt());
				pos+=8; gsc.seek(pos);
				break;
			}
			pos+=4;
		}
		
		int endOfScene = pos+sceneSize, gsdtPos = getGsdtPos();
		int charaCnt=0, enemyCnt=0, teamCnt=0;
		int[] numBytesToSkip = {5,12,4}, numTeammates = new int[2];
		int[][] enemyDataPos=null;
		
		gsc.seek(pos);
		while (pos<endOfScene)
		{
			curr = gsc.readInt();
			pos+=4;
			if (curr==TEAM_HEADER)
			{
				pos+=5; gsc.seek(pos); //skip team index
				short numPlayers = gsc.readShort();
				pos+=3; gsc.seek(pos);
				numTeammates[teamCnt] = getIntFromIndex(LittleEndian.getShort(numPlayers),gsdtPos);
				gsc.seek(pos);
				if (teamCnt==1) 
				{
					numEnemies = numTeammates[teamCnt];
					enemyDataPos = new int[numTeammates[teamCnt]][3];
				}
				teamCnt++;
			}
			else if (curr==CHARA_HEADER)
			{
				charaCnt++;
				if (charaCnt<=numTeammates[0]) continue;
				else
				{
					if (enemyCnt<numTeammates[1]) 
					{
						for (int i=0; i<numBytesToSkip.length; i++)
						{
							pos+=numBytesToSkip[i]; gsc.seek(pos);
							enemyDataPos[enemyCnt][i] = pos;
						}
						pos+=3; gsc.seek(pos);
					}
					enemyCnt++;
				}
			}
		}
		return enemyDataPos;
	}
	public void writeEnemyDataToCsv(String[] enemyData) throws IOException
	{
		File output = new File(Main.OUT_PATH+"output-"+fileName+".csv");
		FileWriter writer = new FileWriter(output);
		writer.write("chara-name,com-diff-lvl,strat-item\n");
		for (String enemy: enemyData) writer.append(enemy+"\n");
		writer.close();
	}
	public void writeEnemyDataToGsc(int[][] charaData, String option) throws IOException
	{
		if (enemyDataPositions==null) enemyDataPositions = getEnemyDataPositions();
		int gsdtPos = getGsdtPos();
		byte[] eofc = new byte[32], gsdt = getGsdt(gsdtPos), newLine = new byte[16];
		for (int i=0; i<charaData.length; i++)
		{
			for (int j=0; j<charaData[0].length; j++)
			{
				int data = charaData[i][j];
				int index = getIndexFromInt(gsdt,data);
				boolean found = index>=0;
				//check if the data is present in the GSDT at all, otherwise add it there
				if (!found)
				{
					int lastOccupiedPos = getPosOfLastOccupiedGsdtData(gsdt);
					if (lastOccupiedPos==gsdt.length-4) //check if GSDT is occupied
					{
						index=gsdt.length/4;
						gsc.seek(gsdtPos+gsdt.length); //go to EOFC
						gsc.read(eofc);
						gsc.seek(gsdtPos+gsdt.length); //go to EOFC again
						System.arraycopy(LittleEndian.getByteArrayFromInt(data), 0, newLine, 0, 4);
						gsc.write(newLine);
						gsc.write(eofc);
						gsc.seek(gsdtPos-8);
						gsc.writeInt(LittleEndian.getInt(gsdt.length+16)); //change GSDT size
						gsc.seek(8);
						int gscfSize = LittleEndian.getInt(gsc.readInt());
						gsc.seek(8);
						gsc.writeInt(LittleEndian.getInt(gscfSize+16));
					}
					else //otherwise, overwrite the last free slot in the GSDT
					{
						index=(lastOccupiedPos+4)/4;
						gsc.seek(gsdtPos+lastOccupiedPos+4);
						gsc.writeInt(LittleEndian.getInt(data));
					}
					gsdt = getGsdt(gsdtPos); //reset GSDT in case there are changes made to it
				}
				gsc.seek(enemyDataPositions[i][j+1]); //skip position of character ID index
				gsc.writeShort(LittleEndian.getShort((short)index));
			}
		}
		gsc.close();
		if (option.equals("w")) enemyDataPositions=null; //this reset is needed in case the reading option (R) is not provided
	}
}