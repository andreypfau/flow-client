/**
 * This file is part of Client, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013-2014 Spoutcraft <http://spoutcraft.org/>
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
package org.spoutcraft.client.network.message.play;

import java.util.Arrays;

import org.spoutcraft.client.network.message.ChannelMessage;

public class ChunkDataBulkMessage extends ChannelMessage {
    private static final Channel[] CHANNELS = new Channel[]{Channel.UNIVERSE};
    private final short columnCount;
    private final int compressedDataLength;
    private final boolean hasSkyLight;
    private final byte[] compressedData;
    private final int[] columnXs;
    private final int[] columnZs;
    private final short[] primaryBitMaps;
    private final short[] additionalDataBitMaps;

    public ChunkDataBulkMessage(short columnCount, int compressedDataLength, boolean hasSkyLight, byte[] compressedData, int[] columnXs, int[] columnZs, short[] primaryBitMaps, short[] additionalDataBitMaps) {
        super(CHANNELS);

        this.columnCount = columnCount;
        this.compressedDataLength = compressedDataLength;
        this.hasSkyLight = hasSkyLight;
        this.compressedData = compressedData;
        this.columnXs = columnXs;
        this.columnZs = columnZs;
        this.primaryBitMaps = primaryBitMaps;
        this.additionalDataBitMaps = additionalDataBitMaps;
    }

    public short getColumnCount() {
        return columnCount;
    }

    public int getCompressedDataLength() {
        return compressedDataLength;
    }

    public boolean hasSkyLight() {
        return hasSkyLight;
    }

    public byte[] getCompressedData() {
        return compressedData;
    }

    public int[] getColumnXs() {
        return columnXs;
    }

    public int[] getColumnZs() {
        return columnZs;
    }

    public short[] getPrimaryBitMaps() {
        return primaryBitMaps;
    }

    public short[] getAdditionalDataBitMaps() {
        return additionalDataBitMaps;
    }

    @Override
    public String toString() {
        return "ChunkDataBulkMessage{" +
                "columnCount=" + columnCount +
                ", compressedDataLength=" + compressedDataLength +
                ", hasSkyLight=" + hasSkyLight +
                ", compressedData=" + Arrays.toString(compressedData) +
                ", columnXs=" + Arrays.toString(columnXs) +
                ", columnZs=" + Arrays.toString(columnZs) +
                ", primaryBitMaps=" + Arrays.toString(primaryBitMaps) +
                ", additionalDataBitMaps=" + Arrays.toString(additionalDataBitMaps) +
                '}';
    }
}
