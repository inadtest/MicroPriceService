package micro.price;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class Test5 {
//    @Mock
//    private MicroPriceCalculator microPriceCalculator;
//
//    @Mock
//    private DatagramSocket socket;
//
//    @Mock
//    private NetworkInterface networkInterface;

    @InjectMocks
    private MicroPriceService microPriceService;

//    private Map<Integer, OrderBookImpl> orderBooks;

    private MicroPriceCalculator microPriceCalculator = mock(MicroPriceCalculator.class);
    private Map<Integer, OrderBookImpl> orderBooks = new HashMap<>();
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress ip;
    private NetworkInterface networkInterface;

    @BeforeEach
    public void setUp() throws Exception {
        socket = mock(DatagramSocket.class);

        // Create a mock DatagramPacket
        byte[] buf = new byte[20];
        packet = new DatagramPacket(buf, buf.length);

        // Create a mock InetAddress
        ip = mock(InetAddress.class);

        // Create a mock NetworkInterface
        networkInterface = mock(NetworkInterface.class);
        doAnswer(invocation -> {
            DatagramPacket receivedPacket = invocation.getArgument(0);
            // Set the necessary values on the received packet
            // You can use the setters or reflection to set the values as needed
            return null; // Return whatever you want to simulate the receive() method
        }).when(socket).receive(any(DatagramPacket.class));

        orderBooks = new HashMap<>();
        microPriceService = new MicroPriceService("224.0.0.1", 12345, "eth0", "localhost", 23456);
        microPriceService.setMicroPriceCalculator(microPriceCalculator);
        microPriceService.setOrderBooks(orderBooks);
    }
    @Test
    public void testStartService() throws IOException {
        byte[] buf = new byte[40];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        buffer.putInt(8); // messageLength
        buffer.putInt(4); // securityId
        buffer.putShort((short) 1); // numberUpdates
        buffer.putInt(1); // level
        buffer.putInt(0); // side
        buffer.putLong(Double.doubleToLongBits(1.0)); // scaledPrice
        buffer.putInt(100); // quantity

        byte[] buf1 = buffer.array();
        DatagramPacket packet = new DatagramPacket(buf1, buf1.length);
        doNothing().when(socket).receive(packet); // configure socket behavior

        OrderBookImpl orderBook = new OrderBookImpl(10);
        orderBooks.put(1, orderBook);

        microPriceService.startService();

    }


}
