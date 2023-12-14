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

package org.apache.lucene.demo;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;

/*
测试Lucene Directory 写磁盘。

启动命令: java -cp ./lucene-core-8.7.0-SNAPSHOT.jar org.apache.lucene.demo.WriteBenchmark

观测系统指标:
iostat -x -dk 1 50000 /dev/nvme0n1
strace -c -v -f -p 110355
strace -tt -yy -T -v -f -o ./strace.log -s 1024 -p 59500
top

100个线程循环 write + fsync。
blockSize = 1时，CPU瓶颈。 fsync实现: open + close + fsync. 频繁系统调用导致CPU瓶颈。
blockSize = 8K时，磁盘IO瓶颈。

HDD 460 MB/s
SSD 3073 MB/s
 */

public class WriteBenchmark {
  public static void main(String[] args) throws Exception {
    int threadCount = 100;
    int blockSize = 1;
    List<String> paths = Arrays.asList(
//        "/media/disk1/io-test"
        "/media/ssd1/io-test"
    );

    for (int i = 0; i < threadCount; i++) {
      String path = paths.get(i % paths.size());
      final int ii = i;
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            FSDirectory directory = FSDirectory.open(new File(path).toPath());
            String fileName = "file" + ii + ".txt";
            List<String> filesToSync = Collections.singletonList(fileName);
            IndexOutput output = directory.createOutput(fileName, IOContext.DEFAULT);
            byte[] bytes = new byte[blockSize];
            for (int i = 0; i < blockSize; i++) {
              bytes[i] = (byte) (i % 255);
            }
            while (true) {
              output.writeBytes(bytes, 0, blockSize);
              directory.sync(filesToSync);
            }
          } catch (Exception e) {
            System.err.println(e);
          }
        }
      }).start();
    }

    Thread.sleep(Long.MAX_VALUE);
  }
}
