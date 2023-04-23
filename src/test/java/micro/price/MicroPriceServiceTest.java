package micro.price;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MicroPriceServiceTest {
    private MulticastSocket socket;
    private Map<Integer, OrderBookImpl> orderBooks;
    private MicroPriceCalculator microPriceCalculator;
    private MicroPriceService microPriceService = new MicroPriceService();

    @Before
    public void setUp() throws Exception {
        socket = mock(MulticastSocket.class);
        orderBooks = new HashMap<>();
        microPriceCalculator = mock(MicroPriceCalculator.class);
    }

    @Test
    public void testProcess() throws IOException {
        byte[] data = new byte[1024];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(16); // messageLength
        buffer.putInt(2356); // securityId
        buffer.putShort((short) 1); // numberUpdates
        buffer.putInt(1); // level
        buffer.putInt(0); // side (0 for bid)
        buffer.putLong(2500000000L); // scaledPrice
        buffer.putInt(50000); // quantity

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        DatagramPacket packet = new DatagramPacket(data, data.length);
        packet.setAddress(InetAddress.getLocalHost());
        packet.setPort(12345);
        doAnswer(invocation -> {
            inputStream.read(data);
            return null;
        }).when(socket).receive(packet);
        OrderBookImpl orderBook = new OrderBookImpl(10);

        List<ExchangeAData> microPrices = microPriceService.process(socket, packet);

        assertNotNull(microPrices);
        assertEquals(microPrices.get(0).securityId, 2356);
        assertEquals(microPrices.get(0).scaledMicroPrice, 255.0, 0.0001);
        verify(socket, times(1)).receive(packet);
    }
}
