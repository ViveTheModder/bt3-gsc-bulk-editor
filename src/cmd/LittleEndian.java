package cmd;
//Little Endian class by ViveTheModder
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LittleEndian 
{
	public static byte[] getByteArrayFromInt(int data)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.asIntBuffer().put(data);
		return bb.array();
	}
	public static int getInt(int data)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.asIntBuffer().put(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}
	public static int getIntFromByteArray(byte[] data)
	{
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}
	public static short getShort(short data)
	{
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.asShortBuffer().put(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getShort();
	}
}
