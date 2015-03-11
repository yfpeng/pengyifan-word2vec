package com.pengyifan.word2vec;

import static com.google.common.base.Preconditions.checkArgument;

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

  /**
   * Returns the vocabulary size.
   * 
   * @return the vocabulary size
   */
  public int getVocabSize() {
    return vocabSize;
  }

  /**
   * Returns the length of vector.
   * 
   * @return the length of vector
   */
  public int getLayer1Size() {
    return layer1Size;
  }

  /**
   * Returns a Set view of the words contained in this word2vec.
   * 
   * @return a Set view of the words contained in this word2vec
   */
  public Set<String> getVocab() {
    return word2vec.keySet();
  }

  /**
   * Returns true if this word2vec contains a mapping for the specified word.
   * 
   * @param word whose presence in this word2vec is to be tested
   * @return true if this word2vec contains a mapping for the specified word
   */
  public boolean contains(String word) {
    return word2vec.containsKey(word);
  }

  /**
   * Returns the vector of the specified word, or null if this word2vec
   * contains no mapping for the word.
   * 
   * @param word whose presence in this word2vec
   * @return Returns the vector of the specified word, or null if this word2vec
   *         contains no mapping for the word
   */
  public RealVector getRealVector(String word) {
    return word2vec.get(word);
  }

  /**
   * Computes the cosine of the angle between two word vectors s and t.
   * 
   * @param s word whose presence in this word2vec
   * @param t word whose presence in this word2vec
   * @return the cosine of the angle between two word vectors s and t
   * @throws IllegalArgumentException if either word is not in this word2vec
   */
  public double cosine(String s, String t) {
    checkArgument(contains(s), "The word2vec doesn't contain word[%s]", s);
    checkArgument(contains(t), "The word2vec doesn't contain word[%s]", t);
    return getRealVector(s).cosine(getRealVector(t));
  }
}
