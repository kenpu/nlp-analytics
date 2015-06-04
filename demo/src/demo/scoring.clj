(ns demo.scoring
  (:use opennlp.nlp))

(def tokenize (make-tokenizer "resources/models/en-token.bin"))
(def pos-maxent (make-pos-tagger "resources/models/en-pos-maxent.bin"))
(def pos-perceptron (make-pos-tagger "resources/models/en-pos-perceptron.bin"))

(defn score-by-pos-maxent
  [s]
  (:probabilities (meta (pos-maxent (tokenize s)))))

(defn score-by-pos-perceptron
  [s]
  (:probabilities (meta (pos-perceptron (tokenize s)))))

