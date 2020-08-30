/*
 * Copyright © 2020 Peter Doornbosch
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

import net.luminis.quic.frame.AckFrame;
import net.luminis.quic.packet.QuicPacket;
import net.luminis.quic.send.SenderV2;

import java.time.Instant;
import java.util.Arrays;

public class GlobalAckGenerator implements FrameProcessor2<AckFrame> {

    private AckGenerator[] ackGenerators;

    public GlobalAckGenerator(SenderV2 sender) {
        ackGenerators = new AckGenerator[PnSpace.values().length];
        Arrays.stream(PnSpace.values()).forEach(pnSpace -> ackGenerators[pnSpace.ordinal()] = new AckGenerator(pnSpace, sender));
    }

    public void packetReceived(QuicPacket packet) {
        if (packet.canBeAcked()) {
            ackGenerators[packet.getPnSpace().ordinal()].packetReceived(packet);
        }
    }

    @Override
    public void process(AckFrame frame, PnSpace pnSpace, Instant timeReceived) {
        ackGenerators[pnSpace.ordinal()].process(frame);
    }

    public AckGenerator getAckGenerator(PnSpace pnSpace) {
        return ackGenerators[pnSpace.ordinal()];
    }
}

