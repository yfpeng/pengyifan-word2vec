package com.pengyifan.word2vec.io;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.StringJoiner;

import com.google.common.primitives.Bytes;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.input.SwappedDataInputStream;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.google.common.collect.Maps;
import com.pengyifan.word2vec.Word2Vec;

public class Word2VecUtils {

  private static final DecimalFormat format = new DecimalFormat("0.######");

  public static void writeToTxtFile(File file, Word2Vec word2vec)
      throws IOException {
    BufferedWriter writer = Files.newBufferedWriter(file.toPath());
    writer.write(String.format(
        "%d %d",
        word2vec.getVocabSize(),
        word2vec.getLayer1Size()));
    writer.newLine();
    for (String key : word2vec.getVocab()) {
      StringJoiner joiner = new StringJoiner(" ");
      joiner.add(key);
      RealVector vector = word2vec.getRealVector(key);
      for (int i = 0; i < vector.getDimension(); i++) {
        joiner.add(format.format(vector.getEntry(i)));
      }
      writer.write(joiner.toString());
      writer.newLine();
    }
    writer.close();
  }

  /**
   * Read word2vec from the bin file. The default byte order is LITTLE_ENDIAN.
   * 
   * @param file the bin file
   * @return word2vec
   * @throws IOException
   */
  public static Word2Vec readFromBinFile(File file)
      throws IOException {
    return readFromBinFile(file, detectByteOrder(file));
  }

  private static ByteOrder detectByteOrder(File file)
      throws IOException {
    FileInputStream fis = new FileInputStream(file);
    BOMInputStream bomIn = new BOMInputStream(fis,
        ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE
        );
    ByteOrder byteOrder = null;
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
    return byteOrder;
  }

  /**
   * Read vectors when their associated words are in the vocabulary. The
   * default byte order is LITTLE_ENDIAN.
   * 
   * @param file word2vec bin file
   * @param vocab vocabulary
   * @return word2vec
   * @throws IOException
   */
  public static Word2Vec readFromBinFile(File file, Set<String> vocab)
      throws IOException {
    return readFromBinFile(file, detectByteOrder(file), vocab);
  }

  /**
   * Read vectors when their associated words are in the vocabulary.
   * 
   * @param file word2vec bin file
   * @param byteOrder byte order of the bin file
   * @param vocab vocabulary
   * @return word2vec
   * @throws IOException
   */
  public static Word2Vec readFromBinFile(File file,
      ByteOrder byteOrder,
      Set<String> vocab)
      throws IOException {
    FileInputStream fis = new FileInputStream(file);
    DataInput in = null;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      in = new DataInputStream(fis);
    } else {
      in = new SwappedDataInputStream(fis);
    }

    List<Byte> list = new ArrayList<Byte>();
    byte c = in.readByte();
    while (c != '\n') {
      list.add(c);
      c = in.readByte();
    }
    String firstLine = new String(Bytes.toArray(list));
    int index = firstLine.indexOf(' ');
    int vocabSize = Integer.parseInt(firstLine.substring(0, index));
    int layer1Size = Integer.parseInt(firstLine.substring(index + 1));

    Map<String, RealVector> map = Maps.newConcurrentMap();
    for (int lineno = 0; lineno < vocabSize; lineno++) {
      list.clear();
      c = in.readByte();
      while (c != ' ') {
        // ignore newlines in front of words (some binary files have newline,
        // some don't)
        if (c != '\n') {
          list.add(c);
        }
        c = in.readByte();
      }
      String word = new String(Bytes.toArray(list));
      double[] d = new double[layer1Size];
      for (int i = 0; i < layer1Size; i++) {
        float f = in.readFloat();
        d[i] = f;
      }
      if (vocab.contains(word)) {
        map.put(word, new ArrayRealVector(d));
      }
    }
    fis.close();
    vocabSize = map.size();
    return new Word2Vec(vocabSize, layer1Size, map);
  }

  /**
   * Read word2vec from the bin file.
   * 
   * @param file the bin file
   * @param byteOrder byte order of the bin file
   * @return word2vec
   * @throws IOException
   */
  public static Word2Vec readFromBinFile(File file, ByteOrder byteOrder)
      throws IOException {
    FileInputStream fis = new FileInputStream(file);
    DataInput in = null;
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      in = new DataInputStream(fis);
    } else {
      in = new SwappedDataInputStream(fis);
    }

    List<Byte> list = new ArrayList<Byte>();
    byte c = in.readByte();
    while (c != '\n') {
      list.add(c);
      c = in.readByte();
    }
    String firstLine = new String(Bytes.toArray(list));
    int index = firstLine.indexOf(' ');
    int vocabSize = Integer.parseInt(firstLine.substring(0, index));
    int layer1Size = Integer.parseInt(firstLine.substring(index + 1));

    Map<String, RealVector> map = Maps.newConcurrentMap();
    for (int lineno = 0; lineno < vocabSize; lineno++) {
      list.clear();
      c = in.readByte();
      while (c != ' ') {
        // ignore newlines in front of words (some binary files have newline,
        // some don't)
        if (c != '\n') {
          list.add(c);
        }
        c = in.readByte();
      }
      String word = new String(Bytes.toArray(list));
      double[] d = new double[layer1Size];
      for (int i = 0; i < layer1Size; i++) {
        float f = in.readFloat();
        d[i] = f;
      }
      map.put(word, new ArrayRealVector(d));
    }
    fis.close();
    return new Word2Vec(vocabSize, layer1Size, map);
  }

  /**
   * Read word2vec from the text file.
   * 
   * @param file the text file
   * @return word2vec
   * @throws IOException
   */
  public static Word2Vec readFromTxtFile(File file)
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
        d[i - 1] = Double.parseDouble(tokens[i]);
      }
      map.put(vocab, new ArrayRealVector(d));
    }
    reader.close();
    return new Word2Vec(vocabSize, layer1Size, map);
  }
}
