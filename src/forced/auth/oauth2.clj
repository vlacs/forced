(ns forced.auth.oauth2
  (:require
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

(defn authenticate!
  [system]
  (rest-request (access-token-request (:auth system))))
