(ns sentence-scorer.analysis)

(defn get-weighted-mean
  "Takes a list and returns the mean"
  [scores]
  (let [k (count scores)
        W (repeat k 1)
        terms (for [i (range k) :let [Wi (nth W i)]]
                (* Wi (get scores i)))]
    (/ (apply + terms) k)))

(defn get-standard-deviations
  "Takes list of scores and returns a corresponding list of how many standard deviations the value is within."
  [scores]
  (let [mean (get-mean scores)
        differences (map #(- % mean) scores)
        variance (/ (apply + (map #(Math/pow % 2) differences)) (count differences))
        standard-dev (Math/sqrt variance)]
    (map #(/ % standard-dev) differences)))