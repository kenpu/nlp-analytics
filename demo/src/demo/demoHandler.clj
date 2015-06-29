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
  (let [words (tokenize sentence)
        blm1 (score-by-blm {:words words :N 1 :includeSpanScores "yes" :lm goog-lm})
        blm2 (score-by-blm {:words words :N 2 :includeSpanScores "yes" :lm goog-lm})
        blm3 (score-by-blm {:words words :N 3 :includeSpanScores "yes" :lm goog-lm})
        blm4 (score-by-blm {:words words :N 4 :includeSpanScores "yes" :lm goog-lm})
        blm5 (score-by-blm {:words words :N 5 :includeSpanScores "yes" :lm goog-lm})]
    {:status 200
     :body {:words words
            :blm_1 (assoc blm1 :spanSize 1 :deviations (get-standard-deviations (:spanScores blm1)))
            :blm_2 (assoc blm2 :spanSize 2 :deviations (get-standard-deviations (:spanScores blm2)))
            :blm_3 (assoc blm3 :spanSize 3 :deviations (get-standard-deviations (:spanScores blm3)))
            :blm_4 (assoc blm4 :spanSize 4 :deviations (get-standard-deviations (:spanScores blm4)))
            :blm_5 (assoc blm5 :spanSize 5 :deviations (get-standard-deviations (:spanScores blm5)))}}))

(defroutes app-routes
  (route/files "/" {:root root})
  (POST "/score/" request
        (let [sentence (param request :sentence)]
          (score-sentence sentence)))
  (POST "/score-maxent/" request
        (let [sentence (param request :sentence)]
          {:status 200
           :body {:maxent (score-by-pos-maxent (tokenize sentence) "yes")}}))
  (POST "/score-perceptron/" request
        (let [sentence (param request :sentence)]
          {:status 200
           :body {:perceptron (score-by-pos-perceptron (tokenize sentence) "yes")}}))
  (route/not-found "Not Found"))

(def app
(-> (handler/site app-routes)
  (middleware/wrap-json-body {:keywords? true}) 
  (middleware/wrap-json-response)))
