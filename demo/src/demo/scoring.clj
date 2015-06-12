(ns demo.scoring
  (:use opennlp.nlp)
  (:import (edu.berkeley.nlp.lm.io LmReaders)))

(def tokenize (make-tokenizer "resources/models/en-token.bin"))
(def pos-maxent (make-pos-tagger "resources/models/en-pos-maxent.bin"))
(def pos-perceptron (make-pos-tagger "resources/models/en-pos-perceptron.bin"))
(def get-sentences (make-sentence-detector "resources/models/en-sent.bin"))

(defn make-google-lm
  []
  (do
    (println "Loading the Google language model.")
    (let [modeldir "/home/blair/NLP/models" ;;(System/getenv "LMDIR")
          vocab (str modeldir "/vocab_cs.gz")
          lm (str modeldir "/eng.blm")
          goog-lm (if (empty? modeldir)
                    (throw (Exception. "Environment variable LMDIR is not set"))
                    (LmReaders/readGoogleLmBinary lm vocab))]
      (println "Completed")
      goog-lm)))

(defn make-google-lm-fake [] nil)

(defn get-log-prob 
  [n-gram lm]
  (if (nil? lm)
    -100
    (.getLogProb lm n-gram)))

(defn make-ngrams
  "Returns a seq for each consecutive n words in words"
  [n words]
  (let [L (max 0 (- (count words) n))]
    (for [i (range 0 (inc L))]
     (subvec words i (min (count words) (+ i n))))))

(defn make-score
  "Prepares return data"
  [total-prob & args]
  (if (empty? args)
    {:sentenceScore total-prob}
    {:sentenceScore total-prob
     :spanScores (first args)}))

(defn score-by-pos-maxent
  [words includeSpanScores]
  (let [probs (map #(Math/log %) (:probabilities (meta (pos-maxent words))))
        total-prob (apply + probs)]
    (if includeSpanScores
      (make-score total-prob probs)
      (make-score total-prob))))

(defn score-by-pos-perceptron
  [words includeSpanScores]
  (let [probs (map #(Math/log %) (:probabilities (meta (pos-perceptron words))))
        total-prob (apply + probs)]
    (if includeSpanScores
      (make-score total-prob probs)
      (make-score total-prob))))

(defn score-by-blm
  "s: sentence<string>, N:n-gram length<int>, includeSpanScores: <boolean>"
  [{:keys [words N includeSpanScores lm]}]
  (let [n-grams (make-ngrams N words)
        probs (map #(get-log-prob % lm) n-grams)
        total-prob (apply + probs)]
    (if includeSpanScores
      (make-score total-prob probs)
      (make-score total-prob))))
