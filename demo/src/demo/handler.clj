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
    (str "Invalid method selection")))

(defroutes app-routes
  (POST "/score-sentence/" request
        (let [sentence (:sentence request)
              method (:method request)
              includeSpanScores (:includeSpanScores request)
              spanNgram (:spanNgram request)
              result {
                      :words (tokenize (str sentence))
                      :sentenceScore (determine-method method (str sentence))
                      }]
          (if (true? includeSpanScores)
            (assoc result (score-spans-by-blm (str sentence) spanNgram))
            result)))
  (route/not-found "Not Found"))

(def app
(-> (handler/site app-routes)
  (middleware/wrap-json-body {:keywords? true}) 
  (middleware/wrap-json-response)))
