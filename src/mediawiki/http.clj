(ns mediawiki.http
  "HTTP is the namespace containing methods for connecting to the MediaWiki instance in a RESTFUL manner."
  (:require [clj-http.client   :as client]
            [clj-http.cookies  :as cookies]))

;; http-session type is needed in order to allow for different instances of http
(deftype http-session [cookie-jar])

(defn new-session
  "Creates a new instance of http-session for subsequent requests.
   `:cookie-jar` is an optional instance of clj-http.cookies/cookie-store."
  [& {cookie-jar :cookie-jar}]
  (if (nil? cookie-jar)
    (->http-session (cookies/cookie-store))
    (->http-session cookie-jar)))

(defn http-get
  "Perform an async HTTP GET with callbacks.
   `session` is a required instance of http-session.
   `:host` is the fully qualified url to the base of the installation.
   `:end-point` is the end-point to use with a leading /.
   `:params` is a hash-map of paramters for the request.
   `:success` is the function to call when a request is successful.
   `:faulure` is the function to call when errors arise."
  [session 
   & {host      :host
      end-point :end-point
      params    :params
      success   :success
      failure   :failure}]
   (let [url (format "%s%s" host end-point)]
     (client/get url {:accept        :json
                      :async?        true
                      :cookie-policy :standard
                      :cookie-store  (.cookie-jar session)
                      :query-params  params}
                 success failure)))

(defn http-post
  "Perform an async HTTP POST with callbacks.
   `session` is a required instance of http-session.
   `:host` is the fully qualified url to the base of the installation.
   `:end-point` is the end-point to use with a leading /.
   `:params` is a hash-map of paramters for the request.
   `:success` is the function to call when a request is successful.
   `:faulure` is the function to call when errors arise."
  [session
   & {host      :host
      end-point :end-point
      params    :params
      success   :success
      failure   :failure}]
  (let [url (format "%s%s" host end-point)]
    (client/post url {:accept        :json
                      :async?        true
                      :cookie-policy :standard
                      :cookie-store  (.cookie-jar session)
                      :form-params   params}
                 success failure)))