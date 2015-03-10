package com.pengyifan.word2vec.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.StringJoiner;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Precision;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.pengyifan.word2vec.Word2Vec;


public class Word2VecTxtWriterTest {

  private final static DecimalFormat format = new DecimalFormat("0.######");
  
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Test
  public void test() throws IOException {
    URL url = this.getClass().getResource("/tokensModel.txt");
    File file = new File(url.getFile());
    Word2Vec expected = Word2VecUtils.readFromTxtFile(file);
    
    File tmpFile= folder.newFile("myfile.txt");
    Word2VecUtils.writeToTxtFile(tmpFile, expected);
    Word2Vec actual = Word2VecUtils.readFromTxtFile(tmpFile);
    
    // test vocab
    for (String vocab : expected.getVocab()) {
      assertTrue(actual.contains(vocab));
    }
    for (String vocab : actual.getVocab()) {
      assertTrue(expected.contains(vocab));
    }
    // test vector
    for (String vocab : expected.getVocab()) {
      RealVector expectedVector = expected.getRealVector(vocab);
      RealVector actualVector = actual.getRealVector(vocab);
      testVector(expectedVector, actualVector, vocab);
    }
  }

  private void testVector(RealVector txtVector, RealVector binVector,
      String vocab) {
    assertEquals(txtVector.getDimension(), binVector.getDimension());
    for (int i = 0; i < txtVector.getDimension(); i++) {
      double txtD = txtVector.getEntry(i);
      double binD = binVector.getEntry(i);
      if (!Precision.equals(txtD, binD, 0.001)) {
        System.out.printf("two vectors are not same @ %d: %s[%s]\n%s\n%s\n",
            i,
            vocab,
            formatByte(vocab),
            formatRealVector(txtVector),
            formatRealVector(binVector));
        System.exit(1);
      }
    }
  }
  
  private String formatByte(String s) {
    StringBuilder sb = new StringBuilder();
    byte[] bs = s.getBytes();
    for(byte b: bs) {
      sb.append(Integer.toHexString(b));
    }
    return sb.toString();
  }
  
  private String formatRealVector(RealVector vector) {
    StringJoiner joiner = new StringJoiner("; ");
    for (int i = 0; i < vector.getDimension(); i++) {
      double d = vector.getEntry(i);
      joiner.add(format.format(d));
    }
    return "{" + joiner + "}";
  }
}
