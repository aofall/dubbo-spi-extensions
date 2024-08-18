/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.serialize.fury;

import org.apache.dubbo.common.serialize.ObjectInput;
import org.apache.dubbo.common.serialize.ObjectOutput;
import org.apache.dubbo.common.serialize.fury.dubbo.FuryObjectInput;
import org.apache.dubbo.common.serialize.fury.dubbo.FuryObjectOutput;
import org.apache.dubbo.common.serialize.fury.dubbo.FurySerialization;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.dubbo.common.serialize.fury.dubbo.FurySerialization.FURY_SERIALIZATION_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class FurySerializationTest {
    private FurySerialization furySerialization;

    @BeforeEach
    public void setUp() {
        this.furySerialization = new FurySerialization();
    }

    @Test
    public void testContentType() {
        assertThat(furySerialization.getContentType(), is("fury/consistent"));
    }

    @Test
    public void testContentTypeId() {
        assertThat(furySerialization.getContentTypeId(), is(FURY_SERIALIZATION_ID));
    }

    @Test
    public void testObjectOutput() throws IOException {
        ObjectOutput objectOutput = furySerialization.serialize(null, mock(OutputStream.class));
        assertThat(objectOutput, Matchers.<ObjectOutput>instanceOf(FuryObjectOutput.class));
    }

    @Test
    public void testObjectInput() throws IOException {
        ObjectInput objectInput = furySerialization.deserialize(null, mock(InputStream.class));
        assertThat(objectInput, Matchers.<ObjectInput>instanceOf(FuryObjectInput.class));
    }
}