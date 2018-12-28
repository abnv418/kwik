package net.luminis.quic;

import java.nio.ByteBuffer;

public class MaxDataFrame extends QuicFrame {

    private int maxData;

    public MaxDataFrame parse(ByteBuffer buffer, Logger log) {
        if (buffer.get() != 0x04) {
            throw new RuntimeException();  // Would be a programming error.
        }

        maxData = QuicPacket.parseVariableLengthInteger(buffer);

        return this;
    }

    @Override
    public String toString() {
        return "MaxDataFrame[" + maxData + "]";
    }

    @Override
    byte[] getBytes() {
        return new byte[0];
    }
}
