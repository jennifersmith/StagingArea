[
   {
    :request-method :get
    :uri "/accounts"
    :content-type :json
    :should-match "$.accounts.account_id"
    :sample-data [
                  {:accounts [{:account_id "1234" } {:account_id "45667"}]}]  ;; HACK!!
    }
   {
    :status 202
    :request-method :get
    :uri "/statuses/user_timeline.json"
    :content-type :json
    :should-match [
                   [ "$.user.name"
                     #"[A-Z]{0,20}"
                     ]
                   [ "$.text"
                      #".{0,140}"
                      ]]
    :sample-data [{ 
                   :text "ZOMG I had toast for breakfast"
                   :user {:name "Jen"} }
                  {
                   :text "Wheels Down ORD!"
                   :user {:name "Jen"}
                   }]}]