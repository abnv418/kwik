/*
 * Copyright © 2019 Peter Doornbosch
 *
 * This file is part of Kwik, a QUIC client Java library
 *
 * Kwik is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Kwik is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.luminis.quic;


import java.nio.ByteBuffer;

public class CryptoFrame extends QuicFrame {

    private int offset;
    private int length;
    private byte[] cryptoData;

    public CryptoFrame(byte[] payload) {
        offset = 0;
        cryptoData = payload;
        length = payload.length;
        ByteBuffer frameBuffer = ByteBuffer.allocate(3 * 4 + payload.length);
        frameBuffer.put(encodeVariableLengthInteger(0x18));
        frameBuffer.put(encodeVariableLengthInteger(offset));
        frameBuffer.put(encodeVariableLengthInteger(payload.length));
        frameBuffer.put(payload);

        cryptoData = new byte[frameBuffer.position()];
        frameBuffer.rewind();
        frameBuffer.get(cryptoData);
    }

    public CryptoFrame() {
    }

    public CryptoFrame parse(ByteBuffer buffer, Logger log) {
        log.debug("Parsing Crypto frame");
        if ((buffer.get() & 0xff) != 0x18) {
            throw new RuntimeException();  // Programming error
        }

        offset = QuicPacket.parseVariableLengthInteger(buffer);
        length = QuicPacket.parseVariableLengthInteger(buffer);

        cryptoData = new byte[length];
        buffer.get(cryptoData);
        log.debug("Crypto data [" + offset + "," + length + "]", cryptoData);

        return this;
    }

    @Override
    public String toString() {
        return "CryptoFrame[" + offset + "," + length + "]";
    }

    public byte[] getBytes() {
        return cryptoData;
    }

    public byte[] getCryptoData() {
        return cryptoData;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }
}