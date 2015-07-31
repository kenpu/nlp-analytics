(ns sentence-scorer.core
  (:use [sentence-scorer.evaluate]
        [sentence-scorer.analysis])
  (:require [clojure.string :as str]))

(def lm (make-google-lm-fake))

(defn score-sentence
  "Takes a sentence and returns score vector"
  [sentence]
  (get-sentence-vectors (get-n-grams lm sentence)))

(defn get-file-lines
  "Takes a filename and returns the file's sentences"
  [filename]
  (let [content (slurp filename)]
    ;;Split on commas and periods followed by whitespace
    (str/split content #",\s|\.\s"))) 

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