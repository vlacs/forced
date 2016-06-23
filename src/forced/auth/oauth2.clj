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
  "Authenticates the current instance with the credentials stored in the system
  map and updates the oauth2-session atom. The return value of this function is
  a deferred value representing the completion of this task by giving the
  system map back."
  [system]
  (d/chain
    (rest-request (access-token-request @(:auth system)))
    (partial update-oauth2-session! system)))

(defn stop-reauth-cron!
  "Stops the recurring task of re-authentication if, it has been started.
  It also swaps in a place-holder indicating that the task has been stopped."
  [system]
  (d/future
    (swap!
      (fn [stop-task-fn!]
        (when stop-task-fn! (stop-task-fn!))
        :forced/task-stopped)
      (get-in system [:tasks :re-auth]))
    system))

(defn start-reauth-cron!
  "Starts the re-authentication recurring task and stores the termination fn
  into the atom representing this task in this instance."
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
  "Restarts the re-authentication service by stopping it (if it's running,)
  then starting it back up again."
  (d/chain
    (stop-reauth-cron! system)
    (start-reauth-cron!)))

