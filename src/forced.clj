(ns forced
  (:require
    [forced.auth.oauth2 :refer [start-reauth-cron!]]
    [forced.http :refer [make-system-rest-request-fn]]
    [clojure.edn :as edn]
    [clojure.spec :as spec]
    [manifold.stream :as s]
    [manifold.deferred :as d]))

(defn skeleton
  []
  {:auth
   (atom
     {:endpoint nil 
      :client-id nil
      :secret nil
      :username nil
      :password nil})
   :api-version (atom "v36.0")
   :oauth2-session
   (atom
     {:issued-at nil
      :instance-url nil
      :signature nil
      :access-token nil})
   :call-queues
   {:rest-single (s/stream)
    :rest-batch (s/stream)}
   :tasks {:re-auth (atom ::task-not-started)}})

(defn set-credentials!
  [[system {:keys [auth-endpoint client-id client-secret username password]}]]
  (reset!
    (:auth system)
    {:endpoint auth-endpoint
     :client-id client-id
     :secret client-secret
     :username username
     :password password})
  system)

(defn add-system-fns
  [system]
  (assoc
    system
    :rest-request (make-system-rest-request-fn system)))

(defn start!
  [credentials]
  (d/chain
    (d/future [(skeleton) credentials])
    set-credentials!
    start-reauth-cron!
    add-system-fns))

(comment

  (def config (edn/read-string (slurp "config.edn")))
  (def system (deref (start! config)))

  (def bar (keys (:body @foo)))

  )

