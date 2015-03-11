package com.pengyifan.word2vec.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.pengyifan.word2vec.Word2Vec;

public class Word2VecBinReaderTest {

  @Test
  public void testReadFromBinFile()
      throws IOException {
    URL url = this.getClass().getResource("/tokensModel.bin");
    File file = new File(url.getFile());
    Word2Vec model = Word2VecUtils.readFromBinFile(file);

    // test vocab
    assertTrue(model.contains("</s>"));
    assertTrue(model.contains("lacks"));

    // test vector
    RealVector vec = model.getRealVector("</s>");
    assertEquals(-0.000563, vec.getEntry(model.getLayer1Size() - 1), 0.0001);

    vec = model.getRealVector("lacks");
    assertEquals(0.000084, vec.getEntry(model.getLayer1Size() - 1), 0.0001);
    
    vec = model.getRealVector("desmoplakin");
    assertEquals(0.029389, vec.getEntry(0), 0.0001);
  }
  
  @Test
  public void testReadFromBinFileWithVocab_empty() throws IOException {
    URL url = this.getClass().getResource("/tokensModel.bin");
    File file = new File(url.getFile());
    
    Set<String> vocab = Sets.newHashSet();
    Word2Vec model = Word2VecUtils.readFromBinFile(file, vocab);
    assertEquals(vocab.size(), model.getVocabSize());
    assertTrue(!model.contains("</s>"));
  }
  
  @Test
  public void testReadFromBinFileWithVocab() throws IOException {
    URL url = this.getClass().getResource("/tokensModel.bin");
    File file = new File(url.getFile());
    
    Set<String> vocab = Sets.newHashSet("</s>", "lacks", "desmoplakin");
    Word2Vec model = Word2VecUtils.readFromBinFile(file, vocab);
    assertEquals(vocab.size(), model.getVocabSize());
    
    // test vector
    RealVector vec = model.getRealVector("</s>");
    assertEquals(-0.000563, vec.getEntry(model.getLayer1Size() - 1), 0.0001);

    vec = model.getRealVector("lacks");
    assertEquals(0.000084, vec.getEntry(model.getLayer1Size() - 1), 0.0001);
    
    vec = model.getRealVector("desmoplakin");
    assertEquals(0.029389, vec.getEntry(0), 0.0001);
  }
}
