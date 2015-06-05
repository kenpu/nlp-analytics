(ns demo.scoring
  (:use opennlp.nlp)
  (:import (edu.berkeley.nlp.lm.io LmReaders)))

(def tokenize (make-tokenizer "resources/models/en-token.bin"))
(def pos-maxent (make-pos-tagger "resources/models/en-pos-maxent.bin"))
(def pos-perceptron (make-pos-tagger "resources/models/en-pos-perceptron.bin"))

(def google-lm
  (let [modeldir "/home/blair/NLP/models" ;;(System/getenv "LMDIR")
        vocab (str modeldir "/vocab_cs.gz")
        lm (str modeldir "/eng.blm")]
    (if (empty? modeldir)
      (throw (Exception. "Environment variable LMDIR is not set"))
      (LmReaders/readGoogleLmBinary lm vocab))))

(defn score-by-pos-maxent
  [s]
  (:probabilities (meta (pos-maxent (tokenize s)))))

(defn score-by-pos-perceptron
  [s]
  (:probabilities (meta (pos-perceptron (tokenize s)))))

(defn score-by-blm
  [s]
  (.getLogProb google-lm (tokenize s)))

(defn score-spans-by-blm
  [s n]
  (loop [remain (tokenize s)
         result []]
    (if (empty? remain)
      result
      (recur (rest remain) (conj result (.getLogProb google-lm (take n remain)))))))