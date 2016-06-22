(ns forced.http
  (:require
    [org.httpkit.client :as http-client]
    [cheshire.core :as json]
    [manifold.stream :as s]
    [manifold.deferred :as d]))

(def ^:dynamic *default-agent* "Forced 1.0 - Clojure 1.9-alpha3 (http-kit 2.1.18)")
(def ^:dynamic *default-keepalive* 30000)
(def ^:dynamic *default-timeout* 30000)

(defn keyword-json [response] (json/parse-string response true))

(defn wrap-json
  [response]
  (if (re-matches 
        #"application\/json.*"
        (get-in response [:headers :content-type]))
    (update-in response [:body] keyword-json) response))

(defn rest-request
  "This function wraps org.httpkit.client/request into a manifold deferred
  value which callbacks can be chained upon. This also includes some defaults
  that Forced suggests if none are provided. It also automatically handles any
  outgoing generating or parsing of JSON from request or response bodies."
  [http-opts]
  (d/chain
    (d/->deferred
      (http-client/request
        (merge
          {:user-agent *default-agent*
           :keepalive *default-keepalive*
           :timeout *default-timeout*
           :headers (when (:body http-opts)
                      (merge
                        {"content-type" "application/json"}  
                        (:headers http-opts)))
           :body (when (:body http-opts)
                   (json/generate-string (:body http-opts)))}
          (dissoc http-opts :headers))))
    wrap-json))

