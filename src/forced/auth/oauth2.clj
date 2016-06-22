(ns forced.auth.oauth2
  (:require
    [manifold.deferred :as d]
    [cheshire.core :as json]
    [forced.http :refer [rest-request]]))

(defn access-token-request
  [{:keys [endpoint client-id secret username password]}]
  {:url endpoint
   :method :post
   :follow-redirects false
   :query-params {"grant_type" "password"
                  "client_id" client-id
                  "client_secret" secret
                  "username" username
                  "password" password}})

(defn clean-key
  [[k v]]
  [(keyword
    (clojure.string/lower-case
      (clojure.string/replace (name k) #"_" "-"))) v])

(defn update-oauth2-session!
  [system response]
  (let [{:keys [body status]} response]
    (when (= status 200)
      (reset!
        (:oauth2-session system)
        (apply
          hash-map
          (mapcat clean-key body)))))
  system)

(defn authenticate!
  [system]
  (d/chain
    (rest-request (access-token-request @(:auth system)))
    (partial update-oauth2-session! system)))
