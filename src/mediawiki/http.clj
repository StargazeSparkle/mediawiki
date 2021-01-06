(ns mediawiki.http
  "HTTP is the namespace containing methods for connecting to the MediaWiki instance in a RESTFUL manner."
  (:require [clj-http.client   :as client]
            [clj-http.cookies  :as cookies]))

;; TODO: Encapsulate this or push the responsibility to the end-user
;; This should not be a global on the off-chance that someone wants to
;; run multiple mediawiki instances.
(def *http-cookie-jar* (cookies/cookie-store))

(defn http-get
  "Perform an async HTTP GET with callbacks.
   `:host` is the fully qualified url to the base of the installation.
   `:end-point` is the end-point to use with a leading /.
   `:params` is a hash-map of paramters for the request.
   `:success` is the function to call when a request is successful.
   `:faulure` is the function to call when errors arise."
  [& {host      :host
      end-point :end-point
      params    :params
      success   :success
      failure   :failure}]
   (let [url (format "%s%s" host end-point)]
     (client/get url {:accept        :json
                      :async?        true
                      :cookie-policy :standard
                      :cookie-store  *http-cookie-jar*
                      :query-params  params}
                 success failure)))

(defn http-post
  "Perform an async HTTP POST with callbacks.
   `:host` is the fully qualified url to the base of the installation.
   `:end-point` is the end-point to use with a leading /.
   `:params` is a hash-map of paramters for the request.
   `:success` is the function to call when a request is successful.
   `:faulure` is the function to call when errors arise."
  [& {host      :host
      end-point :end-point
      params    :params
      success   :success
      failure   :failure}]
  (let [url (format "%s%s" host end-point)]
    (client/post url {:accept        :json
                      :async?        true
                      :cookie-policy :standard
                      :cookie-store  *http-cookie-jar*
                      :form-params   params}
                 success failure)))