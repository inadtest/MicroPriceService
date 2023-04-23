package micro.price;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.*;
import java.nio.ByteBuffer;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InetAddress.class, MulticastSocket.class, NetworkInterface.class, ByteBuffer.class})
public class Test2 {
    @Mock
    private MicroPriceCalculator microPriceCalculator;
    @Mock
    private OrderBookImpl1 orderBook;
    @Mock
    private MicroPriceService1 microPriceService;
    @Mock
    private MulticastSocket socket;
    @Mock
    private DatagramPacket packet;
    @Mock
    private InetAddress ip;
    @Mock
    private NetworkInterface networkInterface;

    private static final int BUF_LENGTH = 100;
    private static final String multicastGroupAddress = "224.0.0.1";
    private static final int multicastPort = 1234;
    private static final String networkAdapter = "eth0";

    @BeforeClass
    public static void setUpBeforeClass() {
        System.setProperty("testng.add-opens", "java.base/java.net=ALL-UNNAMED");
    }

    @Test
    public void testStart() throws Exception {
        PowerMockito.whenNew(MulticastSocket.class).withArguments(multicastPort).thenReturn(socket);
        InetAddress inetAddressMock = PowerMockito.mock(InetAddress.class);
        PowerMockito.when(InetAddress.class, "getByName", multicastGroupAddress).thenReturn(inetAddressMock);
        PowerMockito.when(inetAddressMock.getHostAddress()).thenReturn(String.valueOf(ip));
        //PowerMockito.when(InetAddress.getByName(multicastGroupAddress)).thenReturn(ip);
        PowerMockito.when(NetworkInterface.getByName(networkAdapter)).thenReturn(networkInterface);
        PowerMockito.when(ByteBuffer.wrap(packet.getData(), 4, 4).getInt()).thenReturn(1);
        PowerMockito.when(ByteBuffer.wrap(packet.getData(), 8, 2).getShort()).thenReturn((short) 1);
        PowerMockito.when(ByteBuffer.wrap(packet.getData(), 10, 1).get()).thenReturn((byte) 0);
        PowerMockito.when(ByteBuffer.wrap(packet.getData(), 11, 1).get()).thenReturn((byte) 0);
        PowerMockito.when(ByteBuffer.wrap(packet.getData(), 12, 8).getLong()).thenReturn(100000000L);
        PowerMockito.when(ByteBuffer.wrap(packet.getData(), 20, 4).getInt()).thenReturn(100);

        Mockito.when(microPriceCalculator.calculateMicroPrice(orderBook)).thenReturn(100.0);
        Mockito.doNothing().when(orderBook).updateOrderBook(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong(), Mockito.anyInt());
        Mockito.doNothing().when(microPriceService).sendMicroPriceToExchangeA(Mockito.anyInt(), Mockito.anyDouble());
        Mockito.doNothing().when(socket).close();
        Mockito.doNothing().when(packet).setData(Mockito.isNull());
        Mockito.doNothing().when(packet).setLength(BUF_LENGTH);

        microPriceService.start();
        // Verify the method calls
        Mockito.verify(socket).joinGroup(Mockito.any(InetSocketAddress.class), Mockito.any(NetworkInterface.class));
        Mockito.verify(socket, Mockito.atLeastOnce()).receive(packet);
        Mockito.verify(orderBook, Mockito.atLeastOnce()).updateOrderBook(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(microPriceService, Mockito.atLeastOnce()).sendMicroPriceToExchangeA(Mockito.anyInt(), Mockito.anyDouble());
        Mockito.verify(socket).close();
    }
}
