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

(defn score-sentence
  "Takes a sentence and returns score vector"
  [sentence]
  (get-sentence-vectors (get-n-grams sentence)))

(defn -main
  [& args]
  (loop [userChoice "1"]
    (if (= userChoice "0")
      (println "Exiting. Goodbye!")
      (do 
        (println "Enter two sentences to compare:")
        (let [s1 (read-line)
              score1 (vec (score-sentence s1))
              s2 (read-line)
              score2 (vec (score-sentence s2))]
          (println (str "Sentence 1: " score1))
          (println (str "Sentence 2: " score2))
          (println (str "Comparator: " (compare score1 score2))))
        (println "Enter 1 to evaluate again, 0 to quit:")
        (recur (read-line))))))