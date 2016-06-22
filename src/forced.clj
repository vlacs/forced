(ns forced
  (:require
    [forced.auth.oauth2 :refer [authenticate!]]
    [clojure.edn :as edn]
    [clojure.spec :as spec]
    [manifold.stream :as s]
    [manifold.deferred :as d]))

(def authentication-endpoint "https://test.salesforce.com/services/oauth2/token")

(defn skeleton
  []
  {:auth
   (atom
     {:endpoint nil 
      :client-id nil
      :secret nil
      :username nil
      :password nil})
   :oauth2-session
   (atom
     {:token nil
      :issued-at nil
      :instance-url nil
      :signature nil
      :access-token nil})
   :call-queues
   {:rest-single (s/stream)
    :rest-batch (s/stream)}
   :tasks {:re-auth (atom nil)}})

(defn start!
  [{:keys [auth-endpoint client-id client-secret username password]}]
  (let [state (skeleton)]
    (swap!
      (:auth state) assoc
      :endpoint auth-endpoint
      :client-id client-id
      :secret client-secret
      :username username
      :password password)
    state))


(comment

  (def system (start! (edn/read-string (slurp "config.edn"))))
  (authenticate! system)

  (def bar (keys (:body @foo)))

  )

