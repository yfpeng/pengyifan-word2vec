package com.pengyifan.word2vec.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.StringJoiner;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Precision;
import org.junit.Ignore;
import org.junit.Test;

import com.pengyifan.word2vec.Word2Vec;

public class Word2VecReaderCompareTest {
  
  private final static DecimalFormat format = new DecimalFormat("0.######");

  @Test
  public void test()
      throws IOException {
    URL url = this.getClass().getResource("/tokensModel.bin");
    File binFile = new File(url.getFile());
    Word2VecBinReader binReader = new Word2VecBinReader(binFile);
    Word2Vec binModel = binReader.read();
    binReader.close();

    url = this.getClass().getResource("/tokensModel.txt");
    File txtFile = new File(url.getFile());
    Word2VecTxtReader txtReader = new Word2VecTxtReader(txtFile);
    Word2Vec txtModel = txtReader.read();
    txtReader.close();

    // test vocab
    for (String vocab : txtModel.getVocab()) {
      assertTrue(binModel.contains(vocab));
    }
    for (String vocab : binModel.getVocab()) {
      assertTrue(txtModel.contains(vocab));
    }
    // test vector
    for (String vocab : txtModel.getVocab()) {
      RealVector txtVector = txtModel.getRealVector(vocab);
      RealVector binVector = binModel.getRealVector(vocab);
      testVector(txtVector, binVector, vocab);
    }
  }

  @Test
  @Ignore
  public void test2()
      throws IOException {
    URL url = this.getClass().getResource("/tokensModel.bin");
    File binFile = new File(url.getFile());
    Word2VecBinReader binReader = new Word2VecBinReader(binFile);
    Word2Vec binModel = binReader.read();
    binReader.close();

    url = this.getClass().getResource("/tokensModel.txt");
    File txtFile = new File(url.getFile());
    Word2VecTxtReader txtReader = new Word2VecTxtReader(txtFile);
    Word2Vec txtModel = txtReader.read();
    txtReader.close();

    RealVector txtVector = txtModel.getRealVector("</s>");
    RealVector binVector = binModel.getRealVector("</s>");
    testVector(txtVector, binVector, "</s>");
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
