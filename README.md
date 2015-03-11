# pengyifan-word2vec

This porject contains data structure of word2vec, and io functions to read/write
word2vec from/to either text files or bin files. 

This project doesn't contain the algorithm for computing vector representations
of words.

### Getting started

Word2Vec is basically a map from word to vectors. To read the word2vec model
form a text file, you can

```java
Word2Vec model = Word2VecUtils.readFromTxtFile(file);
```

This project assumes that the bin file created by the [C word2vec
tool](https://code.google.com/p/word2vec/) uses little-endian to write floats
into the bin file. Each float occupies 4 bytes (32 bits) specified by the IEEE
754 standard. To read the word2vec model from a bin file, you can

```java
Word2Vec model = Word2VecUtils.readFromBinFile(file);
```

Alternatively, the project also allows you to specifiy the bye
order of the bin file.

```java
Word2Vec model = Word2VecUtils.readFromBinFile(file, byteOrder);
```

In many cases, the pre-trained word vectors is very large. For example, the
model trained on part of Google New data set contains 300-dimensional vectors
for 3 million words and phrases. The size of the model is about 1.5G in bin.gz
format. This makes loading and using the word2vec model difficult and
inefficient. On the other hand, in some projects we may not need to load the
entire model. Suppose we have already known the vocabulary of the text. For
computing the similarity of words in the text, We only need vectors whose words
are in the vocabulary. For this purpose, the project provides a function that
loads a subset of word2vec specifiec by the vocabulary.

```java
Word2Vec model = Word2VecUtils.readFromBinFile(file, vocab);
```

### Release information

You may download the source code from this website. Alternatively You can pull
it from the centeral Maven repositories:

```XML
<dependency>
  <groupId>com.pengyifan.word2vec</groupId>
  <artifactId>pengyifan-word2vec</artifactId>
  <version>0.0.1</version>
</dependency>
```

or

```XML
<repositories>
    <repository>
        <id>oss-sonatype</id>
        <name>oss-sonatype</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
...
<dependency>
  <groupId>com.pengyifan.word2vec</groupId>
  <artifactId>pengyifan-word2vec</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Developers

* Yifan Peng (yfpeng@udel.edu)

### Webpage

The official word2vec webpage is available with all up-to-date instructions and
code.

* [https://code.google.com/p/word2vec/](https://code.google.com/p/word2vec/)
