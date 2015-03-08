package com.pengyifan.word2vec;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.RealVector;

public class Word2Vec {

  private final Map<String, RealVector> word2vec;
  private final int vocabSize;
  private final int layer1Size;

  public Word2Vec(
      int vocabSize,
      int layer1Size,
      Map<String, RealVector> word2vec) {
    this.vocabSize = vocabSize;
    this.layer1Size = layer1Size;
    this.word2vec = word2vec;
  }

  public int getVocabSize() {
    return vocabSize;
  }

  public int getLayer1Size() {
    return layer1Size;
  }

  public Set<String> getVocab() {
    return word2vec.keySet();
  }
  
  public boolean contains(String word) {
    return word2vec.containsKey(word);
  }

  public RealVector getRealVector(String word) {
    return word2vec.get(word);
  }
}
