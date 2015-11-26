/*
 * Copyright (c) 2015 Uber Technologies, Inc.
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

package com.uber.tchannel.handlers;

import com.uber.tchannel.codecs.MessageCodec;
import com.uber.tchannel.frames.CallFrame;
import com.uber.tchannel.messages.RawRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

public class FrameFragmenterTest {

    private static final int BUFFER_SIZE = 100000;

    @Test
    public void testEncode() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
            new MessageFragmenter()
        );

        // arg1
        byte[] arg1Bytes = new byte[CallFrame.MAX_ARG1_LENGTH];
        new Random().nextBytes(arg1Bytes);
        ByteBuf arg1 = Unpooled.wrappedBuffer(arg1Bytes);

        // arg2
        byte[] arg2Bytes = new byte[BUFFER_SIZE];
        new Random().nextBytes(arg2Bytes);
        ByteBuf arg2 = Unpooled.wrappedBuffer(arg2Bytes);

        // arg 3
        byte[] arg3Bytes = new byte[BUFFER_SIZE];
        new Random().nextBytes(arg3Bytes);
        ByteBuf arg3 = Unpooled.wrappedBuffer(arg3Bytes);

        RawRequest rawRequest = new RawRequest.Builder("some-service", arg1)
            .setArg2(arg2)
            .setArg3(arg3)
            .setId(0)
            .setTimeout(100)
            .build();

        channel.writeOutbound(rawRequest);

        for (int i = 0; i < 4; i++) {
            CallFrame req = (CallFrame) MessageCodec.decode(
                MessageCodec.decode(
                    (ByteBuf) channel.readOutbound()
                )
            );
            req.release();
            assertNotNull(req);
        }

        ByteBuf buf = channel.readOutbound();
        assertNull(buf);

        rawRequest.release();
    }

    @Test
    public void testWriteOutbound() throws Exception {

    }

    @Test
    public void testSendOutbound() throws Exception {

    }

    @Test
    public void testWriteArg() throws Exception {

    }
}
