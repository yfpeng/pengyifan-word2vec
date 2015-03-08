package com.pengyifan.word2vec.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.input.SwappedDataInputStream;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.google.common.collect.Maps;
import com.pengyifan.word2vec.Word2Vec;

public class Word2VecBinReader implements Closeable {

  private final File file;
  private final ByteOrder byteOrder;

  public Word2VecBinReader(File file, ByteOrder byteOrder) {
    this.file = file;
    this.byteOrder = byteOrder;
  }

  public Word2VecBinReader(File file)
      throws IOException {
    this.file = file;

    FileInputStream fis = new FileInputStream(file);
    BOMInputStream bomIn = new BOMInputStream(fis,
        ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE
        );
    if (bomIn.hasBOM() == false) {
      byteOrder = ByteOrder.nativeOrder();
    } else {
      ByteOrderMark mark = bomIn.getBOM();
      if (mark == ByteOrderMark.UTF_16BE) {
        byteOrder = ByteOrder.BIG_ENDIAN;
      } else if (mark == ByteOrderMark.UTF_16LE) {
        byteOrder = ByteOrder.LITTLE_ENDIAN;
      } else {
        bomIn.close();
        throw new IOException(String.format("Cannot detect encoding %s", file));
      }
    }
    bomIn.close();
  }

  // public Word2VecBinReader(File file)
  // throws IOException {
  // this.file = file;
  //
  // FileInputStream fis = new FileInputStream(file);
  // UniversalDetector detector = new UniversalDetector(null);
  // int nread;
  // byte[] buf = new byte[4096];
  // while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
  // detector.handleData(buf, 0, nread);
  // }
  // detector.dataEnd();
  // fis.close();
  //
  // String encoding = detector.getDetectedCharset();
  // System.out.println(encoding);
  //
  // if (encoding == null) {
  // byteOrder = ByteOrder.nativeOrder();
  // } else if (encoding.equals("UTF-16BE")) {
  // byteOrder = ByteOrder.BIG_ENDIAN;
  // } else if (encoding.equals("UTF-16LE")) {
  // byteOrder = ByteOrder.LITTLE_ENDIAN;
  // } else {
  // throw new IOException(String.format("Cannot detect encoding %s", file));
  // }
  // }

  public Word2Vec read()
      throws IOException {
    FileInputStream fis = new FileInputStream(file);
    DataInput in = null;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      in = new DataInputStream(fis);
    } else {
      in = new SwappedDataInputStream(fis);
    }

    StringBuilder sb = new StringBuilder();
    char c = (char) in.readByte();
    while (c != '\n') {
      sb.append(c);
      c = (char) in.readByte();
    }
    String firstLine = sb.toString();
    int index = firstLine.indexOf(' ');
    int vocabSize = Integer.parseInt(firstLine.substring(0, index));
    int layer1Size = Integer.parseInt(firstLine.substring(index + 1));

    Map<String, RealVector> map = Maps.newConcurrentMap();
    for (int lineno = 0; lineno < vocabSize; lineno++) {
      sb = new StringBuilder();
      c = (char) in.readByte();
      while (c != ' ') {
        // ignore newlines in front of words (some binary files have newline,
        // some don't)
        if (c != '\n') {
          sb.append(c);
        }
        c = (char) in.readByte();
      }
      String vocab = sb.toString();
      double[] d = new double[layer1Size];
      for (int i = 0; i < layer1Size; i++) {
        float f = in.readFloat();
        d[i] = f;
      }
      map.put(vocab, new ArrayRealVector(d));
    }
    fis.close();
    return new Word2Vec(vocabSize, layer1Size, map);
  }

  @Override
  public void close()
      throws IOException {
    // TODO Auto-generated method stub

  }
}
