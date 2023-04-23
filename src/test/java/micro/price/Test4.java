package micro.price;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DatagramPacket.class, MicroPriceService.class })
public class Test4 {

    private MicroPriceService microPriceService;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress ip;
    private NetworkInterface networkInterface;
    private MicroPriceCalculator microPriceCalculator;
    private Map<Integer, OrderBookImpl> orderBooks;

    @Before
    public void setUp() throws Exception {
        microPriceCalculator = mock(MicroPriceCalculator.class);
        orderBooks = new HashMap<>();
        microPriceService = new MicroPriceService("224.0.0.1", 12345, "en0", "localhost", 23456);
        microPriceService.microPriceCalculator = microPriceCalculator;
        microPriceService.orderBooks = orderBooks;

        socket = Mockito.mock(DatagramSocket.class);
        packet = mock(DatagramPacket.class);
        ip = mock(InetAddress.class);
        networkInterface = mock(NetworkInterface.class);

        PowerMockito.whenNew(DatagramSocket.class).withParameterTypes(int.class).withArguments(anyInt())
                .thenReturn(socket);
        PowerMockito.whenNew(DatagramPacket.class).withParameterTypes(byte[].class, int.class, InetAddress.class, int.class)
                .withArguments(any(byte[].class), anyInt(), any(InetAddress.class), anyInt()).thenReturn(packet);
    }

    @Test
    public void testStartService() throws IOException {
       //Mockito.doNothing().when(socket).receive(Mockito.any(DatagramPacket.class));
        PowerMockito.doNothing().when(socket).receive(packet);



        byte[] buf = new byte[20];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        buffer.putInt(16); // messageLength
        buffer.putInt(1); // securityId
        buffer.putShort((short) 1); // numberUpdates
        buffer.putInt(1); // level
        buffer.putInt(0); // side
        buffer.putLong(Double.doubleToLongBits(1.0)); // scaledPrice
        buffer.putInt(100); // quantity

        byte[] buf1 = buffer.array();
        when(packet.getData()).thenReturn(buf1);
        when(packet.getLength()).thenReturn(buf1.length);

        OrderBookImpl orderBook = new OrderBookImpl(10);
        orderBooks.put(1, orderBook);
        microPriceService.startService();

        // Add your assertions here
//        verify(socket).setNetworkInterface(networkInterface);
//        verify(socket).joinGroup(any(SocketAddress.class), eq(networkInterface));
//        verify(socket).receive(packet);
//        verify(microPriceCalculator).calculateMicroPrice(orderBook);
//        verify(socket).getOutputStream();
//        verify(socket).close();
    }
}
