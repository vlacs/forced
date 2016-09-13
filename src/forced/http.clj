(ns forced.http
  (:require
    [org.httpkit.client :as http-client]
    [forced.system :refer
     [*http-agent* *keepalive-duration* *timeout-duration*
      *oauth-session* *api-version*]]
    [cheshire.core :as json]
    [manifold.stream :as s]
    [manifold.deferred :as d]))

(defn finish-url-str
  [parts]
  (apply
    str
    (interpose
      "/"
      (concat
        [(:instance-url @(*oauth-session*))]
        parts))))

(defn data-services-uri
  [& parts]
  (concat
    ["services" "data" (deref *api-version*)]
    parts))

(defn keyword-json [response] (json/parse-string response true))

(defn wrap-json
  [response]
  (if (re-matches 
        #"application\/json.*"
        (get-in response [:headers :content-type]))
    (update-in response [:body] keyword-json) response))

(defn prepare-rest-request
  [merged-http-opts]
  (if (:body merged-http-opts)
    (-> merged-http-opts
        (assoc-in
          [:headers "content-length"]

          )
        (assoc-in 
          [:headers "content-type"]
          "application/json"))
    merged-http-opts))

(defn call-rest!
  [endpoint-url http-opts]
  (http-client/request
    (merge
      {:user-agent *http-agent*
       :keepalive *keepalive-duration* 
       :timeout *timeout-duration*
       :url endpoint-url}
      http-opts)))

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
          
          (dissoc http-opts :headers))))
    wrap-json))

(defn authentication-str
  [system]
  (str "Bearer " (:access-token @(:oauth2-session system))))

(defn make-system-rest-request-fn
  [system]
  (fn system-rest-request
    [http-opts]
    (assoc-in http-opts [:headers "Authorization"]
              (authentication-str system))))

