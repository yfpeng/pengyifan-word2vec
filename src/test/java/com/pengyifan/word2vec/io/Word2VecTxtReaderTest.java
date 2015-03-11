package com.pengyifan.word2vec.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import com.pengyifan.word2vec.Word2Vec;

public class Word2VecTxtReaderTest {

  @Test
  public void testReadFromTxtFile()
      throws IOException {
    URL url = this.getClass().getResource("/tokensModel.txt");
    File file = new File(url.getFile());
    Word2Vec model = Word2VecUtils.readFromTxtFile(file);

    // test vocab
    assertTrue("Does not contain \"</s>\"", model.contains("</s>"));
    assertTrue("Does not contain \"lacks\"", model.contains("lacks"));

    // test vector
    RealVector vec = model.getRealVector("</s>");
    assertEquals(-0.000563, vec.getEntry(model.getLayer1Size() - 1), 0.0001);

    vec = model.getRealVector("lacks");
    assertEquals(0.000084, vec.getEntry(model.getLayer1Size() - 1), 0.0001);
    
    vec = model.getRealVector("desmoplakin");
    assertEquals(0.029389, vec.getEntry(0), 0.0001);
  }
}
