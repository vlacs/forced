(ns forced
  (:require
    [clojure.spec :as spec]
    [org.httpkit.client :as http-client]
    [cheshire.core :as json]
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
   :session
   (atom
     {:token nil
      :issued-at nil
      :instance-url nil
      :signature nil
      :access-token nil})
   :call-queues
   {:single (s/stream)
    :batch (s/stream)}
   :tasks {:re-auth (atom nil)}})

(defn start!
  [{:keys [auth-endpoint client-id client-secret username password]}]
  (let [state (skeleton)]
    (swap! state assoc
           :endpoint auth-endpoint
           :client-id client-id
           :secret client-secret
           :username username
           :password password)
    state))

