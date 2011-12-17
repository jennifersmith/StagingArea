(ns cdc_server.core
  (:require [clojure.xml :as xml])
  (:use [ ring.adapter.jetty ] [cheshire.core]))

(def contracts
  [
   {
    :request-method :get
    :uri "/accounts"
    :content-type :json
    :should-match "$.accounts.account_id"
    :sample-data [{:accounts [{:account_id "[A-Z]+" }]}]  ;; HACK!!
    }
   {
    :request-method :get
    :uri "/statuses/user_timeline.xml"
    :content-type :xml
    :should-match "statuses/status/text"
    :sample-data (xml/parse "http://twitter.com/statuses/user_timeline.xml?id=jennifersmithco")
    }
   ])
(def content-type->http-header
  {
   :xml "text/xml"
   :json "application/json"
   })

(def content-type->generator
  {
   :xml xml/emit
   :json generate-string
   })

(defn make-match-predicate [contract]
  (fn [request]
    (first  (filter
             #(and
               (= (% :uri)  (request :uri) )
               (= (% :request-method) (request :request-method) ))
             contracts
             ))))
(defn make-mock-handler [contract]
  (fn [req]
    {:status  200
     :headers {"Content-Type" (content-type->http-header (:content-type contract))}
     :body  ((content-type->generator
              (:content-type contract)) (:sample-data contract))}))

(defn make-mock-handlers [contracts]
  (conj (vec  (map
               (fn [contract]
                 [(make-match-predicate contract)
                  (make-mock-handler contract)])
               contracts
               ))
         [(fn [req] true) four-oh-four]
         ))

(defn four-oh-four [req]
  {
   :status 200
   :headers {"Content-Type" "text/html"}
   :body
   (str "<h1>Page not found</h1> <textarea style=\"height:400px; width:800px\"> "  req "</textarea>" )  })

(defn app [request]
  (first
   (filter (comp not nil?)
           (map (fn [[match? handler]]
                  (if (match? request) (handler request)))
                (make-mock-handlers contracts)))))

(def server (run-jetty #'app {:port 9007 :join? false}))

(defn restart-server []
  (.stop server)
  (.start server)
  )