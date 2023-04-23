package micro.price;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(DatagramPacket.class)
public class Test3 {
    @Mock
    private MulticastSocket mockMulticastSocket;
//    @Mock
//    private DatagramPacket mockDatagramPacket;
    @Mock
    private ByteBuffer mockByteBuffer;
    @Mock
    private OrderBookImpl mockOrderBook;
    @Mock
    private MicroPriceCalculator mockMicroPriceCalculator;
    @Mock
    private Socket mockSocket;
    @Mock
    private OutputStream mockOutputStream;

    private MicroPriceService microPriceService;

    private String multicastGroupAddress = "224.0.0.1";
    private int multicastPort = 12345;
    private String networkAdapter = "en0";
    private String tradingSystemExchangeAHost = "localhost";
    private int tradingSystemExchangeAPort = 23456;

    @Before
    public void setUp() throws IOException {
        // MockitoAnnotations.initMocks(this);
        //MockitoAnnotations.openMocks(this);
        assertNotNull(mockMulticastSocket);
        microPriceService = new MicroPriceService(multicastGroupAddress, multicastPort,
                networkAdapter, tradingSystemExchangeAHost, tradingSystemExchangeAPort);

        microPriceService.orderBooks.put(1, mockOrderBook);

        when(mockMulticastSocket.getLocalPort()).thenReturn(multicastPort);
        when(mockByteBuffer.getInt()).thenReturn(4);
        when(mockByteBuffer.getShort()).thenReturn((short) 1);
        doNothing().when(mockOrderBook).updateBid(anyInt(), anyDouble(), anyInt());
        when(mockMicroPriceCalculator.calculateMicroPrice(any(OrderBookImpl.class))).thenReturn(1.0);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
    }

    @Test
    public void testStartService() throws IOException {
        DatagramPacket mockDatagramPacket = mock(DatagramPacket.class);
        byte[] data = new byte[1024];
        int length = 1024;
        InetAddress address = InetAddress.getLocalHost();
        int port = 12345;
//        when(mockDatagramPacket.getData()).thenReturn(data);
//        when(mockDatagramPacket.getLength()).thenReturn(length);
//        when(mockDatagramPacket.getAddress()).thenReturn(address);
//        when(mockDatagramPacket.getPort()).thenReturn(port);
        byte[] buf = new byte[1024];
        when(mockDatagramPacket.getData()).thenReturn(buf);

        microPriceService.startService();

        verify(mockMulticastSocket).setNetworkInterface(any(NetworkInterface.class));
        verify(mockMulticastSocket).joinGroup(any(InetSocketAddress.class), any(NetworkInterface.class));
        verify(mockMulticastSocket).receive(mockDatagramPacket);
        verify(mockOrderBook).updateBid(anyInt(), anyDouble(), anyInt());
        verify(mockMicroPriceCalculator).calculateMicroPrice(any(OrderBookImpl.class));
        verify(mockSocket).getOutputStream();
        verify(mockOutputStream).write(any(byte[].class));
    }

    @Test
    public void testSendMicroPriceToExchangeA() throws IOException {
        microPriceService.sendMicroPriceToExchangeA(1, 1.0);

        verify(mockSocket).getOutputStream();
        verify(mockOutputStream).write(any(byte[].class));
    }
}
