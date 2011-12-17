(ns cdc_server.core
  (:require [clojure.xml :as xml])
  (:use [ring.middleware.params] [ ring.adapter.jetty ] [cheshire.core]))

(def contracts (load-file "contracts.clj") )

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
    (and
      (= (contract :uri)  (request :uri) )
      (= (contract :request-method) (request :request-method)))))

(defn make-mock-handler [contract]
  (fn [req]
    {:status  (or (:status contract) 200)
     :headers {"Content-Type" (content-type->http-header (:content-type contract))}
     :body  ((content-type->generator
              (:content-type contract)) (:sample-data contract))}))

(defn four-oh-four [req]
  {
   :status 200
   :headers {"Content-Type" "text/html"}
   :body
   (str "<h1>Page not found</h1> <textarea style=\"height:400px; width:800px\"> "  req "</textarea>" )  })

(defn make-mock-handlers [contracts]
  (conj (vec  (map ;; vec is a hack to ensure ordering !
               (fn [contract]
                 [(make-match-predicate contract)
                  (make-mock-handler contract)])
               contracts
               ))
         [(fn [req] true) four-oh-four]))


(defn handler [request]
  (first
   (filter (comp not nil?)
           (map (fn [[match? handler]]
                  (if (match? request) (handler request)))
                (make-mock-handlers contracts)))))

(def app
  (wrap-params handler))

(def server (run-jetty #'app {:port 9007 :join? false}))

(defn restart-server []
  (.stop server)
  (.start server)
  )