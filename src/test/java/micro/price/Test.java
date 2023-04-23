package micro.price;

import java.nio.ByteBuffer;

public class Test {
    @org.junit.Test
    public void testBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putInt(1234);
        buffer.putLong(10L);
        System.out.println(buffer);
    }
}
