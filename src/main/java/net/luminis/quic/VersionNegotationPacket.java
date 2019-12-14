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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Version Negotiation Packet as specified by
 * https://tools.ietf.org/html/draft-ietf-quic-transport-16#section-17.4
 */
public class VersionNegotationPacket extends QuicPacket {

    private int packetSize;

    public VersionNegotationPacket() {
        this(Version.getDefault());
    }

    public VersionNegotationPacket(Version quicVersion) {
        this.quicVersion = quicVersion;
    }

    public List<String> getServerSupportedVersions() {
        return serverSupportedVersions;
    }

    List<String> serverSupportedVersions = new ArrayList<>();

    public VersionNegotationPacket parse(ByteBuffer buffer, Logger log) {
        log.debug("Parsing VersionNegotationPacket");
        buffer.get();     // Type

        // https://tools.ietf.org/html/draft-ietf-quic-transport-16#section-17.4:
        // "A Version Negotiation packet ... will appear to be a packet using the long header, but
        //  will be identified as a Version Negotiation packet based on the
        //  Version field having a value of 0."
        int zeroVersion = buffer.getInt();
        if (zeroVersion != 0) {
            throw new ImplementationError();
        }

        byte[] destinationConnectionId;
        byte[] sourceConnectionId;
        int dstConnIdLength = buffer.get();
        destinationConnectionId = new byte[dstConnIdLength];
        buffer.get(destinationConnectionId);

        int srcConnIdLength = buffer.get();
        sourceConnectionId = new byte[srcConnIdLength];
        buffer.get(sourceConnectionId);
        log.debug("Destination connection id", destinationConnectionId);
        log.debug("Source connection id", sourceConnectionId);

        while (buffer.remaining() >= 4) {
            int versionData = buffer.getInt();
            String supportedVersion = parseVersion(versionData);
            if (supportedVersion != null) {
                serverSupportedVersions.add(supportedVersion);
                log.debug("Server supports version " + supportedVersion);
            }
            else {
                serverSupportedVersions.add(String.format("Unknown version %x", versionData));
                log.debug(String.format("Server supports unknown version %x", versionData));
            }
        }

        packetSize = buffer.limit();
        return this;
    }

    private String parseVersion(int versionData) {
        try {
            return Version.parse(versionData).toString();
        } catch (UnknownVersionException e) {
            return null;
        }
    }

    @Override
    protected EncryptionLevel getEncryptionLevel() {
        return null;
    }

    @Override
    public Long getPacketNumber() {
        // Version Negotiation Packet doesn't have a packet number
        return null;
    }


    @Override
    public byte[] generatePacketBytes(long packetNumber, Keys keys) {
        return new byte[0];
    }

    @Override
    public void accept(PacketProcessor processor, Instant time) {
        processor.process(this, time);
    }

    @Override
    public boolean canBeAcked() {
        // https://tools.ietf.org/html/draft-ietf-quic-transport-18#section-17.2.1
        // "A Version Negotiation packet cannot be explicitly acknowledged in an ACK frame by a client."
        return false;
    }

    @Override
    public String toString() {
        return "Packet "
                + "I" + "|"
                + "-" + "|"
                + "V" + "|"
                + (packetSize >= 0? packetSize: ".") + "|"
                + "0" + "  "
                + serverSupportedVersions.stream().collect(Collectors.joining(", "));
    }

}
