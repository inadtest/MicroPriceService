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

public class Test6 {
    private MulticastSocket socket;
    private DatagramPacket packet;
   // private OrderProcessor orderProcessor;
    private Map<Integer, OrderBookImpl> orderBooks;
    private MicroPriceCalculator microPriceCalculator;
    //private ExchangeAConnector exchangeAConnector;

    @Before
    public void setUp() throws Exception {
        // Create mocked objects
        socket = mock(MulticastSocket.class);
        packet = mock(DatagramPacket.class);
        orderBooks = new HashMap<>();
        microPriceCalculator = mock(MicroPriceCalculator.class);
        //exchangeAConnector = mock(ExchangeAConnector.class);
        //orderProcessor = new OrderProcessor(orderBooks, microPriceCalculator, exchangeAConnector);
    }


    @Test
    public void testProcess() throws IOException {
        // Prepare test data
        byte[] data = new byte[20];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putInt(16); // messageLength
        buffer.putInt(2); // securityId
        buffer.putShort((short) 1); // numberUpdates
        buffer.putInt(1); // level
        buffer.putInt(0); // side (0 for bid)
        buffer.putLong(1000000000L); // scaledPrice
        buffer.putInt(10); // quantity

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        when(packet.getData()).thenReturn(data);
        when(packet.getLength()).thenReturn(data.length);
        when(packet.getAddress()).thenReturn(InetAddress.getLocalHost());
        when(packet.getPort()).thenReturn(12345);
        when(packet.getOffset()).thenReturn(0);
        doAnswer(invocation -> {
            inputStream.read(data);
            return null;
        }).when(socket).receive(packet);

        // Mock the order book and micro price calculator
        OrderBookImpl orderBook = mock(OrderBookImpl.class);
        when(orderBooks.computeIfAbsent(anyInt(), any())).thenReturn(orderBook);
        when(microPriceCalculator.calculateMicroPrice(eq(orderBook))).thenReturn(100.0);

    }
    }
