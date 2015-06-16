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
            :blm_1 (score-by-blm {:words words :N 1 :includeSpanScores "yes" :lm goog-lm})
            :blm_2 (score-by-blm {:words words :N 2 :includeSpanScores "yes" :lm goog-lm})
            :blm_3 (score-by-blm {:words words :N 3 :includeSpanScores "yes" :lm goog-lm})
            :blm_4 (score-by-blm {:words words :N 4 :includeSpanScores "yes" :lm goog-lm})
            :blm_5 (score-by-blm {:words words :N 5 :includeSpanScores "yes" :lm goog-lm})}}))

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
