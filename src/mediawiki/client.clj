(ns mediawiki.client
  (:require [mediawiki.http :refer [http-post]]))

(defn login
  "This function performs the login action on the wiki.
   `:host` is the fully qualified path to the base wiki installation.
   `:username` is the username for the account.
   `:password` is the password for the account.
   `:success` is a function that will be called on login success.
   `:failure` is a function that will be called on login failure."
  [& {host     :host
      username :username
      password :password
      success  :success
      failure  :failure}]
  (let [prom  (promise)
        pass  (fn [res] (deliver prom {:result res}))
        fail  (fn [rej] (deliver prom {:failed true  :result rej}))
        res   {}
        token nil]
    (do
      (http-post :host      host 
                 :end-point "/api.php"
                 :success   pass
                 :failure   fail
                 :params    {:action     "login"
                             :lgname     username
                             :lgpassword password
                             :format     "json"})
      (set! res @prom)
      (if (contains? res :failed)
        (failure res)
        (do
          (set! token (get-in res ["login" "token"]))
          (if (nil? token)
            (failure res)
            (http-post :host host
                       :end-point "/api.php"
                       :success )))))))
