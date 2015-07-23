(ns sentence-scorer.analysis)

(defn get-standard-deviations
  "Takes list of scores and returns a corresponding list of how many standard deviations the value is within."
  [scores]
  (let [mean (/ (apply + scores) (count scores))
        differences (map #(- % mean) scores)
        variance (/ (apply + (map #(Math/pow % 2) differences)) (count differences))
        standard-dev (Math/sqrt variance)]
    (map #(/ % standard-dev) differences)))

(defn get-anomaly-data
  "Returns number of dips and value of largest dip"
  [scores]
  (let [threshold -1.5]
    {:num-dips (count (filter #(< % threshold) scores))
     :max-dip (apply min scores)}))