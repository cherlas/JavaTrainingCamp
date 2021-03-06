package geektime.nio;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

class Processor implements Callable {
    SelectionKey key;

    public Processor(SelectionKey key) {
        this.key = key;
    }

    @Override
    public Object call() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int count = socketChannel.read(buffer);
        if (count <  0) {
            key.cancel();
            socketChannel.close();

            System.out.println("Received invalide data, close the connection");
            return null;
        }else if(count==0) {
            return null;
        }
            System.out.println("Received message"+new String(buffer.array()));
            System.out.println("current thread"+Thread.currentThread().toString());
        return null;
    }
}
