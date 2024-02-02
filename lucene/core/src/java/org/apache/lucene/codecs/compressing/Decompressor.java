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
package org.apache.lucene.codecs.compressing;


import java.io.IOException;

import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.BytesRef;

/**
 * A decompressor.
 */
public abstract class Decompressor implements Cloneable {

  /** Sole constructor, typically called from sub-classes. */
  protected Decompressor() {}

  /**
   * 假设压缩前的数据为 originalStream，originalLength表示 originalStream的长度。
   * decompress方法 从 in 中解压数据并输出到bytes中，
   * 注意: 调用者最终需要的仅是压缩前的一部分数据，即 originalStream[offset:offset+length]，
   * 不同的压缩算法实现可以根据这一特点来优化: 有的压缩算法可能只需要解压一部分数据，有的压缩算法可能需要解压所有的数据复原originalStream，然后再截取一部分。
   * 解压完后，bytes.length == length
   * Decompress bytes that were stored between offsets <code>offset</code> and
   * <code>offset+length</code> in the original stream from the compressed
   * stream <code>in</code> to <code>bytes</code>. After returning, the length
   * of <code>bytes</code> (<code>bytes.length</code>) must be equal to
   * <code>length</code>. Implementations of this method are free to resize
   * <code>bytes</code> depending on their needs.
   *
   * @param in the input that stores the compressed stream
   * @param originalLength the length of the original data (before compression)
   * @param offset bytes before this offset do not need to be decompressed
   * @param length bytes after <code>offset+length</code> do not need to be decompressed
   * @param bytes a {@link BytesRef} where to store the decompressed data
   */
  public abstract void decompress(DataInput in, int originalLength, int offset, int length, BytesRef bytes) throws IOException;

  @Override
  public abstract Decompressor clone();

}
