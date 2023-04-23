package micro.price;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.*;

public class test1 {

    private MicroPriceService1 microPriceService;
    private MicroPriceCalculator microPriceCalculator;
    private OrderBookImpl1 orderBook;
    private String multicastGroupAddress = "224.0.0.1";
    private int multicastPort = 12345;
    private String networkAdapter = "eth0";
    private String tradingSystemExchangeAHost = "localhost";
    private int tradingSystemExchangeAPort = 8080;
    private int BUF_LENGTH = 1024;

    @BeforeEach
    public void setUp() {
        microPriceCalculator = mock(MicroPriceCalculator.class);
        orderBook = mock(OrderBookImpl1.class);
        microPriceService = new MicroPriceService1(multicastGroupAddress, multicastPort, networkAdapter,
                tradingSystemExchangeAHost, tradingSystemExchangeAPort);
        microPriceService.setMicroPriceCalculator(microPriceCalculator);
        microPriceService.setOrderBook(orderBook);
    }

    @Test
    public void testStart() throws IOException {
        MulticastSocket socket = mock(MulticastSocket.class);
        DatagramPacket packet = mock(DatagramPacket.class);
        InetAddress ip = mock(InetAddress.class);
        NetworkInterface networkInterface = mock(NetworkInterface.class);

        when(InetAddress.getByName(multicastGroupAddress)).thenReturn(ip);
        when(new MulticastSocket(multicastPort)).thenReturn(socket);
        when(NetworkInterface.getByName(networkAdapter)).thenReturn(networkInterface);
        when(packet.getData()).thenReturn(new byte[BUF_LENGTH]);
     //   when(socket.receive(packet)).thenReturn(null);
        when(ByteBuffer.wrap(packet.getData(), 4, 4).getInt()).thenReturn(1);
        when(ByteBuffer.wrap(packet.getData(), 8, 2).getShort()).thenReturn((short) 1);
        when(ByteBuffer.wrap(packet.getData(), 10, 1).get()).thenReturn((byte) 0);
        when(ByteBuffer.wrap(packet.getData(), 11, 1).get()).thenReturn((byte) 0);
        when(ByteBuffer.wrap(packet.getData(), 12, 8).getLong()).thenReturn(100000000L);
        when(ByteBuffer.wrap(packet.getData(), 20, 4).getInt()).thenReturn(100);
        when(microPriceCalculator.calculateMicroPrice(orderBook)).thenReturn(100.0);
        doNothing().when(orderBook).updateOrderBook(anyInt(), anyInt(), anyLong(), anyInt());
        doNothing().when(microPriceService).sendMicroPriceToExchangeA(anyInt(), anyDouble());
        doNothing().when(socket).close();
        doNothing().when(packet).setData(null);
        doNothing().when(packet).setLength(BUF_LENGTH);

        microPriceService.start();

        verify(socket).joinGroup(any(InetSocketAddress.class), any(NetworkInterface.class));
        verify(socket, atLeastOnce()).receive(packet);
        verify(orderBook, atLeastOnce()).updateOrderBook(anyInt(), anyInt(), anyLong(), anyInt());
        verify(microPriceCalculator, atLeastOnce()).calculateMicroPrice(orderBook);
        verify(microPriceService, atLeastOnce()).sendMicroPriceToExchangeA(anyInt(), anyDouble());
        verify(socket).close();
        verify(packet).setData(null);
        verify(packet).setLength(BUF_LENGTH);
    }
}
