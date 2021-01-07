(ns mediawiki.client
  (:require [mediawiki.http :as http]))

;; mediawiki-session type is needed in order to manage multiple mediawiki sessions
(deftype mediawiki-session [http-session url username password])

(defn new-session
  "Creates a new instance of mediawiki-session for multiple requests.
  `:url` is the fully qualified path to the base of the installation.
  `:username` is the username of the account this session will represent.
  `:password` is the password for that session. 
   `:cookie-jar` is an optional instance of clj-http.cookies/cookie-store."
  [& {url        :url
      username   :username
      password   :password
      cookie-jar :cookie-jar}]
  (->mediawiki-session (http/new-session :cookie-jar cookie-jar)
                       url
                       username
                       password))

(defn- fetch-login-token 
  "Attempts to fetch a login token from the API.
   `session` is a required instance of mediawiki-session.
   `success` is the function to call upon successful token retrieval.
   `failure` is the function to call if there is an error fetching the token."
  [session success failure]
  (let [params {:action     "login"
                :lgname     (.username session)
                :lgpassword (.password session)
                :format     "json"}]
    (http/http-post (.http-session session)
                    (.url session)
                    "/api.php"
                    params
                    (fn [res] 
                      (let [token (get-in res "login" "token")]
                        (if (nil? token)
                          (failure {:failed   true
                                    :response res})
                          (success token))))
                    (fn [res] (failure {:failed   true
                                        :response res})))))

(defn- complete-login
  "Attempts to complete the login with a token.
   `session` is a required instance of mediawiki-session.
   `token` is a required token.
   `success` is the function to call upon a successful login.
   `failure` is the function to call if the login action fails."
  [session token success failure]
  (let [params {:action     "login"
                :lgname     (.username session)
                :lgpassword (.password session)
                :lgtoken    token
                :format     "json"}]
    (http/http-post (.http-session session)
                    (.url session)
                    "/api.php"
                    params
                    (fn [res]
                      (let [result (get-in res "login" "result")]
                        (if (= result "Success")
                          (success)
                          (failure {:failed   true
                                    :response res}))))
                    (fn [res] (failure {:failed   true
                                        :response res})))))

(defn login
  "Attempts to perform a login request to the mediawiki instance and responds through callbacks.
   `session` is a required instance of mediawiki-session.
   `:success` is the callback for a successful login.
   `:failure` is the callback for a failed login."
  [session
   & {success :success
      failure :failure}]
  (fetch-login-token session
                     (fn [token]
                       (complete-login session
                                       token
                                       (fn [] (success))
                                       (fn [res] (failure {:failed   true
                                                           :response res}))))
                     (fn [res] (failure {:failed   true
                                         :response res}))))