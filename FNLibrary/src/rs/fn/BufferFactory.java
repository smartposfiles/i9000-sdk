package rs.fn;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Queue;

import android.util.SparseArray;

public class BufferFactory {
	
	public static final int BUFFER_DOCUMENT = 32768;
	public static final int BUFFER_RECORD = 1024;
	
	private static SparseArray<Queue<ByteBuffer>> BUFFERS = new SparseArray<>();

	/**
	 * Получить ByteBuffer указанного размера. 
	 * @param size размер буфера в байтах.
	 * @return ByteBuffer с порядком байт Little Endian
	 */
	public static ByteBuffer allocate(int size) {
		synchronized (BUFFERS) {
			Queue<ByteBuffer> q = BUFFERS.get(size);
			ByteBuffer result = null;
			if (q == null || q.isEmpty()) {
				result = ByteBuffer.allocate(size);
				result.order(ByteOrder.LITTLE_ENDIAN);
			} else
				result = q.poll();
			result.clear();
			return result;
		}
	}

	/**
	 * Освободить ранее занятый ByteBuffer
	 * @param bb - ByteBuffer для освобождения
	 */
	public static void release(ByteBuffer bb) {
		synchronized (BUFFERS) {
			if (bb == null)
				return;
			Queue<ByteBuffer> q = BUFFERS.get(bb.capacity());
			if (q == null) {
				q = new LinkedList<>();
				BUFFERS.put(bb.capacity(), q);
			}
			while (q.size() > 9)
				q.poll();
			q.add(bb);
		}
	}

}
