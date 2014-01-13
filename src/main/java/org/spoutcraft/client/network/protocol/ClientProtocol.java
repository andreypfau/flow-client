/**
 * This file is part of Client, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 Spoutcraft <http://spoutcraft.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spoutcraft.client.network.protocol;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.exception.IllegalOpcodeException;
import com.flowpowered.networking.exception.UnknownPacketException;
import com.flowpowered.networking.protocol.Protocol;
import com.flowpowered.networking.protocol.simple.SimpleProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.spoutcraft.client.network.ByteBufUtils;

/**
 * The protocol used for client communication.
 */
public abstract class ClientProtocol extends SimpleProtocol {
    /**
     * The client's default port.
     */
    public static final int DEFAULT_PORT = 25565;
    /**
     * The client's protocol version.
     */
    public static final int VERSION = 4;

    /**
     * Constructs a new client protocol from the name and highest OP code.
     * @param name The name of the client protocol
     * @param highestOpCode The highest op code
     */
    public ClientProtocol(String name, int highestOpCode) {
        super(name, DEFAULT_PORT, highestOpCode + 1);
    }

    @Override
    public Codec<?> readHeader(ByteBuf buf) throws UnknownPacketException {
        int length = -1;
        int opCode = -1;

        try {
            length = ByteBufUtils.readVarInt(buf);
            opCode = ByteBufUtils.readVarInt(buf);
        } catch (IOException e) {
            throw new UnknownPacketException("Unkown packet!", length, opCode);
        }

        try {
            return getCodecLookupService().find(opCode);
        } catch (IllegalOpcodeException e) {
            throw new UnknownPacketException("Unknown packet!", length, opCode);
        }
    }

    @Override
    public ByteBuf writeHeader(Codec<?> codec, ByteBuf data, ByteBuf out) {
        //Length -> opCode -> data
        final int length = data.readableBytes();
        final int opCode = codec.getOpcode();
        //Figure out length of opcode, must be included in total length of packet
        ByteBuf temp = Unpooled.buffer();
        ByteBufUtils.writeVarInt(temp, opCode);
        ByteBufUtils.writeVarInt(out, length + temp.readableBytes());
        ByteBufUtils.writeVarInt(out, opCode);
        return out;
    }
}
