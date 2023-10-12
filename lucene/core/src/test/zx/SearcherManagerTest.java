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

package zx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.LuceneTestCase;

/**
 * @author zouxiang <zouxiang@kuaishou.com>
 * Created on 2023-10-10
 */
public class SearcherManagerTest extends LuceneTestCase {
  public void test1() throws IOException {
    File dir = new File("/Users/xiangzou/test/lucene-dir/test2");
    Path path = dir.toPath();
    Directory directory = FSDirectory.open(path);
    for (String file : directory.listAll()) {
      directory.deleteFile(file);
    }

    IndexWriterConfig config = new IndexWriterConfig();
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter indexWriter = new IndexWriter(directory, config);
    indexWriter.addDocument(createDocument(0));
    indexWriter.flush(); // segment 0
    indexWriter.addDocument(createDocument(1));
    indexWriter.flush(); // segment 1
    indexWriter.commit();

    // 1. 增加文档
    // indexWriter.addDocument(doc);
    // 2. 删除文档
    // indexWriter.deleteDocuments(new Term("id", String.valueOf(0)));
    // 3. 更新文档。先删，再加
    // indexWriter.updateDocument(new Term("id", "0"), doc);
    // 4. 更新doc_values
    // indexWriter.updateDocValues(new Term("id", "0"), field);
    // 5. 软删除
    // indexWriter.softUpdateDocument(...);

    indexWriter.deleteDocuments(new Term("id", String.valueOf(0)));

    DirectoryReader reader = DirectoryReader.open(indexWriter);
    IndexSearcher searcher = new IndexSearcher(reader);
    int count = searcher.count(new MatchAllDocsQuery());
    System.out.println("文档总数: " + count);
  }

  private Document createDocument(int id) {
    Document doc = new Document();
    Field idFiled = new TextField("id", String.valueOf(id), Field.Store.YES);
    doc.add(idFiled);
    return doc;
  }
}
