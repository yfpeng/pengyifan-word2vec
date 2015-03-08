package com.pengyifan.word2vec.io;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.google.common.collect.Maps;
import com.pengyifan.word2vec.Word2Vec;

public class Word2VecTxtReader implements Closeable {

  private File file;

  public Word2VecTxtReader(File file) {
    this.file = file;
  }

  public Word2Vec read()
      throws IOException {
    LineNumberReader reader = new LineNumberReader(
        Files.newBufferedReader(file.toPath()));
    String line = reader.readLine();
    if (line == null) {
      throw new IOException(String.format("Cannot read first line: %s", file));
    }

    int index = line.indexOf(' ');
    int vocabSize = Integer.parseInt(line.substring(0, index));
    int layer1Size = Integer.parseInt(line.substring(index + 1));
    double[] d = new double[layer1Size];
    
    Map<String, RealVector> map = Maps.newConcurrentMap();
    while ((line = reader.readLine()) != null) {
      String[] tokens = line.split(" ");
      String vocab = tokens[0];
      checkArgument(
          layer1Size == tokens.length - 1,
          "For file '%s', on line %s, layer size is %s, but found %s values in the word vector",
          file,
          reader.getLineNumber(),
          layer1Size,
          tokens.length - 1);
      for (int i = 1; i < tokens.length; i++) {
        d[i-1] = Double.parseDouble(tokens[i]);
      }
      map.put(vocab, new ArrayRealVector(d));
    }
    return new Word2Vec(vocabSize, layer1Size, map);
  }

  @Override
  public void close()
      throws IOException {
    // TODO Auto-generated method stub

  }
}
