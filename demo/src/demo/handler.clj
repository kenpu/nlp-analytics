(ns demo.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [ring.util.response :as resp])
  (:use demo.scoring))

(defn determine-method
  [method sentence]
  (condp = method
    "pos-maxent" (score-by-pos-maxent sentence)
    "pos-perceptron" (score-by-pos-perceptron sentence)
    "blm" (score-by-blm sentence)
    (str "Invalid method selection: " method)))

(defn param
  [request name]
  (get-in request [:params name]))

(defn get-span-scores
  [includeSpanScores sentence spanNgram]
  (if includeSpanScores
    (vector (score-spans-by-blm sentence spanNgram))
    [])
  )

(defn score-sentence
  [sentence method includeSpanScores spanNgram]
  {:words (tokenize sentence)
   :sentenceScore (determine-method method sentence)
   :spanScores (get-span-scores includeSpanScores sentence spanNgram)})

(defn score-text
  [text method includeSpanScores spanNgram]
  (loop [remain (get-sentences text) result []]
    (if (empty? remain)
      result
      (recur (rest remain) (conj result {:sentence (first remain)
                                         :words (tokenize (first remain))
                                         :sentenceScore (determine-method method (first remain))
                                         :spanScores (get-span-scores includeSpanScores (first remain) spanNgram)})))))

(defroutes app-routes
  (POST "/score-sentence/" request
        (let [sentence (param request :sentence)
              method (param request :method)
              includeSpanScores (param request :includeSpanScores)
              spanNgram (Integer/parseInt (param request :spanNgram))]
          {:status 200
           :body (score-sentence sentence method includeSpanScores spanNgram)}))
  (POST "/score-text/" request
        (let [text (param request :text)
              method (param request :method)
              includeSpanScores (param request :includeSpanScores)
              spanNgram (Integer/parseInt (param request :spanNgram))]
          {:status 200
           :body {:result (score-text text method includeSpanScores spanNgram)}}))
  (route/not-found "Not Found"))

(def app
(-> (handler/site app-routes)
  (middleware/wrap-json-body {:keywords? true}) 
  (middleware/wrap-json-response)))
