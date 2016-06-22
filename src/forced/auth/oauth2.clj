(ns forced.auth.oauth2
  (:require
    [manifold.deferred :as d]
    [manifold.time :as t]
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

(defn stop-reauth-cron!
  [system]
  (d/future
    (swap!
      (fn [stop-task-fn!]
        (when stop-task-fn! (stop-task-fn!))
        :forced/task-stopped)
      (get-in system [:tasks :re-auth]))
    system))

(defn start-reauth-cron!
  [system]
  (d/future
    (swap!
      system
      assoc-in
      [:tasks :re-auth]
      (t/every (+ (t/hours 1) (t/minutes 55))
               (partial authenticate! system)))
    system))

(defn restart-reauth-cron!
  [system]
  (d/chain
    (stop-reauth-cron! system)
    (start-reauth-cron!)))
