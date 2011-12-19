[
   {
    :request-method :get
    :uri "/accounts"
    :content-type :json
    :should-match "$.accounts.account_id"
    :sample-data [
                  {:accounts
                   [{:acounnt_id 12344} {:account_id 4566} {:account_id 12345 } {:account_id 1234} ] }]
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