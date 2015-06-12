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

(def goog-lm (make-google-lm-fake))

(defn score-sentence
  [sentence method includeSpanScores spanNgram]
  (let [words (tokenize sentence)
        score (condp = method
                "pos-maxent" (score-by-pos-maxent words includeSpanScores)
                "pos-perceptron" (score-by-pos-perceptron words includeSpanScores)
                "blm" (score-by-blm {:words words :N spanNgram :includeSpanScores includeSpanScores :lm goog-lm}))]
    {:status 200
     :body (assoc score :words words)}))

(defroutes app-routes
  (POST "/score-sentence/" request
        (let [sentence (param request :sentence)
              method (param request :method)
              includeSpanScores (param request :includeSpanScores)
              spanNgram (Integer/parseInt (param request :spanNgram))]
           (score-sentence sentence method includeSpanScores spanNgram)))
  
  (POST "/score-text/" request
        (let [text (param request :text)
              method (param request :method)
              includeSpanScores (param request :includeSpanScores)
              spanNgram (Integer/parseInt (param request :spanNgram))]
          {:status 200
           :body "Not implemented"}))
  
  (route/not-found "Not Found"))

(def app
(-> (handler/site app-routes)
  (middleware/wrap-json-body {:keywords? true}) 
  (middleware/wrap-json-response)))
