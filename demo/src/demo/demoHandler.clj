(ns demo.demoHandler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [ring.util.response :as resp])
  (:use demo.scoring))

(def goog-lm (make-google-lm))
(def root (str (System/getProperty "user.dir") "/resources/public"))

(defn param
  "Extract parameter from a request"
  [request name]
  (get-in request [:params name]))

(defn score-sentence
  "Returns n-gram scores of sentence for n 1 through 5"
  [sentence]
  (let [words (tokenize sentence)]
    {:status 200
     :body {:words words
            :blm_1 (assoc (score-by-blm {:words words :N 1 :includeSpanScores "yes" :lm goog-lm}) :n 1)
            :blm_2 (assoc (score-by-blm {:words words :N 2 :includeSpanScores "yes" :lm goog-lm}) :n 2)
            :blm_3 (assoc (score-by-blm {:words words :N 3 :includeSpanScores "yes" :lm goog-lm}) :n 3)
            :blm_4 (assoc (score-by-blm {:words words :N 4 :includeSpanScores "yes" :lm goog-lm}) :n 4)
            :blm_5 (assoc (score-by-blm {:words words :N 5 :includeSpanScores "yes" :lm goog-lm}) :n 5) }})) 

(defroutes app-routes
  (route/files "/" {:root root})
  (POST "/score/" request
        (let [sentence (param request :sentence)]
          (score-sentence sentence)))
  (route/not-found "Not Found"))

(def app
(-> (handler/site app-routes)
  (middleware/wrap-json-body {:keywords? true}) 
  (middleware/wrap-json-response)))
