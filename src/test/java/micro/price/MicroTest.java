package micro.price;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.*;

public class MicroTest {
    @Mock
    private MicroPriceCalculator mockMicroPriceCalculator;
    private MicroPriceService1 microPriceService;
    private String multicastGroupAddress;
    private int multicastPort;
    private String networkAdapter;
    private String tradingSystemExchangeAHost;
    private int tradingSystemExchangeAPort;
    private OrderBookImpl1 mockOrderBook;

    @BeforeEach
    public void setUp() {
        multicastGroupAddress = "224.0.0.1";
        multicastPort = 12345;
        networkAdapter = "eth0";
        tradingSystemExchangeAHost = "localhost";
        tradingSystemExchangeAPort = 5678;
        mockMicroPriceCalculator = mock(MicroPriceCalculator.class);
        mockOrderBook = mock(OrderBookImpl1.class);
        microPriceService = new MicroPriceService1(multicastGroupAddress, multicastPort, networkAdapter,
                tradingSystemExchangeAHost, tradingSystemExchangeAPort);
        microPriceService.setMicroPriceCalculator(mockMicroPriceCalculator);
        microPriceService.setOrderBook(mockOrderBook);
    }

 //   @Test
//    public void testStart() throws IOException {
//        MulticastSocket mockSocket = mock(MulticastSocket.class);
//       // DatagramSocket mockSocket = mock(DatagramSocket.class);
//        DatagramPacket mockPacket = mock(DatagramPacket.class);
//
//        InetAddress mockIp = mock(InetAddress.class);
//        NetworkInterface mockNetworkInterface = mock(NetworkInterface.class);
//        when(mockIp.getByName(multicastGroupAddress)).thenReturn(mockIp);
//        when(mockNetworkInterface.getByName(networkAdapter)).thenReturn(mockNetworkInterface);
//        //when(mockSocket.receive(mockPacket)).thenThrow(new IOException());
//        doThrow(new IOException()).when(mockSocket).receive(mockPacket);
//        mockSocket.receive(mockPacket);
//        when(mockPacket.getData()).thenReturn(new byte[10]);
//        when(mockPacket.getLength()).thenReturn(10);
//        when(mockPacket.getAddress()).thenReturn(mockIp);
//        when(mockPacket.getPort()).thenReturn(multicastPort);
//        microPriceService.start();
//        verify(mockSocket).setNetworkInterface(mockNetworkInterface);
//        verify(mockSocket).joinGroup(Mockito.any(InetSocketAddress.class), eq(mockNetworkInterface));
//        verify(mockSocket, Mockito.atLeastOnce()).receive(mockPacket);
//        verify(mockMicroPriceCalculator).calculateMicroPrice(mockOrderBook);
//        verify(mockOrderBook).updateOrderBook(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong(), Mockito.anyInt());
//        verify(mockPacket).setData(Mockito.any(byte[].class));
//        verify(mockPacket).setLength(eq(1024));
//        verify(mockSocket).close();
//        verify(mockPacket).setData(null);
//    }

    @Test
    public void testStart() throws IOException {
        MulticastSocket mockSocket = mock(MulticastSocket.class);
        DatagramPacket mockPacket = mock(DatagramPacket.class);
        OrderBookImpl1 orderBook = new OrderBookImpl1(0);
        InetAddress mockIp = mock(InetAddress.class);
        NetworkInterface mockNetworkInterface = mock(NetworkInterface.class);

        // Mock the behavior of mockPacket
        when(mockPacket.getData()).thenReturn(new byte[10]);
        when(mockPacket.getLength()).thenReturn(10);
        when(mockPacket.getAddress()).thenReturn(mockIp);
        when(mockPacket.getPort()).thenReturn(multicastPort);

        when(ByteBuffer.wrap(mockPacket.getData(), 4, 4).getInt()).thenReturn(1);
        when(ByteBuffer.wrap(mockPacket.getData(), 8, 2).getShort()).thenReturn((short) 1);
        when(ByteBuffer.wrap(mockPacket.getData(), 10, 1).get()).thenReturn((byte) 0);
        when(ByteBuffer.wrap(mockPacket.getData(), 11, 1).get()).thenReturn((byte) 0);
        when(ByteBuffer.wrap(mockPacket.getData(), 12, 8).getLong()).thenReturn(100000000L);
        when(ByteBuffer.wrap(mockPacket.getData(), 20, 4).getInt()).thenReturn(100);
        when(mockMicroPriceCalculator.calculateMicroPrice(any())).thenReturn(100.0);
        doNothing().when(orderBook).updateOrderBook(anyInt(), anyInt(), anyLong(), anyInt());
        doNothing().when(microPriceService).sendMicroPriceToExchangeA(anyInt(), anyDouble());
        doNothing().when(mockSocket).close();
        doNothing().when(mockPacket).setData(null);
        doNothing().when(mockPacket).setLength(1024);

        microPriceService.start();

        verify(mockSocket).joinGroup(any(InetSocketAddress.class), any(NetworkInterface.class));
        verify(mockSocket, atLeastOnce()).receive(mockPacket);
        verify(orderBook, atLeastOnce()).updateOrderBook(anyInt(), anyInt(), anyLong(), anyInt());
        verify(mockMicroPriceCalculator, atLeastOnce()).calculateMicroPrice(orderBook);
        verify(microPriceService, atLeastOnce()).sendMicroPriceToExchangeA(anyInt(), anyDouble());
        verify(mockSocket).close();
        verify(mockPacket).setData(null);
        verify(mockPacket).setLength(1024);
        // Mock the behavior of mockSocket
        doThrow(new IOException()).when(mockSocket).receive(mockPacket);
        microPriceService.start();



//        verify(mockSocket).setNetworkInterface(mockNetworkInterface);
//        verify(mockSocket).joinGroup(Mockito.any(InetSocketAddress.class), eq(mockNetworkInterface));
//        verify(mockSocket, Mockito.atLeastOnce()).receive(mockPacket);
//        verify(mockMicroPriceCalculator).calculateMicroPrice(mockOrderBook);
//        verify(mockOrderBook).updateOrderBook(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong(), Mockito.anyInt());
//        verify(mockPacket).setData(Mockito.any(byte[].class));
//        verify(mockPacket).setLength(eq(1024));
//        verify(mockSocket).close();
//        verify(mockPacket).setData(null);
    }


    @Test
    public void testSendMicroPriceToExchangeA() throws IOException {
        Socket mockSocket = mock(Socket.class);
        DataOutputStream mockOutputStream = mock(DataOutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        microPriceService.sendMicroPriceToExchangeA(1, 1.23);
        verify(mockOutputStream).writeInt(eq(1));
        verify(mockOutputStream).writeLong(eq(1230000000L));
        verify(mockOutputStream).flush();
        verify(mockSocket).close();
    }
}
