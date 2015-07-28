(ns sentence-scorer.core
  (:use [sentence-scorer.evaluate]
        [sentence-scorer.analysis]))

(def lm (make-google-lm-fake))

(defn get-n-grams
  "Takes a sentence and returns its n-gram scores for n = 5 through 1"
  [sentence]
  (let [words (tokenize sentence)]
    (for [i (range 5 0 -1)]
      (:spanScores (score-by-blm {:words words :N i :lm lm})))))

(defn get-sentence-vectors
  "Takes n-gram scores and returns aggregated relative scores"
  [ngrams]
  (for [i (range 0 5) :let [s (nth ngrams i)]]
    (get-weighted-mean s)))