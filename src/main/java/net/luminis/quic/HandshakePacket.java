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

import net.luminis.tls.TlsState;

import java.nio.ByteBuffer;

public class HandshakePacket extends LongHeaderPacket {

    public HandshakePacket(Version quicVersion) {
        super(quicVersion);
    }

    public HandshakePacket(Version quicVersion, byte[] sourceConnectionId, byte[] destConnectionId, int packetNumber, QuicFrame payload, ConnectionSecrets connectionSecrets) {
        super(quicVersion, sourceConnectionId, destConnectionId, packetNumber, payload, connectionSecrets);
    }

    protected byte getPacketType() {
        return (byte) 0xfd;
    }

    @Override
    protected void generateAdditionalFields() {
    }

    @Override
    protected EncryptionLevel getEncryptionLevel() {
        return EncryptionLevel.Handshake;
    }

    @Override
    public void accept(PacketProcessor processor) {
        processor.process(this);
    }

    @Override
    protected void checkPacketType(byte type) {
        if (type != (byte) 0xfd) {
            // Programming error: this method shouldn't have been called if packet is not Initial
            throw new RuntimeException();
        }
    }

    @Override
    protected void parseAdditionalFields(ByteBuffer buffer) {
    }


}