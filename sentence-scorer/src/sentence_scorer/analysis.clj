(ns sentence-scorer.analysis)

(defn get-weighted-mean
  "Takes a list and returns the mean"
  [scores]
  (let [k (count scores)
        W (repeat k 1)
        terms (for [i (range k) :let [Wi (nth W i)]]
                (* Wi (nth scores i)))]
    (/ (apply + terms) k)))