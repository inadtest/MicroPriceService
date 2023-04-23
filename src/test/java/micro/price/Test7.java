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
import java.util.Map;

import static org.mockito.Mockito.*;

public class Test7 {
    private MulticastSocket socket;
    //private OrderProcessor orderProcessor;
    private Map<Integer, OrderBookImpl> orderBooks;
    private MicroPriceCalculator microPriceCalculator;
    //private ExchangeAConnector exchangeAConnector;
    private MicroPriceService microPriceService = new MicroPriceService();
    @Before
    public void setUp() throws Exception {

        // Create mocked objects
        socket = mock(MulticastSocket.class);
        orderBooks = new HashMap<>();
        microPriceCalculator = mock(MicroPriceCalculator.class);
        //exchangeAConnector = mock(ExchangeAConnector.class);
        //orderProcessor = new OrderProcessor(orderBooks, microPriceCalculator, exchangeAConnector);
    }

    @Test
    public void testProcess() throws IOException {
        // Prepare test data
        byte[] data = new byte[200];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(16); // messageLength
        buffer.putInt(1); // securityId
        buffer.putShort((short) 1); // numberUpdates
        buffer.putInt(1); // level
        buffer.putInt(0); // side (0 for bid)
        buffer.putLong(1000000000L); // scaledPrice
        buffer.putInt(10); // quantity

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        DatagramPacket packet = new DatagramPacket(data, data.length);
        packet.setAddress(InetAddress.getLocalHost());
        packet.setPort(12345);
        //packet.setOffset(0);
        doAnswer(invocation -> {
            inputStream.read(data);
            return null;
        }).when(socket).receive(packet);

        // Mock the order book and micro price calculator
        OrderBookImpl orderBook = new OrderBookImpl(10);
        when(microPriceCalculator.calculateMicroPrice(eq(orderBook))).thenReturn(100.0);

        // Invoke the method under test
        microPriceService.process(socket, packet);

        // Verify the expected method calls and behaviors
        verify(socket, times(1)).receive(packet);
        verify(orderBook, times(1)).updateBid(1, 1.0, 10);
        verify(microPriceCalculator, times(1)).calculateMicroPrice(orderBook);
       // verify(exchangeAConnector, times(1)).sendMicroPriceToExchangeA(1, 100.0);
    }
}
