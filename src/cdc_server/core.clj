(ns cdc_server.core
  (
   :use ring.adapter.jetty))

(def contracts
  [
   {
    :request-method :get
    :uri "/accounts"
    :content-type :json
    :should-match "$.accounts.account"
    }
   ])

(defn matching-contracts [request]
  (first  (filter
           #(and
             (= (% :uri)  (request :uri) )
             (= (% :request-method) (request :request-method) ))
           contracts
           )))

(defn mock-response [req contract]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body   "foo"}
    )


(defn four-oh-four [req]
  {
   :status 200
   :headers {"Content-Type" "text/html"}
   :body (str req)  })

(defn app [req]
  (let [contract (matching-contracts req)]
   (if (nil? contract )      
      (four-oh-four req)
      (mock-response req contract))))

;;(def server (run-jetty #'app {:port 9001 :join? false}))

(defn restart-server []
  (.stop server)
  (.start server)
  )